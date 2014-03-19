/*   
   Copyright 2011-2013 The Cassandra Consortium (cassandra-fp7.eu)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package eu.cassandra.sim.entities.people;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.cassandra.server.mongo.MongoActivities;
import eu.cassandra.sim.Event;
import eu.cassandra.sim.PricingPolicy;
import eu.cassandra.sim.SimulationParams;
import eu.cassandra.sim.entities.Entity;
import eu.cassandra.sim.entities.appliances.Appliance;
import eu.cassandra.sim.math.ProbabilityDistribution;
import eu.cassandra.sim.math.response.Response;
import eu.cassandra.sim.utilities.Constants;
import eu.cassandra.sim.utilities.ORNG;
import eu.cassandra.sim.utilities.RNG;
import eu.cassandra.sim.utilities.Utils;

public class Activity extends Entity {
	static Logger logger = Logger.getLogger(Activity.class);
	
	private final static String WEEKDAYS = "weekdays";
	private final static String WEEKENDS = "weekends";
	private final static String NONWORKING = "nonworking";
	private final static String WORKING = "working";
	private final static String ANY = "any";

	private final HashMap<String, ProbabilityDistribution> nTimesGivenDay;
	private final HashMap<String, ProbabilityDistribution> probStartTime;
	private final HashMap<String, ProbabilityDistribution> probDuration;
	private final HashMap<String, Boolean> shiftable;
	private final HashMap<String, Boolean> config;
	private HashMap<String, Vector<Appliance>> appliances;
	private HashMap<String, Vector<Double>> probApplianceUsed;
	private SimulationParams simulationWorld;
	
	private Vector<DBObject> activityModels;
	private Vector<DBObject> starts;
	private Vector<DBObject> durations;
	private Vector<DBObject> times;
	
	private double maxPower = 0;
	private double cycleMaxPower = 0;
	private double avgPower = 0;
	private double energy = 0;
	private double previousEnergy = 0;
	private double energyOffpeak = 0;
	private double previousEnergyOffpeak = 0;
	private double cost = 0;

	public static class Builder {
		// Required parameters
		private final String id;
		private final String name;
		private final String description;
		private final String type;
		private final HashMap<String, ProbabilityDistribution> nTimesGivenDay;
		private final HashMap<String, ProbabilityDistribution> probStartTime;
		private final HashMap<String, ProbabilityDistribution> probDuration;
		private final HashMap<String, Boolean> shiftable;
		private final HashMap<String, Boolean> config;
		// Optional parameters: not available
		private HashMap<String, Vector<Appliance>> appliances;
		private HashMap<String, Vector<Double>> probApplianceUsed;
		private Vector<DBObject> activityModels;
		private Vector<DBObject> starts;
		private Vector<DBObject> durations;
		private Vector<DBObject> times;
		private SimulationParams simulationWorld;

		public Builder (String aid, String aname, String adesc, String atype, SimulationParams world) {
			id = aid;
			name = aname;
			description = adesc;
			type = atype;
			appliances = new HashMap<String, Vector<Appliance>>();
			probApplianceUsed = new HashMap<String, Vector<Double>>();
			nTimesGivenDay = new HashMap<String, ProbabilityDistribution>();
			probStartTime = new HashMap<String, ProbabilityDistribution>();
			probDuration = new HashMap<String, ProbabilityDistribution>();
			shiftable = new HashMap<String, Boolean>();
			config = new HashMap<String, Boolean>();
			activityModels = new Vector<DBObject>();
			starts = new Vector<DBObject>();
			durations = new Vector<DBObject>();
			times = new Vector<DBObject>();
			simulationWorld = world;
		}
		
		public Builder startTime(String day, ProbabilityDistribution probDist) {
			probStartTime.put(day, probDist);
			return this;
		}
		
		public Builder shiftable(String day, Boolean value) {
			shiftable.put(day, value);
			return this;
		}
		
		public Builder duration(String day, ProbabilityDistribution probDist) {
			probDuration.put(day, probDist);
			return this;
		}

		public Builder times(String day, ProbabilityDistribution probDist) {
			nTimesGivenDay.put(day, probDist);
			return this;
		}

		public Activity build () {
			return new Activity(this);
		}
	}

	private Activity (Builder builder) {
		id = builder.id;
		name = builder.name;
		description = builder.description;
		type = builder.type;
		appliances = builder.appliances;
		nTimesGivenDay = builder.nTimesGivenDay;
		probStartTime = builder.probStartTime;
		probDuration = builder.probDuration;
		shiftable = builder.shiftable;
		config = builder.config;
		probApplianceUsed = builder.probApplianceUsed;
		simulationWorld = builder.simulationWorld;
		starts = builder.starts;
		times = builder.times;
		durations = builder.durations;
		activityModels = builder.activityModels;
	}

	public void addAppliance (String day, Appliance a, Double prob) {
		if(appliances.get(day) == null) {
			appliances.put(day, new Vector<Appliance>());
		}
		Vector<Appliance> vector = appliances.get(day);
		vector.add(a);
		if(probApplianceUsed.get(day) == null) {
			probApplianceUsed.put(day, new Vector<Double>());
		}
		Vector<Double> probVector = probApplianceUsed.get(day);
		probVector.add(prob);
	}
	
	public void addStartTime(String day, ProbabilityDistribution probDist) {
		probStartTime.put(day, probDist);
	}
	
	public void addStarts(DBObject o) {
		starts.add(o);
	}
	
	public void addDurations(DBObject o) {
		durations.add(o);
	}
	
	public void addTimes(DBObject o) {
		times.add(o);
	}
	
	public void addModels(DBObject o) {
		activityModels.add(o);
	}
	
	public Vector<DBObject> getModels() {
		return activityModels;
	}
	
	public Vector<DBObject> getStarts() {
		return starts;
	}
	
	public Vector<DBObject> getTimes() {
		return times;
	}
	
	public Vector<DBObject> getDurations() {
		return durations;
	}
	
	public void addDuration(String day, ProbabilityDistribution probDist) {
		probDuration.put(day, probDist);
	}

	public void addTimes(String day, ProbabilityDistribution probDist) {
		nTimesGivenDay.put(day, probDist);
	}
	
	public void addShiftable(String day, Boolean value) {
		shiftable.put(day, new Boolean(value));
	}
	
	public void addConfig(String day, Boolean value) {
		config.put(day, new Boolean(value));
	}

	public String getName () {
		return name;
	}

	public String getDescription () {
		return description;
	}

	public String getType () {
		return type;
	}

	public SimulationParams getSimulationWorld () {
		return simulationWorld;
	}
	
	private String getKey(String keyword) {
		Set<String> set = nTimesGivenDay.keySet();
		Iterator<String> iter = set.iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			if(key.contains(keyword)) {
				return key;
			}
		}
		return new String();
	}
	
	public double[] calcExpPower() {
		double[] act_exp = new double[Constants.MIN_IN_DAY];
		ProbabilityDistribution numOfTimesProb = null;
		ProbabilityDistribution responseNumOfTimesProb = null;
		ProbabilityDistribution startProb = null;
		ProbabilityDistribution responseStartProb = null;
		ProbabilityDistribution durationProb = null;
		Boolean isShiftable = null;
		Boolean isExclusive = null;
		Vector<Double> probVector = new Vector<Double>();
		Vector<Appliance> vector = new Vector<Appliance>();
		int tick = 0;
		
		// First search for specific days
		String date = simulationWorld.getSimCalendar().getCurrentDate(tick);
		String dayOfWeek = simulationWorld.getSimCalendar().getDayOfWeek(tick);
		String dateKey = getKey(date);
		String dayOfWeekKey = getKey(dayOfWeek);

		boolean weekend = simulationWorld.getSimCalendar().isWeekend(tick);
		numOfTimesProb = nTimesGivenDay.get(dateKey);
		startProb = probStartTime.get(dateKey);
		durationProb = probDuration.get(dateKey);
		probVector = probApplianceUsed.get(dateKey);
		vector = appliances.get(dateKey);
		isShiftable = shiftable.get(dateKey);
		isExclusive = config.get(dateKey);
		// Then search for specific days
		if(!notNull(numOfTimesProb, startProb, durationProb, probVector, vector)) {
			numOfTimesProb = nTimesGivenDay.get(dayOfWeekKey);
			startProb = probStartTime.get(dayOfWeekKey);
			durationProb = probDuration.get(dayOfWeekKey);
			probVector = probApplianceUsed.get(dayOfWeekKey);
			isShiftable = shiftable.get(dayOfWeekKey);
			vector = appliances.get(dayOfWeekKey);
			// Then for weekdays and weekends
			if(!notNull(numOfTimesProb, startProb, durationProb, probVector, vector)) {
				if (weekend) {
					numOfTimesProb = nTimesGivenDay.get(WEEKENDS);
					startProb = probStartTime.get(WEEKENDS);
					durationProb = probDuration.get(WEEKENDS);
					probVector = probApplianceUsed.get(WEEKENDS);
					isShiftable = shiftable.get(WEEKENDS);
					isExclusive = config.get(WEEKENDS);
					vector = appliances.get(WEEKENDS);
				} else {
					numOfTimesProb = nTimesGivenDay.get(WEEKDAYS);
					startProb = probStartTime.get(WEEKDAYS);
					durationProb = probDuration.get(WEEKDAYS);
					probVector = probApplianceUsed.get(WEEKDAYS);
					isShiftable = shiftable.get(WEEKDAYS);
					isExclusive = config.get(WEEKDAYS);
					vector = appliances.get(WEEKDAYS);
				}
				// Backwards compatibility
				if(!notNull(numOfTimesProb, startProb, durationProb, probVector, vector)) {
					if (weekend) {
						numOfTimesProb = nTimesGivenDay.get(NONWORKING);
						startProb = probStartTime.get(NONWORKING);
						durationProb = probDuration.get(NONWORKING);
						probVector = probApplianceUsed.get(NONWORKING);
						isShiftable = shiftable.get(NONWORKING);
						isExclusive = config.get(NONWORKING);
						vector = appliances.get(NONWORKING);
					} else {
						numOfTimesProb = nTimesGivenDay.get(WORKING);
						startProb = probStartTime.get(WORKING);
						durationProb = probDuration.get(WORKING);
						probVector = probApplianceUsed.get(WORKING);
						isShiftable = shiftable.get(WORKING);
						isExclusive = config.get(WORKING);
						vector = appliances.get(WORKING);
					}
					// Then for any
					if(!notNull(numOfTimesProb, startProb, durationProb, probVector, vector)) {
						numOfTimesProb = nTimesGivenDay.get(ANY);
						startProb = probStartTime.get(ANY);
						durationProb = probDuration.get(ANY);
						probVector = probApplianceUsed.get(ANY);
						isShiftable = shiftable.get(ANY);
						isExclusive = config.get(ANY);
						vector = appliances.get(ANY);
					}
				}				
			}
		}
		
		int durationMax = Math.min(Constants.MIN_IN_DAY, durationProb.getHistogram().length - 1);
		
		if(vector != null) {
			for(Appliance app : vector) {
				System.out.println(app.getName());
				Double[] applianceConsumption = app.getActiveConsumption();
				boolean staticConsumption = app.isStaticConsumption();
				for (int j = 0; j < act_exp.length; j++)
					act_exp[j] += aggregatedProbability(durationProb, startProb, applianceConsumption, j, durationMax, staticConsumption);			
			}
		}
		
		return act_exp;
	}
	
	  private double aggregatedProbability (ProbabilityDistribution duration, ProbabilityDistribution startTime,
			  Double[] consumption, int index,
              int durationMax,
              boolean staticConsumption) {
		  
		  double result = 0;
		  for (int i = 0; i < durationMax; i++) {
			  if (staticConsumption)
				  result += duration.getProbabilityGreaterEqual(i) * startTime.getProbability((Constants.MIN_IN_DAY + index - i) % Constants.MIN_IN_DAY) * consumption[0];
			  else
				  result += duration.getProbabilityGreaterEqual(i) * startTime.getProbability((Constants.MIN_IN_DAY + index - i) % Constants.MIN_IN_DAY) * consumption[i % consumption.length];
		  }
		  return result;
	  }

	public void
		updateDailySchedule(int tick, PriorityBlockingQueue<Event> queue,
				PricingPolicy pricing, PricingPolicy baseline, double awareness,
				double sensitivity, String responseType, ORNG orng) {
		/*
		 *  Decide on the number of times the activity is going to be activated
		 *  during a day
		 */
		ProbabilityDistribution numOfTimesProb = null;
		ProbabilityDistribution responseNumOfTimesProb = null;
		ProbabilityDistribution startProb = null;
		ProbabilityDistribution responseStartProb = null;
		ProbabilityDistribution durationProb = null;
		Boolean isShiftable = null;
		Boolean isExclusive = null;
		Vector<Double> probVector = null;
		Vector<Appliance> vector = null;
		
		// First search for specific days
				String date = simulationWorld.getSimCalendar().getCurrentDate(tick);
				String dayOfWeek = simulationWorld.getSimCalendar().getDayOfWeek(tick);
				String dateKey = getKey(date);
				String dayOfWeekKey = getKey(dayOfWeek);
				System.out.println(date);
				boolean weekend = simulationWorld.getSimCalendar().isWeekend(tick);
				numOfTimesProb = nTimesGivenDay.get(dateKey);
				startProb = probStartTime.get(dateKey);
				durationProb = probDuration.get(dateKey);
				probVector = probApplianceUsed.get(dateKey);
				vector = appliances.get(dateKey);
				isShiftable = shiftable.get(dateKey);
				isExclusive = config.get(dateKey);
				// Then search for specific days
				if(!notNull(numOfTimesProb, startProb, durationProb, probVector, vector)) {
					numOfTimesProb = nTimesGivenDay.get(dayOfWeekKey);
					startProb = probStartTime.get(dayOfWeekKey);
					durationProb = probDuration.get(dayOfWeekKey);
					probVector = probApplianceUsed.get(dayOfWeekKey);
					isShiftable = shiftable.get(dayOfWeekKey);
					vector = appliances.get(dayOfWeekKey);
					// Then for weekdays and weekends
					if(!notNull(numOfTimesProb, startProb, durationProb, probVector, vector)) {
						if (weekend) {
							numOfTimesProb = nTimesGivenDay.get(WEEKENDS);
							startProb = probStartTime.get(WEEKENDS);
							durationProb = probDuration.get(WEEKENDS);
							probVector = probApplianceUsed.get(WEEKENDS);
							isShiftable = shiftable.get(WEEKENDS);
							isExclusive = config.get(WEEKENDS);
							vector = appliances.get(WEEKENDS);
						} else {
							numOfTimesProb = nTimesGivenDay.get(WEEKDAYS);
							startProb = probStartTime.get(WEEKDAYS);
							durationProb = probDuration.get(WEEKDAYS);
							probVector = probApplianceUsed.get(WEEKDAYS);
							isShiftable = shiftable.get(WEEKDAYS);
							isExclusive = config.get(WEEKDAYS);
							vector = appliances.get(WEEKDAYS);
						}
						// Backwards compatibility
						if(!notNull(numOfTimesProb, startProb, durationProb, probVector, vector)) {
							if (weekend) {
								numOfTimesProb = nTimesGivenDay.get(NONWORKING);
								startProb = probStartTime.get(NONWORKING);
								durationProb = probDuration.get(NONWORKING);
								probVector = probApplianceUsed.get(NONWORKING);
								isShiftable = shiftable.get(NONWORKING);
								isExclusive = config.get(NONWORKING);
								vector = appliances.get(NONWORKING);
							} else {
								numOfTimesProb = nTimesGivenDay.get(WORKING);
								startProb = probStartTime.get(WORKING);
								durationProb = probDuration.get(WORKING);
								probVector = probApplianceUsed.get(WORKING);
								isShiftable = shiftable.get(WORKING);
								isExclusive = config.get(WORKING);
								vector = appliances.get(WORKING);
							}
							// Then for any
							if(!notNull(numOfTimesProb, startProb, durationProb, probVector, vector)) {
								System.out.println("Any");
								numOfTimesProb = nTimesGivenDay.get(ANY);
								startProb = probStartTime.get(ANY);
								durationProb = probDuration.get(ANY);
								System.out.println(durationProb.toString());
								probVector = probApplianceUsed.get(ANY);
								isShiftable = shiftable.get(ANY);
								isExclusive = config.get(ANY);
								vector = appliances.get(ANY);
							}
						}				
					}
				}
		
		if(notNull(numOfTimesProb, startProb, durationProb, probVector, vector)) {
			// Response
			responseStartProb = startProb;
			responseNumOfTimesProb = numOfTimesProb;
			if(isShiftable.booleanValue()) {
				if(pricing.getType().equalsIgnoreCase("TOUPricing") && 
						baseline.getType().equalsIgnoreCase("TOUPricing")) {
					responseStartProb = Response.respond(startProb, pricing,
							baseline, awareness, sensitivity, responseType);
					responseNumOfTimesProb = Response.respond(numOfTimesProb, pricing,
							baseline, awareness, sensitivity, "Daily");
				}
			}
			
			int numOfTimes = 0;
			try {
				numOfTimes = responseNumOfTimesProb.getPrecomputedBin(orng.nextDouble());
			} catch (Exception e) {
				logger.error(Utils.stackTraceToString(e.getStackTrace()));
				e.printStackTrace();
			}
			
			/*
			 * Decide the duration and start time for each activity activation
			 */
			while (numOfTimes > 0) {
				int duration = Math.max(durationProb.getPrecomputedBin(orng.nextDouble()), 1);
				int startTime = Math.min(Math.max(responseStartProb.getPrecomputedBin(orng.nextDouble()), 0), 1439);
				if(vector.size() > 0) {
					if(isExclusive.booleanValue()) {
						int selectedApp = orng.nextInt(vector.size());
						Appliance a = vector.get(selectedApp);
						addApplianceActivation(a, duration, startTime, tick, queue, this, orng);
					} else {
						for (int j = 0; j < vector.size(); j++) {
							Appliance a = vector.get(j);
							addApplianceActivation(a, duration, startTime, tick, queue, this, orng);
							
						}
					}
				}
				numOfTimes--;
			}
		}
	}
	
	private void addApplianceActivation(Appliance a, int duration, int startTime, int tick, PriorityBlockingQueue<Event> queue, Activity act, ORNG orng) {
		int appDuration = duration;
		int appStartTime = startTime;
		String hash = Utils.hashcode((new Long(orng.nextLong()).toString()));
		Event eOn = new Event(tick + appStartTime, Event.SWITCH_ON, a, hash, act);
		queue.offer(eOn);
		Event eOff = new Event(tick + appStartTime + appDuration, Event.SWITCH_OFF, a, hash, act);
		queue.offer(eOff);
	}
	
	public boolean notNull(Object a, 
			Object b, 
			Object c, 
			Object d, 
			Object e) {
		return a != null && 
				b != null && 
				c != null && 
				d != null &&
				e != null;
	}

	@Override
	public BasicDBObject toDBObject() {
		BasicDBObject obj = new BasicDBObject();
		obj.put("name", name);
		obj.put("description", description);
		obj.put("type", type);
		obj.put("pers_id", parentId);
		return obj;
	}

	@Override
	public String getCollection() {
		return MongoActivities.COL_ACTIVITIES;
	}
	
    public void updateMaxPower(double power) {
    	if(power > maxPower) maxPower = power;
    	if(power > cycleMaxPower) cycleMaxPower = power;
    }
    
    public double getMaxPower() {
    	return maxPower;
    }
    
    public void updateAvgPower(double powerFraction) {
    	avgPower += powerFraction;
    }
    
    public double getAvgPower() {
    	return avgPower;
    }
    
    public void updateEnergy(double power) {
    	energy += (power/1000.0) * Constants.MINUTE_HOUR_RATIO; 
    }
    
    public void updateEnergyOffpeak(double power) {
    	energyOffpeak += (power/1000.0) * Constants.MINUTE_HOUR_RATIO; 
    }
    
    public void updateCost(PricingPolicy pp, int tick) {
    	cost += pp.calculateCost(energy, previousEnergy, energyOffpeak, previousEnergyOffpeak, tick, cycleMaxPower);
    	cycleMaxPower = 0;
    	previousEnergy = energy;
    	previousEnergyOffpeak = energyOffpeak;
    }
    
    public double getEnergy() {
    	return energy;
    }
    
    public double getCost() {
    	return cost;
    }

}

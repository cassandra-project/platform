/*   
   Copyright 2011-2012 The Cassandra Consortium (cassandra-fp7.eu)

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

import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.cassandra.server.mongo.MongoActivities;
import eu.cassandra.sim.Event;
import eu.cassandra.sim.SimulationWorld;
import eu.cassandra.sim.entities.Entity;
import eu.cassandra.sim.entities.appliances.Appliance;
import eu.cassandra.sim.math.ProbabilityDistribution;
import eu.cassandra.sim.utilities.RNG;
import eu.cassandra.sim.utilities.Utils;

public class Activity extends Entity {
	static Logger logger = Logger.getLogger(Activity.class);

	private final HashMap<String, ProbabilityDistribution> nTimesGivenDay;
	private final HashMap<String, ProbabilityDistribution> probStartTime;
	private final HashMap<String, ProbabilityDistribution> probDuration;
	private HashMap<String, Vector<Appliance>> appliances;
	private HashMap<String, Vector<Double>> probApplianceUsed;
	private SimulationWorld simulationWorld;
	
	private Vector<DBObject> activityModels;
	private Vector<DBObject> starts;
	private Vector<DBObject> durations;
	private Vector<DBObject> times;

	public static class Builder {
		// Required parameters
		private final String name;
		private final String description;
		private final String type;
		private final HashMap<String, ProbabilityDistribution> nTimesGivenDay;
		private final HashMap<String, ProbabilityDistribution> probStartTime;
		private final HashMap<String, ProbabilityDistribution> probDuration;
		// Optional parameters: not available
		private HashMap<String, Vector<Appliance>> appliances;
		private HashMap<String, Vector<Double>> probApplianceUsed;
		private Vector<DBObject> activityModels;
		private Vector<DBObject> starts;
		private Vector<DBObject> durations;
		private Vector<DBObject> times;
		private SimulationWorld simulationWorld;

		public Builder (String aname, String adesc, String atype, SimulationWorld world) {
			name = aname;
			description = adesc;
			type = atype;
			appliances = new HashMap<String, Vector<Appliance>>();
			probApplianceUsed = new HashMap<String, Vector<Double>>();
			nTimesGivenDay = new HashMap<String, ProbabilityDistribution>();
			probStartTime = new HashMap<String, ProbabilityDistribution>();
			probDuration = new HashMap<String, ProbabilityDistribution>();
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
		name = builder.name;
		description = builder.description;
		type = builder.type;
		appliances = builder.appliances;
		nTimesGivenDay = builder.nTimesGivenDay;
		probStartTime = builder.probStartTime;
		probDuration = builder.probDuration;
		probApplianceUsed = builder.probApplianceUsed;
		simulationWorld = builder.simulationWorld;
		starts = builder.starts;
		times = builder.times;
		durations = builder.durations;
		activityModels = builder.activityModels;
	}

	public void addAppliance (String day, Appliance a, Double prob) {
		Vector<Appliance> vector = appliances.get(day);
		if(vector == null) {
			vector = new Vector<Appliance>(); 
		}
		vector.add(a);
		Vector<Double> probVector = probApplianceUsed.get(day);
		if(probVector == null) {
			probVector = new Vector<Double>(); 
		}
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

	public String getName () {
		return name;
	}

	public String getDescription () {
		return description;
	}

	public String getType () {
		return type;
	}

	public SimulationWorld getSimulationWorld () {
		return simulationWorld;
	}

	public void
		updateDailySchedule(int tick, PriorityBlockingQueue<Event> queue) {
		/*
		 *  Decide on the number of times the activity is going to be activated
		 *  during a day
		 */
		ProbabilityDistribution numOfTimesProb;
		ProbabilityDistribution startProb;
		ProbabilityDistribution durationProb;
		
		Vector<Double> probVector;
		Vector<Appliance> vector;

		if (simulationWorld.getSimCalendar().isWeekend(tick)) {
			System.out.println("isWeekend");
			numOfTimesProb = nTimesGivenDay.get("nonworking");
			startProb = probStartTime.get("nonworking");
			durationProb = probDuration.get("nonworking");
			probVector = probApplianceUsed.get("nonworking");
			vector = appliances.get("nonworking");
		} else {
			System.out.println("isNotWeekend");
			numOfTimesProb = nTimesGivenDay.get("working");
			startProb = probStartTime.get("working");
			durationProb = probDuration.get("working");
			probVector = probApplianceUsed.get("working");
			vector = appliances.get("working");
		}

		System.out.println("POINT A");
		System.out.println(numOfTimesProb.getNumberOfParameters());
		int numOfTimes = numOfTimesProb.getPrecomputedBin();
		System.out.println("POINT B");
		System.out.println(numOfTimes);
		/*
		 * Decide the duration and start time for each activity activation
		 */
		while (numOfTimes > 0) {
			int duration = Math.max(durationProb.getPrecomputedBin(), 1);
			int startTime = startProb.getPrecomputedBin();
			// Select appliances to be switched on
			for (int j = 0; j < appliances.size(); j++) {
				if (RNG.nextDouble() < probVector.get(j).doubleValue()) {
					Appliance a = vector.get(j);
					int appDuration = duration;
					int appStartTime = startTime;
					String hash = Utils.hashcode((new Long(RNG.nextLong()).toString()));
					Event eOn = new Event(tick + appStartTime, Event.SWITCH_ON, a, hash);
					queue.offer(eOn);
					Event eOff =
							new Event(tick + appStartTime + appDuration, Event.SWITCH_OFF, a, hash);
					queue.offer(eOff);
				}
			}
			numOfTimes--;
		}
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

}

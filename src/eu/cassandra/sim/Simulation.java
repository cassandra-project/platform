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
package eu.cassandra.sim;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.server.mongo.MongoActivityModels;
import eu.cassandra.server.mongo.MongoDistributions;
import eu.cassandra.server.mongo.MongoResults;
import eu.cassandra.server.mongo.MongoRuns;
import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.sim.entities.Entity;
import eu.cassandra.sim.entities.appliances.Appliance;
import eu.cassandra.sim.entities.appliances.ConsumptionModel;
import eu.cassandra.sim.entities.installations.Installation;
import eu.cassandra.sim.entities.people.Activity;
import eu.cassandra.sim.entities.people.Person;
import eu.cassandra.sim.math.Gaussian;
import eu.cassandra.sim.math.GaussianMixtureModels;
import eu.cassandra.sim.math.Histogram;
import eu.cassandra.sim.math.ProbabilityDistribution;
import eu.cassandra.sim.math.Uniform;
import eu.cassandra.sim.utilities.Constants;
import eu.cassandra.sim.utilities.RNG;
import eu.cassandra.sim.utilities.Utils;

/**
 * The Simulation class can simulate up to 4085 years of simulation.
 * 
 * @author Kyriakos C. Chatzidimitriou (kyrcha [at] iti [dot] gr)
 * 
 */
public class Simulation implements Runnable {
	
	static Logger logger = Logger.getLogger(Simulation.class);

	private Vector<Installation> installations;

	private PriorityBlockingQueue<Event> queue;

	private int tick;

	private int endTick;
	
	private int mcruns;
  
	private MongoResults m;

	private SimulationParams simulationWorld;
	
	private PricingPolicy pricing;
	
	private String scenario;
	
	private String dbname;

	public Collection<Installation> getInstallations () {
		return installations;
	}

	public Installation getInstallation (int index) {
		return installations.get(index);
	}

	public int getCurrentTick () {
		return tick;
	}

	public int getEndTick () {
		return endTick;
	}
  
	public Simulation(String ascenario, String adbname) {
		scenario = ascenario;
		dbname = adbname;
		m = new MongoResults(dbname);
		m.createIndexes();
  		RNG.init();
	}
  
  	public SimulationParams getSimulationWorld () {
  		return simulationWorld;
  	}

  	public void run () {
  		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(dbname));
		DBObject objRun = DBConn.getConn().getCollection(MongoRuns.COL_RUNS).findOne(query);
  		try {
  			System.out.println("Run " + dbname + " started @ " + Calendar.getInstance().getTimeInMillis());
  			long startTime = System.currentTimeMillis();
  			int percentage = 0;
  			int mccount = 0;
  			double mcrunsRatio = 1.0/mcruns;
  			for(int i = 0; i < mcruns; i++) {
  				tick = 0;
  				double avgPPowerPerHour = 0;
  				double avgQPowerPerHour = 0;
  				double[] avgPPowerPerHourPerInst = new double[installations.size()];
  				double[] avgQPowerPerHourPerInst = new double[installations.size()];
  	  			double maxPower = 0;
  	  			double avgPower = 0;
  	  			double energy = 0;
  	  			double energyOffpeak = 0;
  	  			double cost = 0;
  	  			double billingCycleEnergy = 0;
  	  			double billingCycleEnergyOffpeak = 0;
  	  			double billingCycleDays = 0;
  	  			
  	  			while (tick < endTick) {
  	  				// If it is the beginning of the day create the events
  	  				if (tick % Constants.MIN_IN_DAY == 0) {
//  	  				System.out.println("Day " + ((tick / Constants.MIN_IN_DAY) + 1));
  	  					for (Installation installation: installations) {
//  						System.out.println(installation.getName());
  	  						installation.updateDailySchedule(tick, queue);
  	  					}
  	  					billingCycleDays++;
//  					System.out.println("Daily queue size: " + queue.size() + "(" + 
//  					simulationWorld.getSimCalendar().isWeekend(tick) + ")");
  	  				}
  	  				Event top = queue.peek();
  	  				while (top != null && top.getTick() == tick) {
  	  					Event e = queue.poll();
  	  					boolean applied = e.apply();
  	  					if(applied) {
  	  						if(e.getAction() == Event.SWITCH_ON) {
  	  							try {
  	  								//m.addOpenTick(e.getAppliance().getId(), tick);
  	  							} catch (Exception exc) {
  	  								throw exc;
  	  							}
  	  						} else if(e.getAction() == Event.SWITCH_OFF){
  	  							//m.addCloseTick(e.getAppliance().getId(), tick);
  	  						}
  	  					}
  	  					top = queue.peek();
  	  				}

					/*
					 *  Calculate the total power for this simulation step for all the
					 *  installations.
					 */
					float sumP = 0;
					float sumQ = 0;
					int counter = 0;
		  			for(Installation installation: installations) {
		  				installation.nextStep(tick);
		  				double p = installation.getCurrentPowerP();
		  				double q = installation.getCurrentPowerQ();
		  				installation.updateMaxPower(p);
		  				installation.updateAvgPower(p/endTick);
		  				if(pricing.isOffpeak(tick)) {
		  					installation.updateEnergyOffpeak(p);
		  				} else {
		  					installation.updateEnergy(p);
		  				}
		  				m.addTickResultForInstallation(tick, 
		  						installation.getId(), 
		  						p * mcrunsRatio, 
		  						q * mcrunsRatio, 
		  						MongoResults.COL_INSTRESULTS);
		  				sumP += p;
		  				sumQ += q;
		  				avgPPowerPerHour += p;
		  				avgQPowerPerHour += q;
		  				avgPPowerPerHourPerInst[counter] += p;
		  				avgQPowerPerHourPerInst[counter] += q;
		//  				String name = installation.getName();
		//  				logger.info("Tick: " + tick + " \t " + "Name: " + name + " \t " 
		//  				+ "Power: " + power);
		//  				System.out.println("Tick: " + tick + " \t " + "Name: " + name + " \t " 
		//  		  				+ "Power: " + power);
		  				if(billingCycleDays % pricing.getBillingCycle() == 0 || pricing.getType().equalsIgnoreCase("TOUPricing")) {
		  					installation.updateCost(pricing, tick);
		  				}
		  				counter++;
		  			}
		  			if(sumP > maxPower) maxPower = sumP;
		  			avgPower += sumP/endTick;
		  			if(pricing.isOffpeak(tick)) {
		  				energyOffpeak += (sumP/1000.0) * Constants.MINUTE_HOUR_RATIO;
		  			} else {
		  				energy += (sumP/1000.0) * Constants.MINUTE_HOUR_RATIO;
		  			}
		  			if(billingCycleDays % pricing.getBillingCycle() == 0 || pricing.getType().equalsIgnoreCase("TOUPricing")) {
		  				cost += pricing.calculateCost(energy, 
		  						billingCycleEnergy, 
		  						energyOffpeak,
		  						billingCycleEnergyOffpeak,
		  						tick);
		  				billingCycleEnergy = energy;
		  				billingCycleEnergyOffpeak = energyOffpeak;
		  			}
		  			m.addAggregatedTickResult(tick, 
		  					sumP * mcrunsRatio, 
		  					sumQ * mcrunsRatio, 
		  					MongoResults.COL_AGGRRESULTS);
		  			tick++;
		  			if(tick % Constants.MIN_IN_HOUR == 0) {
		  				m.addAggregatedTickResult((tick/Constants.MIN_IN_HOUR), 
		  						(avgPPowerPerHour/Constants.MIN_IN_HOUR) * mcrunsRatio, 
		  						(avgQPowerPerHour/Constants.MIN_IN_HOUR) * mcrunsRatio, 
		  						MongoResults.COL_AGGRRESULTS_HOURLY);
		  				m.addAggregatedTickResult((tick/Constants.MIN_IN_HOUR), 
		  						(avgPPowerPerHour) * mcrunsRatio, 
		  						(avgQPowerPerHour) * mcrunsRatio, 
		  						MongoResults.COL_AGGRRESULTS_HOURLY_EN);
		  				avgPPowerPerHour = 0;
		  				avgQPowerPerHour = 0;
		  				counter = 0;
			  			for(Installation installation: installations) {
			  				m.addTickResultForInstallation((tick/Constants.MIN_IN_HOUR), 
			  						installation.getId(),
			  						(avgPPowerPerHourPerInst[counter]/Constants.MIN_IN_HOUR) * mcrunsRatio, 
			  						(avgQPowerPerHourPerInst[counter]/Constants.MIN_IN_HOUR) * mcrunsRatio, 
			  						MongoResults.COL_INSTRESULTS_HOURLY);
			  				m.addTickResultForInstallation((tick/Constants.MIN_IN_HOUR), 
			  						installation.getId(),
			  						(avgPPowerPerHourPerInst[counter]) * mcrunsRatio, 
			  						(avgQPowerPerHourPerInst[counter]) * mcrunsRatio, 
			  						MongoResults.COL_INSTRESULTS_HOURLY_EN);
			  				avgPPowerPerHourPerInst[counter] = 0;
			  				avgQPowerPerHourPerInst[counter] = 0;
			  				counter++;
			  			}
		  			}
		  			mccount++;
		  			percentage = (int)(mccount * 100.0 / (mcruns * endTick));
		  			objRun.put("percentage", percentage);
		  	  		DBConn.getConn().getCollection(MongoRuns.COL_RUNS).update(query, objRun);
  	  			}
  	  			for(Installation installation: installations) {
  	  				installation.updateCost(pricing, tick); // update the rest of the energy
  	  				m.addKPIs(installation.getId(), 
  	  						installation.getMaxPower() * mcrunsRatio, 
  	  						installation.getAvgPower() * mcrunsRatio, 
  	  						installation.getEnergy() * mcrunsRatio, 
  	  						installation.getCost() * mcrunsRatio);
  	  			}
  	  			cost += pricing.calculateCost(energy, 
  	  					billingCycleEnergy,
  						energyOffpeak,
  						billingCycleEnergyOffpeak,
  						tick);
  	  			m.addKPIs(MongoResults.AGGR, 
  	  					maxPower * mcrunsRatio, 
  	  					avgPower * mcrunsRatio, 
  	  					energy * mcrunsRatio, 
  	  					cost * mcrunsRatio);
  			}
  			// Obsolete normalization code (to be removed) now it is done on the fly
//  			for(int i = 0; i < endTick; i++) {
//  				for(Installation installation: installations) {
//  					m.normalize(i,installation.getId(), mcruns, MongoResults.COL_INSTRESULTS);
//  					System.out.println(installation.getId());
//  					m.normalize(i,installation.getId(), mcruns, MongoResults.COL_INSTRESULTS_HOURLY);
//  					System.out.println(installation.getId());
//  				}
//  				m.normalize(i, mcruns, MongoResults.COL_AGGRRESULTS);
//  				m.normalize(i, mcruns, MongoResults.COL_AGGRRESULTS_HOURLY);
//  			}
	  		long endTime = System.currentTimeMillis();
	  		objRun.put("ended", endTime);
	  		DBConn.getConn().getCollection(MongoRuns.COL_RUNS).update(query, objRun);
	  		logger.info("Time elapsed for Run " + dbname + ": " + ((endTime - startTime)/(1000.0 * 60)) + " mins");
	  		logger.info("Run " + dbname + " ended @ " + Calendar.getInstance().toString());
  		} catch(Exception e) {
  			System.out.println(Utils.stackTraceToString(e.getStackTrace()));
  			// Change the run object in the db to reflect the exception
  			if(objRun != null) {
  				objRun.put("percentage", -1);
  				objRun.put("state", e.getMessage());
  				DBConn.getConn().getCollection(MongoRuns.COL_RUNS).update(query, objRun);
  			}
  		}
  	}

  	public void setup() throws Exception {
  		installations = new Vector<Installation>();
  		/* TODO  Change the Simulation Calendar initialization */
  		simulationWorld = new SimulationParams();
  		logger.info("Simulation setup started: " + dbname);
  		DBObject jsonScenario = (DBObject) JSON.parse(scenario);
  		DBObject scenarioDoc = (DBObject) jsonScenario.get("scenario");
  		DBObject simParamsDoc = (DBObject) jsonScenario.get("sim_params");
  		DBObject pricingDoc = (DBObject) jsonScenario.get("pricing");
  		if(pricingDoc != null) {
  			pricing = new PricingPolicy(pricingDoc);
  		} else {
  			pricing = new PricingPolicy();
  		}
  		int numOfDays = ((Integer)simParamsDoc.get("numberOfDays")).intValue();
  		
  		endTick = Constants.MIN_IN_DAY * numOfDays;
  		mcruns = ((Integer)simParamsDoc.get("mcruns")).intValue();
  		// Check type of setup
  		String setup = (String)scenarioDoc.get("setup"); 
  		if(setup.equalsIgnoreCase("static")) {
  			staticSetup(jsonScenario);
  		} else if(setup.equalsIgnoreCase("dynamic")) {
  			dynamicSetup(jsonScenario);
  		} else {
  			throw new Exception("Problem with setup property!!!");
  		}
  		logger.info("Simulation setup finished: " + dbname);
  	}

  	public void staticSetup (DBObject jsonScenario) throws Exception {
	    int numOfInstallations = ((Integer)jsonScenario.get("instcount")).intValue();
	    queue = new PriorityBlockingQueue<Event>(2 * numOfInstallations);
	    for (int i = 1; i <= numOfInstallations; i++) {
	    	DBObject instDoc = (DBObject)jsonScenario.get("inst"+i);
	    	String id = ((ObjectId)instDoc.get("_id")).toString();
	    	String name = (String)instDoc.get("name");
	    	String description = (String)instDoc.get("description");
	    	String type = (String)instDoc.get("type");
	    	Installation inst = new Installation.Builder(
	    			id, name, description, type).build();
	    	int appcount = ((Integer)instDoc.get("appcount")).intValue();
	    	// Create the appliances
	    	HashMap<String,Appliance> existing = new HashMap<String,Appliance>();
	    	for (int j = 1; j <= appcount; j++) {
	    		DBObject applianceDoc = (DBObject)instDoc.get("app"+j);
	    		String appid = ((ObjectId)applianceDoc.get("_id")).toString();
	    		String appname = (String)applianceDoc.get("name");
		    	String appdescription = (String)applianceDoc.get("description");
		    	String apptype = (String)applianceDoc.get("type");
		    	double standy = Utils.getDouble(applianceDoc.get("standy_consumption"));
		    	boolean base = Utils.getBoolean(applianceDoc.get("base"));
		    	DBObject consModDoc = (DBObject)applianceDoc.get("consmod");
		    	ConsumptionModel pconsmod = new ConsumptionModel(consModDoc.get("pmodel").toString(), "p");
		    	ConsumptionModel qconsmod = new ConsumptionModel(consModDoc.get("qmodel").toString(), "q");
	    		Appliance app = new Appliance.Builder(
	    				appid,
	    				appname,
	    				appdescription,
	    				apptype, 
	    				inst,
	    				pconsmod,
	    				qconsmod,
	    				standy,
	            		base).build();
	    		existing.put(appid, app);
	    		inst.addAppliance(app);
	    	}
	    	DBObject personDoc = (DBObject)instDoc.get("person1");
	    	String personid = ((ObjectId)personDoc.get("_id")).toString();
    		String personName = (String)personDoc.get("name");
	    	String personDescription = (String)personDoc.get("description");
	    	String personType = (String)personDoc.get("type");
	    	Person person = new Person.Builder(
	    	        		  personid,
	    	        		  personName, 
	    	        		  personDescription,
	    	                  personType, inst).build();
	    	inst.addPerson(person);
	    	int actcount = ((Integer)personDoc.get("activitycount")).intValue();
	    	for (int j = 1; j <= actcount; j++) {
	    		DBObject activityDoc = (DBObject)personDoc.get("activity"+j);
	    		String activityName = (String)activityDoc.get("name");
	    		String activityType = (String)activityDoc.get("type");
	    		int actmodcount = ((Integer)activityDoc.get("actmodcount")).intValue();
	    		Activity act = new Activity.Builder(activityName, "", 
	    				activityType, simulationWorld).build();
	    		ProbabilityDistribution startDist;
	    		ProbabilityDistribution durDist;
	    		ProbabilityDistribution timesDist;
	    		for (int k = 1; k <= actmodcount; k++) {
	    			DBObject actmodDoc = (DBObject)activityDoc.get("actmod"+k);
	    			String actmodName = (String)actmodDoc.get("name");
	    			String actmodType = (String)actmodDoc.get("type");
	    			String actmodDayType = (String)actmodDoc.get("day_type");
	    			DBObject duration = (DBObject)actmodDoc.get("duration");
	    			durDist = json2dist(duration);
	    			DBObject start = (DBObject)actmodDoc.get("start");
	    			startDist = json2dist(start);
	    			DBObject rep = (DBObject)actmodDoc.get("repetitions");
	    			timesDist = json2dist(rep);
	    			act.addDuration(actmodDayType, durDist);
	    			act.addStartTime(actmodDayType, startDist);
	    			act.addTimes(actmodDayType, timesDist);
	    			// add appliances
		    		BasicDBList containsAppliances = (BasicDBList)actmodDoc.get("containsAppliances");
		    		for(int l = 0; l < containsAppliances.size(); l++) {
		    			String containAppId = (String)containsAppliances.get(l);
		    			Appliance app  = existing.get(containAppId);
		    			act.addAppliance(actmodDayType,app,1.0/containsAppliances.size());
		    		}
	    		}
	    		person.addActivity(act);
	    	}
	    	installations.add(inst);
	    }
  }
  	
  	private String addEntity(Entity e) {
  		BasicDBObject obj = e.toDBObject();
  		DBConn.getConn(dbname).getCollection(e.getCollection()).insert(obj);
  		ObjectId objId = (ObjectId)obj.get("_id");
  		return objId.toString();
  	}

  	public void dynamicSetup(DBObject jsonScenario) throws Exception {
  		DBObject scenario = (DBObject)jsonScenario.get("scenario");
  		String scenario_id =  ((ObjectId)scenario.get("_id")).toString();
  		DBObject demog = (DBObject)jsonScenario.get("demog");
  		BasicDBList generators = (BasicDBList) demog.get("generators");
  		// Initialize simulation variables
  		int numOfInstallations = Utils.getInt(demog.get("numberOfEntities"));
  		//System.out.println(numOfInstallations+"");
  		queue = new PriorityBlockingQueue<Event>(2 * numOfInstallations);
  		for (int i = 1; i <= numOfInstallations; i++) {
	    	DBObject instDoc = (DBObject)jsonScenario.get("inst"+1);
	    	String id = i+"";
	    	String name = ((String)instDoc.get("name")) + i;
	    	String description = (String)instDoc.get("description");
	    	String type = (String)instDoc.get("type");
	    	Installation inst = new Installation.Builder(
	    			id, name, description, type).build();
	    	inst.setParentId(scenario_id);
	    	String inst_id = addEntity(inst);
	    	inst.setId(inst_id);
	    	int appcount = Utils.getInt(instDoc.get("appcount"));
	    	// Create the appliances
	    	HashMap<String,Appliance> existing = new HashMap<String,Appliance>();
	    	for (int j = 1; j <= appcount; j++) {
	    		DBObject applianceDoc = (DBObject)instDoc.get("app"+j);
	    		String appid = ((ObjectId)applianceDoc.get("_id")).toString();
	    		String appname = (String)applianceDoc.get("name");
		    	String appdescription = (String)applianceDoc.get("description");
		    	String apptype = (String)applianceDoc.get("type");
		    	double standy = Utils.getDouble(applianceDoc.get("standy_consumption"));
		    	boolean base = Utils.getBoolean(applianceDoc.get("base"));
		    	DBObject consModDoc = (DBObject)applianceDoc.get("consmod");
		    	ConsumptionModel pconsmod = new ConsumptionModel(consModDoc.get("pmodel").toString(), "p");
		    	ConsumptionModel qconsmod = new ConsumptionModel(consModDoc.get("qmodel").toString(), "q");
	    		Appliance app = new Appliance.Builder(
	    				appid,
	    				appname,
	    				appdescription,
	    				apptype, 
	    				inst,
	    				pconsmod,
	    				qconsmod,
	    				standy,
	            		base).build();
	    		existing.put(appid, app);
	    	}
	    	
	    	HashMap<String,Double> gens = new HashMap<String,Double>();
	    	for(int k = 0; k < generators.size(); k++) {
    			DBObject generator = (DBObject)generators.get(k);
    			String entityId = (String)generator.get("entity_id");
    			double prob = Utils.getDouble(generator.get("probability"));
    			gens.put(entityId, new Double(prob));
	    	}
	    	
	    	Set<String> keys = existing.keySet();
	    	for(String key : keys) {
	    		Double prob = gens.get(key);
	    		if(prob != null) {
	    			double probValue = prob.doubleValue();
	    			if(RNG.nextDouble() < probValue) {
    					Appliance selectedApp = existing.get(key);
    					selectedApp.setParentId(inst.getId());
    			    	String app_id = addEntity(selectedApp);
    			    	selectedApp.setId(app_id);
    			    	inst.addAppliance(selectedApp);
    			    	ConsumptionModel cm = selectedApp.getPConsumptionModel();
    			    	cm.setParentId(app_id);
    			    	String cm_id = addEntity(cm);
    			    	cm.setId(cm_id);
    				}
	    		}
	    	}

	    	int personcount = Utils.getInt(instDoc.get("personcount"));
	    	// Create the appliances
	    	HashMap<String,Person> existingPersons = new HashMap<String,Person>();
	    	for (int j = 1; j <= personcount; j++) {
	    		DBObject personDoc = (DBObject)instDoc.get("person"+j);
		    	String personid = ((ObjectId)personDoc.get("_id")).toString();
	    		String personName = (String)personDoc.get("name");
		    	String personDescription = (String)personDoc.get("description");
		    	String personType = (String)personDoc.get("type");
		    	Person person = new Person.Builder(
		    	        		  personid,
		    	        		  personName, 
		    	        		  personDescription,
		    	                  personType, inst).build();
		    	int actcount = Utils.getInt(personDoc.get("activitycount"));
		    	//System.out.println("Act-Count: " + actcount);
		    	for (int k = 1; k <= actcount; k++) {
		    		DBObject activityDoc = (DBObject)personDoc.get("activity"+k);
		    		String activityName = (String)activityDoc.get("name");
		    		String activityType = (String)activityDoc.get("type");
		    		int actmodcount = Utils.getInt(activityDoc.get("actmodcount"));
		    		Activity act = new Activity.Builder(activityName, "", 
		    				activityType, simulationWorld).build();
		    		ProbabilityDistribution startDist;
		    		ProbabilityDistribution durDist;
		    		ProbabilityDistribution timesDist;
		    		for (int l = 1; l <= actmodcount; l++) {
		    			DBObject actmodDoc = (DBObject)activityDoc.get("actmod"+l);
		    			act.addModels(actmodDoc);
		    			String actmodName = (String)actmodDoc.get("name");
		    			String actmodType = (String)actmodDoc.get("type");
		    			String actmodDayType = (String)actmodDoc.get("day_type");
		    			DBObject duration = (DBObject)actmodDoc.get("duration");
		    			act.addDurations(duration);
		    			durDist = json2dist(duration);
		    			//System.out.println(durDist.getPrecomputedBin());
		    			DBObject start = (DBObject)actmodDoc.get("start");
		    			act.addStarts(start);
		    			startDist = json2dist(start);
		    			//System.out.println(startDist.getPrecomputedBin());
		    			DBObject rep = (DBObject)actmodDoc.get("repetitions");
		    			act.addTimes(rep);
		    			timesDist = json2dist(rep);
		    			//System.out.println(timesDist.getPrecomputedBin());
		    			act.addDuration(actmodDayType, durDist);
		    			act.addStartTime(actmodDayType, startDist);
		    			act.addTimes(actmodDayType, timesDist);
		    			// add appliances
			    		BasicDBList containsAppliances = (BasicDBList)actmodDoc.get("containsAppliances");
			    		for(int m = 0; m < containsAppliances.size(); m++) {
			    			String containAppId = (String)containsAppliances.get(m);
			    			Appliance app  = existing.get(containAppId);
			    			//act.addAppliance(actmodDayType,app,1.0/containsAppliances.size());
			    			act.addAppliance(actmodDayType,app,1.0);
			    		}
		    		}
		    		person.addActivity(act);
		    	}
		    	existingPersons.put(personid, person);
	    	}
	    	
	    	double roulette = RNG.nextDouble();
	    	double sum = 0;
	    	for(int k = 0; k < generators.size(); k++) {
	    		DBObject generator = (DBObject)generators.get(k);
	    		String entityId = (String)generator.get("entity_id");
	    		if(existingPersons.containsKey(entityId)) {
	    			double prob = Utils.getDouble(generator.get("probability"));
	    			sum += prob;
	    			if(roulette < sum) {
	    				Person selectedPerson = existingPersons.get(entityId);
	    				selectedPerson.setParentId(inst.getId());
    			    	String person_id = addEntity(selectedPerson);
    			    	selectedPerson.setId(person_id);
	    				inst.addPerson(selectedPerson);
	    				Vector<Activity> activities = selectedPerson.getActivities();
	    				for(Activity a : activities) {
	    					a.setParentId(person_id);
	    					String act_id = addEntity(a);
	    					a.setId(act_id);
	    					Vector<DBObject> models = a.getModels();
	    					Vector<DBObject> starts = a.getStarts();
	    					Vector<DBObject> durations = a.getDurations();
	    					Vector<DBObject> times = a.getTimes();
	    					for(int l = 0; l < models.size();  l++ ) {
	    						DBObject m = models.get(l);
	    						m.put("act_id", act_id);
	    						DBConn.getConn(dbname).getCollection(MongoActivityModels.COL_ACTMODELS).insert(m);
	    				  		ObjectId objId = (ObjectId)m.get("_id");
	    				  		String actmod_id = objId.toString();
	    				  		DBObject s = starts.get(l);
	    				  		s.put("actmod_id", actmod_id);
	    				  		DBConn.getConn(dbname).getCollection(MongoDistributions.COL_DISTRIBUTIONS).insert(s);
	    				  		DBObject d = durations.get(l);
	    				  		d.put("actmod_id", actmod_id);
	    				  		DBConn.getConn(dbname).getCollection(MongoActivityModels.COL_ACTMODELS).insert(d);
	    				  		DBObject t = times.get(l);
	    				  		t.put("actmod_id", actmod_id);
	    				  		DBConn.getConn(dbname).getCollection(MongoActivityModels.COL_ACTMODELS).insert(t);
	    					}
	    				}
	    				break;
	    			}
	    		}
	    	}
	    	
	    	installations.add(inst);
	    }
  		
  	}
  
	public static ProbabilityDistribution json2dist(DBObject distribution) throws Exception {
  		String distType = (String)distribution.get("distrType");
  		switch (distType) {
  		case ("Normal Distribution"):
  			BasicDBList normalList = (BasicDBList)distribution.get("parameters");
  			DBObject normalDoc = (DBObject)normalList.get(0);
  			double mean = Double.parseDouble(normalDoc.get("mean").toString());
  			double std = Double.parseDouble(normalDoc.get("std").toString());
  			Gaussian normal = new Gaussian(mean, std);
  			normal.precompute(0, 1439, 1440);
  			return normal;
        case ("Uniform Distribution"):
   			BasicDBList unifList = (BasicDBList)distribution.get("parameters");
   			DBObject unifDoc = (DBObject)unifList.get(0);
   			double from = Double.parseDouble(unifDoc.get("start").toString()); 
   			double to = Double.parseDouble(unifDoc.get("end").toString()); 
   			Uniform uniform = new Uniform(from, to);
   			uniform.precompute(from, to, (int) to + 1);
   			return uniform;
   		case ("Gaussian Mixture Models"):
        	BasicDBList mixList = (BasicDBList)distribution.get("parameters");
   			int length = mixList.size();
   			double[] w = new double[length];
         	double[] means = new double[length];
         	double[] stds = new double[length];
         	for(int i = 0; i < mixList.size(); i++) {
         		DBObject tuple = (DBObject)mixList.get(i);
         		w[i] = Double.parseDouble(tuple.get("w").toString()); 
         		means[i] = Double.parseDouble(tuple.get("mean").toString()); 
         		stds[i] = Double.parseDouble(tuple.get("std").toString()); 
    		} 
         	GaussianMixtureModels gmm = new GaussianMixtureModels(length, w, means, stds);
         	gmm.precompute(0, 1439, 1440);
         	return gmm;
   		case ("Histogram"):
   			BasicDBList hList = (BasicDBList)distribution.get("values");
   			int l = hList.size();
			double[] v = new double[l];
			for(int i = 0; i < l; i++) {
				v[i] = Double.parseDouble(hList.get(i).toString());
			}
			Histogram h = new Histogram(v);
   			return h;
        default:
        	throw new Exception("Non existing distribution type. Problem in setting up the simulation.");
        }
  	}

}

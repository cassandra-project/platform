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
package eu.cassandra.sim;

import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.server.mongo.MongoResults;
import eu.cassandra.sim.entities.appliances.Appliance;
import eu.cassandra.sim.entities.appliances.ConsumptionModel;
import eu.cassandra.sim.entities.installations.Installation;
import eu.cassandra.sim.entities.people.Activity;
import eu.cassandra.sim.entities.people.Person;
import eu.cassandra.sim.math.Gaussian;
import eu.cassandra.sim.math.GaussianMixtureModels;
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

	private int tick = 0;

	private int endTick;
  
	private MongoResults m;

	private SimulationWorld simulationWorld;
	
	private String scenario;

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
  
	public Simulation(String ascenario) {
		scenario = ascenario;
		RNG.init();
	}
  
  	public SimulationWorld getSimulationWorld () {
  		return simulationWorld;
  	}

  	public void run () {
  		while (tick < endTick) {
  			// If it is the beginning of the day create the events
  			if (tick % Constants.MIN_IN_DAY == 0) {
  				logger.info("Day " + ((tick / Constants.MIN_IN_DAY) + 1));
  				for (Installation installation: installations) {
  					installation.updateDailySchedule(tick, queue);
  				}
  				logger.info("Daily queue size: " + queue.size() + "(" + 
  				simulationWorld.getSimCalendar().isWeekend(tick) + ")");
  			}

  			Event top = queue.peek();
  			while (top != null && top.getTick() == tick) {
  				Event e = queue.poll();
  				e.apply();
  				top = queue.peek();
  			}

  			/*
  			 *  Calculate the total power for this simulation step for all the
  			 *  installations.
  			 */
  			float sumPower = 0;
  			for(Installation installation: installations) {
  				installation.nextStep(tick);
  				double power = installation.getCurrentPower();
  				sumPower += power;
  				String name = installation.getName();
  				logger.info("Tick: " + tick + " \t " + "Name: " + name + " \t " 
  				+ "Power: " + power);
  			}
  			tick++;
  		}
  	}

  	public void setup() throws Exception {
  		logger.info("Simulation setup started.");
  		installations = new Vector<Installation>();
    
  		/* TODO  Change the Simulation Calendar initialization */
  		simulationWorld = new SimulationWorld();
    
  		DBObject jsonScenario = (DBObject) JSON.parse(scenario);
  		DBObject scenarioDoc = (DBObject) jsonScenario.get("scenario");
  		DBObject simParamsDoc = (DBObject) scenarioDoc.get("sim_param");
    
  		int numOfDays = ((Integer)simParamsDoc.get("numberOfDay")).intValue();    

  		endTick = Constants.MIN_IN_DAY * numOfDays;

  		// Check type of setup
  		String setup = (String)scenarioDoc.get("setup"); 
  		if(setup.equalsIgnoreCase("static")) {
  			staticSetup(jsonScenario);
  		} else if(setup.equalsIgnoreCase("dynamic")) {
  			dynamicSetup(jsonScenario);
  			// Load possible activities
  			BasicDBList activities = (BasicDBList)jsonScenario.get("activities");
  			// Put persons inside installations along with activities
  			int typesOfPersons =  ((Integer)jsonScenario.get("person-types")).intValue();
  			for(int i = 0; i < installations.size(); i++) {
  				Installation inst = installations.get(i);
  				int type = RNG.nextInt(typesOfPersons) + 1;
  				Person person =
  						new Person.Builder(i+"","Person " + i, "Person Type " + 
  				type, Integer.toString(type), inst).build();
  				inst.addPerson(person);

  				for(int j = 0; j < activities.size(); j++) {
  					BasicDBList appsNeeded = 
  							(BasicDBList)jsonScenario.get(activities.get(j) + 
  									".apps");
  					Vector<Appliance> existing = new Vector<Appliance>();
  					for (int k = 0; k < appsNeeded.size(); k++) {
  						System.out.println((String)appsNeeded.get(k));
  						Appliance a = 
  								inst.applianceExists((String)appsNeeded.get(k));
  						if (a != null) {
  							existing.add(a);
  						}
  					}

  					if(existing.size() > 0) {
  						logger.info(i + " " + activities.get(j));
  						double mu = 0, sigma = 0, from = 0, to = 0;
  						ProbabilityDistribution start = null, duration = null, 
  								weekday = null, weekend = null;
  						double[] means, sigmas, pi;
  						String distribution = "";

  						// Start Time Distribution 

  						distribution = (String) jsonScenario.get(
  								activities.get(j) + 
  								".startTime.distribution." + 
  								type);
  						switch (distribution) {
  						case ("normal"):
  							mu = (Double)jsonScenario.get(
  									activities.get(j) + 
  									".startTime.mu." + 
  									type);
  							sigma = (Double)jsonScenario.get(
  								activities.get(j) + 
  								".startTime.sigma." + 
  								type);
  							start = new Gaussian(mu, sigma);
  							start.precompute(0, 1439, 1440);
  							break;
  						case ("uniform"):
  							from = (Double)jsonScenario.get(
  									activities.get(j) + 
  									".startTime.start." + 
  									type);
  							to = (Double)jsonScenario.get(
  									activities.get(j) + 
  									".startTime.end." + 
  									type);
  							start = new Uniform(from, to);
  							start.precompute(from, to, (int) to + 1);
  							break;
  						case ("mixture"):
  							means = Utils.dblist2doubleArr(
  									(BasicDBList)jsonScenario.get(
  									activities.get(j) + ".startTime.means." + 
  									type));
  							sigmas = Utils.dblist2doubleArr(
  									(BasicDBList)jsonScenario.get(
  									activities.get(j) + 
  									".startTime.sigmas." + 
  									type));
  							pi = Utils.dblist2doubleArr(
  									(BasicDBList)jsonScenario.get(
  									activities.get(j) + ".startTime.pi." + type)
  									);
  							start = new GaussianMixtureModels(
  									pi.length, pi, means, sigmas);
  							start.precompute(0, 1439, 1440);
  							break;

  						default:
  							System.out.println(
  									"Non existing start time distribution type");
  						}

  						System.out.println("Start Time Distribution");
  						start.status();

  						// Duration Distribution

  						distribution = (String) jsonScenario.get(
  								activities.get(j) + ".duration.distribution." + 
  								type);
          		  
  						switch (distribution) {
  						case ("normal"):
  							mu = (Double)jsonScenario.get(activities.get(j) + 
  									".duration.mu." + type);
  							sigma = (Double)jsonScenario.get(activities.get(j) + 
  									".duration.sigma." + type);
  							duration = new Gaussian(mu, sigma);
  							duration.precompute(0, 1439, 1440);
  							break;
  						case ("uniform"):
  							from = (Double)jsonScenario.get(
  									activities.get(j) + 
  									".duration.start." + 
  									type);
  							to = (Double)jsonScenario.get(
  									activities.get(j) + 
  									".duration.end." + 
  									type);
  							duration = new Uniform(from, to);
  							duration.precompute(from, to, (int) to + 1);
  							break;
  						case ("mixture"):
  							means = Utils.dblist2doubleArr(
  									(BasicDBList)jsonScenario.get(
  									activities.get(j) + 
  									".duration.means." + type));
  							sigmas = Utils.dblist2doubleArr(
  									(BasicDBList)jsonScenario.get(
  									activities.get(j) + ".duration.sigmas." + 
  									type));
  							pi = Utils.dblist2doubleArr(
  									(BasicDBList)jsonScenario.get(
  									activities.get(j) + 
  									".duration.pi." + type));
  							duration = new GaussianMixtureModels(
  									pi.length, pi, means, sigmas);
  							duration.precompute(0, 1439, 1440);
  							break;
  						default:
  							System.out.println(
  									"Non existing duration distribution type");
  						}

  						System.out.println("Duration Distribution");
  						duration.status();

  						// Weekday Times Distribution

  						distribution = (String) jsonScenario.get(
  								activities.get(j) + ".weekday.distribution." + 
  								type);

  						switch (distribution) {
  						case ("normal"):
  							mu = (Double)jsonScenario.get(activities.get(j) + 
  									".weekday.mu." + 
  									type);
  							sigma = (Double)jsonScenario.get(activities.get(j) + 
  									".weekday.sigma." + 
  									type);
  							weekday = new Gaussian(mu, sigma);
  							weekday.precompute(0, 1439, 1440);
  							break;
  						case ("uniform"):
  							from = (Double)jsonScenario.get(activities.get(j) + 
  									".weekday.start." + 
  									type);
  							to = (Double)jsonScenario.get(activities.get(j) + 
  									".weekday.end." + 
  									type);
  							weekday = new Uniform(from, to);
  							weekday.precompute(from, to, (int) to + 1);
  							break;
  						case ("mixture"):
  							means = Utils.dblist2doubleArr(
  									(BasicDBList)jsonScenario.get(
  									activities.get(j) + 
  									".weekday.means." + 
  									type));
  							sigmas = Utils.dblist2doubleArr(
  									(BasicDBList)jsonScenario.get(
  									activities.get(j) + 
  									".weekday.sigmas." + 
  									type));
  							pi = Utils.dblist2doubleArr(
  									(BasicDBList)jsonScenario.get(
  									activities.get(j) + 
  									".weekday.pi." + 
  									type));
  							weekday = new GaussianMixtureModels(
  									pi.length, pi, means, sigmas);
  							weekday.precompute(0, 1439, 1440);
  							break;
  						default:
  							System.out.println(
  									"Non existing duration distribution type");
  						}

  						System.out.println("Weekday Distribution");
  						weekday.status();

  						// Weekend Times Distribution

  						distribution = (String) jsonScenario.get(
  								activities.get(j) + 
  								".weekend.distribution." + 
  								type);

  						switch (distribution) {
  						case ("normal"):
  							mu = (Double)jsonScenario.get(
  									activities.get(j) + 
  									".weekend.mu." + 
  									type);
  							sigma = (Double)jsonScenario.get(
  									activities.get(j) + 
  									".weekend.sigma." + 
  									type);
  							weekend = new Gaussian(mu, sigma);
  							weekend.precompute(0, 1439, 1440);
  							break;
  						case ("uniform"):
  							from = (Double)jsonScenario.get(
  									activities.get(j) + 
  									".weekend.start." + 
  									type);
  							to = (Double)jsonScenario.get(
  									activities.get(j) + ".weekend.end." + 
  									type);
  							weekend = new Uniform(from, to);
  							weekend.precompute(from, to, (int) to + 1);
  							break;
  						case ("mixture"):
  							means = Utils.dblist2doubleArr(
  									(BasicDBList)jsonScenario.get(
  									activities.get(j) + 
  									".weekend.means." + 
  									type));
  							sigmas = Utils.dblist2doubleArr(
  									(BasicDBList)jsonScenario.get(
  									activities.get(j) + ".weekend.sigmas." + 
  									type));
  							pi = Utils.dblist2doubleArr(
  									(BasicDBList)jsonScenario.get(
  									activities.get(j) + 
  									".weekend.pi." + type));
  							weekend = new GaussianMixtureModels(
  									pi.length, pi, means, sigmas);
  							weekend.precompute(0, 1439, 1440);
  							break;
  						default:
  							System.out.println(
  									"Non existing duration distribution type");
  						}

  						System.out.println("Weekend Distribution");
  						weekend.status();

  						String activity = (String)activities.get(j);
  						
  						Activity act =
  								new Activity.Builder(activity, 
  										"Typical " + activity + " Activity", 
  										activity, start, duration, 
  										simulationWorld)
  								.times("weekday", weekday)
  								.times("weekend", weekend)
  								.build();
  						for (Appliance e: existing) {
  							act.addAppliance(e, 1.0 / existing.size());
  						}
  						person.addActivity(act);
  					}
  				}
  			}
  		} else {
  			throw new Exception("Problem with setup property");
  		}
  		logger.info("Simulation setup finished.");
  	}

  	public void staticSetup (DBObject jsonScenario) {
	    int numOfInstallations = ((Integer)jsonScenario.get("instcount")).intValue();
	    queue = new PriorityBlockingQueue<Event>(2 * numOfInstallations);
	    for (int i = 1; i <= numOfInstallations; i++) {
	    	DBObject instDoc = (DBObject)jsonScenario.get("inst"+i);
	    	String id = ((ObjectId)instDoc.get("_id")).toString();
	    	System.out.println(id);
	    	String name = (String)instDoc.get("name");
	    	String description = (String)instDoc.get("description");
	    	String type = (String)instDoc.get("type");
	    	Installation inst = new Installation.Builder(
	    			id, name, description, type).build();
	    	int appcount = ((Integer)instDoc.get("appcount")).intValue();
	    	// Create the appliances
	    	for (int j = 1; j <= appcount; j++) {
	    		DBObject applianceDoc = (DBObject)instDoc.get("app"+j);
	    		String appid = ((ObjectId)applianceDoc.get("_id")).toString();
	    		String appname = (String)applianceDoc.get("name");
		    	String appdescription = (String)applianceDoc.get("description");
		    	String apptype = (String)applianceDoc.get("type");
		    	double standy = ((Double)applianceDoc.get("standy_consumption")).doubleValue();
		    	boolean base = ((Boolean)applianceDoc.get("base")).booleanValue();
		    	DBObject consModDoc = (DBObject)applianceDoc.get("consmod");
		    	ConsumptionModel consmod = new ConsumptionModel((String)consModDoc.get("model"));
	    		Appliance app = new Appliance.Builder(
	    				appid,
	    				appname,
	    				appdescription,
	    				apptype, 
	    				inst,
	    				consmod,
	    				standy,
	            		base).build();
	    		inst.addAppliance(app);
	    	}
	    	DBObject personDoc = (DBObject)instDoc.get("person");
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
	    	int actcount = ((Integer)instDoc.get("activitycount")).intValue();
	    	for (int j = 1; j <= actcount; j++) {
	    		DBObject activityDoc = (DBObject)instDoc.get("act"+j);
	    		String activityName = (String)activityDoc.get("name");
	    		String activityType = (String)activityDoc.get("type");
	    		int actmodcount = ((Integer)instDoc.get("actmodcount")).intValue();
	    		ProbabilityDistribution startDist;
	    		ProbabilityDistribution durDist;
	    		ProbabilityDistribution workingDist;
	    		ProbabilityDistribution nonWorkingDist;
	    		for (int k = 1; k <= actmodcount; k++) {
	    			DBObject actmodDoc = (DBObject)activityDoc.get("actmod"+k);
	    			String actmodName = (String)actmodDoc.get("name");
	    			String actmodType = (String)actmodDoc.get("type");
	    			String actmodDayType = (String)actmodDoc.get("day_type");
	    			DBObject duration = (DBObject)actmodDoc.get("duration");
	    			DBObject start = (DBObject)actmodDoc.get("start");
	    			DBObject rep = (DBObject)actmodDoc.get("rep");
	    		}
//	    		 Activity act =
//	    	              new Activity.Builder(
//	    	            		  activity, 
//	    	            		  "Typical " + activity + " Activity", 
//	    	            		  activity,
//	    	            		  json2dist(start), 
//	    	            		  json2dist(duration), 
//	    	            		  simulationWorld)
//	    	                      .times("weekday", json2dist)
//	    	                      .times("weekend", weekend)
//	    	                      .build();
	    	}
	    	installations.add(inst);
	    }
  }

  	public void dynamicSetup(DBObject jsonScenario) {
  		// Initialize simulation variables
  		int numOfInstallations = ((Integer)jsonScenario.get("installations")).intValue();
  		queue = new PriorityBlockingQueue<Event>(2 * numOfInstallations);
  		// Read the different kinds of appliances
  		BasicDBList appliances = (BasicDBList)jsonScenario.get("appliances"); 
  		// Read appliances statistics
  		double[] ownershipPerc = new double[appliances.size()];
  		for (int i = 0; i < appliances.size(); i++) {
  			ownershipPerc[i] = ((Double)jsonScenario.get(appliances.get(i) + ".perc")).doubleValue(); 
  		}
  		// Create the installations and put appliances inside
  		for (int i = 0; i < numOfInstallations; i++) {
  			// Make the installation
  			Installation inst =
  					new Installation.Builder(i+"", "Generic Installation", "Generic", i + "").build();
  			// Create the appliances
  			for (int j = 0; j < appliances.size(); j++) {
  				double dice = RNG.nextDouble();
  				String appliance = (String)appliances.get(j);
  				double[] power = Utils.dblist2doubleArr((BasicDBList)jsonScenario.get(appliance + ".power"));
  				int[] period = Utils.dblist2intArr((BasicDBList)jsonScenario.get(appliance + ".periods"));
  				double standby = (Double)jsonScenario.get(appliance + ".stand-by");
  				boolean base = (Boolean)jsonScenario.get(appliance + ".base");
  				if (dice < ownershipPerc[j]) {
  					Appliance app = new Appliance.Builder("id", appliance, 
  							"A Typical " + appliance,
  							appliance, 
  							inst,
  							new ConsumptionModel(""),
  							standby,
  							base).build();
  					inst.addAppliance(app);
  					logger.info(i + " " + appliances.get(i));
  				}
  			}
  			installations.add(inst);
  		}
  	}
  
	public static ProbabilityDistribution json2dist(DBObject distribution) {
  		String distType = (String)distribution.get("distrType");
  		switch (distType) {
  		case ("normal"):
  			BasicDBList normalList = (BasicDBList)distribution.get("parameters");
  			DBObject normalDoc = (DBObject)normalList.get(0);
  			double mean = ((Double)normalDoc.get("mean")).doubleValue();
  			double std = ((Double)normalDoc.get("std")).doubleValue();
  			Gaussian normal = new Gaussian(mean, std);
  			normal.precompute(0, 1439, 1440);
  			return normal;
        case ("uniform"):
   			BasicDBList unifList = (BasicDBList)distribution.get("parameters");
   			DBObject unifDoc = (DBObject)unifList.get(0);
   			double from = ((Double)unifDoc.get("from")).doubleValue();
   			double to = ((Double)unifDoc.get("to")).doubleValue();
   			Uniform uniform = new Uniform(from, to);
   			uniform.precompute(from, to, (int) to + 1);
   			return uniform;
   		case ("mixture"):
        	 BasicDBList mixList = (BasicDBList)distribution.get("parameters");
   			int length = mixList.size();
   			double[] w = new double[length];
         	double[] means = new double[length];
         	double[] stds = new double[length];
         	for(int i = 0; i < mixList.size(); i++) {
         		DBObject tuple = (DBObject)mixList.get(i);
         		w[i] = ((Double)tuple.get("w")).doubleValue();
         		means[i] = ((Double)tuple.get("mean")).doubleValue();
         		stds[i] = ((Double)tuple.get("std")).doubleValue();
    		} 
         	GaussianMixtureModels gmm = new GaussianMixtureModels(length, w, means, stds);
         	gmm.precompute(0, 1439, 1440);
         	return gmm;
        default:
        	System.out.println("Non existing start time distribution type");
        }
  		return null;
  	}

}

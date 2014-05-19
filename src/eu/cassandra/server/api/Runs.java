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
package eu.cassandra.server.api;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

import eu.cassandra.server.mongo.MongoActivities;
import eu.cassandra.server.mongo.MongoActivityModels;
import eu.cassandra.server.mongo.MongoAppliances;
import eu.cassandra.server.mongo.MongoConsumptionModels;
import eu.cassandra.server.mongo.MongoDemographics;
import eu.cassandra.server.mongo.MongoDistributions;
import eu.cassandra.server.mongo.MongoInstallations;
import eu.cassandra.server.mongo.MongoPersons;
import eu.cassandra.server.mongo.MongoPricingPolicy;
import eu.cassandra.server.mongo.MongoProjects;
import eu.cassandra.server.mongo.MongoRuns;
import eu.cassandra.server.mongo.MongoScenarios;
import eu.cassandra.server.mongo.MongoSimParam;
import eu.cassandra.server.mongo.MongoThermal;
import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.Simulation;
import eu.cassandra.sim.utilities.Utils;

@Path("runs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Runs {
	
	static Logger logger = Logger.getLogger(Runs.class);
	
	private JSONtoReturn jSON2Rrn = new JSONtoReturn();
	
	@javax.ws.rs.core.Context 
	ServletContext context;
	
	/**
	 * 
	 * Gets the scenarios under a project id
	 * @param message contains the project_id to search the related scenarios
	 * @return
	 */
	@GET
	public Response getRuns(@QueryParam("prj_id") String prj_id, @QueryParam("count") boolean count,
			@Context HttpHeaders httpHeaders) {
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new MongoRuns().getRuns(httpHeaders,prj_id,count)));
	}
	
	/**
	 * Create a run.
	 * In order to create and start a run, we need to have the simulation 
	 * parameter id passed as a JSON property via the POST request. After that
	 * the procedure goes as follows:
	 * <ol>
	 * <i>Create a database to hold the run documents and results (dbname same as run_id)</i>
	 * <i>Parse the smp_id from the JSON request</i>
	 * <i>From smp_id get scn_id</i>
	 * <i>From scn_id gather all scenario documents and create a full JSON scenario</i>
	 * <i>If the scenario is dynamic, instantiate as documents all the virtual installations</i>
	 * <i>Store the full scenario in a new MongoDB database</i>
	 * <i>Create thread with JSON scenario</i>
	 * <i>Run the thread</i>
	 * <i>Store the run document</i>
	 * </ol>
	 * 
	 * if with CSN
	 * <ol>
	 * <li>Check that the db of the run_id exists</li>
	 * <li>Get the scenario</li>
	 * <li>Update the scenario</li>
	 * <li>Re run</li>
	 * </ol>
	 * 
	 * Change how the run handles the pricing schemes.
	 * 
	 */
	@POST
	public Response createRun(String message) {
		System.out.println(message);
		DBObject index = new BasicDBObject("_id", 1);
		DBConn.getConn().getCollection(MongoRuns.COL_RUNS).createIndex(index);
		DBObject query = new BasicDBObject(); // A query
		
		try {
			// Create the new database
			ObjectId objid = ObjectId.get();
			String dbname = objid.toString();
			DB db = createDB(dbname);
			
			// Create the scenario document
			DBObject scenario = new BasicDBObject();
			
			// Simulation parameters
			DBObject jsonMessage = (DBObject) JSON.parse(message);
			boolean rerun = false;
			String smp_id = null;
			Object run_id = jsonMessage.get("run_id");
			if( run_id != null) {
				rerun = true;
			}
			if(rerun) {
				DBObject simParams = DBConn.getConn(run_id.toString()).getCollection(MongoSimParam.COL_SIMPARAM).findOne();
				ObjectId simParamsOID = (ObjectId)simParams.get("_id");
				smp_id = simParamsOID.toString();;
				System.out.println("Sim Params id: " + smp_id);
			} else {
				smp_id =  (String)jsonMessage.get("smp_id");
			}
			checkForNull(smp_id, "Simulation Parameters id not posted.");
			query.put("_id", new ObjectId(smp_id));			
			DBObject simParams = DBConn.getConn().getCollection(MongoSimParam.COL_SIMPARAM).findOne(query);
			checkForNull(simParams, "The provided Simulation Parameters were not found in the DB.");
			db.getCollection(MongoSimParam.COL_SIMPARAM).insert(simParams);
			scenario.put("sim_params", simParams);
			String seedStr = simParams.get("seed").toString();
			String runName = (String)simParams.get("name");
			
			// Scenario
			String scn_id = (String) simParams.get("scn_id");
			checkForNull(scn_id, "Scenario id not found in posted Simulation Parameters.");
			query.put("_id", new ObjectId(scn_id));
			DBObject scn = DBConn.getConn().getCollection(MongoScenarios.COL_SCENARIOS).findOne(query);
			checkForNull(scn, "The provided Scenario was not found in the DB.");
			db.getCollection(MongoScenarios.COL_SCENARIOS).insert(scn);
			scenario.put("scenario", scn);
			
			HashMap<String, String> clusterToInst = new HashMap<String, String>();
			
			//TODO Create 2 hashmaps with installation as key and pricing policy as value.
			if(rerun) {
				BasicDBList clusters = (BasicDBList)jsonMessage.get("clusters");
				Iterator<Object> iter = clusters.iterator();
				while(iter.hasNext()) {
					DBObject obj = (DBObject)iter.next();
					String name = obj.get("name").toString();
					// Current pricing
					String pricingId = obj.get("pricing_id").toString();
					query.put("_id", new ObjectId(pricingId));
					DBObject pricingPolicy = DBConn.getConn().getCollection(MongoPricingPolicy.COL_PRICING).findOne(query);
					checkForNull(pricingPolicy, "The provided Baseline Pricing Policy was not found in the DB.");
					db.getCollection(MongoPricingPolicy.COL_PRICING).insert(pricingPolicy);
					scenario.put("pricing-" + name, pricingPolicy);
					// Baseline pricing
					String basePricingId = obj.get("base_prc_id").toString();
					query.put("_id", new ObjectId(basePricingId));
					DBObject basePricingPolicy = DBConn.getConn().getCollection(MongoPricingPolicy.COL_PRICING).findOne(query);
					checkForNull(basePricingPolicy, "The provided Baseline Pricing Policy was not found in the DB.");
					db.getCollection(MongoPricingPolicy.COL_PRICING).insert(basePricingPolicy);
					scenario.put("baseline_pricing-"+name, basePricingPolicy);
					BasicDBList installations = (BasicDBList)obj.get("installations");
					Iterator<Object> iter2 = installations.iterator();
					while(iter2.hasNext()) {
						String inst_id = (String)iter2.next();
						clusterToInst.put(inst_id, name);
						scenario.put("pricing-" + name + "-" + inst_id, pricingPolicy);
						scenario.put("baseline_pricing-" + name + "-" + inst_id, basePricingPolicy);
					}
				}
			}	
			
			// Pricing Policy
			String prc_id = (String)simParams.get("prc_id");
			if(prc_id != null && prc_id.matches("[a-z0-9]{24}")) { // Optionally provided
				query.put("_id", new ObjectId(prc_id));
				DBObject pricingPolicy = DBConn.getConn().getCollection(MongoPricingPolicy.COL_PRICING).findOne(query);
				checkForNull(pricingPolicy, "The provided Pricing Policy was not found in the DB.");
				db.getCollection(MongoPricingPolicy.COL_PRICING).insert(pricingPolicy);
				scenario.put("pricing", pricingPolicy);
			}
			
			// Pricing Policy
			String base_prc_id = (String)simParams.get("base_prc_id");
			if(base_prc_id != null && base_prc_id.matches("[a-z0-9]{24}")) { // Optionally provided
				query.put("_id", new ObjectId(base_prc_id));
				DBObject basePricingPolicy = DBConn.getConn().getCollection(MongoPricingPolicy.COL_PRICING).findOne(query);
				checkForNull(basePricingPolicy, "The provided Baseline Pricing Policy was not found in the DB.");
				db.getCollection(MongoPricingPolicy.COL_PRICING).insert(basePricingPolicy);
				scenario.put("baseline_pricing", basePricingPolicy);
			}
			
			// Project
			String prj_id = (String)scn.get("project_id");
			checkForNull(prj_id, "Project id not found in posted Scenario.");
			query.put("_id", new ObjectId(prj_id));
			DBObject project = DBConn.getConn().getCollection(MongoProjects.COL_PROJECTS).findOne(query);
			checkForNull(project, "The provided Project was not found in the DB.");
			db.getCollection(MongoProjects.COL_PROJECTS).insert(project);
			scenario.put("project", project);
			
			// Demographics
			query = new BasicDBObject();
			query.put("scn_id", scn_id);
			String setup = (String)scn.get("setup");
			checkForNull(setup, "Setup property not set.");
			String name = (String)scn.get("name");
			boolean isDynamic = setup.equalsIgnoreCase("dynamic");
			if(isDynamic) {
				DBObject demog = DBConn.getConn().getCollection(MongoDemographics.COL_DEMOGRAPHICS).findOne(query);
				checkForNull(demog, "The provided Demographics were not found in the DB.");
				db.getCollection(MongoDemographics.COL_DEMOGRAPHICS).insert(demog);
				scenario.put("demog", demog);
			}
			
			// Installations
			// TODO Add installation cluster number in each installation
			query = new BasicDBObject();
			query.put("scenario_id", scn_id);
			DBCursor cursor = DBConn.getConn().getCollection(MongoInstallations.COL_INSTALLATIONS).find(query);
			checkForZero(cursor.size(), "No istallations found");
			int countInst = 0;
			while(cursor.hasNext()) {
				countInst++;
				DBObject obj = cursor.next();
				if(!isDynamic) db.getCollection(MongoInstallations.COL_INSTALLATIONS).insert(obj);
				String inst_id = obj.get("_id").toString();
				// Thermal module
				query = new BasicDBObject();
				query.put("inst_id", inst_id);
				DBObject thermal = DBConn.getConn().getCollection(MongoThermal.COL_THERMAL).findOne(query);
				if(thermal != null) {
					db.getCollection(MongoThermal.COL_THERMAL).insert(thermal);
					obj.put("thermal", thermal);
				}
				
				// Persons
				query = new BasicDBObject();
				query.put("inst_id", inst_id);
				DBCursor persons = DBConn.getConn().getCollection(MongoPersons.COL_PERSONS).find(query);
				int personCount = 0;
				while(persons.hasNext()) {
					personCount++;
					DBObject person = persons.next();
					if(!isDynamic) db.getCollection(MongoPersons.COL_PERSONS).insert(person);
					
					// Activities
					String pers_id = person.get("_id").toString();
					query = new BasicDBObject();
					query.put("pers_id", pers_id);
					DBCursor activities = DBConn.getConn().getCollection(MongoActivities.COL_ACTIVITIES).find(query);
					int countAct = 0;
					while(activities.hasNext()) {
						countAct++;
						DBObject activity = activities.next();
						if(!isDynamic) db.getCollection(MongoActivities.COL_ACTIVITIES).insert(activity);
						
						// Activity Models
						String act_id = activity.get("_id").toString();
						query = new BasicDBObject();
						query.put("act_id", act_id);
						DBCursor activityModels = 
								DBConn.getConn().getCollection(MongoActivityModels.COL_ACTMODELS).find(query);
						int countActMod = 0;
						while(activityModels.hasNext()) {
							countActMod++;
							DBObject activityModel = activityModels.next();
							if(!isDynamic) db.getCollection(MongoActivityModels.COL_ACTMODELS).insert(activityModel);

							// Duration distribution
							String dur_id = activityModel.get("duration").toString();
							checkForNull(dur_id, "Activity Model with name '" + activityModel.get("name") + "' does not have a duration distribution.");
							query = new BasicDBObject();
							query.put("_id", new ObjectId(dur_id));
							DBObject durDist = 
									DBConn.getConn().getCollection(MongoDistributions.COL_DISTRIBUTIONS).findOne(query);
							checkForNull(durDist, "Duration distribution of '" + activityModel.get("name") + "' not found in the DB.");
							if(!isDynamic) db.getCollection(MongoDistributions.COL_DISTRIBUTIONS).insert(durDist);
							activityModel.put("duration", durDist);
							
							// Start time distribution
							String start_id = activityModel.get("startTime").toString();
							checkForNull(start_id, "Activity Model with name '" + activityModel.get("name") + "' does not have a start time distribution.");
							query = new BasicDBObject();
							query.put("_id", new ObjectId(start_id));
							DBObject startDist = 
									DBConn.getConn().getCollection(MongoDistributions.COL_DISTRIBUTIONS).findOne(query);
							checkForNull(startDist, "Start distribution of '" + activityModel.get("name") + "' not found in the DB.");
							if(!isDynamic) db.getCollection(MongoDistributions.COL_DISTRIBUTIONS).insert(startDist);
							activityModel.put("start", startDist);
							
							// Repetitions distribution
							String rep_id = activityModel.get("repeatsNrOfTime").toString();
							checkForNull(rep_id, "Activity Model with name '" + activityModel.get("name") + "' does not have a number of times distribution.");
							query = new BasicDBObject();
							query.put("_id", new ObjectId(rep_id));
							DBObject repDist = 
									DBConn.getConn().getCollection(MongoDistributions.COL_DISTRIBUTIONS).findOne(query);
							checkForNull(repDist, "Number of times distribution of '" + activityModel.get("name") + "' not found in the DB.");
							if(!isDynamic) db.getCollection(MongoDistributions.COL_DISTRIBUTIONS).insert(repDist);
							activityModel.put("repetitions", repDist);
							activity.put("actmod"+countActMod, activityModel);
						}
						activity.put("actmodcount", new Integer(countActMod));
						person.put("activity"+countAct, activity);
					}
					person.put("activitycount", new Integer(countAct));
					obj.put("person"+personCount, person);
				}
				obj.put("personcount", new Integer(personCount));
				// Appliances
				query = new BasicDBObject();
				query.put("inst_id", inst_id);
				DBCursor appliances = DBConn.getConn().getCollection(MongoAppliances.COL_APPLIANCES).find(query);
				int countApps = 0;
				while(appliances.hasNext()) {
					countApps++;
					DBObject appliance = appliances.next();
					if(!isDynamic) db.getCollection(MongoAppliances.COL_APPLIANCES).insert(appliance);
					
					// Consumption model
					String app_id = appliance.get("_id").toString();
					query = new BasicDBObject();
					query.put("app_id", app_id);
					DBObject consModel = 
							DBConn.getConn().getCollection(MongoConsumptionModels.COL_CONSMODELS).findOne(query);
					checkForNull(consModel, "Consumption model of appliance '" + appliance.get("name") + "' not found in the DB.");
					if(!isDynamic) db.getCollection(MongoConsumptionModels.COL_CONSMODELS).insert(consModel);
					appliance.put("consmod", consModel);
					obj.put("app"+countApps, appliance);
				}
				obj.put("cluster", clusterToInst.get(inst_id));
				obj.put("appcount", new Integer(countApps));
				scenario.put("inst"+countInst,obj);
			}
			scenario.put("instcount", new Integer(countInst));
			String resources_path = (String)context.getAttribute("RESOURCES_PATH");
			int seed = 0;
			try {
				seed = Integer.parseInt(seedStr);
			} catch(NumberFormatException e) {
				seed = 0;
			}
			// TODO insert the hashmaps here as argument
			Simulation sim = new Simulation(scenario.toString(), dbname, resources_path, seed);
			sim.setup(false);
			// Scenario building finished
			DBObject run = buildRunObj(objid, runName, prj_id, "sim");
			DBConn.getConn().getCollection(MongoRuns.COL_RUNS).insert(run);
			String returnMsg = PrettyJSONPrinter.prettyPrint(jSON2Rrn.createJSON(run, "Sim creation successful"));
			logger.info(returnMsg);
			ThreadPoolExecutor executorPool = (ThreadPoolExecutor)context.getAttribute("MY_EXECUTOR");
			Utils.printExecutorSummary(executorPool);
			executorPool.execute(sim);
			return Utils.returnResponse(returnMsg);
		} catch (UnknownHostException | MongoException e1) {
			String returnMsg = "{ \"success\": false, \"message\": \"Sim creation failed\", \"errors\": { \"hostMongoException\": \""+ e1.getMessage() + "\" } }"; 
			e1.printStackTrace();
			return Utils.returnResponse(returnMsg); 
		} catch(Exception e) {
			String returnMsg = "{ \"success\": false, \"message\": \"Sim creation failed\", \"errors\": { \"generalException\": \"" + e.getMessage() + "\" } }";
			e.printStackTrace();
			logger.error(Utils.stackTraceToString(e.getStackTrace()));
			return Utils.returnResponse(returnMsg);
		}
	}
	
	private static void checkForNull(String o, String message) throws Exception {
		if(o == "" || o == null) throw new Exception(message);
	}
	
	private static void checkForNull(Object o, String message) throws Exception {
		if(o == null) throw new Exception(message);
	}
	
	private static void checkForZero(int counter, String message) throws Exception {
		if(counter == 0) throw new Exception(message);
	}
	
	private static DB createDB(String dbname) throws UnknownHostException, MongoException {
		return DBConn.getConn(dbname);
	}
	
	private static DBObject buildRunObj(ObjectId objid, String runName, String prj_id, String type) {
		DBObject run = new BasicDBObject();
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
		String name = "Run " + runName + " on " + sdf.format(calendar.getTime());
		run.put("_id", objid);
		run.put("name", name);
		run.put("started", System.currentTimeMillis());
		run.put("ended", -1);
		run.put("type", type);
		run.put("prj_id", prj_id);
		run.put("percentage", 0);
		return run;
	}
	
	public static void main(String[] args) {
		String message = "message";
		String returnMsg = "{ \"success\": false, \"message\": \"Sim creation failed\", \"errors\": { \"generalException\": \"" + message + "\" } }";
		System.out.println(returnMsg);
	}

}

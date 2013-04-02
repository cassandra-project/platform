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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

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

import org.bson.types.ObjectId;

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
import eu.cassandra.server.mongo.MongoProjects;
import eu.cassandra.server.mongo.MongoRuns;
import eu.cassandra.server.mongo.MongoScenarios;
import eu.cassandra.server.mongo.MongoSimParam;
import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.Simulation;
import eu.cassandra.sim.utilities.Utils;

@Path("runs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Runs {
	
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
	 */
	@POST
	public Response createRun(String message) {
		
		DBObject query = new BasicDBObject(); // A query
		
		try {
			// Create the new database
			Mongo m = new Mongo("localhost");
			ObjectId objid = new ObjectId();
			String dbname = objid.toString();
			DB db = m.getDB(dbname);
			System.out.println("1");
			// Create the scenario document
			DBObject scenario = new BasicDBObject();
			
			// Simulation params
			DBObject jsonMessage = (DBObject) JSON.parse(message);
			String smp_id =  (String)jsonMessage.get("smp_id");
			query.put("_id", new ObjectId(smp_id));			
			DBObject simParams = DBConn.getConn().getCollection(MongoSimParam.COL_SIMPARAM).findOne(query);
			if(simParams == null) {
				return Utils.returnResponse("{ \"success\": false, \"message\": \"Sim creation failed\", \"errors\": { \"error\": \"No simulation params found\" }}"); 
			}
			db.getCollection(MongoSimParam.COL_SIMPARAM).insert(simParams);
			scenario.put("sim_params", simParams);
			System.out.println("2");
			// Scenario
			String scn_id = (String) simParams.get("scn_id");
			query.put("_id", new ObjectId(scn_id));
			DBObject scn = DBConn.getConn().getCollection(MongoScenarios.COL_SCENARIOS).findOne(query);
			db.getCollection(MongoScenarios.COL_SCENARIOS).insert(scn);
			scenario.put("scenario", scn);
			System.out.println("3");
			// Project
			String prj_id = (String)scn.get("project_id");
			query.put("_id", new ObjectId(prj_id));
			DBObject project = DBConn.getConn().getCollection(MongoProjects.COL_PROJECTS).findOne(query);
			db.getCollection(MongoProjects.COL_PROJECTS).insert(project);
			scenario.put("project", project);
			System.out.println("4");
			// Demographics
			query = new BasicDBObject();
			query.put("scn_id", scn_id);
			String setup = (String)scn.get("setup");
			String name = (String)scn.get("name");
			System.out.println("5");
			boolean isDynamic = setup.equalsIgnoreCase("dynamic");
			if(isDynamic) {
				DBObject demog = DBConn.getConn().getCollection(MongoDemographics.COL_DEMOGRAPHICS).findOne(query);
				db.getCollection(MongoDemographics.COL_DEMOGRAPHICS).insert(demog);
				scenario.put("demog", demog);
			}	
			System.out.println("6");
			// Installations
			query = new BasicDBObject();
			query.put("scenario_id", scn_id);
			DBCursor cursor = DBConn.getConn().getCollection(MongoInstallations.COL_INSTALLATIONS).find(query);
			if(cursor.size() == 0) {
				return Utils.returnResponse("{ \"success\": false, \"message\": \"Sim creation failed\", \"errors\": { \"error\": \"No istallations found\" }}"); 
			}
			int countInst = 0;
			while(cursor.hasNext()) {
				countInst++;
				DBObject obj = cursor.next();
				if(!isDynamic) db.getCollection(MongoInstallations.COL_INSTALLATIONS).insert(obj);
				
				// Persons
				String inst_id = obj.get("_id").toString();
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
							query = new BasicDBObject();
							query.put("_id", new ObjectId(dur_id));
							DBObject durDist = 
									DBConn.getConn().getCollection(MongoDistributions.COL_DISTRIBUTIONS).findOne(query);
							if(!isDynamic) db.getCollection(MongoDistributions.COL_DISTRIBUTIONS).insert(durDist);
							activityModel.put("duration", durDist);
							
							// Start time distribution
							String start_id = activityModel.get("startTime").toString();
							query = new BasicDBObject();
							query.put("_id", new ObjectId(start_id));
							DBObject startDist = 
									DBConn.getConn().getCollection(MongoDistributions.COL_DISTRIBUTIONS).findOne(query);
							if(!isDynamic) db.getCollection(MongoDistributions.COL_DISTRIBUTIONS).insert(startDist);
							activityModel.put("start", startDist);
							
							// Repetitions distribution
							String rep_id = activityModel.get("repeatsNrOfTime").toString();
							query = new BasicDBObject();
							query.put("_id", new ObjectId(rep_id));
							DBObject repDist = 
									DBConn.getConn().getCollection(MongoDistributions.COL_DISTRIBUTIONS).findOne(query);
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
				System.out.println("7");
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
					if(!isDynamic) db.getCollection(MongoConsumptionModels.COL_CONSMODELS).insert(consModel);
					appliance.put("consmod", consModel);
					obj.put("app"+countApps, appliance);
				}
				obj.put("appcount", new Integer(countApps));
				scenario.put("inst"+countInst,obj);
			}
			scenario.put("instcount", new Integer(countInst));
			// Scenario building finished
			System.out.println("8");
			HashMap<String,Future<?>> runs = (HashMap<String,Future<?>>)context.getAttribute("MY_RUNS");
			Simulation sim = new Simulation(scenario.toString(), dbname);
			sim.setup();
			System.out.println("9");
			ExecutorService executor = (ExecutorService)context.getAttribute("MY_EXECUTOR");
			Future<?> f = executor.submit(sim);
			System.out.println(dbname);
			runs.put(dbname, f);
			System.out.println("10");
			BasicDBObject run = new BasicDBObject();
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
			String runName = "Run for " + name + " on " + sdf.format(calendar.getTime());
			run.put("_id", objid);
			run.put("name", runName);
			run.put("started", System.currentTimeMillis());
			run.put("ended", -1);
			run.put("prj_id", prj_id);
			run.put("percentage", 0);
			DBConn.getConn().getCollection(MongoRuns.COL_RUNS).insert(run);
			DBObject returnObj = new BasicDBObject();
			returnObj.put("success", true);
			returnObj.put("message", "Sim creation successful");
			returnObj.put("data", run);
			String returnMsg = PrettyJSONPrinter.prettyPrint(returnObj);
			System.out.println(returnMsg);
			return Utils.returnResponse(returnMsg);
		} catch (UnknownHostException | MongoException e1) {
			String returnMsg = "{ \"success\": false, \"message\": \"Sim creation failed\", \"errors\": { \"hostMongoException\": \""+ e1.getMessage() + "\" } }"; 
			System.out.println(returnMsg);
			return Utils.returnResponse(returnMsg); 
		} catch(Exception e) {
			String returnMsg = "{ \"success\": false, \"message\": \"Sim creation failed\", \"errors\": { \"generalException\": \"" + e.getMessage() + "\" } }";
			e.printStackTrace();
			System.out.println(returnMsg);
			return Utils.returnResponse(returnMsg);
		}
	}

}

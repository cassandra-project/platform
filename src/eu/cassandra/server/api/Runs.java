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
package eu.cassandra.server.api;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.server.mongo.MongoActivities;
import eu.cassandra.server.mongo.MongoActivityModels;
import eu.cassandra.server.mongo.MongoAppliances;
import eu.cassandra.server.mongo.MongoConsumptionModels;
import eu.cassandra.server.mongo.MongoDemographics;
import eu.cassandra.server.mongo.MongoDistributions;
import eu.cassandra.server.mongo.MongoInstallations;
import eu.cassandra.server.mongo.MongoPersons;
import eu.cassandra.server.mongo.MongoRuns;
import eu.cassandra.server.mongo.MongoScenarios;
import eu.cassandra.server.mongo.MongoSimParam;
import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.MongoDBQueries;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.server.threads.DemoThread;
import eu.cassandra.sim.Simulation;

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
	public String getRuns(@QueryParam("prj_id") String prj_id) {
		return PrettyJSONPrinter.prettyPrint(new MongoRuns().getRuns(prj_id));
	}
	
	/**
	 * Create a run.
	 * In order to create a run we need to have the simulation parameter id.
	 * Now the procedure works as follows:
	 * <ol>
	 * <i>smp_id => simulation parameters JSON</i>
	 * <i>Simulation parameters JSON => scn_id</i>
	 * <i>scn_id => gather all scenario data and create a full JSON scenario</i>
	 * <i>store the full scenario in the run JSON</i>
	 * <i>Create thread with JSON scenario and storage contact point</i>
	 * <i>Run the thread</i>
	 * </ol>
	 *
	 */
	@POST
	public String createRun(String message) {
		DBObject scenario = new BasicDBObject();
		// Parse message to db object
		DBObject jsonMessage = (DBObject) JSON.parse(message);
		// Get the simulations params id
		String smp_id =  (String)jsonMessage.get("smp_id");
		DBObject query = new BasicDBObject(); 
		query.put("_id", new ObjectId(smp_id));
		// Find the sim params
		DBObject simParams = DBConn.getConn().getCollection(MongoSimParam.COL_SIMPARAM).findOne(query);
		if(simParams == null) {
			return "No simulation params found\n";
		}
		scenario.put("sim_params", simParams);
//		System.out.println(result.toString());
		String scn_id = (String) simParams.get("scn_id");
		System.out.println(scn_id);
		query.put("_id", new ObjectId(scn_id));
		DBObject scn = DBConn.getConn().getCollection(MongoScenarios.COL_SCENARIOS).findOne(query);
		scenario.put("scenario", scn);
		query = new BasicDBObject();
		query.put("scn_id", scn_id);
		// Get the demographics
		String setup = (String)scn.get("setup");
		if(setup.equalsIgnoreCase("dynamic")) {
			DBObject demog = DBConn.getConn().getCollection(MongoDemographics.COL_DEMOGRAPHICS).findOne(query);
			scenario.put("demog", demog);
		}
		query = new BasicDBObject();
		query.put("scenario_id", scn_id);
		// Get the installations
		DBCursor cursor = DBConn.getConn().getCollection(MongoInstallations.COL_INSTALLATIONS).find(query);
		if(cursor.size() == 0) {
			return "No istallations found\n";
		}
		int countInst = 0;
		while(cursor.hasNext()) {
			countInst++;
			DBObject obj = cursor.next();
			String inst_id = obj.get("_id").toString();
			query = new BasicDBObject();
			query.put("inst_id", inst_id);
			DBCursor persons = DBConn.getConn().getCollection(MongoPersons.COL_PERSONS).find(query);
			int personCount = 0;
			while(persons.hasNext()) {
				personCount++;
				DBObject person = persons.next();
				String pers_id = person.get("_id").toString();
				query = new BasicDBObject();
				query.put("pers_id", pers_id);
				DBCursor activities = DBConn.getConn().getCollection(MongoActivities.COL_ACTIVITIES).find(query);
				int countAct = 0;
				while(activities.hasNext()) {
					countAct++;
					DBObject activity = activities.next();
					String act_id = activity.get("_id").toString();
					query = new BasicDBObject();
					query.put("act_id", act_id);
					DBCursor activityModels = 
							DBConn.getConn().getCollection(MongoActivityModels.COL_ACTMODELS).find(query);
					int countActMod = 0;
					while(activityModels.hasNext()) {
						countActMod++;
						DBObject activityModel = activityModels.next();
						System.out.println(activityModel + " ");
						// Duration distribution
						String dur_id = activityModel.get("duration").toString();
						query = new BasicDBObject();
						query.put("_id", new ObjectId(dur_id));
						DBObject durDist = 
								DBConn.getConn().getCollection(MongoDistributions.COL_DISTRIBUTIONS).findOne(query);
						activityModel.put("duration", durDist);
						// Start time distribution
						String start_id = activityModel.get("startTime").toString();
						query = new BasicDBObject();
						query.put("_id", new ObjectId(start_id));
						DBObject startDist = 
								DBConn.getConn().getCollection(MongoDistributions.COL_DISTRIBUTIONS).findOne(query);
						activityModel.put("start", startDist);
						// Repetitions distribution
						String rep_id = activityModel.get("repeatsNrOfTime").toString();
						query = new BasicDBObject();
						query.put("_id", new ObjectId(rep_id));
						DBObject repDist = 
								DBConn.getConn().getCollection(MongoDistributions.COL_DISTRIBUTIONS).findOne(query);
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
			query = new BasicDBObject();
			query.put("inst_id", inst_id);
			DBCursor appliances = DBConn.getConn().getCollection(MongoAppliances.COL_APPLIANCES).find(query);
			int countApps = 0;
			while(appliances.hasNext()) {
				countApps++;
				DBObject appliance = appliances.next();
				String app_id = appliance.get("_id").toString();
				query = new BasicDBObject();
				query.put("app_id", app_id);
				DBObject consModel = 
						DBConn.getConn().getCollection(MongoConsumptionModels.COL_CONSMODELS).findOne(query);
				appliance.put("consmod", consModel);
				obj.put("app"+countApps, appliance);
			}
			obj.put("appcount", new Integer(countApps));
			scenario.put("inst"+countInst,obj);
		}
		scenario.put("instcount", new Integer(countInst));
		System.out.println(PrettyJSONPrinter.prettyPrint(scenario.toString()));
		Simulation sim = new Simulation(scenario.toString());
		try {
			sim.setup();
		} catch (Exception e) {
			e.printStackTrace();
			return "Sim creation failed " + e.getMessage();
		}
		ExecutorService executor = (ExecutorService )context.getAttribute("MY_EXECUTOR");
		executor.submit(sim);
		return "Simulation Submitted\n";
//		return PrettyJSONPrinter.prettyPrint(new MongoRuns().createRun(message));
	}

}

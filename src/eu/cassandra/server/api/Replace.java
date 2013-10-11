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

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.server.mongo.MongoActivityModels;
import eu.cassandra.server.mongo.MongoAppliances;
import eu.cassandra.server.mongo.MongoCopyEntities;
import eu.cassandra.server.mongo.MongoInstallations;
import eu.cassandra.server.mongo.MongoPersons;
import eu.cassandra.server.mongo.MongoScenarios;
import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.utilities.Utils;

@Path("replace")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Replace {

	@POST
	public Response replace(String message) {
		HttpHeaders httpHeaders = null;
		// parse what will be replaced
		JSONtoReturn jSON2Rrn = new JSONtoReturn();
		DBObject jsonResponse = (DBObject) JSON.parse(message);
		BasicDBList replacedEntities = (BasicDBList)jsonResponse.get("replaced_ids");
		String newEntity = (String)jsonResponse.get("replacement_id");
		String scenario = (String)jsonResponse.get("scn_id");
		String entityType = (String)jsonResponse.get("entity_type");
		if(Utils.getUserWithId(scenario) != null) {
				return Utils.returnResponse(jSON2Rrn.createJSONError("Cannot replace entities that exist in libraries", 
						"Invalid replacement").toString());
		}
		String answer = "";
		MongoCopyEntities copy = new MongoCopyEntities(httpHeaders);
		for(Object o : replacedEntities) {
			String entity = (String)o;
			switch(entityType) {
				case "inst":
					// Get the replacement object
					String inst = new MongoInstallations().getInstallation(httpHeaders, newEntity);
					DBObject instObj = ((DBObject)((BasicDBList)((DBObject)JSON.parse(inst)).get("data")).get(0));
					// Delete the replaced
					new MongoInstallations().deleteInstallation(entity);
					// Deep copy into scenario
					answer = copy.copyInstallationToScenario((String)instObj.get("_id"), scenario, null, true);
					
					break;
				case "pers":
					// Get the replacement object
					String pers = new MongoPersons().getPerson(httpHeaders, newEntity);
					DBObject persObj = ((DBObject)((BasicDBList)((DBObject)JSON.parse(pers)).get("data")).get(0));
					// Deep copy into installation
					// But first get the inst_id of the old person
					String replacedPers = new MongoPersons().getPerson(httpHeaders, entity);
					DBObject replacedPersObj = ((DBObject)((BasicDBList)((DBObject)JSON.parse(replacedPers)).get("data")).get(0));
					String inst_id = (String)replacedPersObj.get("inst_id");
					// Delete the replaced
					new MongoPersons().deletePerson(entity);
					answer = copy.copyPersonToInstallation((String)persObj.get("_id"), inst_id, null, false, null);
					System.out.println(answer);
					break;
				case "app":
					// Get the replacement object
					String app = new MongoAppliances().getAppliance(httpHeaders, newEntity);
					DBObject appObj = ((DBObject)((BasicDBList)((DBObject)JSON.parse(app)).get("data")).get(0));
					// Deep copy into installation
					// But first get the inst_id of the old person
					String replacedApp = new MongoAppliances().getAppliance(httpHeaders, entity);
					DBObject replacedAppObj = ((DBObject)((BasicDBList)((DBObject)JSON.parse(replacedApp)).get("data")).get(0));
					inst_id = (String)replacedAppObj.get("inst_id");
					// Deep copy into scenario
					answer = copy.copyApplianceToInstallation((String)appObj.get("_id"), inst_id, null);
					String idToPush = (String)((DBObject)((DBObject)JSON.parse(answer)).get("data")).get("_id");
					// Find in which activities the replaced id exists
					DBObject qry = new BasicDBObject();
					DBObject obj = new BasicDBObject("$push",new BasicDBObject(MongoActivityModels.REF_CONTAINSAPPLIANCES, idToPush));
					int added = DBConn.getConn().getCollection(MongoActivityModels.COL_ACTMODELS).update(qry, obj, false, true).getN();
					// Delete the replaced it will make a cascade delete as well...
					new MongoAppliances().deleteAppliance(entity);
					System.out.println(added);
					break;
				default:
					return Utils.returnResponse(jSON2Rrn.createJSONError("Entity type is not a match", 
							"Bad Request").toString());
			}
		}
		// for each replacement
		// Get the replacement, get the reference
		// 
//		// 0 add the replacement in its collection and get the id
//		// 1. Search installations in the scenario and replace them
//		// 2. Search persons in installations collection and replace them
//		// 3. Search appliances in installations collection and replace them
//		// 4. Search appliances in act_models and replace them
		DBObject ret = (DBObject)JSON.parse(answer);
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(jSON2Rrn.createJSON(ret, "Replace succesful!")));
	}
	
	

}

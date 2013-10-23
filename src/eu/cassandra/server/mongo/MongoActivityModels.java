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
package eu.cassandra.server.mongo;


import javax.ws.rs.core.HttpHeaders;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.server.api.exceptions.RestQueryParamMissingException;
import eu.cassandra.server.mongo.util.JSONValidator;
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.MongoDBQueries;
import eu.cassandra.sim.utilities.Utils;

public class MongoActivityModels {

	public final static String COL_ACTMODELS = "act_models";
	public final static String REF_ACTIVITY = "act_id";
	public final static String REF_CONTAINSAPPLIANCES = "containsAppliances";
	
	public final static String REF_DISTR_DURATION = "duration";
	public final static String REF_DISTR_STARTTIME = "startTime";
	public final static String REF_DISTR_REPEATS = "repeatsNrOfTime";
	
	
	
	public static boolean checkAppliancesExists(BasicDBList apps, String act_id) {
		boolean check = true;
		String inst_id = MongoPersons.getParentId(MongoActivities.getParentId(act_id));
		if(apps == null || apps.isEmpty() || apps.size() == 0) return true;
		for(Object o : apps) {
			String app_id = (String)o;
			String app_inst_id = MongoAppliances.getParentId(app_id);
			if(inst_id.equalsIgnoreCase(app_inst_id)) check  = check && true;
			else check = check && false;
		}
		return check;
	}
	

	/**
	 * curl -i http://localhost:8080/cassandra/api/actmod/4fedc0cde4b00db232508ea6
	 * 
	 * @param cid
	 * @return
	 */
	public String getActivityModel(HttpHeaders httpHeaders,String id) {
		return new MongoDBQueries().getEntity(httpHeaders,COL_ACTMODELS,"_id", 
				id, "Activity Model retrieved successfully").toString();
	}

	/**
	 * curl -i http://localhost:8080/cassandra/api/actmod?act_id=4fedb39be4b0445cc271fb4d
	 * 
	 * @param inst_id
	 * @return
	 */
	public String getActivityModels(HttpHeaders httpHeaders,String act_id, boolean count) {
		if(act_id == null) {
			return new JSONtoReturn().createJSONError(
					"Only the Activity Models of a particular Activity can be retrieved", 
					new RestQueryParamMissingException("act_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(httpHeaders,COL_ACTMODELS,"act_id", 
					act_id, "Activity Models retrieved successfully",count).toString();
		}
	}

	/**
	 * curl -i --data  @activitymodel.json    --header Content-type:application/json http://localhost:8080/cassandra/api/actmod
	 * 
	 * @param dataToInsert
	 * @return
	 */
	
	public String createActivityModel(String dataToInsert) {
		String response = createActivityModelObj(dataToInsert).toString();
		return withAddedWarnings(response, false, true);
	}
	
	public static DBObject createActivityModelObj(String dataToInsert) {
		MongoDBQueries q = new MongoDBQueries();
		DBObject obj = (DBObject) JSON.parse(dataToInsert);
		BasicDBList apps = (BasicDBList)obj.get(REF_CONTAINSAPPLIANCES);
		String act_id = (String)obj.get("act_id");
		boolean  checkPass = checkAppliancesExists(apps, act_id);
		DBObject returnObj = null;
		if(checkPass) {
			returnObj = q.insertData(COL_ACTMODELS ,dataToInsert,
					"Activity Model created successfully", 
					new String[] {MongoActivities.COL_ACTIVITIES, MongoDistributions.COL_DISTRIBUTIONS, 
					MongoDistributions.COL_DISTRIBUTIONS, MongoDistributions.COL_DISTRIBUTIONS  },
					new String[] {"act_id","duration","startTime","repeatsNrOfTime"},
					new boolean[] {false,true,true,true},JSONValidator.ACTIVITYMODEL_SCHEMA
					);
			if(Utils.failed(returnObj.toString())) {
				// Perhaps should be added to the user library
				returnObj = q.insertData(COL_ACTMODELS ,dataToInsert,
						"Activity Model created successfully", 
						new String[] {"users"},
						new String[] {"act_id"},
						new boolean[] {false},JSONValidator.ACTIVITYMODEL_SCHEMA);
			}
		} else {
			returnObj = new JSONtoReturn().createJSONError("Appliances do not exist " + 
					"in the Installation or Activity Model defined inside in the user " +
					"library should not contain appliances", "Activity model appliances error");
		}
		return returnObj;
	}
	
	private String withAddedWarnings(String response, boolean ary, boolean appliances) {
		if(Utils.failed(response)) return response;
		DBObject jsonResponse = (DBObject) JSON.parse(response);
		DBObject data = (DBObject) jsonResponse.get("data");
		if(ary) {
			data = (DBObject)((BasicDBList)data).get(0);
		}
		BasicDBList list = new BasicDBList();
		if(appliances && (((BasicDBList)data.get("containsAppliances")) == null || ((BasicDBList)data.get("containsAppliances")).isEmpty())) {
			String warning = "Add at least one appliance for this activity model.";
			list.add(warning);
		}
		if((String)data.get("duration") == null || 
				((String)data.get("duration") != null && ((String)data.get("duration")).isEmpty())) {
			String warning = "Add a duration distribution for this activity model.";
			list.add(warning);
		}
		if((String)data.get("startTime") == null || 
				((String)data.get("startTime") != null && ((String)data.get("startTime")).isEmpty())) {
			String warning = "Add a start time distribution for this activity model.";
			list.add(warning);
		}
		if((String)data.get("repeatsNrOfTime") == null || 
				((String)data.get("repeatsNrOfTime") != null && ((String)data.get("repeatsNrOfTime")).isEmpty())) {
			String warning = "Add a number of times distribution for this activity model.";
			list.add(warning);
		}
		if(!list.isEmpty()) {
			jsonResponse.put("warnings", list);
		}
		return jsonResponse.toString();
	}

	/**
	 * curl -i -X DELETE http://localhost:8080/cassandra/api/actmod/4ff153dbe4b0c855ac36d9a7
	 * 
	 * @param id
	 * @return
	 */
	public String deleteActivityModel(String id) {
		return new MongoDBQueries().deleteDocument(COL_ACTMODELS, id).toString();
	}

	/**
	 * curl -X PUT -d @activitymodel.json   --header Content-type:application/json   http://localhost:8080/cassandra/api/actmod/4fec8b53df4ffdb8d1d1ce57
	 * 
	 * @param cid
	 * @param jsonToUpdate
	 * @return
	 */
	public String updateActivityModel(String id,String jsonToUpdate) {
		MongoDBQueries q = new MongoDBQueries();
		DBObject obj = (DBObject) JSON.parse(jsonToUpdate);
		BasicDBList apps = (BasicDBList)obj.get(REF_CONTAINSAPPLIANCES);
		String act_id = (String)obj.get("act_id");
		boolean  checkPass = checkAppliancesExists(apps, act_id);
		String returnMsg = null;
		boolean appliances = true;
		if(checkPass) {
			returnMsg = q.updateDocument("_id", id,jsonToUpdate,
					COL_ACTMODELS, "Activity Model updated successfully",
					MongoActivities.COL_ACTIVITIES ,"act_id",
					JSONValidator.ACTIVITYMODEL_SCHEMA).toString();
			if(Utils.failed(returnMsg)) {
				// Perhaps should be added to the user library
				returnMsg = q.updateDocument("_id", id,jsonToUpdate,
						COL_ACTMODELS, "Activity Model updated successfully",
						"users" ,"act_id",JSONValidator.ACTIVITYMODEL_SCHEMA).toString();
				appliances = false;
			}
		} else {
			returnMsg = new JSONtoReturn().createJSONError("Appliances do not exist " + 
						"in the Installation or Activity Model defined inside in the user " +
						"library should not contain appliances", "Activity model appliances error").toString();
		}
		return withAddedWarnings(returnMsg, true, appliances);
	}
}

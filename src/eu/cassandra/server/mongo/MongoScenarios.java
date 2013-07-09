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

public class MongoScenarios {

	public final static String COL_SCENARIOS = "scenarios";
	public final static String REF_PROJECT= "project_id";

	/**
	 * curl -i http://localhost:8080/cassandra/api/scn/4fec747cdf4ffdb8d1d1ce55
	 * curl -i http://localhost:8080/cassandra/api/scn
	 * 
	 * @param id
	 * @return
	 */
	public String getScenarios(HttpHeaders httpHeaders,String project_id, boolean count) {
		if(project_id == null) {
			return new JSONtoReturn().createJSONError(
					"Only the Scenarios of a particular Project can be retrieved", 
					new RestQueryParamMissingException("prj_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(httpHeaders,COL_SCENARIOS,"project_id", project_id, 
					"Scenarios retrieved successfully",count).toString();
		}
	}

	/**
	 * 
	 * @param scenario_id
	 * @return
	 */
	public String getScenario(HttpHeaders httpHeaders,String scenario_id) {
		return new MongoDBQueries().getEntity(httpHeaders,COL_SCENARIOS,"_id", scenario_id, 
				"Scenarios retrieved successfully").toString();
	}

	/**
	 * curl -i --data  @scenario.json    --header Content-type:application/json http://localhost:8080/cassandra/api/scn
	 * 
	 * @param message
	 * @return
	 */
	public String createScenario(String dataToInsert) {
		String response = new MongoDBQueries().insertData(COL_SCENARIOS,dataToInsert,
				"Scenario created successfully", MongoProjects.COL_PROJECTS ,
				"project_id",JSONValidator.SCENARIO_SCHEMA).toString();
		return withAddedWarnings(response, false);
	}
	
	private String withAddedWarnings(String response, boolean ary) {
		if(Utils.failed(response)) return response;
		DBObject jsonResponse = (DBObject) JSON.parse(response);
		DBObject data = (DBObject) jsonResponse.get("data");
		String objID =  new String();
		if(ary) {
			objID = (String)((DBObject)((BasicDBList)data).get(0)).get("_id");
		} else {
			objID = (String)data.get("_id");
		}
		BasicDBList list = new BasicDBList();
		DBObject returnQuery = 
				new MongoDBQueries().getEntity(MongoInstallations.COL_INSTALLATIONS, MongoInstallations.REF_SCENARIO, objID);
		if(returnQuery == null) {
			String warning = "Add at least one installation for this scenario.";
			list.add(warning);
		}
		returnQuery = 
				new MongoDBQueries().getEntity(MongoSimParam.COL_SIMPARAM, "scn_id", objID);
		if(returnQuery == null) {
			String warning = "Add at least one set of simulation parameters for this scenario.";
			list.add(warning);
		}
		if(!list.isEmpty()) {
			jsonResponse.put("warnings", list);
		}
		return jsonResponse.toString();
	}

	/**
	 * curl -i -X DELETE http://localhost:8080/cassandra/api/scn/4fed8930e4b0026f6740d7fa
	 * 
	 * @param id
	 * @return
	 */
	public String deleteScenario(String id) {
		return new MongoDBQueries().deleteDocument(COL_SCENARIOS, id).toString();
	}

	/**
	 * curl -X PUT -d @scenario.json   --header Content-type:application/json   http://localhost:8080/cassandra/api/scn/4fed8682e4b019410d75e578
	 * 
	 * @param id
	 * @param jsonToUpdate
	 * @return
	 */
	public String updateScenario(String id,String jsonToUpdate) {
		String response = new MongoDBQueries().updateDocument("_id", id,jsonToUpdate,
				COL_SCENARIOS, "Scenarios updated successfully", 
				MongoProjects.COL_PROJECTS ,"project_id",JSONValidator.SCENARIO_SCHEMA).toString(); 
		return withAddedWarnings(response, true);
	}

}

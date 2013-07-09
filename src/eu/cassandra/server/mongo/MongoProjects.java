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

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.server.api.exceptions.RestQueryParamMissingException;
import eu.cassandra.server.mongo.util.JSONValidator;
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.MongoDBQueries;
import eu.cassandra.sim.utilities.Utils;

public class MongoProjects {

	public final static String COL_PROJECTS = "projects";

	/**
	 * curl -i https://localhost:8443/cassandra/api/prj/4fec374fdf4ffdb8d1d1ce38
	 * curl -i https://localhost:8443/cassandra/api/prj
	 * 
	 * @param projectID
	 * @return
	 */
	public String getProjects(HttpHeaders httpHeaders, String usr_id, String prj_id, boolean count) {
		if(usr_id != null) {
			return new MongoDBQueries().getEntity(httpHeaders,COL_PROJECTS, "usr_id", usr_id, 
					"Project(s) retrieved successfully",count).toString();
		}
		else if(prj_id != null) {
			return new MongoDBQueries().getEntity(httpHeaders,COL_PROJECTS, "_id", prj_id, 
					"Project(s) retrieved successfully",count).toString();
		} else {
			return new JSONtoReturn().createJSONError(
					"Only the Projects of a particular User or of a particular project id can be retrieved", 
					new RestQueryParamMissingException("usr_id or prj_id QueryParam is missing")).toString();
		}
	}

	/**
	 * curl -i --data  @project.json    --header Content-type:application/json http://localhost:8080/cassandra/api/prj
	 * @param message
	 * @return
	 */
	public String createProject(String dataToInsert) {
		String response =  new MongoDBQueries().insertData(COL_PROJECTS, dataToInsert, 
				"Project created successfully",JSONValidator.PROJECT_SCHEMA).toString();
		return withAddedWarnings(response, false);
	}
	
	private String withAddedWarnings(String response, boolean ary) {
		if(Utils.failed(response)) return response;
		DBObject jsonResponse = (DBObject) JSON.parse(response);
		DBObject data = (DBObject) jsonResponse.get("data");
		String objID = new String();
		if(ary) {
			objID = (String)((DBObject)((BasicDBList)data).get(0)).get("_id");
		} else {
			objID = (String)data.get("_id");
		}
		DBObject returnQuery = 
				new MongoDBQueries().getEntity(MongoScenarios.COL_SCENARIOS, MongoScenarios.REF_PROJECT, objID);
		System.out.println(returnQuery);
		if(returnQuery == null) {
			BasicDBList list = new BasicDBList();
			String warning = "Add at least one scenario for this project.";
			list.add(warning);
			jsonResponse.put("warnings", list);
		}
		return jsonResponse.toString();
	}

	/**
	 * curl -i -X DELETE http://localhost:8080/cassandra/api/prj/4fed6693e4b0cea9dcb3cd6d
	 * 
	 * @param projectID
	 * @return
	 */
	public String deleteProject(String id) {
		return new MongoDBQueries().deleteDocument(COL_PROJECTS, id).toString();
	}

	/**
	 * curl -X PUT -d @project.json   --header Content-type:application/json   http://localhost:8080/cassandra/api/prj/4fec374fdf4ffdb8d1d1c
	 * 
	 * @param projectID
	 * @param jsonToUpdate
	 * @return
	 */
	public String updateProject(String id,String jsonToUpdate) {
		String response = new MongoDBQueries().updateDocument("_id", 
				id,jsonToUpdate,COL_PROJECTS,"Project updated successfully",
				JSONValidator.PROJECT_SCHEMA).toString();
		return withAddedWarnings(response, true);
	}
}

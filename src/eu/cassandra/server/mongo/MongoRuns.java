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

import eu.cassandra.server.api.exceptions.RestQueryParamMissingException;
import eu.cassandra.server.mongo.util.JSONValidator;
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.MongoDBQueries;

public class MongoRuns {
	
	public final static String COL_RUNS = "runs";
	public final static String REF_PROJECT = "prj_id";
	
	/**
	 * curl -i http://localhost:8080/cassandra/api/runs/4fec747cdf4ffdb8d1d1ce55
	 * curl -i http://localhost:8080/cassandra/api/runs
	 * 
	 * @param id
	 * @return
	 */
	public String getRuns(HttpHeaders httpHeaders,String project_id, boolean count) {
		if(project_id == null) {
			return new JSONtoReturn().createJSONError(
					"Only the Runs of a particular Project can be retrieved", 
					new RestQueryParamMissingException("prj_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(httpHeaders,COL_RUNS,"prj_id", project_id, 
					"Runs retrieved successfully",count).toString();
		}
	}
	
	/**
	 * curl -i --data  @run.json --header Content-type:application/json http://localhost:8080/cassandra/api/scn
	 * 
	 * @param message
	 * @return
	 */
	public String createRun(String dataToInsert) {
		return new MongoDBQueries().insertData(COL_RUNS, dataToInsert,
				"Run created successfully", 
				MongoProjects.COL_PROJECTS ,"prj_id", -1).toString();
		// TODO -1 needs to change when the REST for runs is completed
	}
	
	/**
	 * 
	 * @param run_id
	 * @return
	 */
	public String getRun(HttpHeaders httpHeaders,String run_id) {
		return new MongoDBQueries().getEntity(httpHeaders,COL_RUNS,"_id", run_id, 
				"Runs retrieved successfully").toString();
	}
	
	/**
	 * curl -i -X DELETE http://localhost:8080/cassandra/api/run/4fed8930e4b0026f6740d7fa
	 * 
	 * @param id
	 * @return
	 */
	public String deleteRun(String id) {
		return new MongoDBQueries().deleteDocument(COL_RUNS, id).toString();
	}
	
	/**
	 * curl -X PUT -d @scenario.json   --header Content-type:application/json   http://localhost:8080/cassandra/api/scn/4fed8682e4b019410d75e578
	 * 
	 * @param id
	 * @param jsonToUpdate
	 * @return
	 */
	public String updateRun(String id,String jsonToUpdate) {
		// pause or resume
//		return new MongoDBQueries().updateDocument("_id", id,jsonToUpdate,
//				COL_RUNS, "Scenarios updated successfully", 
//				MongoProjects.COL_PROJECTS ,"prj_id",JSONValidator.SCENARIO_SCHEMA).toString(); 
		return "";
	}

}

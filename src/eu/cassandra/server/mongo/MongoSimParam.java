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

public class MongoSimParam {
	
	public final static String COL_SIMPARAM = "sim_param";
	public final static String REF_SCENARIO = "scn_id";
	
	/**
	 * curl -i http://localhost:8080/cassandra/api/smp?scn_id=4ff1a8e2e4b0ed82920aa45b
	 * 
	 * @param cid
	 * @return
	 */
	public String getSimParams(HttpHeaders httpHeaders,String scn_id, boolean count) {
		if(scn_id == null) {
			return new JSONtoReturn().createJSONError(
					"Only the Simulation Parameters of a particular Scenario can be retrieved", 
					new RestQueryParamMissingException("scn_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(httpHeaders,MongoSimParam.COL_SIMPARAM ,"scn_id", 
					scn_id, "Simulation Parameters retrieved successfully",count).toString();
		}
	}

	/**
	 * curl -i http://localhost:8080/cassandra/api/smp/4ff1d91de4b0704e300fec4f
	 * 
	 * @param inst_id
	 * @return
	 */
	public String getSimParam(HttpHeaders httpHeaders, String id) {
		return new MongoDBQueries().getEntity(httpHeaders,MongoSimParam.COL_SIMPARAM,"_id", 
				id, "Simulation Parameter retrieved successfully").toString();
	}

	/**
	 * curl -i --data  @simparam.json    --header Content-type:application/json http://localhost:8080/cassandra/api/smp
	 * 
	 * @param dataToInsert
	 * @return
	 */
	public String createSimParam(String dataToInsert) {
		return new MongoDBQueries().insertData(COL_SIMPARAM ,dataToInsert,
				"Simulation parameters created successfully", 
				MongoScenarios.COL_SCENARIOS,
				"scn_id",
				JSONValidator.SIMPARAM_SCHEMA).toString();
	}

	/**
	 * curl -i -X DELETE http://localhost:8080/cassandra/api/smp/4ff1a986e4b0ed82920aa45f
	 * db.scenarios.update( {"sim_param.cid":ObjectId("4ff1a937e4b0ed82920aa45d")}, { $unset : {sim_param  : 1 }}, false,true)
	 * 
	 * @param cid
	 * @return
	 */
	public String deleteSimParam(String id) {
		return new MongoDBQueries().deleteDocument(COL_SIMPARAM, id).toString();
	}

	/**
	 * curl -X PUT -d @simparam.json   --header Content-type:application/json   http://localhost:8080/cassandra/api/smp/4ff1b2bae4b0f3f709eba132
	 * 
	 * @param cid
	 * @param jsonToUpdate
	 * @return
	 */
	public String updateSimParam(String id,String jsonToUpdate) {
		return new MongoDBQueries().updateDocument("_id", id,jsonToUpdate,
				COL_SIMPARAM, "Simulation Parameters updated successfully",
				MongoScenarios.COL_SCENARIOS ,"scenario_id",JSONValidator.SIMPARAM_SCHEMA).toString();
	}
}

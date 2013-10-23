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

public class MongoDemographics {

	public final static String COL_DEMOGRAPHICS = "demographics";
	public final static String REF_SCENARIO = "scn_id";
	public final static String REF_ENTITY = "entity_id";
	
	/**
	 * curl -i http://localhost:8080/cassandra/api/demog/4ff1d9d4e4b0ddb832a310bc
	 * 
	 * @param id
	 * @return
	 */
	public String getDemographic(HttpHeaders httpHeaders,String id) {
		return new MongoDBQueries().getEntity(httpHeaders,COL_DEMOGRAPHICS,"_id", 
				id, "Demographics retrieved successfully").toString();
	}

	/**
	 * curl -i http://localhost:8080/cassandra/api/demog?scn_id=4ff1d9d4e4b0ddb832a310bc
	 * 
	 * @param scn_id
	 * @return
	 */
	public String getDemographics(HttpHeaders httpHeaders,String scn_id, boolean count) {
		if(scn_id == null) {
			return new JSONtoReturn().createJSONError(
					"Only the Demographics of a particular Scenario can be retrieved", 
					new RestQueryParamMissingException("scn_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(httpHeaders,COL_DEMOGRAPHICS,"scn_id", 
					scn_id, "Demographics retrieved successfully",count).toString();
		}
	}

	/**
	 * curl -i --data  @demographics.json    --header Content-type:application/json http://localhost:8080/cassandra/api/demog
	 * 
	 * @param dataToInsert
	 * @return
	 */
	public String createDemographic(String dataToInsert) {
		return new MongoDBQueries().insertData(COL_DEMOGRAPHICS ,dataToInsert,
				"Demographics created successfully", MongoScenarios.COL_SCENARIOS ,
				"scn_id",JSONValidator.DEMOGRAPHICS_SCHEMA).toString();
	}

	/**
	 * curl -i -X DELETE http://localhost:8080/cassandra/api/demog/4ff153dbe4b0c855ac36d9a7
	 * 
	 * @param id
	 * @return
	 */
	public String deleteDemographics(String id) {
		return new MongoDBQueries().deleteDocument(COL_DEMOGRAPHICS, id).toString();
	}

	/**
	 * curl -X PUT -d @demographics.json   --header Content-type:application/json   http://localhost:8080/cassandra/api/demog/4fec8b53df4ffdb8d1d1ce57
	 * 
	 * @param id
	 * @param jsonToUpdate
	 * @return
	 */
	public String updateDemographics(String id,String jsonToUpdate) {
		return new MongoDBQueries().updateDocument("_id", id,jsonToUpdate,
				COL_DEMOGRAPHICS, "Demographics updated successfully",
				MongoScenarios.COL_SCENARIOS ,"scn_id",JSONValidator.DEMOGRAPHICS_SCHEMA).toString();
	}
}


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
package eu.cassandra.server.mongo;

import eu.cassandra.server.api.exceptions.RestQueryParamMissingException;
import eu.cassandra.server.mongo.util.JSONValidator;
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.MongoDBQueries;

public class MongoInstallations {

	public final static String COL_INSTALLATIONS = "installations";
	public final static String REF_SCENARIO = "scenario_id";
	public final static String REF_BELONGS_TO_INST = "belongsToInstallation";

	/**
	 * curl -i http://localhost:8080/cassandra/api/inst/4ff1ddfde4b0bfe3a2fa6cd9
	 * 
	 * @param id
	 * @return
	 */
	public String getInstallation(String id) {
		System.out.println("GET:" +id);
		return new MongoDBQueries().getEntity(COL_INSTALLATIONS,"_id", 
				id, "Installation retrieved successfully").toString();
	}

	/**
	 *  curl 'http://localhost:8080/cassandra/api/inst?scn_id=4ff5bca7e4b0082c63d08df3&limt=20&filter=\{x:234.232\}&sort=\{y:-1\}'
	 * 
	 * @param scn_id
	 * @return
	 */
	public String getInstallations(String scn_id,String filters, String sort, int limit, int skip, boolean count) {
		if(scn_id == null) {
			return new JSONtoReturn().createJSONError(
					"Only the Installations of a particular Scenario can be retrieved", 
					new RestQueryParamMissingException("scn_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(COL_INSTALLATIONS,"scenario_id", 
					scn_id, filters, sort, limit, skip, "Installations retrieved successfully",count).toString();
		}
	}


	/**
	 * curl -i --data  @installation.json    --header Content-type:application/json http://localhost:8080/cassandra/api/inst
	 * 
	 * @param scenarioID
	 * @param message
	 * @return
	 */
	public String createInstallation(String dataToInsert) {
		return new MongoDBQueries().insertData(COL_INSTALLATIONS ,dataToInsert,
				"Installation created successfully", 
				new String[] {MongoScenarios.COL_SCENARIOS,COL_INSTALLATIONS} ,
				new String[] {"scenario_id","belongsToInstallation"},
				new boolean[] {false,true},JSONValidator.INSTALLATION_SCHEMA).toString();
	}

	/**
	 * curl -i -X DELETE http://localhost:8080/cassandra/api/inst/4ff1ddfde4b0bfe3a2fa6cd9
	 * 
	 * @param id
	 * @return
	 */
	public String deleteInstallation(String id) {
		return new MongoDBQueries().deleteDocument(COL_INSTALLATIONS, id).toString();
	}

	/**
	 * curl -X PUT -d @installation.json   --header Content-type:application/json   http://localhost:8080/cassandra/api/inst/4ff1ddfde4b0bfe3a2fa6cd9
	 * 
	 * @param id
	 * @param jsonToUpdate
	 * @return
	 */
	public String updateInstallation(String id,String jsonToUpdate) {
		return new MongoDBQueries().updateDocument("_id", id,jsonToUpdate,
				COL_INSTALLATIONS, "Installations updated successfully",
				MongoScenarios.COL_SCENARIOS ,"scenario_id",JSONValidator.INSTALLATION_SCHEMA).toString();
	}


}

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
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

public class MongoConsumptionModels {

	public final static String COL_CONSMODELS = "cons_models";
	public final static String REF_APPLIANCE = "app_id";
	

	/**
	 * curl -i http://localhost:8080/cassandra/api/consmod/4fedc0cde4b00db232508ea6
	 * 
	 * @param id
	 * @return
	 */
	public String getConsumptionModel(HttpHeaders httpHeaders,String id) {
		return new MongoDBQueries().getEntity(httpHeaders,COL_CONSMODELS,"_id", 
				id, "Consumption Model retrieved successfully").toString();
	}

	/**
	 * curl -i http://localhost:8080/cassandra/api/consmod?app_id=4fedb39be4b0445cc271fb4d
	 * 
	 * @param app_id
	 * @return
	 */
	public String getConsumptionModels(HttpHeaders httpHeaders,String app_id, boolean count) {
		if(app_id == null) {
			return PrettyJSONPrinter.prettyPrint(new JSONtoReturn().createJSONError(
					"Only the Consumption Models of a particular Appliance can be retrieved", 
					new RestQueryParamMissingException("app_id QueryParam is missing")));
		}
		else {
			return PrettyJSONPrinter.prettyPrint(new MongoDBQueries().getEntity(httpHeaders,COL_CONSMODELS,"app_id", 
					app_id, "Consumption Models retrieved successfully",count));
		}
	}

	/**
	 * curl -i --data  @consumptionmodel.json    --header Content-type:application/json http://localhost:8080/cassandra/api/consmod
	 * 
	 * @param dataToInsert
	 * @return
	 */
	public String createConsumptionModel(String dataToInsert) {
		return PrettyJSONPrinter.prettyPrint(new MongoDBQueries().insertData(COL_CONSMODELS ,dataToInsert,
				"Consumption Model created successfully", 
				MongoAppliances.COL_APPLIANCES,
				"app_id",JSONValidator.CONSUMPTIONMODEL_SCHEMA));
	}

	/**
	 * curl -i -X DELETE http://localhost:8080/cassandra/api/consmod/4ff153dbe4b0c855ac36d9a7
	 * 
	 * @param id
	 * @return
	 */
	public String deleteConsumptionModel(String id) {
		return PrettyJSONPrinter.prettyPrint(new MongoDBQueries().deleteDocument(COL_CONSMODELS, id));
	}

	/**
	 * curl -X PUT -d @consumptionmodel.json   --header Content-type:application/json   http://localhost:8080/cassandra/api/consmod/4fec8b53df4ffdb8d1d1ce57
	 * 
	 * @param id
	 * @param jsonToUpdate
	 * @return
	 */
	public String updateConsumptionModel(String id,String jsonToUpdate) {
		return PrettyJSONPrinter.prettyPrint(new MongoDBQueries().updateDocument("_id", id,jsonToUpdate,
				COL_CONSMODELS, "Consumption Model updated successfully",
				MongoAppliances.COL_APPLIANCES ,"app_id",JSONValidator.CONSUMPTIONMODEL_SCHEMA));
	}
}

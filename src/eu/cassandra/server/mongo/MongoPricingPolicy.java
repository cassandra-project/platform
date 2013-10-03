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

import com.mongodb.DBObject;

import eu.cassandra.server.api.exceptions.RestQueryParamMissingException;
import eu.cassandra.server.mongo.util.JSONValidator;
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.MongoDBQueries;
import eu.cassandra.sim.utilities.Utils;

public class MongoPricingPolicy {

	public final static String COL_PRICING = "pricing";
	public final static String REF_PROJECT = "prj_id";
	public final static String REF_ENTITY = "entity_id";
	
	/**
	 * curl -i http://localhost:8080/cassandra/api/prc/4ff1d9d4e4b0ddb832a310bc
	 * 
	 * @param id
	 * @return
	 */
	public String getPricingPolicy(HttpHeaders httpHeaders,String id) {
		return new MongoDBQueries().getEntity(httpHeaders,COL_PRICING,"_id", 
				id, "Pricing retrieved successfully").toString();
	}

	/**
	 * 
	 * @param scn_id
	 * @return
	 */
	public String getPricingPolicies(HttpHeaders httpHeaders,String prj_id, boolean count) {
		if(prj_id == null) {
			return new JSONtoReturn().createJSONError(
					"Only the pricing policies of a particular Project can be retrieved", 
					new RestQueryParamMissingException("scn_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(httpHeaders, COL_PRICING, REF_PROJECT, 
					prj_id, "Pricing policies retrieved successfully",count).toString();
		}
	}

	/**
	 * curl -i --data  pricing.json    --header Content-type:application/json http://localhost:8080/cassandra/api/prc
	 * 
	 * @param dataToInsert
	 * @return
	 */
	public String createPricingPolicy(String dataToInsert) {
		String response = createPricingObj(dataToInsert).toString();
		return withAddedWarnings(response, false);
	}
	
	public static DBObject createPricingObj(String dataToInsert) {
		MongoDBQueries q = new MongoDBQueries();
		DBObject returnObj = q.insertData(COL_PRICING, dataToInsert,
				"Pricing policy created successfully", MongoProjects.COL_PROJECTS,
				REF_PROJECT, JSONValidator.PRICING_SCHEMA);
		if(Utils.failed(returnObj.toString())) {
			// Perhaps should be added to the user library
			returnObj = q.insertData(COL_PRICING, dataToInsert,
					"Pricing policy created successfully", "users",
					REF_PROJECT, JSONValidator.PRICING_SCHEMA);
		}
		return returnObj;
	}
	
	private String withAddedWarnings(String response, boolean ary) {
		return response;
	}

	/**
	 * curl -i -X DELETE http://localhost:8080/cassandra/api/prc/4ff153dbe4b0c855ac36d9a7
	 * 
	 * @param id
	 * @return
	 */
	public String deletePricingPolicy(String id) {
		return new MongoDBQueries().deleteDocument(COL_PRICING, id).toString();
	}

	/**
	 * curl -X PUT -d @pricing.json   --header Content-type:application/json   http://localhost:8080/cassandra/api/demog/4fec8b53df4ffdb8d1d1ce57
	 * 
	 * @param id
	 * @param jsonToUpdate
	 * @return
	 */
	public String updatePricingPolicy(String id,String jsonToUpdate) {
		MongoDBQueries q = new MongoDBQueries();
		String returnMsg = q.updateDocument("_id", id,jsonToUpdate,
				COL_PRICING, "Pricing policy updated successfully",
				MongoProjects.COL_PROJECTS, REF_PROJECT,JSONValidator.PRICING_SCHEMA).toString();
		if(Utils.failed(returnMsg)) {
			returnMsg = q.updateDocument("_id", id,jsonToUpdate,
					COL_PRICING, "Pricing policy updated successfully",
					"users", REF_PROJECT, JSONValidator.PRICING_SCHEMA).toString();
		}
		return withAddedWarnings(returnMsg, true);
	}
	
}


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

public class MongoDistributions {

	public final static String COL_DISTRIBUTIONS = "distributions";
	public final static String REF_ACTIVITYMODEL = "actmod_id";


	/**
	 * curl -i --data  @distribution.json    --header Content-type:application/json http://localhost:8080/cassandra/api/distr
	 * 
	 * @param dataToInsert
	 * @return
	 */
	public String createDistribution(String dataToInsert) {
		return PrettyJSONPrinter.prettyPrint(new MongoDBQueries().insertData(COL_DISTRIBUTIONS ,dataToInsert,
				"Distribution created successfully",JSONValidator.DISTRIBUTION_SCHEMA).toString());
	}

	/**
	 * curl -i http://localhost:8080/cassandra/api/distr/4ffafb1ae4b0540961e1f71c
	 * 
	 * @param id
	 * @return
	 */
	public String getDistribution(HttpHeaders httpHeaders,String id) {
		return new MongoDBQueries().getEntity(httpHeaders,COL_DISTRIBUTIONS,"_id", 
				id, "Distribution retrieved successfully").toString();
	}

	/**
	 * curl -i http://localhost:8080/cassandra/api/distr?actmod_id=4ff5c44ee4b0d4da260a9a9
	 * 
	 * @param scn_id
	 * @return
	 */
	public String getDistributions(HttpHeaders httpHeaders, String actmod_id, boolean count) {
		if(actmod_id == null) {
			return new JSONtoReturn().createJSONError(
					"Only the Distributions of a particular Activity Model can be retrieved", 
					new RestQueryParamMissingException("actmod_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(httpHeaders,COL_DISTRIBUTIONS,"actmod_id", 
					actmod_id, "Distributions retrieved successfully",count).toString();
		}
	}

	/**
	 * curl -i -X DELETE http://localhost:8080/cassandra/api/distr/4ffafab8e4b08f07dee2f5f3
	 * 
	 * @param id
	 * @return
	 */
	public String deleteDistribution(String id) {
		return new MongoDBQueries().deleteDocument(COL_DISTRIBUTIONS, id).toString();
	}

	/**
	 * curl -X PUT -d @distribution.json   --header Content-type:application/json   http://localhost:8080/cassandra/api/distr/4ffafb1ae4b0540961e1f71c
	 * 
	 * @param id
	 * @param jsonToUpdate
	 * @return
	 */
	public String updateDistribution(String id,String jsonToUpdate) {
		return new MongoDBQueries().updateDocument("_id", id,jsonToUpdate,
				COL_DISTRIBUTIONS, "DIstribution updated successfully",
				MongoActivityModels.COL_ACTMODELS ,"actmod_id",JSONValidator.DISTRIBUTION_SCHEMA).toString();
	}
}

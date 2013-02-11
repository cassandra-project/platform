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


import javax.ws.rs.core.HttpHeaders;

import eu.cassandra.server.api.exceptions.RestQueryParamMissingException;
import eu.cassandra.server.mongo.util.JSONValidator;
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.MongoDBQueries;

public class MongoActivityModels {

	public final static String COL_ACTMODELS = "act_models";
	public final static String REF_ACTIVITY = "act_id";
	public final static String REF_CONTAINSAPPLIANCES = "containsAppliances";
	
	public final static String REF_DISTR_DURATION = "duration";
	public final static String REF_DISTR_STARTTIME = "startTime";
	public final static String REF_DISTR_REPEATS = "repeatsNrOfTime";
	

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
		return new MongoDBQueries().insertData(COL_ACTMODELS ,dataToInsert,
				"Activity Model created successfully", 
				new String[] {MongoActivities.COL_ACTIVITIES, MongoDistributions.COL_DISTRIBUTIONS, 
				MongoDistributions.COL_DISTRIBUTIONS, MongoDistributions.COL_DISTRIBUTIONS  },
				new String[] {"act_id","duration","startTime","repeatsNrOfTime"},
				new boolean[] {false,true,true,true},JSONValidator.ACTIVITYMODEL_SCHEMA
				).toString();

		//         : "4ff31bc9e4b0721a5785a1bc",
		//         : "4ff31bc9e4b0721a5785a1bc",
		//         : "4ff45dc1e4b00acd8d6457e3"
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
		return new MongoDBQueries().updateDocument("_id", id,jsonToUpdate,
				COL_ACTMODELS, "Activity Model updated successfully",
				MongoActivities.COL_ACTIVITIES ,"act_id",
				JSONValidator.ACTIVITYMODEL_SCHEMA).toString();
	}
}

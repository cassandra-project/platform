package eu.cassandra.server.mongo;

import eu.cassandra.server.api.exceptions.RestQueryParamMissingException;
import eu.cassandra.server.mongo.util.MongoDBQueries;

public class MongoActivityModels {

	private final static String COL_ACTMODELS = "act_models";

	/**
	 * curl -i http://localhost:8080/cassandra/api/app/4fedc0cde4b00db232508ea6
	 * 
	 * @param cid
	 * @return
	 */
	public String getActivityModel(String id) {
		return new MongoDBQueries().getEntity(COL_ACTMODELS,"_id", 
				id, "Activity Model retrieved successfully").toString();
	}

	/**
	 * curl -i http://localhost:8080/cassandra/api/app?inst_id=4fedb39be4b0445cc271fb4d
	 * 
	 * @param inst_id
	 * @return
	 */
	public String getActivityModels(String act_id) {
		if(act_id == null) {
			return new MongoDBQueries().createJSONError(
					"Only the Activity Models of a particular Activity can be retrieved", 
					new RestQueryParamMissingException("act_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(COL_ACTMODELS,"act_id", 
					act_id, "Activity Models retrieved successfully").toString();
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
				new boolean[] {false,true,true,true}
				).toString();

		//         : "4ff31bc9e4b0721a5785a1bc",
		//         : "4ff31bc9e4b0721a5785a1bc",
		//         : "4ff45dc1e4b00acd8d6457e3"
	}

	/**
	 * curl -i -X DELETE http://localhost:8080/cassandra/api/app/4ff153dbe4b0c855ac36d9a7
	 * 
	 * @param id
	 * @return
	 */
	public String deleteActivityModel(String id) {
		return new MongoDBQueries().deleteDocument(COL_ACTMODELS, id).toString();
	}

	/**
	 * curl -X PUT -d @appliance.json   --header Content-type:application/json   http://localhost:8080/cassandra/api/app/4fec8b53df4ffdb8d1d1ce57
	 * 
	 * @param cid
	 * @param jsonToUpdate
	 * @return
	 */
	public String updateActivityModel(String id,String jsonToUpdate) {
		return new MongoDBQueries().updateDocument("_id", id,jsonToUpdate,
				COL_ACTMODELS, "Activity Model updated successfully",
				MongoActivities.COL_ACTIVITIES ,"act_id" ).toString();
	}
}

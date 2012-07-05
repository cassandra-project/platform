package cassandra.mongo;

import cassandra.exceptions.RestQueryParamMissingException;
import cassandra.mongo.util.MongoDBQueries;

public class MongoActivities {

	protected final static String COL_ACTIVITIES = "activities";

	/**
	 * 
	 * curl -i http://localhost:8080/cassandra/api/act/4fedc0cde4b00db232508ea6
	 * 
	 * @param cid
	 * @return
	 */
	public String getActivity(String id) {
		return new MongoDBQueries().getEntity(COL_ACTIVITIES,"_id", 
				id, "Activity retrieved successfully").toString();
	}

	/**
	 * curl -i http://localhost:8080/cassandra/api/act?inst_id=4ff154f8e4b0c855ac36d9ad
	 * 
	 * @param inst_id
	 * @return
	 */
	public String getActivities(String inst_id) {
		if(inst_id == null) {
			return new MongoDBQueries().createJSONError(
					"Only the Activities of a particular Installation can be retrieved", 
					new RestQueryParamMissingException("inst_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(COL_ACTIVITIES,"pers_id", 
					inst_id, "Activities retrieved successfully").toString();
		}
	}

	/**
	 * curl -i --data  @activity.json    --header Content-type:application/json http://localhost:8080/cassandra/api/act
	 * 
	 * @param dataToInsert
	 * @return
	 */
	public String createActivity(String dataToInsert) {
		return new MongoDBQueries().insertData(COL_ACTIVITIES ,dataToInsert,
				"Activity created successfully", MongoPersons.COL_PERSONS ,"pers_id" ).toString();
	}

	/**
	 * curl -i -X DELETE http://localhost:8080/cassandra/api/act/4ff153dbe4b0c855ac36d9a7
	 * 
	 * @param cid
	 * @return
	 */
	public String deleteActivity(String id) {
		return new MongoDBQueries().deleteDocument(COL_ACTIVITIES, id).toString();
	}

	/**
	 * curl -X PUT -d @activity.json   --header Content-type:application/json   http://localhost:8080/cassandra/api/act/4fec8b53df4ffdb8d1d1ce57
	 * 
	 * @param cid
	 * @param jsonToUpdate
	 * @return
	 */
	public String updateActivity(String id,String jsonToUpdate) {
		return new MongoDBQueries().updateDocument("_id", id,jsonToUpdate,
				COL_ACTIVITIES, "Activity updated successfully",
				 MongoPersons.COL_PERSONS ,"pers_id" ).toString();
	}

}

package eu.cassandra.server.mongo;

import eu.cassandra.server.api.exceptions.RestQueryParamMissingException;
import eu.cassandra.server.mongo.util.JSONValidator;
import eu.cassandra.server.mongo.util.MongoDBQueries;

public class MongoAppliances {

	protected final static String COL_APPLIANCES = "appliances";

	/**
	 * curl -i http://localhost:8080/cassandra/api/app/4ff1d9d4e4b0ddb832a310bc
	 * 
	 * @param cid
	 * @return
	 */
	public String getAppliance(String id) {
		return new MongoDBQueries().getEntity(COL_APPLIANCES,"_id", 
				id, "Appliance retrieved successfully").toString();
	}

	/**
	 * curl -i http://localhost:8080/cassandra/api/app?inst_id=4ff1d9d4e4b0ddb832a310bc
	 * 
	 * @param inst_id
	 * @return
	 */
	public String getAppliances(String inst_id) {
		if(inst_id == null) {
			return new MongoDBQueries().createJSONError(
					"Only the Appliances of a particular Installation can be retrieved", 
					new RestQueryParamMissingException("inst_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(COL_APPLIANCES,"inst_id", 
					inst_id, "Appliances retrieved successfully").toString();
		}
	}

	/**
	 * curl -i --data  @appliance.json    --header Content-type:application/json http://localhost:8080/cassandra/api/app
	 * 
	 * @param dataToInsert
	 * @return
	 */
	public String createAppliance(String dataToInsert) {
		return new MongoDBQueries().insertData(COL_APPLIANCES ,dataToInsert,
				"Appliance created successfully", MongoInstallations.COL_INSTALLATIONS ,
				"inst_id",JSONValidator.APPLIANCE_SCHEMA).toString();
	}

	/**
	 * curl -i -X DELETE http://localhost:8080/cassandra/api/app/4ff153dbe4b0c855ac36d9a7
	 * 
	 * @param id
	 * @return
	 */
	public String deleteAppliance(String id) {
		return new MongoDBQueries().deleteDocument(COL_APPLIANCES, id).toString();
	}

	/**
	 * curl -X PUT -d @appliance.json   --header Content-type:application/json   http://localhost:8080/cassandra/api/app/4fec8b53df4ffdb8d1d1ce57
	 * 
	 * @param cid
	 * @param jsonToUpdate
	 * @return
	 */
	public String updateAppliance(String id,String jsonToUpdate) {
		return new MongoDBQueries().updateDocument("_id", id,jsonToUpdate,
				COL_APPLIANCES, "Appliance updated successfully",
				MongoInstallations.COL_INSTALLATIONS ,"inst_id" ).toString();
	}
}

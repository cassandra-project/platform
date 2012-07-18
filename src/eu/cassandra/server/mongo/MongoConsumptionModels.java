package eu.cassandra.server.mongo;

import eu.cassandra.server.api.exceptions.RestQueryParamMissingException;
import eu.cassandra.server.mongo.util.JSONValidator;
import eu.cassandra.server.mongo.util.MongoDBQueries;

public class MongoConsumptionModels {

	public final static String COL_CONSMODELS = "cons_models";

	/**
	 * curl -i http://localhost:8080/cassandra/api/consmod/4fedc0cde4b00db232508ea6
	 * 
	 * @param id
	 * @return
	 */
	public String getConsumptionModel(String id) {
		return new MongoDBQueries().getEntity(COL_CONSMODELS,"_id", 
				id, "Consumption Model retrieved successfully").toString();
	}

	/**
	 * curl -i http://localhost:8080/cassandra/api/consmod?app_id=4fedb39be4b0445cc271fb4d
	 * 
	 * @param app_id
	 * @return
	 */
	public String getConsumptionModels(String app_id) {
		if(app_id == null) {
			return new MongoDBQueries().createJSONError(
					"Only the Consumption Models of a particular Appliance can be retrieved", 
					new RestQueryParamMissingException("app_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(COL_CONSMODELS,"app_id", 
					app_id, "Consumption Models retrieved successfully").toString();
		}
	}

	/**
	 * curl -i --data  @consumptionmodel.json    --header Content-type:application/json http://localhost:8080/cassandra/api/consmod
	 * 
	 * @param dataToInsert
	 * @return
	 */
	public String createConsumptionModel(String dataToInsert) {
		return new MongoDBQueries().insertData(COL_CONSMODELS ,dataToInsert,
				"Consumption Model created successfully", 
				MongoAppliances.COL_APPLIANCES,
				"app_id",JSONValidator.CONSUMPTIONMODEL_SCHEMA
				).toString();
	}

	/**
	 * curl -i -X DELETE http://localhost:8080/cassandra/api/consmod/4ff153dbe4b0c855ac36d9a7
	 * 
	 * @param id
	 * @return
	 */
	public String deleteConsumptionModel(String id) {
		return new MongoDBQueries().deleteDocument(COL_CONSMODELS, id).toString();
	}

	/**
	 * curl -X PUT -d @consumptionmodel.json   --header Content-type:application/json   http://localhost:8080/cassandra/api/consmod/4fec8b53df4ffdb8d1d1ce57
	 * 
	 * @param id
	 * @param jsonToUpdate
	 * @return
	 */
	public String updateConsumptionModel(String id,String jsonToUpdate) {
		return new MongoDBQueries().updateDocument("_id", id,jsonToUpdate,
				COL_CONSMODELS, "Consumption Model updated successfully",
				MongoAppliances.COL_APPLIANCES ,"app_id",JSONValidator.CONSUMPTIONMODEL_SCHEMA).toString();
	}
}

package eu.cassandra.server.mongo;

import eu.cassandra.server.api.exceptions.RestQueryParamMissingException;
import eu.cassandra.server.mongo.util.JSONValidator;
import eu.cassandra.server.mongo.util.MongoDBQueries;

public class MongoDistributions {

	protected final static String COL_DISTRIBUTIONS = "distributions";

	/**
	 * curl -i --data  @distribution.json    --header Content-type:application/json http://localhost:8080/cassandra/api/distr
	 * 
	 * @param dataToInsert
	 * @return
	 */
	public String createDistribution(String dataToInsert) {
		return new MongoDBQueries().insertData(COL_DISTRIBUTIONS ,dataToInsert,
				"Distribution created successfully",MongoActivityModels.COL_ACTMODELS,
				"actmod_id",JSONValidator.DISTRIBUTION_SCHEMA).toString();
	}

	/**
	 * curl -i http://localhost:8080/cassandra/api/distr/4ffafb1ae4b0540961e1f71c
	 * 
	 * @param id
	 * @return
	 */
	public String getDistribution(String id) {
		System.out.println(id);
		return new MongoDBQueries().getEntity(COL_DISTRIBUTIONS,"_id", 
				id, "Distribution retrieved successfully").toString();
	}

	/**
	 * curl -i http://localhost:8080/cassandra/api/distr?actmod_id=4ff5c44ee4b0d4da260a9a9
	 * 
	 * @param scn_id
	 * @return
	 */
	public String getDistributions(String actmod_id) {
		if(actmod_id == null) {
			return new MongoDBQueries().createJSONError(
					"Only the Distributions of a particular Activity Model can be retrieved", 
					new RestQueryParamMissingException("actmod_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(COL_DISTRIBUTIONS,"actmod_id", 
					actmod_id, "Distributions retrieved successfully").toString();
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

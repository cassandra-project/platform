package cassandra.mongo;

import cassandra.mongo.util.MongoDBQueries;

public class MongoDistributions {

	protected final static String COL_DISTRIBUTIONS = "distributions";
	
	public String getDistribution(String id) {
		return new MongoDBQueries().getEntity(COL_DISTRIBUTIONS,"_id", 
				id, "Distribution retrieved successfully").toString();
	}
	
	/**
	 * curl -i --data  @distribution.json    --header Content-type:application/json http://localhost:8080/cassandra/api/distr
	 * 
	 * @param dataToInsert
	 * @return
	 */
	public String createDistribution(String dataToInsert) {
		return new MongoDBQueries().insertData(COL_DISTRIBUTIONS ,dataToInsert,
				"Distribution created successfully").toString();
	}
}

package eu.cassandra.server.mongo;

import eu.cassandra.server.api.exceptions.RestQueryParamMissingException;
import eu.cassandra.server.mongo.util.MongoDBQueries;

public class MongoSimParam {
	private final static String KEYNAME_SIMPARAM = "sim_param";

	/**
	 * curl -i http://localhost:8080/cassandra/api/smp?scn_id=4ff1a8e2e4b0ed82920aa45b
	 * 
	 * @param cid
	 * @return
	 */
	public String getSimParams(String scn_id) {
		if(scn_id == null) {
			return new MongoDBQueries().createJSONError(
					"Only the Simulation Parameters of a particular Scenario can be retrieved", 
					new RestQueryParamMissingException("scn_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(MongoScenarios.COL_SCENARIOS ,"_id", 
					scn_id, "Simulation Parameters retrieved successfully", 
					new String[]{KEYNAME_SIMPARAM}).toString();
		}
	}

	/**
	 * curl -i http://localhost:8080/cassandra/api/smp/4ff1d91de4b0704e300fec4f
	 * 
	 * @param inst_id
	 * @return
	 */
	public String getSimParam(String smp_id) {
		return new MongoDBQueries().getEntity(MongoScenarios.COL_SCENARIOS,"sim_param.cid", 
				smp_id, "Simulation Parameter retrieved successfully",
				new String[]{KEYNAME_SIMPARAM}).toString();
	}

	/**
	 * curl -i --data  @simparam.json    --header Content-type:application/json http://localhost:8080/cassandra/api/smp
	 * 
	 * @param dataToInsert
	 * @return
	 */
	public String createSimParam(String dataToInsert) {
		return  new MongoDBQueries().insertNestedDocument(dataToInsert, 
				MongoScenarios.COL_SCENARIOS, "scn_id").toString();
	}

	/**
	 * curl -i -X DELETE http://localhost:8080/cassandra/api/smp/4ff1a986e4b0ed82920aa45f
	 * db.scenarios.update( {"sim_param.cid":ObjectId("4ff1a937e4b0ed82920aa45d")}, { $unset : {sim_param  : 1 }}, false,true)
	 * 
	 * @param cid
	 * @return
	 */
	public String deleteSimParam(String cid) {
		return new MongoDBQueries().deleteDocumentField(MongoScenarios.COL_SCENARIOS,KEYNAME_SIMPARAM, cid).toString();
	}

	/**
	 * curl -X PUT -d @simparam.json   --header Content-type:application/json   http://localhost:8080/cassandra/api/smp/4ff1b2bae4b0f3f709eba132
	 * 
	 * @param cid
	 * @param jsonToUpdate
	 * @return
	 */
	public String updateSimParam(String cid,String jsonToUpdate) {
		return new MongoDBQueries().updateDocument(KEYNAME_SIMPARAM + ".cid", cid,jsonToUpdate,
				MongoScenarios.COL_SCENARIOS, "Simulation Parameters updated successfully",
				MongoScenarios.COL_SCENARIOS, "scn_id", KEYNAME_SIMPARAM).toString();
	}
}

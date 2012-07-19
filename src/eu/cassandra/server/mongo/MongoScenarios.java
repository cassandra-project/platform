package eu.cassandra.server.mongo;

import eu.cassandra.server.api.exceptions.RestQueryParamMissingException;
import eu.cassandra.server.mongo.util.JSONValidator;
import eu.cassandra.server.mongo.util.MongoDBQueries;

public class MongoScenarios {

	protected final static String COL_SCENARIOS = "scenarios";

	/**
	 * curl -i http://localhost:8080/cassandra/api/scn/4fec747cdf4ffdb8d1d1ce55
	 * curl -i http://localhost:8080/cassandra/api/scn
	 * 
	 * @param id
	 * @return
	 */
	public String getScenarios(String project_id) {
		if(project_id == null) {
			return new MongoDBQueries().createJSONError(
					"Only the Scenarios of a particular Project can be retrieved", 
					new RestQueryParamMissingException("prj_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(COL_SCENARIOS,"project_id", project_id, 
					"Scenarios retrieved successfully").toString();
		}
	}

	/**
	 * 
	 * @param scenario_id
	 * @return
	 */
	public String getScenario(String scenario_id) {
		return new MongoDBQueries().getEntity(COL_SCENARIOS,"_id", scenario_id, 
				"Scenarios retrieved successfully").toString();
	}

	/**
	 * curl -i --data  @scenario.json    --header Content-type:application/json http://localhost:8080/cassandra/api/scn
	 * 
	 * @param message
	 * @return
	 */
	public String createScenario(String dataToInsert) {
		return new MongoDBQueries().insertData(COL_SCENARIOS,dataToInsert,
				"Scenario created successfully", MongoProjects.COL_PROJECTS ,
				"project_id",JSONValidator.SCENARIO_SCHEMA).toString();
	}

	/**
	 * curl -i -X DELETE http://localhost:8080/cassandra/api/scn/4fed8930e4b0026f6740d7fa
	 * 
	 * @param id
	 * @return
	 */
	public String deleteScenario(String id) {
		return new MongoDBQueries().deleteDocument(COL_SCENARIOS, id).toString();
	}

	/**
	 * curl -X PUT -d @scenario.json   --header Content-type:application/json   http://localhost:8080/cassandra/api/scn/4fed8682e4b019410d75e578
	 * 
	 * @param id
	 * @param jsonToUpdate
	 * @return
	 */
	public String updateScenario(String id,String jsonToUpdate) {
		return new MongoDBQueries().updateDocument("_id", id,jsonToUpdate,
				COL_SCENARIOS, "Scenarios updated successfully", 
				MongoProjects.COL_PROJECTS ,"project_id",JSONValidator.SCENARIO_SCHEMA).toString(); 
	}

}

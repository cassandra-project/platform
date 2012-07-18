package eu.cassandra.server.mongo;

import eu.cassandra.server.api.exceptions.RestQueryParamMissingException;
import eu.cassandra.server.mongo.util.MongoDBQueries;

public class MongoRuns {
	
	protected final static String COL_RUNS = "runs";
	
	/**
	 * curl -i http://localhost:8080/cassandra/api/runs/4fec747cdf4ffdb8d1d1ce55
	 * curl -i http://localhost:8080/cassandra/api/runs
	 * 
	 * @param id
	 * @return
	 */
	public String getRuns(String project_id) {
		if(project_id == null) {
			return new MongoDBQueries().createJSONError(
					"Only the Runs of a particular Project can be retrieved", 
					new RestQueryParamMissingException("prj_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(COL_RUNS,"project_id", project_id, 
					"Runs retrieved successfully").toString();
		}
	}
	
	/**
	 * curl -i --data  @run.json --header Content-type:application/json http://localhost:8080/cassandra/api/scn
	 * 
	 * @param message
	 * @return
	 */
	public String createRun(String dataToInsert) {
		return new MongoDBQueries().insertData(COL_RUNS, dataToInsert,
				"Run created successfully", 
				MongoProjects.COL_PROJECTS ,"project_id", -1).toString();
		// TODO -1 needs to change when the REST for runs is completed
	}
	
	/**
	 * 
	 * @param run_id
	 * @return
	 */
	public String getRun(String run_id) {
		return new MongoDBQueries().getEntity(COL_RUNS,"_id", run_id, 
				"Runs retrieved successfully").toString();
	}
	
	/**
	 * curl -i -X DELETE http://localhost:8080/cassandra/api/run/4fed8930e4b0026f6740d7fa
	 * 
	 * @param id
	 * @return
	 */
	public String deleteRun(String id) {
		return new MongoDBQueries().deleteDocument(COL_RUNS, id).toString();
	}
	
	/**
	 * curl -X PUT -d @scenario.json   --header Content-type:application/json   http://localhost:8080/cassandra/api/scn/4fed8682e4b019410d75e578
	 * 
	 * @param id
	 * @param jsonToUpdate
	 * @return
	 */
	public String updateRun(String id,String jsonToUpdate) {
		// pause or resume
		return new MongoDBQueries().updateDocument("_id", id,jsonToUpdate,
				COL_RUNS, "Scenarios updated successfully", 
				MongoProjects.COL_PROJECTS ,"project_id" ).toString(); 
	}

}

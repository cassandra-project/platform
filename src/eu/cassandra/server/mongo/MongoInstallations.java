package cassandra.mongo;

import cassandra.exceptions.RestQueryParamMissingException;
import cassandra.mongo.util.MongoDBQueries;

public class MongoInstallations {

	protected final static String COL_INSTALLATIONS = "installations";

	/**
	 * curl -i http://localhost:8080/cassandra/api/inst/4ff1ddfde4b0bfe3a2fa6cd9
	 * 
	 * @param id
	 * @return
	 */
	public String getInstallation(String id) {
		System.out.println("GET:" +id);
		return new MongoDBQueries().getEntity(COL_INSTALLATIONS,"_id", 
				id, "Installation retrieved successfully").toString();
	}

	/**
	 * curl -i http://localhost:8080/cassandra/api/inst?scn_id=scenarioID
	 * 
	 * @param scn_id
	 * @return
	 */
	public String getInstallations(String scn_id) {
		System.out.println("GET s:" +scn_id);
		if(scn_id == null) {
			return new MongoDBQueries().createJSONError(
					"Only the Installations of a particular Scenario can be retrieved", 
					new RestQueryParamMissingException("scn_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(COL_INSTALLATIONS,"scenario_id", 
					scn_id, "Installations retrieved successfully").toString();
		}
	}

	/**
	 * curl -i --data  @installation.json    --header Content-type:application/json http://localhost:8080/cassandra/api/inst
	 * 
	 * @param scenarioID
	 * @param message
	 * @return
	 */
	public String createInstallation(String dataToInsert) {
		return new MongoDBQueries().insertData(COL_INSTALLATIONS ,dataToInsert,
				"Installation created successfully", 
				new String[] {MongoScenarios.COL_SCENARIOS,COL_INSTALLATIONS} ,
				new String[] {"scenario_id","belongsToInstallation"},
				new boolean[] {false,true}).toString();
	}

	/**
	 * curl -i -X DELETE http://localhost:8080/cassandra/api/inst/4ff1ddfde4b0bfe3a2fa6cd9
	 * 
	 * @param id
	 * @return
	 */
	public String deleteInstallation(String id) {
		System.out.println("DELETE:" + id);
		return new MongoDBQueries().deleteDocument(COL_INSTALLATIONS, id).toString();
	}

	/**
	 * curl -X PUT -d @installation.json   --header Content-type:application/json   http://localhost:8080/cassandra/api/inst/4ff1ddfde4b0bfe3a2fa6cd9
	 * 
	 * @param id
	 * @param jsonToUpdate
	 * @return
	 */
	public String updateInstallation(String id,String jsonToUpdate) {
		System.out.println("UPDATE s:" +id);
		return new MongoDBQueries().updateDocument("_id", id,jsonToUpdate,
				COL_INSTALLATIONS, "Installations updated successfully",
				MongoScenarios.COL_SCENARIOS ,"scenario_id" ).toString();
	}


}

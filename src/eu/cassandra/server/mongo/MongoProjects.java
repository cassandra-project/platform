package cassandra.mongo;

import cassandra.mongo.util.MongoDBQueries;

public class MongoProjects {

	protected final static String COL_PROJECTS = "projects";

	/**
	 * curl -i http://localhost:8080/cassandra/api/prj/4fec374fdf4ffdb8d1d1ce38
	 * curl -i http://localhost:8080/cassandra/api/prj
	 * 
	 * @param projectID
	 * @return
	 */
	public String getProjects(String id) {
		return new MongoDBQueries().getEntity(COL_PROJECTS, "_id", id, 
				"Project(s) retrieved successfully").toString();
	}

	/**
	 * curl -i --data  @project.json    --header Content-type:application/json http://localhost:8080/cassandra/api/prj
	 * @param message
	 * @return
	 */
	public String createProject(String dataToInsert) {
		return new MongoDBQueries().insertData(COL_PROJECTS, dataToInsert, 
				"Project created successfully").toString();
	}

	/**
	 * curl -i -X DELETE http://localhost:8080/cassandra/api/prj/4fed6693e4b0cea9dcb3cd6d
	 * 
	 * @param projectID
	 * @return
	 */
	public String deleteProject(String id) {
		return new MongoDBQueries().deleteDocument(COL_PROJECTS, id).toString();
	}

	/**
	 * curl -X PUT -d @project.json   --header Content-type:application/json   http://localhost:8080/cassandra/api/prj/4fec374fdf4ffdb8d1d1c
	 * 
	 * @param projectID
	 * @param jsonToUpdate
	 * @return
	 */
	public String updateProject(String id,String jsonToUpdate) {
		return new MongoDBQueries().updateDocument("_id", 
				id,jsonToUpdate,COL_PROJECTS,"Project updated successfully").toString();
	}
}

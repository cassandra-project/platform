package cassandra;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

@Path("prj/{prj_id: [a-z0-9][a-z0-9]*}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Project {
	
	private final static String COL_PROJECTS = "projects";
	
	/**
	 * Returns the project data based on the project id
	 * @param prj_id
	 * @return
	 */
	@GET
	public String getProject(@PathParam("prj_id") String prj_id) {
		// TODO
		return null;
	}
	
	/**
	 * Receives the post data and returns the full data again
	 * @param message
	 * @return
	 */
	@PUT
	public String updateProject(String message) {
		DBObject dbObject = (DBObject) JSON.parse(message);
		WriteResult wr = 
				DBConn.getConn().getCollection(COL_PROJECTS).insert(dbObject);
		return message + " " + wr;
	}
	
	/**
	 * Delete a project
	 */
	@DELETE
	public String deleteProject(@PathParam("prj_id") String prj_id) {
		// TODO
		return null;
	}
}

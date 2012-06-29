package cassandra;

import javax.ws.rs.GET;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mongodb.util.JSON;
import com.mongodb.DBCursor;

@Path("prj")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Projects {
	
	private final static String COL_PROJECTS = "projects";
	
	/**
	 * 
	 * @return
	 */
	@GET
	public String getProjects() {
		DBCursor cursorDoc = DBConn.getConn().getCollection(COL_PROJECTS).find();
		StringBuilder sb = new StringBuilder();
		while (cursorDoc.hasNext()) {
			sb.append(JSON.serialize(cursorDoc.next()));
		}
		return sb.toString();
	}
	
	/**
	 * Create a project
	 */
	@POST
	public String createProject(String message) {
		
		return null;
	}

}

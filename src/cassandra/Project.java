package cassandra;
import javax.ws.rs.GET;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import com.mongodb.util.JSON;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

@Path("/project")
public class Project {
	
	private final static String COL_PROJECTS = "projects";
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String sayJSONHello() {
		DBCursor cursorDoc = DBConn.getConn().getCollection(COL_PROJECTS).find();
		StringBuilder sb = new StringBuilder();
		while (cursorDoc.hasNext()) {
			sb.append(JSON.serialize(cursorDoc.next()));
		}
		return sb.toString();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String postProject(String message) {
		DBObject dbObject = (DBObject) JSON.parse(message);
		WriteResult wr = 
				DBConn.getConn().getCollection(COL_PROJECTS).insert(dbObject);
		return message + " " + wr;
	}

}

package cassandra;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("act")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Activities {
	
	/**
	 * Create a activity
	 */
	@POST
	public String createActivity(String message) {
		
		return null;
	}


}

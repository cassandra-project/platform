package cassandra;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("actmod")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ActivityModels {
	
	/**
	 * Create a ActivityModel
	 */
	@POST
	public String createActivityModel(String message) {
		
		return null;
	}

}

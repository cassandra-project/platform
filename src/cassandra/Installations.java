package cassandra;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("inst")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Installations {
	
	/**
	 * Gets the installations under a scenario id
	 * @param message contains the scenario_id to search the related scenarios
	 * @return
	 */
	@GET
	public String getInstallations(@QueryParam("scn_id") String prj_id) {
		System.out.println(prj_id);
		return null;
	}
	
	/**
	 * Create a installation
	 */
	@POST
	public String createInstallation(String message) {
		
		return null;
	}

}

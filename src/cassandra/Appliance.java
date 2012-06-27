package cassandra;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("app/{app_id: [a-z0-9][a-z0-9]*}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Appliance {
	
	/**
	 * Returns the appliance data based on the appliance id
	 * @param app_id
	 * @return
	 */
	@GET
	public String getAppliance(@PathParam("app_id") String app_id) {
		System.out.println(app_id);
		return null;
	}
	
	/**
	 * Scenario update
	 * @param scn_id
	 * @return
	 */
	@PUT
	public String updateAppliance(@PathParam("app_id") String app_id, String message) {
		System.out.println(app_id + " | " + message);
		return null;
	}
	
	/**
	 * Delete a scenario
	 */
	@DELETE
	public String deleteAppliance(@PathParam("app_id") String app_id) {
		// TODO
		return null;
	}

}

package cassandra;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("actmod/{actmod_id: [a-z0-9][a-z0-9]*}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ActivityModel {
	
	/**
	 * Returns the scenario data based on the scenario id
	 * @param scn_id
	 * @return
	 */
	@GET
	public String getActivityModel(@PathParam("actmod_id") String actmod_id) {
		System.out.println(actmod_id);
		return null;
	}
	
	/**
	 * Scenario update
	 * @param scn_id
	 * @return
	 */
	@PUT
	public String updateActivityModel(@PathParam("actmod_id") String actmod_id, String message) {
		System.out.println(actmod_id + " | " + message);
		return null;
	}
	
	/**
	 * Delete a scenario
	 */
	@DELETE
	public String deleteActivityModel(@PathParam("actmod_id") String actmod_id) {
		// TODO
		return null;
	}

}

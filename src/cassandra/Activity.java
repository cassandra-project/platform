package cassandra;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("act/{act_id: [a-z0-9][a-z0-9]*}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Activity {
	
	/**
	 * Returns the scenario data based on the scenario id
	 * @param scn_id
	 * @return
	 */
	@GET
	public String getActivity(@PathParam("act_id") String act_id) {
		System.out.println(act_id);
		return null;
	}
	
	/**
	 * Scenario update
	 * @param scn_id
	 * @return
	 */
	@PUT
	public String updateActivity(@PathParam("act_id") String act_id, String message) {
		System.out.println(act_id + " | " + message);
		return null;
	}
	
	/**
	 * Delete a scenario
	 */
	@DELETE
	public String deleteActivity(@PathParam("act_id") String act_id) {
		// TODO
		return null;
	}

}

package cassandra;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("scn/{scn_id: [a-z0-9][a-z0-9]*}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Scenario {
	
	private final static String COL_SCENARIOS = "scenarios";
	
	/**
	 * Returns the scenario data based on the scenario id
	 * @param scn_id
	 * @return
	 */
	@GET
	public String getScenario(@PathParam("scn_id") String scn_id) {
		return null;
	}
	
	/**
	 * Scenario update
	 * @param scn_id
	 * @return
	 */
	@PUT
	public String updateScenario(@PathParam("scn_id") String scn_id, String message) {
		return null;
	}
	
	/**
	 * Delete a scenario
	 */
	@DELETE
	public String deleteProject(@PathParam("scn_id") String scn_id) {
		// TODO
		return null;
	}

}

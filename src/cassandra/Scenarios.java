package cassandra;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("scn")
@Produces(MediaType.APPLICATION_JSON)
public class Scenarios {
	
	private final static String COL_SCENARIOS = "scenarios";
	
	/**
	 * Gets the scenarios under a project id
	 * @param message contains the project_id to search the related scenarios
	 * @return
	 */
	@GET
	public String getScenarios(@QueryParam("prj_id") String prj_id) {
		System.out.println(prj_id);
		return null;
	}
	
	/**
	 * Create a scenario
	 */
	@POST
	public String createScenario(String message) {
		
		return null;
	}

}

package cassandra;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("smp")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimulationParams {
	
	/**
	 * Create a simulation parameters
	 */
	@POST
	public String createSimulationParam(String message) {
		
		return null;
	}

}

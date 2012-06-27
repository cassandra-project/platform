package cassandra;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("smp/{smp_id: [a-z0-9][a-z0-9]*}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimulationParam {
	
	/**
	 * Returns the simulation param data based on the simulation param id
	 * @param smp_id
	 * @return
	 */
	@GET
	public String getSimulationParam(@PathParam("smp_id") String smp_id) {
		System.out.println(smp_id);
		return null;
	}
	
	/**
	 * Simulation param update
	 * @param smp_id
	 * @return
	 */
	@PUT
	public String updateSimulationParam(@PathParam("smp_id") String smp_id, String message) {
		System.out.println(smp_id + " | " + message);
		return null;
	}
	
	/**
	 * Delete a simulation param
	 */
	@DELETE
	public String deleteSimulationParam(@PathParam("smp_id") String smp_id) {
		// TODO
		return null;
	}

}

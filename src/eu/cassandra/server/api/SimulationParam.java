package eu.cassandra.server.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.cassandra.server.mongo.MongoSimParam;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

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
		return PrettyJSONPrinter.prettyPrint(new MongoSimParam().getSimParam(smp_id));
	}
	
	/**
	 * Simulation param update
	 * @param smp_id
	 * @return
	 */
	@PUT
	public String updateSimulationParam(@PathParam("smp_id") String smp_id, String message) {
		return PrettyJSONPrinter.prettyPrint(new MongoSimParam().updateSimParam(smp_id,message));
	}
	
	/**
	 * Delete a simulation param
	 */
	@DELETE
	public String deleteSimulationParam(@PathParam("smp_id") String smp_id) {
		return PrettyJSONPrinter.prettyPrint(new MongoSimParam().deleteSimParam(smp_id));
	}

}

package cassandra;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import cassandra.mongo.MongoSimParam;
import cassandra.mongo.util.PrettyJSONPrinter;

@Path("smp")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimulationParams {

	/**
	 * curl -i --data  @simparam.json    --header Content-type:application/json http://localhost:8080/cassandra/smp
	 * 
	 * Create simulation parameters
	 */
	@POST
	public String createSimulationParam(String message) {
		return PrettyJSONPrinter.prettyPrint(new MongoSimParam().createSimParam(message));
	}

	/**
	 * curl -i http://localhost:8080/cassandra/smp?scn_id=4fed8675e4b019410d75e577
	 * 
	 * @param smp_id
	 * @return
	 */
	@GET
	public String getSimulationParam(@QueryParam("scn_id") String scn_id) {
		return PrettyJSONPrinter.prettyPrint(new MongoSimParam().getSimParams(scn_id));
	}

	/**
	 * 
	 * @param smp_id
	 * @param message
	 * @return
	 */
	@PUT
	public String updateSimulationParam(@PathParam("smp_id") String smp_id, String message) {
		return  PrettyJSONPrinter.prettyPrint(new MongoSimParam().updateSimParam(smp_id,message));
	}

}

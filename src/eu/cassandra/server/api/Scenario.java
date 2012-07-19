package eu.cassandra.server.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.cassandra.server.mongo.MongoScenarios;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

@Path("scn/{scn_id: [a-z0-9][a-z0-9]*}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Scenario {

	/**
	 * 
	 * Returns the scenario data based on the scenario id
	 * @param scn_id
	 * @return
	 */
	@GET
	public String getScenario(@PathParam("scn_id") String scn_id) {
		return PrettyJSONPrinter.prettyPrint(new MongoScenarios().getScenario(scn_id));
	}

	/**
	 * 
	 * Scenario update
	 * @param scn_id
	 * @return
	 */
	@PUT
	public String updateScenario(@PathParam("scn_id") String scn_id, String message) {
		return  PrettyJSONPrinter.prettyPrint(new MongoScenarios().updateScenario(scn_id,message));
	}

	/**
	 * Delete a scenario
	 */
	@DELETE
	public String deleteScenario(@PathParam("scn_id") String scn_id) {
		// TODO delete references
		return PrettyJSONPrinter.prettyPrint(new MongoScenarios().deleteScenario(scn_id));
	}

}

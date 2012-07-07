package eu.cassandra.server.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.cassandra.server.mongo.MongoActivityModels;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

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
		return PrettyJSONPrinter.prettyPrint(new MongoActivityModels().getActivityModel(actmod_id));
	}

	/**
	 * Scenario update
	 * @param scn_id
	 * @return
	 */
	@PUT
	public String updateActivityModel(@PathParam("actmod_id") String actmod_id, String message) {
		return PrettyJSONPrinter.prettyPrint(new MongoActivityModels().updateActivityModel(actmod_id,message));
	}

	/**
	 * Delete a scenario
	 */
	@DELETE
	public String deleteActivityModel(@PathParam("actmod_id") String actmod_id) {
		// TODO remove references
		return PrettyJSONPrinter.prettyPrint(new MongoActivityModels().deleteActivityModel(actmod_id));
	}

}

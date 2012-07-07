package eu.cassandra.server.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.cassandra.server.mongo.MongoActivities;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

@Path("act/{act_id: [a-z0-9][a-z0-9]*}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Activity {

	/**
	 * Returns an activity data based on the activity id
	 * @param scn_id
	 * @return
	 */
	@GET
	public String getActivity(@PathParam("act_id") String act_id) {
		return PrettyJSONPrinter.prettyPrint(new MongoActivities().getActivity(act_id));
	}

	/**
	 * Activity update
	 * @param scn_id
	 * @return
	 */
	@PUT
	public String updateActivity(@PathParam("act_id") String act_id, String message) {
		return  PrettyJSONPrinter.prettyPrint(new MongoActivities().updateActivity(act_id,message));
	}

	/**
	 * Delete an activity
	 */
	@DELETE
	public String deleteActivity(@PathParam("act_id") String act_id) {
		// TODO remove references
		return PrettyJSONPrinter.prettyPrint(new MongoActivities().deleteActivity(act_id));
	}

}

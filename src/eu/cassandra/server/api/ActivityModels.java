package eu.cassandra.server.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import eu.cassandra.server.mongo.MongoActivityModels;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

@Path("actmod")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ActivityModels {

	/**
	 * Create a ActivityModel
	 */
	@POST
	public String createActivityModel(String message) {
		return PrettyJSONPrinter.prettyPrint(new MongoActivityModels().createActivityModel(message));
	}

	/**
	 * 
	 * @param act_id
	 * @return
	 */
	@GET
	public String getActivityModels(@QueryParam("act_id") String act_id) {
		return PrettyJSONPrinter.prettyPrint(new MongoActivityModels().getActivityModels(act_id));
	}


}

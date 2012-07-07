package eu.cassandra.server.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import eu.cassandra.server.mongo.MongoActivities;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

@Path("act")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Activities {


	/**
	 * Gets the activities of an installation
	 * 
	 * @param inst_id
	 * @return
	 */
	@GET
	public String getActivities(@QueryParam("pers_id") String pers_id) {
		return PrettyJSONPrinter.prettyPrint(new MongoActivities().getActivities(pers_id));
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	@POST
	public String createActivity(String message) {
		return PrettyJSONPrinter.prettyPrint(new MongoActivities().createActivity(message));
	}


}

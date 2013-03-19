package eu.cassandra.server.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.utilities.Utils;

@Path("usr")
@Produces(MediaType.APPLICATION_JSON)
public class User {

	/**
	 * 
	 * @return
	 */
	@GET
	public Response getUser(@Context HttpHeaders httpHeaders) {
		String usr_id = Utils.userChecked(httpHeaders);
		if(usr_id != null) {
			DBObject user = new BasicDBObject();
			user.put("usr_id",usr_id);
			JSONtoReturn jsonMsg = new JSONtoReturn();
			String json = PrettyJSONPrinter.prettyPrint(jsonMsg.createJSON(user, "User retrieved successfully"));
			return Response.ok(json, MediaType.APPLICATION_JSON).build();
		} else {
			JSONtoReturn jsonMsg = new JSONtoReturn();
			String json = PrettyJSONPrinter.prettyPrint(jsonMsg.createJSONError("User and or password do not match", new Exception("User and or password do not match")));
			Response r = Response.status(Response.Status.UNAUTHORIZED).entity(json).build();
			return r; 
		}
	}
	
}

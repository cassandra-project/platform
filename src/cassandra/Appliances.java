package cassandra;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import cassandra.mongo.MongoAppliances;
import cassandra.mongo.util.PrettyJSONPrinter;

@Path("app")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Appliances {

	/**
	 * 
	 * Gets the appliances under an installation
	 * @param message contains the inst_id to search the related installation
	 * @return
	 */
	@GET
	public String getAppliances(@QueryParam("inst_id") String inst_id) {
		return PrettyJSONPrinter.prettyPrint(new MongoAppliances().getAppliances(inst_id));
	}

	/**
	 * Create an appliance
	 */
	@POST
	public String createAppliance(String message) {
		return PrettyJSONPrinter.prettyPrint(new MongoAppliances().createAppliance(message));
	}


}

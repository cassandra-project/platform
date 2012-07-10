package eu.cassandra.server.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import eu.cassandra.server.mongo.MongoConsumptionModels;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

@Path("consmod")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConsumptionModels {

	/**
	 * 
	 * @param message
	 * @return
	 */
	@POST
	public String createConsumptionModel(String message) {
		return PrettyJSONPrinter.prettyPrint(new MongoConsumptionModels().createConsumptionModel(message));
	}

	/**
	 * 
	 * @param app_id
	 * @return
	 */
	@GET
	public String getConsumptionModels(@QueryParam("app_id") String app_id) {
		return PrettyJSONPrinter.prettyPrint(new MongoConsumptionModels().getConsumptionModels(app_id));
	}


}

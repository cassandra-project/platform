package eu.cassandra.server.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import eu.cassandra.server.mongo.MongoDistributions;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

@Path("distr")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class Distributions {
	
	/**
	 * 
	 * @param actmod_id
	 * @return
	 */
	@GET
	public String getDistributions(@QueryParam("actmod_id") String actmod_id) {
		return PrettyJSONPrinter.prettyPrint(new MongoDistributions().getDistributions(actmod_id));
	}
	
	/**
	 * 
	 * @param message
	 * @return
	 */
	@POST
	public String createDistribution(String message) {
		return PrettyJSONPrinter.prettyPrint(new MongoDistributions().createDistribution(message));
	}
}

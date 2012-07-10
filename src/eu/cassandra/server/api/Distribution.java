package eu.cassandra.server.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.cassandra.server.mongo.MongoDistributions;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

@Path("distr/{distr_id: [a-z0-9][a-z0-9]*}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Distribution {

	/**
	 * 
	 * @param distr_id
	 * @return
	 */
	@GET
	public String getDistribution(@PathParam("distr_id") String distr_id) {
		return PrettyJSONPrinter.prettyPrint(new MongoDistributions().getDistribution(distr_id));
	}

	/**
	 * 
	 * @param distr_id
	 * @param message
	 * @return
	 */
	@PUT
	public String updateDistribution(@PathParam("distr_id") String distr_id, String message) {
		return  PrettyJSONPrinter.prettyPrint(new MongoDistributions().updateDistribution(distr_id,message));
	}

	/**
	 * 
	 * @param distr_id
	 * @return
	 */
	@DELETE
	public String deleteDistribution(@PathParam("distr_id") String distr_id) {
		// TODO remove references
		return PrettyJSONPrinter.prettyPrint(new MongoDistributions().deleteDistribution(distr_id));
	}
}

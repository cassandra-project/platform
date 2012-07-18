package eu.cassandra.server.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import eu.cassandra.server.mongo.MongoInstallations;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

@Path("inst")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Installations {

	/**
	 * 
	 * Gets the installations under a scenario id
	 * @param message contains the scenario_id to search the related scenarios
	 * @return
	 */
	@GET
	public String getInstallations(
			@QueryParam("scn_id") String scn_id,
			@QueryParam("filter") String filters,
			@QueryParam("sort") String sort,
			@QueryParam("limit") int limit,
			@QueryParam("skip") int skip) {
		return PrettyJSONPrinter.prettyPrint(new MongoInstallations().
				getInstallations(scn_id,filters,sort, limit,skip));
	}

	/**
	 * 
	 * Create a installation
	 */
	@POST
	public String createInstallation(String message) {
		return PrettyJSONPrinter.prettyPrint(new MongoInstallations().createInstallation(message));
	}

}

package eu.cassandra.server.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import eu.cassandra.server.mongo.MongoDemographics;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

@Path("demog")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Demographics {

	@GET
	public String getDemographics(@QueryParam("scn_id") String scn_id) {
		return PrettyJSONPrinter.prettyPrint(new MongoDemographics().getDemographics(scn_id));
	}

	@POST
	public String createDemographic(String message) {
		return PrettyJSONPrinter.prettyPrint(new MongoDemographics().createDemographic(message));
	}

}

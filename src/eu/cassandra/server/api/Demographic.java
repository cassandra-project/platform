package eu.cassandra.server.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.cassandra.server.mongo.MongoDemographics;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

@Path("demog/{demog_id: [a-z0-9][a-z0-9]*}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Demographic {

	@GET
	public String getDemographic(@PathParam("demog_id") String demog_id) {
		return PrettyJSONPrinter.prettyPrint(new MongoDemographics().getDemographic(demog_id));
	}

	@PUT
	public String updateDemographic(@PathParam("demog_id") String demog_id, String message) {
		return  PrettyJSONPrinter.prettyPrint(new MongoDemographics().updateDemographics(demog_id,message));
	}

	@DELETE
	public String deleteDemographic(@PathParam("demog_id") String demog_id) {
		// TODO delete references
		return PrettyJSONPrinter.prettyPrint(new MongoDemographics().deleteDemographics(demog_id));
	}

}

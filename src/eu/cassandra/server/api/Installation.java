package cassandra;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cassandra.mongo.MongoInstallations;
import cassandra.mongo.util.PrettyJSONPrinter;

@Path("inst/{inst_id: [a-z0-9][a-z0-9]*}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Installation {

	/**
	 * 
	 * Returns the scenario data based on the scenario id
	 * @param scn_id
	 * @return
	 */
	@GET
	public String getInstallation(@PathParam("inst_id") String inst_id) {
		return PrettyJSONPrinter.prettyPrint(new MongoInstallations().getInstallation(inst_id));
	}

	/**
	 * 
	 * Scenario update
	 * @param scn_id
	 * @return
	 */
	@PUT
	public String updateInstallation(@PathParam("inst_id") String inst_id, String message) {
		return  PrettyJSONPrinter.prettyPrint(new MongoInstallations().updateInstallation(inst_id,message));
	}

	/**
	 * Delete a scenario
	 */
	@DELETE
	public String deleteInstallation(@PathParam("inst_id") String inst_id) {
		// TODO delete references
		return PrettyJSONPrinter.prettyPrint(new MongoInstallations().deleteInstallation(inst_id));
	}

}

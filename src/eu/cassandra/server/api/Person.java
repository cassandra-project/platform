package eu.cassandra.server.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.cassandra.server.mongo.MongoPersons;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

@Path("pers/{pers_id: [a-z0-9][a-z0-9]*}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Person {
	/**
	 * Returns a Person based on the Person id
	 * @param scn_id
	 * @return
	 */
	@GET
	public String getPerson(@PathParam("pers_id") String pers_id) {
		return PrettyJSONPrinter.prettyPrint(new MongoPersons().getPerson(pers_id));
	}

	/**
	 * Activity Person
	 * @param scn_id
	 * @return
	 */
	@PUT
	public String updatePerson(@PathParam("pers_id") String pers_id, String message) {
		return  PrettyJSONPrinter.prettyPrint(new MongoPersons().updatePerson(pers_id,message));
	}

	/**
	 * Delete a Person
	 */
	@DELETE
	public String deletePerson(@PathParam("pers_id") String pers_id) {
		// TODO remove references
		return PrettyJSONPrinter.prettyPrint(new MongoPersons().deletePerson(pers_id));
	}
}

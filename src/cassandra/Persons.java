package cassandra;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import cassandra.mongo.MongoPersons;
import cassandra.mongo.util.PrettyJSONPrinter;

@Path("pers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Persons {
	
	/**
	 * 
	 * Gets the Persons under an installation
	 * @param message contains the inst_id to search the related installation
	 * @return
	 */
	@GET
	public String getPersons(@QueryParam("pers_id") String pers_id) {
		return PrettyJSONPrinter.prettyPrint(new MongoPersons().getPersons(pers_id));
	}
	
	/**
	 * Create a Person
	 */
	@POST
	public String create(String message) {
		return PrettyJSONPrinter.prettyPrint(new MongoPersons().createPerson(message));
	}
}

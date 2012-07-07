package cassandra;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cassandra.mongo.MongoDistributions;
import cassandra.mongo.util.PrettyJSONPrinter;

@Path("distr")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class Distributions {
	
	@POST
	public String createDistribution(String message) {
		return PrettyJSONPrinter.prettyPrint(new MongoDistributions().createDistribution(message));
	}
}

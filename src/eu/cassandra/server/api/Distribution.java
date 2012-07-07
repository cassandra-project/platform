package cassandra;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cassandra.mongo.MongoDistributions;
import cassandra.mongo.util.PrettyJSONPrinter;

@Path("distr/{dist_id: [a-z0-9][a-z0-9]*}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Distribution {

	@GET
	public String getDistribution(@PathParam("distr_id") String distr_id) {
		return PrettyJSONPrinter.prettyPrint(new MongoDistributions().getDistribution(distr_id));
	}
}

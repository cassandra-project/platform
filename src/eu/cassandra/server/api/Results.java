package eu.cassandra.server.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import eu.cassandra.server.mongo.util.MongoDBQueries;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

@Path("results")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Results {

	@GET
	public String getResults(
			@QueryParam("run_id") String run_id,
			@QueryParam("inst_id") String inst_id,
			@QueryParam("metric") String metric,
			@QueryParam("aggr_unit") String aggr_unit,
			@QueryParam("from") String from,
			@QueryParam("to") String to) {
		return PrettyJSONPrinter.prettyPrint(new MongoDBQueries().mongoResultQuery(run_id,inst_id,metric,aggr_unit,from,to).toString());
	}
}

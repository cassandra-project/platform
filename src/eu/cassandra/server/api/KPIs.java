package eu.cassandra.server.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import eu.cassandra.server.mongo.util.MongoDBQueries;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

@Path("kpis")
@Produces(MediaType.APPLICATION_JSON)
public class KPIs {

	@GET
	public String getResults(
			@QueryParam("inst_id") String inst_id,
			@Context HttpHeaders httpHeaders) {
		return PrettyJSONPrinter.prettyPrint(new MongoDBQueries().mongoKPIsQuery(httpHeaders,inst_id).toString());
	}
	
}

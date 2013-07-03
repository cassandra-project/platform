package eu.cassandra.server.api.csn;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import eu.cassandra.server.mongo.csn.MongoGraphs;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.utilities.Utils;

@Path("csnnodes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CSNNodes {

	/**
	 * curl -k -i  --header dbname:51c34c7a712efe578ab670f6 'https://localhost:8443/cassandra/api/csnnodes?graph_id=51d13998e4b0ebea2a3e81f6'
	 * curl -k -i  --header dbname:51c34c7a712efe578ab670f6 'https://localhost:8443/cassandra/api/csnnodes?graph_id=51d13998e4b0ebea2a3e81f6&limit=200&skip=100&filter=\{sumP:\{$gt:1000\}\}'
	 * @param graph_id
	 * @param httpHeaders
	 * @return
	 */
	@GET
	public Response getNodes(
			@QueryParam("graph_id") String graph_id, 
			@QueryParam("filter") String filters,
			@QueryParam("limit") int limit,
			@QueryParam("skip") int skip,
			@QueryParam("count") boolean count,
			@Context HttpHeaders httpHeaders) 
	{
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new MongoGraphs().getNodes(graph_id, httpHeaders,filters, 
				limit, skip, count)));
	}
}

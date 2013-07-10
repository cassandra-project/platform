package eu.cassandra.server.api.csn;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import eu.cassandra.server.mongo.csn.MongoGraphs;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.utilities.Utils;


@Path("csnedges/{csnedges_id: [a-z0-9][a-z0-9]*}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CSNEdge {

	//curl -k -i    --header Content-type:application/json --header dbname:51c34c7a712efe578ab670f6 'https://localhost:8443/cassandra/api/csnedges/51d1399be4b0ebea2a3e822b'
	@GET
	public Response getEdges(
			@PathParam("csnedges_id") String edge_id,
			@Context HttpHeaders httpHeaders){
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new MongoGraphs().getEdge(edge_id,httpHeaders)));

	}

}

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
import eu.cassandra.server.mongo.util.MongoDBQueries;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.utilities.Utils;

@Path("csnclusterofnode/{csnnode_id: [a-z0-9][a-z0-9]*}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CSNClusterOfNode {


	@GET
	public Response getClusterID(@QueryParam("csnnode_id") @Context HttpHeaders httpHeaders){
		String r = PrettyJSONPrinter.prettyPrint(
				new MongoDBQueries().getEntity(httpHeaders, MongoGraphs.COL_CSN_CLUSTERS,null,null,
						"CSN clusters retrieved successfully",new String[]{"_id","method","graph_id","n"}));
		return Utils.returnResponse(r);
	}

}

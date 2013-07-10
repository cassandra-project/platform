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
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.MongoDBQueries;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.utilities.Utils;

@Path("csnclusterofnode")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CSNClusterOfNode {

	//curl -k -i    --header Content-type:application/json --header dbname:51c34c7a712efe578ab670f6 'https://localhost:8443/cassandra/api/csnclusterofnode?csnnode_id=51dd2b65e4b06c9eb5702d09&clusters_id=51dd2e10e4b080a6f1ea30cd'
	@GET
	public Response getClusterID(@QueryParam("csnnode_id") String csnnode_id, @QueryParam("clusters_id") String clusters_id, @Context HttpHeaders httpHeaders){
		if(csnnode_id != null ) {
			String filters = null;
			if(clusters_id != null)
				filters = "{\"clustersid\":\"" + clusters_id + "\"}";
			String r = PrettyJSONPrinter.prettyPrint(
					new MongoDBQueries().getEntity(httpHeaders, MongoGraphs.COL_CSN_NODES2CLUSTERS,"node_id",csnnode_id,filters,null,0, 0, 
							"CSN cluster of node retrieved successfully",false,(String[])null));
			return Utils.returnResponse(r);
		}else {
			return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new JSONtoReturn().createJSONError("Error csnnode_id is null", new NullPointerException())));
		}
	}

}

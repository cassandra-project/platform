package eu.cassandra.server.api.csn;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import eu.cassandra.server.mongo.csn.MongoCluster;
import eu.cassandra.server.mongo.csn.MongoGraphs;
import eu.cassandra.server.mongo.util.MongoDBQueries;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.utilities.Utils;

@Path("csnclusters")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CSNClusters {

	//curl -k -i --data  @clusterparams.json    --header Content-type:application/json --header dbname:51c34c7a712efe578ab670f6 https://localhost:8443/cassandra/api/csnclusters
	@POST
	public Response clusterGraph(String message,@Context HttpHeaders httpHeaders) {
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new MongoCluster().cluster(message, httpHeaders)));
	}

	//curl -k -i    --header Content-type:application/json --header dbname:51c34c7a712efe578ab670f6 https://localhost:8443/cassandra/api/csnclusters
	@GET
	public Response getClusters(@Context HttpHeaders httpHeaders){
		String r = PrettyJSONPrinter.prettyPrint(
				new MongoDBQueries().getEntity(httpHeaders, MongoGraphs.COL_CSN_CLUSTERS,null,null,
						"CSN clusters retrieved successfully",new String[]{"_id","method","graph_id","n"}));
		return Utils.returnResponse(r);
	}
}

package eu.cassandra.server.api.csn;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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

	//curl -k -i --data  @clusterparams.json    --header Content-type:application/json https://localhost:8443/cassandra/api/csnclusters
	@POST
	public Response clusterGraph(String message) {
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new MongoCluster().cluster(message)));
	}

	//curl -k -i    --header Content-type:application/json  'https://localhost:8443/cassandra/api/csnclusters?run_id=f'
	@GET
	public Response getClusters(@QueryParam("run_id") String run_id){
		String run_idK = null;
		if(run_id != null)
			run_idK = "run_id";
		String r = PrettyJSONPrinter.prettyPrint(
				new MongoDBQueries().getEntity((HttpHeaders)null, MongoGraphs.COL_CSN_CLUSTERS,run_idK,run_id,
						"CSN clusters retrieved successfully",new String[]{"_id","method","graph_id","n"}));
		return Utils.returnResponse(r);
	}
}

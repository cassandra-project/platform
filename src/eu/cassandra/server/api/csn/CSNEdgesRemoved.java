package eu.cassandra.server.api.csn;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import eu.cassandra.server.mongo.csn.MongoGraphs;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.utilities.Utils;

@Path("csnedgesremoved/{clusters_id: [a-z0-9][a-z0-9]*}")
public class CSNEdgesRemoved {
	
	//curl -k -i    --header Content-type:application/json  'https://localhost:8443/cassandra/api/csnedgesremoved/51dd99ece4b00484089d9f5d'
	@GET
	public Response getEdgesRemoved(
			@PathParam("clusters_id") String clustersid) 
	{
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new MongoGraphs().getEdgesRemoved(clustersid)));
	}
}

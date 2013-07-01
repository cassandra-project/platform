package eu.cassandra.server.api.csn;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

@Path("csn")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CSNGraphs {

	//curl -k -i --data  @graph.json    --header Content-type:application/json --header dbname:51c34c7a712efe578ab670f6 https://localhost:8443/cassandra/api/csn
	@POST
	public Response createGraph(String message,@Context HttpHeaders httpHeaders) {
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new MongoGraphs().createGraph(message,httpHeaders)));
	}
	//curl -k -i    --header Content-type:application/json --header dbname:51c34c7a712efe578ab670f6 https://localhost:8443/cassandra/api/csn
	@GET
	public Response getGraphs(@Context HttpHeaders httpHeaders){
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new MongoGraphs().getGraphs(httpHeaders)));
	}


	@DELETE
	public Response deleteGraph(@PathParam("graph_id") String graph_id,@Context HttpHeaders httpHeaders) {
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new MongoGraphs().deleteGraph(graph_id,httpHeaders)));
	}
}

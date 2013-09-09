package eu.cassandra.server.api.csn;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import eu.cassandra.server.mongo.csn.MongoGraphs;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.utilities.Utils;

@Path("csn")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CSNGraphs {

	//curl -k -i --data  @graph.json    --header Content-type:application/json  https://localhost:8443/cassandra/api/csn
	@POST
	public Response createGraph(String message) {
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new MongoGraphs().createGraph(message)));
	}
	
	//curl -k -i    --header Content-type:application/json  https://localhost:8443/cassandra/api/csn?run_id=f
	@GET
	public Response getGraphs(@QueryParam("run_id") String run_id, @QueryParam("prj_id") String prj_id){
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new MongoGraphs().getGraphs(run_id,prj_id)));
	}
}

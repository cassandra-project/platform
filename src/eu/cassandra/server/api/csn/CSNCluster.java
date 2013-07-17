package eu.cassandra.server.api.csn;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.cassandra.server.mongo.csn.MongoGraphs;
import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.MongoDBQueries;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.utilities.Utils;


@Path("csnclusters/{csncluster_id: [a-z0-9][a-z0-9]*}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CSNCluster {

	//curl -k -i    --header Content-type:application/json --header dbname:51c34c7a712efe578ab670f6 'https://localhost:8443/cassandra/api/csnclusters/51da9206e4b0def72b1e65b8'
	@GET
	public Response getCluster(
			@PathParam("csncluster_id") String csncluster_id,
			@Context HttpHeaders httpHeaders){
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(
				new MongoDBQueries().getEntity(httpHeaders,MongoGraphs.COL_CSN_CLUSTERS,"_id",csncluster_id,"CSN cluster retrieved successfully")));
	}

	//curl -i -k -X DELETE --header dbname:51c34c7a712efe578ab670f6 'https://localhost:8443/cassandra/api/csnclusters/51da9206e4b0def72b1e65b8'
	@DELETE
	public Response deleteCluster(@PathParam("csncluster_id") String csncluster_id,@Context HttpHeaders httpHeaders) {
		JSONtoReturn jSON2Rrn = new JSONtoReturn();
		DBObject objRemoved = null;
		try {
			DBObject deleteQuery = new BasicDBObject("_id", new ObjectId(csncluster_id));
			objRemoved = DBConn.getConn(MongoDBQueries.getDbNameFromHTTPHeader(httpHeaders)).getCollection(MongoGraphs.COL_CSN_CLUSTERS).findAndRemove(deleteQuery);
		}catch(Exception e) {
			return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(jSON2Rrn.createJSONError("remove db." + MongoGraphs.COL_CSN_CLUSTERS + " with id=" + csncluster_id,e)));
		}
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(jSON2Rrn.createJSONRemovePostMessage(MongoGraphs.COL_CSN_CLUSTERS,csncluster_id,objRemoved)));
	}
}

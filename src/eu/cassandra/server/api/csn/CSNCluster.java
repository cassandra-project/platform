/*   
   Copyright 2011-2013 The Cassandra Consortium (cassandra-fp7.eu)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package eu.cassandra.server.api.csn;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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

	//curl -k -i    --header Content-type:application/json 'https://localhost:8443/cassandra/api/csnclusters/51da9206e4b0def72b1e65b8'
	@GET
	public Response getCluster(
			@PathParam("csncluster_id") String csncluster_id){
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(
				new MongoDBQueries().getEntity((HttpHeaders)null,MongoGraphs.COL_CSN_CLUSTERS,"_id",csncluster_id,"CSN cluster retrieved successfully")));
	}

	//curl -i -k -X DELETE  'https://localhost:8443/cassandra/api/csnclusters/51da9206e4b0def72b1e65b8'
	@DELETE
	public Response deleteCluster(@PathParam("csncluster_id") String csncluster_id) {
		JSONtoReturn jSON2Rrn = new JSONtoReturn();
		DBObject objRemoved = null;
		try {
			DBObject deleteQuery = new BasicDBObject("_id", new ObjectId(csncluster_id));
			objRemoved = DBConn.getConn().getCollection(MongoGraphs.COL_CSN_CLUSTERS).findAndRemove(deleteQuery);
		}catch(Exception e) {
			return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(jSON2Rrn.createJSONError("remove db." + MongoGraphs.COL_CSN_CLUSTERS + " with id=" + csncluster_id,e)));
		}
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(jSON2Rrn.createJSONRemovePostMessage(MongoGraphs.COL_CSN_CLUSTERS,csncluster_id,objRemoved)));
	}
}

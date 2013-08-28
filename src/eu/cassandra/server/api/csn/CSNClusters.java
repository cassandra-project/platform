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
	public Response getClusters(@QueryParam("run_id") String run_id,@QueryParam("graph_id") String graph_id){
		if(graph_id != null) {
			String r = PrettyJSONPrinter.prettyPrint(
					new MongoDBQueries().getEntity((HttpHeaders)null, MongoGraphs.COL_CSN_CLUSTERS,"graph_id",graph_id,null,"{\"_id\":-1}",1,0,
							"CSN clusters retrieved successfully",false,(String[])null));//new String[]{"_id","method","graph_id","n"}
			return Utils.returnResponse(r);
		}
		String run_idK = null;
		if(run_id != null)
			run_idK = "run_id";
		String r = PrettyJSONPrinter.prettyPrint(
				new MongoDBQueries().getEntity((HttpHeaders)null, MongoGraphs.COL_CSN_CLUSTERS,run_idK,run_id,
						"CSN clusters retrieved successfully"));//,new String[]{"_id","method","graph_id","n"}
		return Utils.returnResponse(r);
	}
}

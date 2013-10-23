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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import eu.cassandra.server.mongo.csn.MongoGraphs;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.utilities.Utils;


@Path("csnedges/{csnedges_id: [a-z0-9][a-z0-9]*}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CSNEdge {

	//curl -k -i    --header Content-type:application/json 'https://localhost:8443/cassandra/api/csnedges/51d1399be4b0ebea2a3e822b'
	@GET
	public Response getEdges(
			@PathParam("csnedges_id") String edge_id){
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new MongoGraphs().getEdge(edge_id)));

	}

}

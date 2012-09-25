/*   
   Copyright 2011-2012 The Cassandra Consortium (cassandra-fp7.eu)


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
package eu.cassandra.server.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import eu.cassandra.server.mongo.MongoSimParam;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

@Path("smp")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimulationParams {

	/**
	 * curl -i --data  @simparam.json    --header Content-type:application/json http://localhost:8080/cassandra/smp
	 * 
	 * Create simulation parameters
	 */
	@POST
	public String createSimulationParam(String message) {
		return PrettyJSONPrinter.prettyPrint(new MongoSimParam().createSimParam(message));
	}

	/**
	 * curl -i http://localhost:8080/cassandra/api/smp?scn_id=4fed8675e4b019410d75e577
	 * 
	 * @param smp_id
	 * @return
	 */
	@GET
	public String getSimulationParam(@QueryParam("scn_id") String scn_id, @QueryParam("count") boolean count) {
		return PrettyJSONPrinter.prettyPrint(new MongoSimParam().getSimParams(scn_id,count));
	}

	/**
	 * 
	 * @param smp_id
	 * @param message
	 * @return
	 */
	@PUT
	public String updateSimulationParam(@PathParam("smp_id") String smp_id, String message) {
		return  PrettyJSONPrinter.prettyPrint(new MongoSimParam().updateSimParam(smp_id,message));
	}

}

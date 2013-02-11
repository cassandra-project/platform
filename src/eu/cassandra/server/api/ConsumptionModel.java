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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import eu.cassandra.server.mongo.MongoConsumptionModels;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

@Path("consmod/{consmod_id: [a-z0-9][a-z0-9]*}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConsumptionModel {

	/**
	 * 
	 * @param consmod_id
	 * @return
	 */
	@GET
	public String getConsumptionModel(@PathParam("consmod_id") String consmod_id,
			@Context HttpHeaders httpHeaders) {
		return PrettyJSONPrinter.prettyPrint(new MongoConsumptionModels().getConsumptionModel(httpHeaders,consmod_id));
	}

	/**
	 * 
	 * @param consmod_id
	 * @param message
	 * @return
	 */
	@PUT
	public String updateConsumptionModel(@PathParam("consmod_id") String consmod_id, String message) {
		return PrettyJSONPrinter.prettyPrint(new MongoConsumptionModels().updateConsumptionModel(consmod_id,message));
	}

	/**
	 * 
	 * @param consmod_id
	 * @return
	 */
	@DELETE
	public String deleteConsumptionModel(@PathParam("consmod_id") String consmod_id) {
		return PrettyJSONPrinter.prettyPrint(new MongoConsumptionModels().deleteConsumptionModel(consmod_id));
	}

}

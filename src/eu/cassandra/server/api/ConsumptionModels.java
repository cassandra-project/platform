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
package eu.cassandra.server.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import eu.cassandra.server.mongo.MongoConsumptionModels;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.utilities.Utils;

@Path("consmod")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConsumptionModels {

	/**
	 * 
	 * @param message
	 * @return
	 */
	@POST
	public Response createConsumptionModel(String message) {
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new MongoConsumptionModels().createConsumptionModel(message)));
	}

	/**
	 * 
	 * @param app_id
	 * @return
	 */
	@GET
	public Response getConsumptionModels(@QueryParam("app_id") String app_id, @QueryParam("count") boolean count,
			@Context HttpHeaders httpHeaders) {
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new MongoConsumptionModels().getConsumptionModels(httpHeaders,app_id,count)));
	}


}

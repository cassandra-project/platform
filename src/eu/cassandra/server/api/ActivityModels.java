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

import eu.cassandra.server.mongo.MongoActivityModels;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.utilities.Utils;

@Path("actmod")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ActivityModels {

	/**
	 * Create a ActivityModel
	 */
	@POST
	public Response createActivityModel(String message) {
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new MongoActivityModels().createActivityModel(message)));
	}

	/**
	 * 
	 * @param act_id
	 * @return
	 */
	@GET
	public Response getActivityModels(@QueryParam("act_id") String act_id, @QueryParam("count") boolean count,
			@Context HttpHeaders httpHeaders) {
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new MongoActivityModels().getActivityModels(httpHeaders,act_id,count)));
	}


}

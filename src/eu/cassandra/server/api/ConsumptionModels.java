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
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import eu.cassandra.server.mongo.MongoConsumptionModels;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

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
	public String createConsumptionModel(String message) {
		return PrettyJSONPrinter.prettyPrint(new MongoConsumptionModels().createConsumptionModel(message));
	}

	/**
	 * 
	 * @param app_id
	 * @return
	 */
	@GET
	public String getConsumptionModels(@QueryParam("app_id") String app_id) {
		return PrettyJSONPrinter.prettyPrint(new MongoConsumptionModels().getConsumptionModels(app_id));
	}


}

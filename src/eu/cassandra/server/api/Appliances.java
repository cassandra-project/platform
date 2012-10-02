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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import eu.cassandra.server.mongo.MongoAppliances;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

@Path("app")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Appliances {

	/**
	 * 
	 * Gets the appliances under an installation
	 * @param message contains the inst_id to search the related installation
	 * @return
	 */
	@GET
	public String getAppliances(@QueryParam("inst_id") String inst_id, 
			@QueryParam("actmod_id") String actmod_id, @QueryParam("count") boolean count,
			@Context HttpHeaders httpHeaders) {
		if(actmod_id != null) {
			return PrettyJSONPrinter.prettyPrint(new MongoAppliances().getApplianceFromActivityModel(httpHeaders,actmod_id));
		}
		else
			return PrettyJSONPrinter.prettyPrint(new MongoAppliances().getAppliances(httpHeaders,inst_id, count));
	}

	/**
	 * Create an appliance
	 */
	@POST
	public String createAppliance(String message) {
		return PrettyJSONPrinter.prettyPrint(new MongoAppliances().createAppliance(message));
	}


}

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

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.server.mongo.MongoInstallations;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.utilities.Utils;

@Path("inst")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Installations {

	/**
	 * 
	 * Gets the installations under a scenario id
	 * @param message contains the scenario_id to search the related scenarios
	 * @return
	 */
	@GET
	public Response getInstallations(
			@QueryParam("scn_id") String scn_id,
			@QueryParam("filter") String filters,
			@QueryParam("sort") String sort,
			@QueryParam("limit") int limit,
			@QueryParam("skip") int skip,
			@QueryParam("count") boolean count,
			@QueryParam("pertype") boolean pertype,
			@Context HttpHeaders httpHeaders) {
		String page = new MongoInstallations().
				getInstallations(httpHeaders,scn_id,filters,sort,limit,skip,count,pertype);
		String countResponse = 
				(new MongoInstallations()).getInstallations(httpHeaders,scn_id,null,null,0,0,true,false);
		DBObject jsonResponse = (DBObject) JSON.parse(countResponse);
		BasicDBList list = (BasicDBList)jsonResponse.get("data");
		DBObject object = (DBObject)list.get(0);
		return Utils.returnResponseWithAppend(page, "total_size", (Integer)object.get("count"));
	}

	/**
	 * 
	 * Create a installation
	 */
	@POST
	public Response createInstallation(String message) {
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new MongoInstallations().createInstallation(message)));
	}

}

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

import javax.ws.rs.GET;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.mongodb.DB;

import eu.cassandra.server.mongo.MongoProjects;
import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.utilities.Constants;
import eu.cassandra.sim.utilities.Utils;

@Path("prj")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Projects {
	
	/**
	 * 
	 * @return
	 */
	@GET
	public String getProjects(@QueryParam("count") boolean count,
			@Context HttpHeaders httpHeaders) {
		if(httpHeaders == null || httpHeaders.getRequestHeaders() == null ||
				 httpHeaders.getRequestHeader("Authorization") == null) {
			return Constants.AUTHORIZATION_FAIL;
		}
		DB db = DBConn.getConn();
		if(Utils.authenticate(Utils.extractCredentials(httpHeaders), db)) {
			String username = Utils.extractUsername(Utils.extractCredentials(httpHeaders));
			String usr_id = Utils.getUser(username, db).get("_id").toString();
			return PrettyJSONPrinter.prettyPrint(new MongoProjects().getProjects(httpHeaders, usr_id, count));
		} else {
			return Constants.AUTHORIZATION_FAIL;
		}
			
	}
	
	/**
	 * Create a project
	 */
	@POST
	public String createProject(String message) {
		return PrettyJSONPrinter.prettyPrint(new MongoProjects().createProject(message));
	}

}

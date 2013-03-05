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

import javax.ws.rs.GET;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mongodb.DB;

import eu.cassandra.server.mongo.MongoProjects;
import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.utilities.Constants;
import eu.cassandra.sim.utilities.Utils;

@Path("prj")
public class Projects {
	
	/**
	 * 
	 * @return
	 */
	@GET
	public Response getProjects(@QueryParam("count") boolean count,
			@Context HttpHeaders httpHeaders) {
		String usr_id = Utils.userChecked(httpHeaders);
		if(usr_id != null) {
			String json = PrettyJSONPrinter.prettyPrint(new MongoProjects().getProjects(httpHeaders, usr_id, count));
			return Response.ok(json, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.UNAUTHORIZED).entity("User and or password do not match").build();
		}
	}
	
	/**
	 * Create a project
	 */
	@POST
	public Response createProject(String message, @Context HttpHeaders httpHeaders) {
		String usr_id = Utils.userChecked(httpHeaders);
		if(usr_id != null) {
			String patchedMessage = message;
			try {
				patchedMessage = Utils.inject(message, "usr_id", usr_id);
			}catch(com.mongodb.util.JSONParseException e) {
				//JSONtoReturn jSON2Rrn = new JSONtoReturn();
				//PrettyJSONPrinter.prettyPrint(jSON2Rrn.createJSONError("Error parsing JSON input", e.getMessage()));
				Response.status(Response.Status.BAD_REQUEST).entity("Error parsing JSON input - " + e.getMessage()).build();
			}catch(Exception e) {
				//JSONtoReturn jSON2Rrn = new JSONtoReturn();
				//PrettyJSONPrinter.prettyPrint(jSON2Rrn.createJSONError(patchedMessage, e));
				Response.status(Response.Status.BAD_REQUEST).entity(patchedMessage + " - " + e.getMessage()).build();
			}
			String json = PrettyJSONPrinter.prettyPrint(new MongoProjects().createProject(patchedMessage));
			return Response.ok(json, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.UNAUTHORIZED).entity("User and or password do not match").build();
		}
	}

}

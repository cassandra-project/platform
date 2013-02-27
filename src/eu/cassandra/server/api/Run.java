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

import java.util.HashMap;
import java.util.concurrent.Future;

import javax.servlet.ServletContext;
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

import eu.cassandra.server.mongo.MongoRuns;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

@Path("runs/{run_id: [a-z0-9][a-z0-9]*}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Run {
	
	@javax.ws.rs.core.Context 
	ServletContext context;
	
	/**
	 * 
	 * Returns the run data based on the run id
	 * @param run_id
	 * @return
	 */
	@GET
	public String getRun(@PathParam("run_id") String run_id, @Context HttpHeaders httpHeaders) {
		return PrettyJSONPrinter.prettyPrint(new MongoRuns().getRun(httpHeaders,run_id));
	}
	
	/**
	 * 
	 * Run update, if paused, resume and if resumed, pause.
	 * @param run_id
	 * @return
	 */
	@PUT
	public String updateRun(@PathParam("run_id") String run_id) {
		HashMap<String,Future<?>> runs = (HashMap<String,Future<?>>)context.getAttribute("My_RUNS");
		if(runs.containsKey(run_id)) {
			Future<?> future = runs.get(run_id);
			if(future.isDone()) {
				return "{\"success\":false, \"message\":\"Run " + run_id + " finished\"}";
			} else {
				future.cancel(true);
				return "{\"success\":true, \"message\":\"Run " + run_id + " cancelled\"}";
			}
		} else {
			return "{\"success\":false, \"message\":\"Run " + run_id + " not found in running threads\"}";
		}
		// check if paused or resumed
		//String message = null;
		//return  PrettyJSONPrinter.prettyPrint(new MongoRuns().updateRun(run_id, message));
		
	}

	/**
	 * Delete a run
	 */
	@DELETE
	public String deleteRun(@PathParam("run_id") String run_id) {
		// TODO delete references
		return PrettyJSONPrinter.prettyPrint(new MongoRuns().deleteRun(run_id));
	}

}

package eu.cassandra.server.api;

import javax.ws.rs.Consumes;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.cassandra.server.mongo.MongoRuns;
import eu.cassandra.server.mongo.MongoScenarios;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

@Path("runs/{run_id: [a-z0-9][a-z0-9]*}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Run {
	
	/**
	 * 
	 * Returns the run data based on the run id
	 * @param run_id
	 * @return
	 */
	@GET
	public String getRun(@PathParam("run_id") String run_id) {
		return PrettyJSONPrinter.prettyPrint(new MongoRuns().getRun(run_id));
	}
	
	/**
	 * 
	 * Run update, if paused, resume and if resumed, pause.
	 * @param run_id
	 * @return
	 */
	@PUT
	public String updateRun(@PathParam("run_id") String run_id) {
		// check if paused or resumed
		String message = null;
		return  PrettyJSONPrinter.prettyPrint(new MongoRuns().updateRun(run_id, message));
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

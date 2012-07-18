package eu.cassandra.server.api;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import eu.cassandra.server.mongo.MongoRuns;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.server.threads.DemoThread;
import eu.cassandra.sim.Simulation;

@Path("runs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Runs {
	
	@javax.ws.rs.core.Context 
	ServletContext context;
	
	/**
	 * 
	 * Gets the scenarios under a project id
	 * @param message contains the project_id to search the related scenarios
	 * @return
	 */
	@GET
	public String getRuns(@QueryParam("prj_id") String prj_id) {
		return PrettyJSONPrinter.prettyPrint(new MongoRuns().getRuns(prj_id));
	}
	
	/**
	 * Create a run.
	 * In order to create a run we need to have the simulation parameter id.
	 * Now the procedure works as follows:
	 * <ol>
	 * <i>smp_id => simulation parameters JSON</i>
	 * <i>Simulation parameters JSON => scn_id</i>
	 * <i>scn_id => gather all scenario data and create a full JSON scenario</i>
	 * <i>store the full scenario in the run JSON</i>
	 * <i>Create thread with JSON scenario and storage contact point</i>
	 * <i>Run the thread</i>
	 * </ol>
	 *
	 */
	@POST
	public String createRun(String message) {
		Simulation sim = new Simulation(message);
		try {
			sim.setup();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Sim creation failed";
		}
		ExecutorService executor = (ExecutorService )context.getAttribute("MY_EXECUTOR");
		executor.submit(sim);
		return "Simulation Submitted\n";
//		return PrettyJSONPrinter.prettyPrint(new MongoRuns().createRun(message));
	}

}

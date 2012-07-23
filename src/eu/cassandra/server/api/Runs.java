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

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.server.mongo.MongoRuns;
import eu.cassandra.server.mongo.MongoScenarios;
import eu.cassandra.server.mongo.MongoSimParam;
import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.MongoDBQueries;
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
		DBObject scenario = new BasicDBObject();
		// Parse message to db object
		System.out.println(message);
		DBObject jsonMessage = (DBObject) JSON.parse(message);
		String smp_id =  (String)jsonMessage.get("smp_id");
		System.out.println(smp_id);
		DBObject query = new BasicDBObject(); 
		query.put("sim_param.cid", new ObjectId(smp_id));
		DBObject result = DBConn.getConn().getCollection(MongoScenarios.COL_SCENARIOS).findOne(query);
		System.out.println(result.toString());
		DBObject simParams = (DBObject) result.get("sim_param");
		System.out.println(simParams.toString());
		String scenario_id = (String) simParams.get("scn_id");
		System.out.println(scenario_id);
		
		// Add installations
		// Add persons
		// Add activities
		// Add activity models
		// Add appliances
		// Add consumption models
		// Add simulation parameters
		// Add distributions
		
//		Simulation sim = new Simulation(message);
//		try {
//			sim.setup();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return "Sim creation failed";
//		}
//		ExecutorService executor = (ExecutorService )context.getAttribute("MY_EXECUTOR");
//		executor.submit(sim);
		return "Simulation Submitted\n";
//		return PrettyJSONPrinter.prettyPrint(new MongoRuns().createRun(message));
	}

}

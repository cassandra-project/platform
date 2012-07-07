package eu.cassandra.server.api;

import java.util.concurrent.ExecutorService;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


import com.mongodb.util.JSON;
import com.mongodb.DBCursor;

import eu.cassandra.server.mongo.MongoProjects;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.server.threads.DemoThread;

@Path("prj")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Projects {
	
	private final static String COL_PROJECTS = "projects";
	
	@javax.ws.rs.core.Context 
	ServletContext context;
	
	/**
	 * 
	 * @return
	 */
	@GET
	public String getProjects() {
		ExecutorService executor = (ExecutorService )context.getAttribute("MY_EXECUTOR");
		executor.submit(new DemoThread());
		return PrettyJSONPrinter.prettyPrint(new MongoProjects().getProjects(null));
	}
	
	/**
	 * Create a project
	 */
	@POST
	public String createProject(String message) {
		return PrettyJSONPrinter.prettyPrint(new MongoProjects().createProject(message));
	}

}

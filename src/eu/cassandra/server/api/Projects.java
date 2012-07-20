package eu.cassandra.server.api;

import javax.ws.rs.GET;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.cassandra.server.mongo.MongoProjects;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

@Path("prj")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Projects {
	
	/**
	 * 
	 * @return
	 */
	@GET
	public String getProjects() {
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

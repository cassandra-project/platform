package eu.cassandra.server.api;

import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.server.mongo.MongoAppliances;
import eu.cassandra.server.mongo.MongoInstallations;
import eu.cassandra.server.mongo.MongoPersons;
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.utilities.Utils;

@Path("search")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Search {
	
	@GET
	public Response search(
			@QueryParam("scn_id") String scn_id,
			@QueryParam("col") String collection,
			@QueryParam("name") String name,
			@QueryParam("type") String type,
			@QueryParam("description") String desc,
			@Context HttpHeaders httpHeaders) {
		// construct the filters
		DBObject filter = new BasicDBObject();
		if(name != null) {
			Pattern regex = Pattern.compile(name);
			filter.put("name", regex);
		}
		if(type != null) {
			Pattern regex = Pattern.compile(type);
			filter.put("type", regex);
		}
		if(desc != null) {
			Pattern regex = Pattern.compile(desc);
			filter.put("description", regex);
		}
		String strFilter = null;
		if(filter.keySet().size() > 0) {
			strFilter = filter.toString();
		}
		
		// start searching
		String page = new String();
		if(collection == null) {
			return Utils.returnBadRequest("Invalid search request. Collection (col parameter) not specified.");
		}
		switch(collection) {
			case "inst":
				page = new MongoInstallations().
				getInstallations(httpHeaders, scn_id, strFilter, null, 0, 0, false, false);
				String countResponse = 
						(new MongoInstallations())
						.getInstallations(httpHeaders,scn_id,null,null,0,0,true,false);
				DBObject jsonResponse = (DBObject) JSON.parse(countResponse);
				BasicDBList list = (BasicDBList)jsonResponse.get("data");
				DBObject object = (DBObject)list.get(0);
				return Utils.returnResponseWithAppend(page, "total_size", (Integer)object.get("count"));
			case "app":
				return new MongoAppliances().
				getAppliances(httpHeaders, scn_id, strFilter, null, 0, 0, false, false);
			case "pers":
				return new MongoPersons().
						getPersons(httpHeaders, scn_id, strFilter, null, 0, 0, false, false);
			default:
				return Utils.returnBadRequest("Invalid search request. No appropriate collection specified.");
		}
		
	}

}

package eu.cassandra.server.threads;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.Future;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import eu.cassandra.server.mongo.MongoRuns;
import eu.cassandra.server.mongo.util.DBConn;

public class RunsContextListener implements ServletContextListener {
	
	private HashMap<String, Future<?>> runs;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		for(Future<?> f : runs.values()) {
			f.cancel(true);
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		ServletContext context = arg0.getServletContext();
		runs = new HashMap<String, Future<?>>();
		context.setAttribute("MY_RUNS", runs);
		// Delete all unfinished thread from db and their dbs:
		DBObject query = new BasicDBObject();
		query.put("ended", -1);
		DBCursor cursor = DBConn.getConn().getCollection(MongoRuns.COL_RUNS).find(query);
		while(cursor.hasNext()) {
			DBObject run = cursor.next();
			DBConn.getConn().getCollection(MongoRuns.COL_RUNS).remove(run);
			String run_id = run.get("_id").toString();
			Mongo mongo;
			try {
				mongo = new Mongo();
				mongo.dropDatabase(run_id);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (MongoException e) {
				e.printStackTrace();
			}
		}
	}

}

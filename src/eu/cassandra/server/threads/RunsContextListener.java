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

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
package eu.cassandra.server.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.cassandra.server.api.exceptions.MongoRefNotFoundException;
import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.MongoDBQueries;

public class MongoResults {

	public final static String COL_APPRESULTS = "app_results";
	public final static String COL_INSTRESULTS = "inst_results";
	public final static String COL_AGGRRESULTS = "aggr_results";
	
	/**
	 * 
	 * @param app_id
	 * @param openTick
	 * @throws MongoRefNotFoundException
	 */
	public void addOpenTick(String app_id, int openTick) throws MongoRefNotFoundException {
		DBObject refObj = new MongoDBQueries().getEntity(MongoAppliances.COL_APPLIANCES, app_id);
		if(refObj == null) {
			throw new MongoRefNotFoundException("Reference Key: " + 
					app_id + " was not found for " + MongoAppliances.COL_APPLIANCES);
		}
		DBObject data = new BasicDBObject();
		data.put("app_id", app_id);
		data.put("opentick", openTick);
		DBConn.getConn().getCollection(COL_APPRESULTS).insert(data);
	}

	/**
	 * 
	 * @param app_id
	 * @param closeTick
	 */
	public void addCloseTick(String app_id, int closeTick) {
		DBObject q = new BasicDBObject();
		q.put("app_id", app_id);
		DBObject data = new BasicDBObject();
		data.put("$set", new BasicDBObject("closetick", closeTick));
		q.put("closetick:", new BasicDBObject("$exists",false));
		DBConn.getConn().getCollection(COL_APPRESULTS).update(q,data,false,false);
	}

	/**
	 * 
	 * @param tick
	 * @param inst_id
	 * @param p
	 * @param q
	 */
	public void addTickResultForInstallation(int tick,
			String inst_id, double p, double q) {
		DBObject data = new BasicDBObject();
		data.put("inst_id",inst_id);
		data.put("tick",tick);
		data.put("p",p);
		data.put("q",q);
		DBConn.getConn().getCollection(COL_INSTRESULTS).insert(data);
	}

	/**
	 * 
	 * @param tick
	 * @param p
	 * @param q
	 */
	public void addAggregatedTickResult(String runId, int tick, double p, double q) {
		DBObject data = new BasicDBObject();
		data.put("run_id",runId);
		data.put("tick",tick);
		data.put("p",p);
		data.put("q",q);
		DBConn.getConn().getCollection(COL_AGGRRESULTS).insert(data);
	}


//	public static void main(String args[]) {
//		MongoResults m = new MongoResults();
//		m.addAggregatedTickResult("5006a550e4b05ff53eb83fe0",1, 0.1, 2.3);
//		m.addAggregatedTickResult("5006a550e4b05ff53eb83fe0",2, 0.1, 2.3);
//		m.addAggregatedTickResult("5006a550e4b05ff53eb83fe0",3, 0.1, 2.3);
//		m.mongoResultQuery("instID_", MongoResults.ACTIVE_POWER_P, 2, null, null); 
		//		m.addTickResultForInstallation(1,"instID_", 0.1, 2.3);
		//		m.addTickResultForInstallation(2,"instID_", 0.2, 2.3); 
		//		m.addTickResultForInstallation(3,"instID_", 0.3, 2.3); 
		//		m.addTickResultForInstallation(4,"instID_", 0.1, 2.3);
		//		m.addTickResultForInstallation(5,"instID_", 0.2, 2.3); 
		//		m.addTickResultForInstallation(6,"instID_", 0.3, 2.3); 
		//		m.addTickResultForInstallation(7,"instID_", 0.4, 2.3);
		//		m.addTickResultForInstallation(8,"instID_", 0.5, 2.3); 
		//		m.addTickResultForInstallation(9,"instID_", 0.6, 2.3); 
//	}
}

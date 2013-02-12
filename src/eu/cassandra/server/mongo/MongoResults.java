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
	
	private String dbname;
	
	public MongoResults(String adbname) {
 		dbname = adbname;
	}
	
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
		DBConn.getConn(dbname).getCollection(COL_APPRESULTS).insert(data);
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
		DBConn.getConn(dbname).getCollection(COL_APPRESULTS).update(q,data,false,false);
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
		boolean first = false;
		DBObject query = new BasicDBObject();
		query.put("inst_id", inst_id);
		query.put("tick", tick);
		DBObject data = DBConn.getConn(dbname).getCollection(COL_INSTRESULTS).findOne(query);
		double newp = p;
		double newq = q;
		if(data == null) {
			data = new BasicDBObject();
			first = true;
			data.put("inst_id",inst_id);
			data.put("tick",tick);
		} else {
			newp += ((Double)data.get("p")).doubleValue();
			newq += ((Double)data.get("q")).doubleValue();
		}
		data.put("p",newp);
		data.put("q",newq);
		if(first) {
			DBConn.getConn(dbname).getCollection(COL_INSTRESULTS).insert(data);
		} else {
			DBConn.getConn(dbname).getCollection(COL_INSTRESULTS).update(query, data, false, false);
		}
	}
	
	public void normalize(int tick, String inst_id, int divisor) {
		DBObject query = new BasicDBObject();
		query.put("inst_id", inst_id);
		query.put("tick", tick);
		DBObject data = DBConn.getConn(dbname).getCollection(COL_INSTRESULTS).findOne(query);
		double newp = ((Double)data.get("p")).doubleValue() / divisor;
		double newq = ((Double)data.get("q")).doubleValue() / divisor;
		data.put("p",newp);
		data.put("q",newq);
		DBConn.getConn(dbname).getCollection(COL_INSTRESULTS).update(query, data, false, false);
	}

	/**
	 * 
	 * @param tick
	 * @param p
	 * @param q
	 */
	public void addAggregatedTickResult(int tick, double p, double q) {
		boolean first = false;
		DBObject query = new BasicDBObject();
		query.put("tick", tick);
		DBObject data = DBConn.getConn(dbname).getCollection(COL_AGGRRESULTS).findOne(query);
		double newp = p;
		double newq = q;
		if(data == null) {
			data = new BasicDBObject();
			first = true;
			data.put("tick",tick);
		} else {
			newp += ((Double)data.get("p")).doubleValue();
			newq += ((Double)data.get("q")).doubleValue();
			
		}
		data.put("p",newp);
		data.put("q",newq);
		if(first) {
			DBConn.getConn(dbname).getCollection(COL_AGGRRESULTS).insert(data);
		} else {
			DBConn.getConn(dbname).getCollection(COL_AGGRRESULTS).update(query, data, false, false);
		}
	}
	
	public void normalize(int tick, int divisor) {
		DBObject query = new BasicDBObject();
		query.put("tick", tick);
		DBObject data = DBConn.getConn(dbname).getCollection(COL_AGGRRESULTS).findOne(query);
		double newp = ((Double)data.get("p")).doubleValue() / divisor;
		double newq = ((Double)data.get("q")).doubleValue() / divisor;
		data.put("p",newp);
		data.put("q",newq);
		DBConn.getConn(dbname).getCollection(COL_AGGRRESULTS).update(query, data, false, false);
	}
}

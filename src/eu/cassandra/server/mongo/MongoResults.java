/*   
   Copyright 2011-2013 The Cassandra Consortium (cassandra-fp7.eu)


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
	public final static String COL_APPRESULTS_EXP = "app_expected";
	public final static String COL_ACTRESULTS_EXP = "act_expected";
	public final static String COL_INSTRESULTS = "inst_results";
	public final static String COL_INSTRESULTS_EXP = "inst_expected";
	public final static String COL_INSTRESULTS_HOURLY = "inst_results_hourly";
	public final static String COL_INSTRESULTS_HOURLY_EN = "inst_results_hourly_energy";
	public final static String COL_AGGRRESULTS = "aggr_results";
	public final static String COL_AGGRRESULTS_EXP = "aggr_expected";
	public final static String COL_AGGRRESULTS_HOURLY = "aggr_results_hourly";
	public final static String COL_AGGRRESULTS_HOURLY_EN = "aggr_results_hourly_energy";
	public final static String COL_INSTKPIS = "inst_kpis";
	public final static String COL_APPKPIS = "app_kpis";
	public final static String COL_ACTKPIS = "act_kpis";
	public final static String COL_AGGRKPIS = "aggr_kpis";
	public final static String AGGR = "aggr";
	
	
	
	private String dbname;
	
	public MongoResults(String adbname) {
 		dbname = adbname;
	}
	
	public void createIndexes() {
		DBObject index = new BasicDBObject("tick", 1);
		DBConn.getConn(dbname).getCollection(COL_AGGRRESULTS).createIndex(index);
		index = new BasicDBObject("tick", 1);
		DBConn.getConn(dbname).getCollection(COL_AGGRRESULTS_HOURLY).createIndex(index);
		index = new BasicDBObject("inst_id", 1);
		index.put("tick", 1);
		DBConn.getConn(dbname).getCollection(COL_INSTRESULTS).createIndex(index);
		index = new BasicDBObject("inst_id", 1);
		index.put("tick", 1);
		DBConn.getConn(dbname).getCollection(COL_INSTRESULTS_HOURLY).createIndex(index);
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
	 * @param inst_id
	 * @param maxPower
	 * @param avgPower
	 * @param energy
	 */
	public void addKPIs(String inst_id, double maxPower, double avgPower, double energy, double cost, double co2) {
		boolean first = false;
		DBObject query = new BasicDBObject();
		String collection;
		String tick_collection;
		String id;
		DBObject order = new BasicDBObject();
		order.put("p", -1);
		if(inst_id.equalsIgnoreCase(AGGR)) {
			id = AGGR;
			collection = COL_AGGRKPIS;
			tick_collection = COL_AGGRRESULTS;
		} else {
			id = inst_id;
			collection = COL_INSTKPIS;
			tick_collection = COL_INSTRESULTS;
		}
		query.put("inst_id", id);
		DBObject data = DBConn.getConn(dbname).getCollection(collection).findOne(query);
		double newMaxPower = maxPower;
		double newAvgPower = avgPower;
		double newEnergy = energy;
		double newCost = cost;
		double newCo2 = co2;
		if(data == null) {
			data = new BasicDBObject();
			first = true;
			data.put("inst_id", id);
		} else {
			newMaxPower += ((Double)data.get("maxPower")).doubleValue();
			newAvgPower += ((Double)data.get("avgPower")).doubleValue();
			newEnergy += ((Double)data.get("energy")).doubleValue();
			newCost += ((Double)data.get("cost")).doubleValue();
			newCo2 += ((Double)data.get("co2")).doubleValue();
		}
		DBObject maxavg = null;
		if(inst_id.equalsIgnoreCase(AGGR)) {
			maxavg = DBConn.getConn(dbname).getCollection(tick_collection).find().sort(order).limit(1).next();
		} else {
			DBObject query2 = new BasicDBObject();
			query2.put("inst_id", inst_id);
			maxavg = DBConn.getConn(dbname).getCollection(tick_collection).find(query2).sort(order).limit(1).next();
		}
		double maxavgValue = newAvgPower;
		if(maxavg != null) maxavgValue = ((Double)maxavg.get("p")).doubleValue();
		data.put("avgPeak", newMaxPower);
		data.put("maxPower", maxavgValue);
		data.put("avgPower", newAvgPower);
		data.put("energy", newEnergy);
		data.put("cost", newCost);
		data.put("co2", newCo2);
		if(first) {
			DBConn.getConn(dbname).getCollection(collection).insert(data);
		} else {
			DBConn.getConn(dbname).getCollection(collection).update(query, data, false, false);
		}
	}

	public void addAppKPIs(String app_id, double maxPower, double avgPower, double energy, double cost, double co2) {
		boolean first = false;
		DBObject query = new BasicDBObject();
		String collection;
		String id = app_id;
		collection = COL_APPKPIS;
		query.put("app_id", id);
		DBObject data = DBConn.getConn(dbname).getCollection(collection).findOne(query);
		double newMaxPower = maxPower;
		double newAvgPower = avgPower;
		double newEnergy = energy;
		double newCost = cost;
		double newCo2 = co2;
		if(data == null) {
			data = new BasicDBObject();
			first = true;
			data.put("app_id", id);
		} else {
			newMaxPower += ((Double)data.get("maxPower")).doubleValue();
			newAvgPower += ((Double)data.get("avgPower")).doubleValue();
			newEnergy += ((Double)data.get("energy")).doubleValue();
			newCost += ((Double)data.get("cost")).doubleValue();
			newCo2 += ((Double)data.get("co2")).doubleValue();
		}
		data.put("maxPower", newMaxPower);
		data.put("avgPower", newAvgPower);
		data.put("energy", newEnergy);
		data.put("cost", newCost);
		data.put("co2", newCo2);
		if(first) {
			DBConn.getConn(dbname).getCollection(collection).insert(data);
		} else {
			DBConn.getConn(dbname).getCollection(collection).update(query, data, false, false);
		}
	}
	
	public void addActKPIs(String app_id, double maxPower, double avgPower, double energy, double cost, double co2) {
		boolean first = false;
		DBObject query = new BasicDBObject();
		String collection;
		String id = app_id;
		collection = COL_ACTKPIS;
		query.put("act_id", id);
		DBObject data = DBConn.getConn(dbname).getCollection(collection).findOne(query);
		double newMaxPower = maxPower;
		double newAvgPower = avgPower;
		double newEnergy = energy;
		double newCost = cost;
		double newCo2 = co2;
		if(data == null) {
			data = new BasicDBObject();
			first = true;
			data.put("act_id", id);
		} else {
			newMaxPower += ((Double)data.get("maxPower")).doubleValue();
			newAvgPower += ((Double)data.get("avgPower")).doubleValue();
			newEnergy += ((Double)data.get("energy")).doubleValue();
			newCost += ((Double)data.get("cost")).doubleValue();
			newCo2 += ((Double)data.get("co2")).doubleValue();
		}
		data.put("maxPower", newMaxPower);
		data.put("avgPower", newAvgPower);
		data.put("energy", newEnergy);
		data.put("cost", newCost);
		data.put("co2", newCo2);
		if(first) {
			DBConn.getConn(dbname).getCollection(collection).insert(data);
		} else {
			DBConn.getConn(dbname).getCollection(collection).update(query, data, false, false);
		}
	}

	
	/**
	 * 
	 * @param tick
	 * @param inst_id
	 * @param p
	 * @param q
	 */
	public void addTickResultForInstallation(int tick,
			String inst_id, double p, double q, String collection) {
		boolean first = false;
		DBObject query = new BasicDBObject();
		query.put("inst_id", inst_id);
		query.put("tick", tick);
		DBObject data = DBConn.getConn(dbname).getCollection(collection).findOne(query);
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
			DBConn.getConn(dbname).getCollection(collection).insert(data);
		} else {
			DBConn.getConn(dbname).getCollection(collection).update(query, data, false, false);
		}
	}
	
	public void addExpectedPowerTick(int tick, String id, double p, double q, String collection) {
		DBObject data = new BasicDBObject();
		data.put("id", id);
		data.put("tick", tick);
		data.put("p", p);
		data.put("q", q);
		DBConn.getConn(dbname).getCollection(collection).insert(data);
	}
	
	public DBObject getTickResultForInstallation(int tick,
			String inst_id, String collection) {
		DBObject query = new BasicDBObject();
		query.put("inst_id", inst_id);
		query.put("tick", tick);
		return DBConn.getConn(dbname).getCollection(collection).findOne(query);
	}
	
	public void normalize(int tick, String inst_id, int divisor, String collection) {
		DBObject query = new BasicDBObject();
		query.put("inst_id", inst_id);
		query.put("tick", tick);
		DBObject data = DBConn.getConn(dbname).getCollection(collection).findOne(query);
		double newp = ((Double)data.get("p")).doubleValue() / divisor;
		double newq = ((Double)data.get("q")).doubleValue() / divisor;
		data.put("p",newp);
		data.put("q",newq);
		DBConn.getConn(dbname).getCollection(collection).update(query, data, false, false);
	}

	/**
	 * 
	 * @param tick
	 * @param p
	 * @param q
	 */
	public void addAggregatedTickResult(int tick, double p, double q, String collection) {
		boolean first = false;
		DBObject query = new BasicDBObject();
		query.put("tick", tick);
		DBObject data = DBConn.getConn(dbname).getCollection(collection).findOne(query);
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
			DBConn.getConn(dbname).getCollection(collection).insert(data);
		} else {
			DBConn.getConn(dbname).getCollection(collection).update(query, data, false, false);
		}
	}
	
	public void normalize(int tick, int divisor, String collection) {
		DBObject query = new BasicDBObject();
		query.put("tick", tick);
		DBObject data = DBConn.getConn(dbname).getCollection(collection).findOne(query);
		double newp = ((Double)data.get("p")).doubleValue() / divisor;
		double newq = ((Double)data.get("q")).doubleValue() / divisor;
		data.put("p",newp);
		data.put("q",newq);
		DBConn.getConn(dbname).getCollection(collection).update(query, data, false, false);
	}
}

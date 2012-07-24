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
	public void addAggregatedTickResult(int tick, double p, double q) {
		DBObject data = new BasicDBObject();
		data.put("tick",tick);
		data.put("p",p);
		data.put("q",q);
		DBConn.getConn().getCollection(COL_AGGRRESULTS).insert(data);
	}


//	public static void main(String args[]) {
//		MongoResults m = new MongoResults();
//		m.mongoResultQuery("instID_", MongoResults.ACTIVE_POWER_P, 2, null, null); 
//		//		m.addTickResultForInstallation(1,"instID_", 0.1, 2.3);
//		//		m.addTickResultForInstallation(2,"instID_", 0.2, 2.3); 
//		//		m.addTickResultForInstallation(3,"instID_", 0.3, 2.3); 
//		//		m.addTickResultForInstallation(4,"instID_", 0.1, 2.3);
//		//		m.addTickResultForInstallation(5,"instID_", 0.2, 2.3); 
//		//		m.addTickResultForInstallation(6,"instID_", 0.3, 2.3); 
//		//		m.addTickResultForInstallation(7,"instID_", 0.4, 2.3);
//		//		m.addTickResultForInstallation(8,"instID_", 0.5, 2.3); 
//		//		m.addTickResultForInstallation(9,"instID_", 0.6, 2.3); 
//	}
}

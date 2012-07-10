package eu.cassandra.server.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.cassandra.server.api.exceptions.MongoRefNotFoundException;
import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.MongoDBQueries;

public class MongoResults {

	private final static String COL_APPRESULTS = "app_results";
	private final static String COL_INSTRESULTS = "inst_results";
	private final static String COL_AGGRRESULTS = "aggr_results";

	/**
	 * 
	 * @param app_id
	 * @param openTick
	 * @throws MongoRefNotFoundException
	 */
	public void addOpenTick(String app_id, long openTick) throws MongoRefNotFoundException {
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
	public void addCloseTick(String app_id, long closeTick) {
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
	public void addTickResultForInstallation(long tick,
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
	public void addAggregatedTickResult(long tick, double p, double q) {
		DBObject data = new BasicDBObject();
		data.put("tick",tick);
		data.put("p",p);
		data.put("q",q);
		DBConn.getConn().getCollection(COL_AGGRRESULTS).insert(data);
	}


	//	public static void main(String args[]) {
	//		MongoResults m = new MongoResults();
	//		m.addTickResultForInstallation(1,"dszfs123", 0.1, 2.3);
	//		m.addTickResultForInstallation(2,"dszfs123", 0.1, 2.3); 
	//		m.addTickResultForInstallation(3,"dszfs123", 0.1, 2.3); 
	//	}
}

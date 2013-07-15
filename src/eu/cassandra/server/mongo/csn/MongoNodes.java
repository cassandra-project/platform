package eu.cassandra.server.mongo.csn;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Vector;

import javax.ws.rs.core.HttpHeaders;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import eu.cassandra.server.mongo.MongoInstallations;
import eu.cassandra.server.mongo.MongoPersons;
import eu.cassandra.server.mongo.MongoResults;
import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.MongoDBQueries;

public class MongoNodes {



	/**
	 * Creates a node per installations and adds all the available information in it
	 * 
	 * @param graph_id
	 * @param httpHeaders
	 * @return
	 */
	public Vector<DBObject>  createNodes(String graph_id,HttpHeaders httpHeaders) {
		DecimalFormat decim = new DecimalFormat("#.##");
		String dbName = MongoDBQueries.getDbNameFromHTTPHeader(httpHeaders);

		Vector<DBObject> nodes = new Vector<DBObject>();
		DBCursor cursorDoc = DBConn.getConn(dbName).getCollection(MongoInstallations.COL_INSTALLATIONS).find();
		while(cursorDoc.hasNext()) {
			DBObject installationsObj = cursorDoc.next();
			String inst_id = installationsObj.get("_id").toString();

			Double maxPd = 0.0;
			DBCursor maxPcursor = DBConn.getConn(dbName).getCollection(MongoResults.COL_INSTRESULTS_HOURLY).find(
					new BasicDBObject("inst_id",inst_id),new BasicDBObject("p",1)).sort(new BasicDBObject( "p",-1)).limit(1);
			DBObject maxP = null;
			if(maxPcursor != null) {
				if(maxPcursor.hasNext()) {
					maxP = maxPcursor.next();
					maxPd = Double.parseDouble(maxP.get("p").toString());
				}
				maxPcursor.close();
			}

			Double maxQd = 0.0;
			DBCursor maxQcursor = DBConn.getConn(dbName).getCollection(MongoResults.COL_INSTRESULTS_HOURLY).find(
					new BasicDBObject("inst_id",inst_id),new BasicDBObject("q",1)).sort(new BasicDBObject( "q",-1)).limit(1);
			DBObject maxQ = null;
			if(maxQcursor != null) {
				if(maxQcursor.hasNext()) {
					maxQ = maxQcursor.next();
					maxQd = Double.parseDouble(maxQ.get("q").toString());
				}
				maxQcursor.close();
			}

			Double minPd = 0.0;
			DBCursor minPcursor = DBConn.getConn(dbName).getCollection(MongoResults.COL_INSTRESULTS_HOURLY).find(
					new BasicDBObject("inst_id",inst_id),new BasicDBObject("p",1)).sort(new BasicDBObject( "p",1)).limit(1);
			DBObject minP = null;
			if(minPcursor != null) {
				if(minPcursor.hasNext()) {
					minP = minPcursor.next();
					minPd = Double.parseDouble(minP.get("p").toString());
				}
				minPcursor.close();
			}

			Double minQd = 0.0;
			DBCursor minQcursor = DBConn.getConn(dbName).getCollection(MongoResults.COL_INSTRESULTS_HOURLY).find(
					new BasicDBObject("inst_id",inst_id),new BasicDBObject("q",1)).sort(new BasicDBObject( "q",1)).limit(1);
			DBObject minQ = null;
			if(minQcursor != null) {
				if(minQcursor.hasNext()) {
					minQ = minQcursor.next();
					minQd = Double.parseDouble(minQ.get("q").toString());
				}
				minQcursor.close();
			}

			DBCursor cursor = DBConn.getConn(dbName).getCollection(MongoResults.COL_INSTRESULTS_HOURLY).find(
					new BasicDBObject("inst_id",inst_id));
			int hours = 0;
			Double avgP = 0.0;
			Double avgQ = 0.0;
			Double sumP = 0.0;
			Double sumQ = 0.0;
			HashMap<Integer,Double> perHourP = new HashMap<Integer,Double>();
			HashMap<Integer,Double> perHourQ = new HashMap<Integer,Double>();
			while(cursor.hasNext()) {
				DBObject inst_result_hour = cursor.next();
				String p = inst_result_hour.get("p").toString();
				String q = inst_result_hour.get("q").toString();
				if(p != null) {
					sumP += Double.parseDouble(p);
					double v = Double.parseDouble(p);
					if(perHourP.containsKey((int)(hours%24)))
						v += perHourP.get((int)(hours%24));
					perHourP.put((int)(hours%24), v);
				}
				if(q != null) {
					sumQ += Double.parseDouble(q);
					double v = 0.0; 
					if(perHourQ.containsKey((int)(hours%24))) {
						v += perHourQ.get((int)(hours%24));
					}
					perHourQ.put((int)(hours%24), v);
				}
				hours++;
			}
			avgP = sumP/hours;
			avgQ = sumQ/hours;


			DBObject personObj = DBConn.getConn(dbName).getCollection(MongoPersons.COL_PERSONS).findOne(new BasicDBObject("inst_id",inst_id));
			String personType = ((personObj.containsField("type"))?personObj.get("type").toString():null);

			DBObject installationNode = new BasicDBObject("inst_id",inst_id);
			installationNode.put("graph_id", graph_id);
			installationNode.put("type", installationsObj.get("type"));
			installationNode.put("personType", personType);

			installationNode.put("maxP",  Double.parseDouble(decim.format(maxPd)));
			installationNode.put("maxQ", Double.parseDouble(decim.format(maxQd)));
			installationNode.put("minP", Double.parseDouble(decim.format(minPd)));
			installationNode.put("minQ", Double.parseDouble(decim.format(minQd)));

			installationNode.put("sumP", Double.parseDouble(decim.format(sumP)));
			installationNode.put("sumQ", Double.parseDouble(decim.format(sumQ)));
			installationNode.put("avgP", Double.parseDouble(decim.format(avgP)));
			installationNode.put("avgQ", Double.parseDouble(decim.format(avgQ)));

			installationNode.put("nHours", hours);

			Vector<Double> hourVecP = new Vector<Double>();
			for(int i=0;i<24;i++) {
				if(perHourP.containsKey(i))
					hourVecP.add(Double.parseDouble(decim.format(perHourP.get(i))));
			}
			Vector<Double> hourVecQ = new Vector<Double>();
			for(int i=0;i<24;i++) {
				if(perHourQ.containsKey(i))
					hourVecQ.add(Double.parseDouble(decim.format(perHourQ.get(i))));
			}

			installationNode.put("hoursP", hourVecP);
			installationNode.put("hoursQ", hourVecQ);

			DBConn.getConn(dbName).getCollection(MongoGraphs.COL_CSN_NODES).insert(installationNode);
			nodes.add(installationNode);
		}
		return nodes;
	}
}

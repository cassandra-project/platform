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


			Double maxHourlyEnergyConsumption = 0.0;
			DBCursor maxEcursor = DBConn.getConn(dbName).getCollection(MongoResults.COL_INSTRESULTS_HOURLY_EN).find(
					new BasicDBObject("inst_id",inst_id),new BasicDBObject("p",1)).sort(new BasicDBObject( "p",-1)).limit(1);
			DBObject maxE = null;
			if(maxEcursor != null) {
				if(maxEcursor.hasNext()) {
					maxE = maxEcursor.next();
					maxHourlyEnergyConsumption = Double.parseDouble(maxE.get("p").toString());
				}
				maxEcursor.close();
			}


			Double minHourlyEnergyConsumption = 0.0;
			DBCursor minEcursor = DBConn.getConn(dbName).getCollection(MongoResults.COL_INSTRESULTS_HOURLY_EN).find(
					new BasicDBObject("inst_id",inst_id),new BasicDBObject("p",1)).sort(new BasicDBObject( "p",1)).limit(1);
			DBObject minE = null;
			if(minEcursor != null) {
				if(minEcursor.hasNext()) {
					minE = minEcursor.next();
					minHourlyEnergyConsumption = Double.parseDouble(minE.get("p").toString());
				}
				minEcursor.close();
			}


			Double maxActivePowerPerHour = 0.0;
			DBCursor maxPcursor = DBConn.getConn(dbName).getCollection(MongoResults.COL_INSTRESULTS_HOURLY).find(
					new BasicDBObject("inst_id",inst_id),new BasicDBObject("p",1)).sort(new BasicDBObject( "p",-1)).limit(1);
			DBObject maxP = null;
			if(maxPcursor != null) {
				if(maxPcursor.hasNext()) {
					maxP = maxPcursor.next();
					maxActivePowerPerHour = Double.parseDouble(maxP.get("p").toString());
				}
				maxPcursor.close();
			}


			Double maxReactivePowerPerHour = 0.0;
			DBCursor maxQcursor = DBConn.getConn(dbName).getCollection(MongoResults.COL_INSTRESULTS_HOURLY).find(
					new BasicDBObject("inst_id",inst_id),new BasicDBObject("q",1)).sort(new BasicDBObject( "q",-1)).limit(1);
			DBObject maxQ = null;
			if(maxQcursor != null) {
				if(maxQcursor.hasNext()) {
					maxQ = maxQcursor.next();
					maxReactivePowerPerHour = Double.parseDouble(maxQ.get("q").toString());
				}
				maxQcursor.close();
			}


			Double minActivePowerPerHour = 0.0;
			DBCursor minPcursor = DBConn.getConn(dbName).getCollection(MongoResults.COL_INSTRESULTS_HOURLY).find(
					new BasicDBObject("inst_id",inst_id),new BasicDBObject("p",1)).sort(new BasicDBObject( "p",-1)).limit(1);
			DBObject minP = null;
			if(minPcursor != null) {
				if(minPcursor.hasNext()) {
					minP = minPcursor.next();
					minActivePowerPerHour = Double.parseDouble(minP.get("p").toString());
				}
				minPcursor.close();
			}


			Double minReactivePowerPerHour = 0.0;
			DBCursor minQcursor = DBConn.getConn(dbName).getCollection(MongoResults.COL_INSTRESULTS_HOURLY).find(
					new BasicDBObject("inst_id",inst_id),new BasicDBObject("q",1)).sort(new BasicDBObject( "q",-1)).limit(1);
			DBObject minQ = null;
			if(minQcursor != null) {
				if(minQcursor.hasNext()) {
					minQ = minQcursor.next();
					minReactivePowerPerHour = Double.parseDouble(minQ.get("q").toString());
				}
				minQcursor.close();
			}

			
			DBCursor c = DBConn.getConn(dbName).getCollection(MongoResults.COL_INSTRESULTS_HOURLY_EN).find(
					new BasicDBObject("inst_id",inst_id));
			double totalEnergyConsumption = 0.0;
			HashMap<Integer,Double> perHourE = new HashMap<Integer,Double>();
			int hours = 0;
			while(c.hasNext()) {
				DBObject inst_result_hour = c.next();
				if(inst_result_hour.containsField("p")) {
					String p = inst_result_hour.get("p").toString();
					double v = Double.parseDouble(p);
					totalEnergyConsumption += v;
					
					if(perHourE.containsKey((int)(hours%24))) {
						v += perHourE.get((int)(hours%24));
					}
					perHourE.put((int)(hours%24), v);
					
				}
				hours++;
			}
			c.close();


			DBCursor cursor = DBConn.getConn(dbName).getCollection(MongoResults.COL_INSTRESULTS_HOURLY).find(
					new BasicDBObject("inst_id",inst_id));
			hours = 0;
			Double averageActivePowerPerHour = 0.0;
			Double averageReactivePowerPerHour = 0.0;
			HashMap<Integer,Double> perHourP = new HashMap<Integer,Double>();
			HashMap<Integer,Double> perHourQ = new HashMap<Integer,Double>();
			while(cursor.hasNext()) {
				DBObject inst_result_hour = cursor.next();
				
				if(inst_result_hour.containsField("p")) {
					String p = inst_result_hour.get("p").toString();
					double v = Double.parseDouble(p);
					if(perHourP.containsKey((int)(hours%24))) {
						v += perHourP.get((int)(hours%24));
						averageReactivePowerPerHour += v;
					}
					perHourP.put((int)(hours%24), v);
				}
				if(inst_result_hour.containsField("q")) {
					String q = inst_result_hour.get("q").toString();
					double v= Double.parseDouble(q);
					if(perHourQ.containsKey((int)(hours%24))) {
						v += perHourQ.get((int)(hours%24));
						averageReactivePowerPerHour += v;
					}
					perHourQ.put((int)(hours%24), v);
				}
				hours++;
			}
			cursor.close();
			averageActivePowerPerHour = averageReactivePowerPerHour/hours;
			averageReactivePowerPerHour = averageReactivePowerPerHour/hours;

			
			DBCursor cEn = DBConn.getConn(dbName).getCollection(MongoResults.COL_INSTRESULTS_HOURLY_EN).find(
					new BasicDBObject("inst_id",inst_id));
			int hoursEn = 0;
			HashMap<Integer,Double> perHourEn = new HashMap<Integer,Double>();
			while(cEn.hasNext()) {
				DBObject inst_result_hour = cEn.next();
				
				if(inst_result_hour.containsField("p")) {
					String p = inst_result_hour.get("p").toString();
					double v = Double.parseDouble(p);
					if(perHourEn.containsKey((int)(hoursEn%24))) {
						v += perHourEn.get((int)(hoursEn%24));
					}
					perHourEn.put((int)(hoursEn%24), v);
				}
				hoursEn++;
			}
			cEn.close();
			

			DBObject personObj = DBConn.getConn(dbName).getCollection(MongoPersons.COL_PERSONS).findOne(new BasicDBObject("inst_id",inst_id));
			String personType = ((personObj.containsField("type"))?personObj.get("type").toString():null);

			DBObject installationNode = new BasicDBObject("inst_id",inst_id);
			installationNode.put("graph_id", graph_id);
			installationNode.put(MongoEdges.InstallationType, installationsObj.get("type"));
			installationNode.put(MongoEdges.PersonType, personType);

			installationNode.put(MongoEdges.TotalEnergyConsumption, Double.parseDouble(decim.format(totalEnergyConsumption)));

			installationNode.put(MongoEdges.MaxHourlyEnergyConsumption, Double.parseDouble(decim.format(maxHourlyEnergyConsumption)));
			installationNode.put(MongoEdges.MinHourlyEnergyConsumption, Double.parseDouble(decim.format(minHourlyEnergyConsumption)));

			installationNode.put(MongoEdges.AverageActivePowerPerHour, Double.parseDouble(decim.format(averageActivePowerPerHour)));
			installationNode.put(MongoEdges.AverageReactivePowerPerHour, Double.parseDouble(decim.format(averageReactivePowerPerHour)));

			installationNode.put(MongoEdges.MaxActivePowerPerHour, Double.parseDouble(decim.format(maxActivePowerPerHour)));
			installationNode.put(MongoEdges.MaxReactivePowerPerHour, Double.parseDouble(decim.format(maxReactivePowerPerHour)));

			installationNode.put(MongoEdges.MinActivePowerPerHour, Double.parseDouble(decim.format(minActivePowerPerHour)));
			installationNode.put(MongoEdges.MinReactivePowerPerHour, Double.parseDouble(decim.format(minReactivePowerPerHour)));

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
			
			Vector<Double> hourVecE = new Vector<Double>();
			for(int i=0;i<24;i++) {
				if(perHourE.containsKey(i))
					hourVecE.add(Double.parseDouble(decim.format(perHourE.get(i))));
			}
			

			installationNode.put("hoursP", hourVecP);
			installationNode.put("hoursQ", hourVecQ);
			installationNode.put("hoursE", hourVecE);

			DBConn.getConn(dbName).getCollection(MongoGraphs.COL_CSN_NODES).insert(installationNode);
			nodes.add(installationNode);
		}
		return nodes;
	}
}

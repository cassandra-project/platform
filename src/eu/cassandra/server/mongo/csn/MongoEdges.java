package eu.cassandra.server.mongo.csn;

import java.util.Vector;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.cassandra.server.mongo.MongoPersons;
import eu.cassandra.server.mongo.util.DBConn;

public class MongoEdges {

	public final static String InstallationType = "InstallationType";
	public final static String PersonType = "PersonType";
	public final static String TransformerID = "TransformerID";
	public final static String TopologicalDistance = "TopologicalDistance";
	public final static String Location = "Location";
	public final static String SocialDistance = "SocialDistance";

	public final static String TotalEnergyConsumption = "TotalEnergyConsumption";

	public final static String MaxHourlyEnergyConsumption = "MaxHourlyEnergyConsumption";
	public final static String MinHourlyEnergyConsumption = "MinHourlyEnergyConsumption";

	public final static String AverageActivePowerPerHour = "AverageActivePowerPerHour";
	public final static String AverageReactivePowerPerHour = "AverageReactivePowerPerHour";

	public final static String MaxActivePowerPerHour = "MaxActivePowerPerHour";
	public final static String MaxReactivePowerPerHour = "MaxReactivePowerPerHour";

	public final static String MinActivePowerPerHour = "MinActivePowerPerHour";
	public final static String MinReactivePowerPerHour = "MinReactivePowerPerHour";
	
	public final static String hoursP = "hoursP";
	public final static String hoursQ = "hoursQ";
	public final static String hoursE = "hoursE";
	
	

	/**
	 * 
	 * @param nodes
	 * @param graph_id
	 * @param graphType
	 * @param minWeight
	 * @param httpHeaders
	 */
	public int createEdges(Vector<DBObject> nodes, String graph_id, String graphType, Double minWeight, String dbName) {
		int edgeCounter = 0;
		for(int i=0;i<nodes.size()-1;i++) {
			for(int j=i+1;j<nodes.size();j++) {
				DBObject edge = createEdge(graphType, minWeight, nodes.get(i),nodes.get(j),dbName);
				if(edge != null) {
					edgeCounter++;
					edge.put("graph_id", graph_id);
					edge.put("node_id1", nodes.get(i).get("_id").toString());
					edge.put("node_id2", nodes.get(j).get("_id").toString());
					edge.put("inst_id1", nodes.get(i).get("inst_id").toString());
					edge.put("inst_id2", nodes.get(j).get("inst_id").toString());
					edge.put("run_id", dbName);
					DBConn.getConn().getCollection(MongoGraphs.COL_CSN_EDGES).insert(edge);
				}
			}
		}
		return edgeCounter;
	}

	private DBObject createEdge(String graphType, Double minWeight, DBObject inst1, DBObject inst2, String dbName) {
		DBObject edge = null;
		boolean createEdge = false;
		//InstallationType
		if(graphType.equalsIgnoreCase(InstallationType) || graphType.equalsIgnoreCase(InstallationType)) {
			if(equalInNodes("type", inst1, inst2)){
				createEdge = true;
			}
		}
		//PersonType
		else if(graphType.equalsIgnoreCase(PersonType)) {
			if(equalPersonType(inst1, inst2, dbName)){
				createEdge = true;
			}
		}
		//TransformerID
		else if(graphType.equalsIgnoreCase(TransformerID)) {
			if(equalInNodes(TransformerID, inst1, inst2)){
				createEdge = true;
			}
		}
		//TopologicalDistance
		else if(graphType.equalsIgnoreCase(TopologicalDistance)) {
			//@ToDo
		}
		//Locations
		else if(graphType.equalsIgnoreCase(Location)) {
			if(equalInNodes(Location.toLowerCase(), inst1, inst2)){
				createEdge = true;
			}
		}
		//SocialDistance
		else if(graphType.equalsIgnoreCase(SocialDistance)) {
			if(equalInNodes(Location.toLowerCase(), inst1, inst2)){
				createEdge = true;
			}
		}
		
		//All other 
		else {
			edge = decideIfToCreateEdge(inst1, inst2, graphType, minWeight);
		}

		//		public final static String MaxHourlyEnergyConsumption = "MaxHourlyEnergyConsumption";
		//		public final static String MinHourlyEnergyConsumption = "MinHourlyEnergyConsumption";
		//		public final static String AverageActivePowerPerHour = "AverageActivePowerPerHour";
		//		public final static String AverageReactivePowerPerHour = "AverageReactivePowerPerHour";
		//		public final static String MaxActivePowerPerHour = "MaxActivePowerPerHour";
		//		public final static String MaxReactivePowerPerHour = "MaxReactivePowerPerHour";
		//		public final static String MinActivePowerPerHour = "MinActivePowerPerHour";
		//		public final static String MinReactivePowerPerHour = "MinReactivePowerPerHour";

		//
		//		else if(graphType.equalsIgnoreCase(TotalConsumptionQ) ) {
		//			edge = decideIfToCreateEdge(edge, inst1, inst2, "sumQ", graphType,minWeight); 
		//		}
		//		//avgP
		//		else if(graphType.equalsIgnoreCase(AverageConsumptionP) ) {
		//			edge = decideIfToCreateEdge(edge, inst1, inst2, "avgP", graphType,minWeight); 
		//		}
		//		else if(graphType.equalsIgnoreCase(AverageConsumptionQ) ) {
		//			edge = decideIfToCreateEdge(edge, inst1, inst2, "avgQ", graphType,minWeight); 
		//		}
		//		//Min
		//		else if(graphType.equalsIgnoreCase(MinConsumptionP)  ) {
		//			edge = decideIfToCreateEdge(edge, inst1, inst2, "minP", graphType,minWeight); 
		//		}
		//		else if(graphType.equalsIgnoreCase(MinConsumptionQ) ) {
		//			edge = decideIfToCreateEdge(edge, inst1, inst2, "minQ", graphType,minWeight); 
		//		}
		//		//Max
		//		else if(graphType.equalsIgnoreCase(MaxConsumptionP)) {
		//			edge = decideIfToCreateEdge(edge, inst1, inst2, "maxP", graphType,minWeight); 
		//		}
		//		else if(graphType.equalsIgnoreCase(MaxConsumptionQ) ) {
		//			edge = decideIfToCreateEdge(edge, inst1, inst2, "maxQ", graphType,minWeight); 
		//		}

		if(createEdge)
			edge = new BasicDBObject("type",graphType);

		return edge;
	}


	/**
	 * 
	 * @param instObjectKey
	 * @param inst1
	 * @param inst2
	 * @return
	 */
	private boolean equalInNodes(String instObjectKey, DBObject inst1, DBObject inst2) {
		if(inst1.get(instObjectKey) != null && inst2.get(instObjectKey) != null) {
			if(inst1.get(instObjectKey).toString().equalsIgnoreCase(inst2.get(instObjectKey).toString())) {
				return true;
			}
		}
		return false;
	}


	/**
	 * 
	 * @param inst1
	 * @param inst2
	 * @param instObjectKey
	 * @param minWeight
	 * @param httpHeaders
	 * @return
	 */
	private DBObject decideIfToCreateEdge(DBObject inst1, DBObject inst2, String instObjectKey, Double minWeight) {
		//		Double v2 = Double.parseDouble(inst2.get(instObjectKey).toString());
		DBObject edge = null;
		if(instObjectKey.equalsIgnoreCase(TotalEnergyConsumption) ||
				instObjectKey.equalsIgnoreCase(MaxHourlyEnergyConsumption) || 
				instObjectKey.equalsIgnoreCase(MinHourlyEnergyConsumption) || 
				instObjectKey.equalsIgnoreCase(AverageActivePowerPerHour) || 
				instObjectKey.equalsIgnoreCase(AverageReactivePowerPerHour) || 
				instObjectKey.equalsIgnoreCase(MaxReactivePowerPerHour) || 
				instObjectKey.equalsIgnoreCase(MaxHourlyEnergyConsumption) || 
				instObjectKey.equalsIgnoreCase(MinActivePowerPerHour) || 
				instObjectKey.equalsIgnoreCase(MinReactivePowerPerHour)) {
			if(inst1.containsField(instObjectKey) && inst2.containsField(instObjectKey)) {
				Double v1 = Double.parseDouble(inst1.get(instObjectKey).toString());
				Double v2 = Double.parseDouble(inst2.get(instObjectKey).toString());
				double dif = Math.abs(v1-v2);
				if( dif > minWeight) {
					edge = new BasicDBObject("type",instObjectKey);
					edge.put("minWeight", minWeight);
					edge.put("weight", dif);
				}
			}
		}
		return edge;
	}


//	/**
//	 * 
//	 * @param v1
//	 * @param v2
//	 * @param minWeight
//	 * @param instObjectKey
//	 * @return
//	 */
//	private DBObject createEdge(Double v1, Double v2, Double minWeight, String instObjectKey) {
//		DBObject edge = null;
//		double dif = Math.abs(v1-v2);
//		if( dif < minWeight) {
//			edge = new BasicDBObject("type",instObjectKey);
//			edge.put("minWeight", minWeight);
//			edge.put("weight", dif);
//		}
//		return edge;
//	}


	/**
	 * 
	 * @param inst1
	 * @param inst2
	 * @param httpHeaders
	 * @return
	 */
	private boolean equalPersonType(DBObject inst1, DBObject inst2, String dbName) {
		String instObjectKey = "type";
		DBObject person1 = DBConn.getConn(dbName).getCollection(MongoPersons.COL_PERSONS).findOne(new BasicDBObject("inst_id",inst1.get("inst_id").toString()));
		DBObject person2 = DBConn.getConn(dbName).getCollection(MongoPersons.COL_PERSONS).findOne(new BasicDBObject("inst_id",inst2.get("inst_id").toString()));
		if(person1.get(instObjectKey) != null && person2.get(instObjectKey) != null) {
			if(person1.get(instObjectKey).toString().equalsIgnoreCase(person1.get(instObjectKey).toString())) {
				return true;
			}
		}
		return false;
	}


//	private double getSum(DBObject inst, HttpHeaders httpHeaders) {
//		double value = 0.0;
//		DBCursor results = DBConn.getConn(MongoDBQueries.getDbNameFromHTTPHeader(httpHeaders)).getCollection(
//				MongoResults.COL_INSTRESULTS_HOURLY_EN).find(new BasicDBObject("inst_id",inst.get("_id").toString()));
//		while(results.hasNext()) {
//			DBObject obj = results.next();
//			if(obj.containsField("p")) {
//				String v = obj.get("p").toString();
//				value += Double.parseDouble(v);
//			}
//		}
//		results.close();
//		return value;
//	}


	//	/**
	//	 * 
	//	 * @param edge
	//	 * @param inst1
	//	 * @param inst2
	//	 * @param instObjectKey
	//	 * @param graphType
	//	 * @return
	//	 */
	//	private DBObject decideIfToCreateEdge(DBObject edge, DBObject inst1, DBObject inst2, String instObjectKey, String graphType, Double minWeight) {
	//
	//		if(inst1.get(instObjectKey) != null && inst2.get(instObjectKey) != null) {
	//			//If create edges based on Node Type or Person Type
	//			if(minWeight == null) {
	//				if(inst1.get(instObjectKey).toString().equalsIgnoreCase(inst2.get(instObjectKey).toString())) {
	//					edge = new BasicDBObject("type",graphType);
	//				}
	//			}
	//			//Else if create edges based on P or Q (max,min, avg)
	//			else if(!graphType.toLowerCase().contains("per")) {
	//				Double v1 = Double.parseDouble(inst1.get(instObjectKey).toString());
	//				Double v2 = Double.parseDouble(inst2.get(instObjectKey).toString());
	//				double dif = Math.abs(v1-v2);
	//				if( dif < minWeight) {
	//					edge = new BasicDBObject("type",graphType);
	//					edge.put("minWeight", minWeight);
	//					edge.put("weight", dif);
	//				}
	//			}
	//			//Else perday or perhour
	//			else {
	//			}
	//		}
	//		return edge;
	//	}
}

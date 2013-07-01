package eu.cassandra.server.mongo.csn;

import java.util.Vector;

import javax.ws.rs.core.HttpHeaders;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.MongoDBQueries;

public class MongoEdges {

	public final static String InstallationType = "InstallationType";
	public final static String PersonType = "PersonType";

	public final static String TotalConsumptionP = "TotalConsumptionP";
	public final static String AverageConsumptionP = "AverageConsumptionP";
	public final static String  MinConsumptionP = "MinConsumptionP";
	public final static String MaxConsumptionP = "MaxConsumptionP";

	public final static String TotalConsumptionQ = "TotalConsumptionQ";
	public final static String AverageConsumptionQ = "AverageConsumptionQ";
	public final static String  MinConsumptionQ = "MinConsumptionQ";
	public final static String MaxConsumptionQ = "MaxConsumptionQ";

	public final static String TotalConsumptionPerHourP = "TotalConsumptionPerHourP";
	public final static String AverageConsumptionPerHourP = "AverageConsumptionPerHourP";
	public final static String MinConsumptionPerHourP = "MinConsumptionPerHourP";
	public final static String MaxConsumptionPerHourP = "MaxConsumptionPerHourP";
	public final static String TotalConsumptionPerHourQ = "TotalConsumptionPerHourQ";
	public final static String AverageConsumptionPerHourQ = "AverageConsumptionPerHourQ";
	public final static String MinConsumptionPerHourQ = "MinConsumptionPerHourQ";
	public final static String MaxConsumptionPerHourQ = "MaxConsumptionPerHourQ";

	public final static String TotalConsumptionPerDayP = "TotalConsumptionPerDayP";
	public final static String AverageConsumptionPerDayP = "AverageConsumptionPerDayP";
	public final static String MinConsumptionPerDayP = "MinConsumptionPerDayP";
	public final static String MaxConsumptionPerDayP = "MaxConsumptionPerDayP";
	public final static String TotalConsumptionPerDayQ = "TotalConsumptionPerDayQ";
	public final static String AverageConsumptionPerDayQ = "AverageConsumptionPerDayQ";
	public final static String MinConsumptionPerDayQ = "MinConsumptionPerDayQ";
	public final static String MaxConsumptionPerDayQ = "MaxConsumptionPerDayQ";

	/**
	 * 
	 * @param nodes
	 * @param graph_id
	 * @param graphType
	 * @param minWeight
	 * @param httpHeaders
	 */
	public void createEdges(Vector<DBObject> nodes, String graph_id, String graphType, Double minWeight, HttpHeaders httpHeaders) {
		int edgeCounter = 0;
		String dbName = MongoDBQueries.getDbNameFromHTTPHeader(httpHeaders);
		for(int i=0;i<nodes.size()-1;i++) {
			for(int j=i+1;j<nodes.size();j++) {
				DBObject edge = createEdge(graphType, minWeight, nodes.get(i),nodes.get(j),httpHeaders);
				if(edge != null) {
					System.out.println(edgeCounter++  + "\t" + i + "," + j);
					edge.put("graph_id", graph_id);
					edge.put("inst_id1", nodes.get(i).get("_id").toString());
					edge.put("inst_id2", nodes.get(j).get("_id").toString());
					DBConn.getConn(dbName).getCollection(MongoGraphs.COL_CSN_EDGES).insert(edge);
				}
			}
		}
	}

	private DBObject createEdge(String graphType, Double minWeight, DBObject inst1, DBObject inst2, HttpHeaders httpHeaders) {
		DBObject edge = null;
		//InstallationType
		if(graphType.equalsIgnoreCase(InstallationType)) {
			edge = decideIfToCreateEdge(edge, inst1, inst2, "type", graphType,null); 
		}
		//PersonType
		else if(graphType.equalsIgnoreCase(PersonType)) {
			edge = decideIfToCreateEdge(edge, inst1, inst2, "personType", graphType,null); 
		}
		//Sum
		else if(graphType.equalsIgnoreCase(TotalConsumptionP) || graphType.equalsIgnoreCase(TotalConsumptionPerHourP) || graphType.equalsIgnoreCase(TotalConsumptionPerDayP)) {
			edge = decideIfToCreateEdge(edge, inst1, inst2, "sumP", graphType,minWeight); 
		}
		else if(graphType.equalsIgnoreCase(TotalConsumptionQ) || graphType.equalsIgnoreCase(TotalConsumptionPerHourQ) || graphType.equalsIgnoreCase(TotalConsumptionPerDayQ)) {
			edge = decideIfToCreateEdge(edge, inst1, inst2, "sumQ", graphType,minWeight); 
		}
		//avgP
		else if(graphType.equalsIgnoreCase(AverageConsumptionP) || graphType.equalsIgnoreCase(AverageConsumptionPerHourP) || graphType.equalsIgnoreCase(AverageConsumptionPerDayP)) {
			edge = decideIfToCreateEdge(edge, inst1, inst2, "avgP", graphType,minWeight); 
		}
		else if(graphType.equalsIgnoreCase(AverageConsumptionQ) || graphType.equalsIgnoreCase(AverageConsumptionPerHourQ) || graphType.equalsIgnoreCase(AverageConsumptionPerDayQ)) {
			edge = decideIfToCreateEdge(edge, inst1, inst2, "avgQ", graphType,minWeight); 
		}
		//Min
		else if(graphType.equalsIgnoreCase(MinConsumptionP) || graphType.equalsIgnoreCase(MinConsumptionPerHourP) || graphType.equalsIgnoreCase(MinConsumptionPerDayP) ) {
			edge = decideIfToCreateEdge(edge, inst1, inst2, "minP", graphType,minWeight); 
		}
		else if(graphType.equalsIgnoreCase(MinConsumptionQ) || graphType.equalsIgnoreCase(MinConsumptionPerHourQ) || graphType.equalsIgnoreCase(MinConsumptionPerDayQ)) {
			edge = decideIfToCreateEdge(edge, inst1, inst2, "minQ", graphType,minWeight); 
		}
		//Max
		else if(graphType.equalsIgnoreCase(MaxConsumptionP) || graphType.equalsIgnoreCase(MaxConsumptionPerHourP) || graphType.equalsIgnoreCase(MaxConsumptionPerDayP)) {
			edge = decideIfToCreateEdge(edge, inst1, inst2, "maxP", graphType,minWeight); 
		}
		else if(graphType.equalsIgnoreCase(MaxConsumptionQ) || graphType.equalsIgnoreCase(MaxConsumptionPerHourQ) || graphType.equalsIgnoreCase(MaxConsumptionPerDayQ)) {
			edge = decideIfToCreateEdge(edge, inst1, inst2, "maxQ", graphType,minWeight); 
		}

		return edge;
	}

	/**
	 * 
	 * @param edge
	 * @param inst1
	 * @param inst2
	 * @param instObjectKey
	 * @param graphType
	 * @return
	 */
	private DBObject decideIfToCreateEdge(DBObject edge, DBObject inst1, DBObject inst2, String instObjectKey, String graphType, Double minWeight) {

		System.out.println(instObjectKey);
		System.out.println(graphType);
		System.out.println(minWeight);

		if(inst1.get(instObjectKey) != null && inst2.get(instObjectKey) != null) {
			//If create edges based on Node Type or Person Type
			if(minWeight == null) {
				System.out.println(inst1.get(instObjectKey));
				System.out.println(inst2.get(instObjectKey));
				if(inst1.get(instObjectKey).toString().equalsIgnoreCase(inst2.get(instObjectKey).toString())) {
					edge = new BasicDBObject("type",graphType);
				}
			}
			//Else if create edges based on P or Q (max,min, avg)
			else if(!graphType.toLowerCase().contains("per")) {
				Double v1 = Double.parseDouble(inst1.get(instObjectKey).toString());
				Double v2 = Double.parseDouble(inst2.get(instObjectKey).toString());
				System.out.println(v1 + "\t" + v2 + "\t" + minWeight);
				double dif = Math.abs(v1-v2);
				if( dif < minWeight) {
					edge = new BasicDBObject("type",graphType);
					edge.put("minWeight", minWeight);
					edge.put("dif", dif);
				}
			}
			//Else perday or perhour
			else {
			}
		}
		return edge;
	}
}

package eu.cassandra.server.mongo;

import java.util.Vector;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.JSONValidator;
import eu.cassandra.server.mongo.util.MongoDBQueries;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

public class MongoGraphs {

	public final static String COL_GRAPHS = "graphs";
	public final static String COL_CSN_NODES = "csn_nodes";
	public final static String COL_CSN_EDGES = "csn_edges";


	public final static String InstallationType = "InstallationType";
	public final static String PersonType = "PersonType";
	public final static String TotalConsumption = "TotalConsumption";
	public final static String AverageConsumption = "AverageConsumption";
	public final static String  MinConsumption= "MinConsumption";
	public final static String MaxConsumption = "MaxConsumption";
	public final static String TotalConsumptionPerHour = "TotalConsumptionPerHour";
	public final static String AverageConsumptionPerHour = "AverageConsumptionPerHour";
	public final static String MinConsumptionPerHour = "MinConsumptionPerHour";
	public final static String MaxConsumptionPerHour = "MaxConsumptionPerHour";
	public final static String TotalConsumptionPerDay = "TotalConsumptionPerDay";
	public final static String AverageConsumptionPerDay = "AverageConsumptionPerDay";
	public final static String MinConsumptionPerDay = "MinConsumptionPerDay";
	public final static String MaxConsumptionPerDay = "MaxConsumptionPerDay";
	public final static String ActivityConsumption = "ActivityConsumption";

	/**
	 * 
	 * @param dataToInsert
	 * @param httpHeaders
	 * @return
	 */
	public String createGraph(String dataToInsert,@Context HttpHeaders httpHeaders) {
		DBObject answer = new MongoDBQueries().insertData(COL_GRAPHS ,dataToInsert,
				"Graph created successfully",JSONValidator.GRAPH_SCHEMA,httpHeaders);


		String graph_id = ((DBObject)(answer.get("data"))).get("_id").toString();
		String graphType = ((DBObject)(answer.get("data"))).get("graphType").toString();
		String minWeight = ((DBObject)(answer.get("data"))).get("minWeight").toString();

		Vector<DBObject> nodes = createNodes(graph_id,httpHeaders);
		createEdges(nodes, graph_id, graphType, minWeight, httpHeaders);
		return answer.toString();
	}

	/**
	 * 
	 * @param httpHeaders
	 * @return
	 */
	public String getGraphs(HttpHeaders httpHeaders) {
		return new MongoDBQueries().getEntity(httpHeaders,COL_GRAPHS,null,null,"CSN graphs retrieved successfully").toString();
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public String deleteGraph(String id,HttpHeaders httpHeaders) {
		String dbName = MongoDBQueries.getDbNameFromHTTPHeader(httpHeaders);

		DBConn.getConn(dbName).getCollection(COL_CSN_NODES).findAndRemove(new BasicDBObject("graph_id",id));
		DBConn.getConn(dbName).getCollection(COL_CSN_EDGES).findAndRemove(new BasicDBObject("graph_id",id));

		return new MongoDBQueries().deleteDocument(COL_GRAPHS, id).toString();
	}


	/**
	 * 
	 * @param nodeID
	 * @param httpHeaders
	 * @return
	 */
	public String getNode(String nodeID, HttpHeaders httpHeaders) {
		return new MongoDBQueries().getEntity(httpHeaders,COL_CSN_NODES,"_id",nodeID,"CSN nodes retrieved successfully").toString();
	}


	/**
	 * 
	 * @param graph_id
	 * @param httpHeaders
	 * @return
	 */
	private Vector<DBObject>  createNodes(String graph_id,HttpHeaders httpHeaders) {
		String dbName = MongoDBQueries.getDbNameFromHTTPHeader(httpHeaders);

		Vector<DBObject> nodes = new Vector<DBObject>();
		DBCursor cursorDoc = DBConn.getConn(dbName).getCollection(MongoInstallations.COL_INSTALLATIONS).find();
		while(cursorDoc.hasNext()) {
			DBObject installationsObj = cursorDoc.next();
			String instID = installationsObj.get("_id").toString();
			DBObject installationNode = new BasicDBObject("inst_id",instID);
			installationNode.put("graph_id", graph_id);
			installationNode.put("type", installationsObj.get("type"));
			DBConn.getConn(dbName).getCollection(COL_CSN_NODES).insert(installationNode);
			nodes.add(installationNode);
			System.out.println("Node: " + installationNode);
		}
		return nodes;
	}

	/**
	 * 
	 * @param nodes
	 * @param graph_id
	 * @param graphType
	 * @param minWeight
	 * @param httpHeaders
	 */
	private void createEdges(Vector<DBObject> nodes, String graph_id, String graphType, String minWeight, HttpHeaders httpHeaders) {
		String dbName = MongoDBQueries.getDbNameFromHTTPHeader(httpHeaders);

		for(int i=0;i<nodes.size()-1;i++) {
			for(int j=i+1;j<nodes.size();j++) {
				System.out.println(i + "," + j);
				DBObject edge = createEdge(graphType, minWeight, nodes.get(i),nodes.get(j),httpHeaders);
				if(edge != null) {
					edge.put("graph_id", graph_id);
					edge.put("inst_id1", nodes.get(i).get("_id").toString());
					edge.put("inst_id2", nodes.get(j).get("_id").toString());
					DBConn.getConn(dbName).getCollection(COL_CSN_EDGES).insert(edge);
					System.out.println("Edge: " + edge);
				}
			}
		}
	}

	private DBObject createEdge(String graphType, String minWeight, DBObject inst1, DBObject inst2, HttpHeaders httpHeaders) {
		DBObject edge = null;
		//InstallationType
		if(graphType.equalsIgnoreCase(InstallationType)) {
			System.out.println(inst1);
			System.out.println(inst2);

			if(inst1.get("type") != null && inst2.get("type") != null && inst1.get("type").equals(inst2.get("type"))) {
				edge = new BasicDBObject("type",graphType);
			}
		}
		//PersonType
		else if(graphType.equalsIgnoreCase(PersonType)) {
			Object pType1 = new MongoDBQueries().getEntity(httpHeaders,MongoPersons.COL_PERSONS,"inst_id", inst1.get("_id").toString() ,
					"Persons retrieved successfully",false).get("type");
			Object pType2 = new MongoDBQueries().getEntity(httpHeaders,MongoPersons.COL_PERSONS,"inst_id", inst2.get("_id").toString() ,
					"Persons retrieved successfully",false).get("type");
			if(pType1 != null && pType2 != null &&  pType1.equals(pType2)) {
				edge = new BasicDBObject("type",graphType);
			}
		}
		else if(graphType.equalsIgnoreCase(TotalConsumption)) {

		}
		else if(graphType.equalsIgnoreCase(AverageConsumption)) {

		}
		else if(graphType.equalsIgnoreCase(MinConsumption)) {

		}
		else if(graphType.equalsIgnoreCase(MaxConsumption)) {

		}
		else if(graphType.equalsIgnoreCase(TotalConsumptionPerHour)) {

		}
		else if(graphType.equalsIgnoreCase(AverageConsumptionPerHour)) {

		}
		else if(graphType.equalsIgnoreCase(MinConsumptionPerHour)) {

		}
		else if(graphType.equalsIgnoreCase(MaxConsumptionPerHour)) {

		}
		else if(graphType.equalsIgnoreCase(TotalConsumptionPerDay)) {

		}
		else if(graphType.equalsIgnoreCase(AverageConsumptionPerDay)) {

		}
		else if(graphType.equalsIgnoreCase(MinConsumptionPerDay)) {

		}
		else if(graphType.equalsIgnoreCase(MaxConsumptionPerDay)) {

		}
		else if(graphType.equalsIgnoreCase(ActivityConsumption)) {

		}

		return edge;
	}
}

package eu.cassandra.server.mongo.csn;

import java.util.Vector;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.JSONValidator;
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.MongoDBQueries;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

public class MongoGraphs {

	public final static String COL_GRAPHS = "graphs";
	public final static String COL_CSN_NODES = "csn_nodes";
	public final static String COL_CSN_EDGES = "csn_edges";


	/**
	 * 
	 * @param dataToInsert
	 * @param httpHeaders
	 * @return
	 */
	public String createGraph(String dataToInsert,@Context HttpHeaders httpHeaders) {
		System.out.println(dataToInsert);
		if(dataToInsert != null) {
			DBObject answer = new MongoDBQueries().insertData(COL_GRAPHS ,dataToInsert,
					"Graph created successfully",JSONValidator.GRAPH_SCHEMA,httpHeaders);
System.out.println(PrettyJSONPrinter.prettyPrint(answer));
			String graph_id = ((DBObject)(answer.get("data"))).get("_id").toString();
			String graphType = ((DBObject)(answer.get("data"))).get("graphType").toString();
			String minWeight = ((DBObject)(answer.get("data"))).get("minWeight").toString();
			Double minWeightD = null;
			if(minWeight != null) 
				minWeightD = Double.parseDouble(minWeight);

			Vector<DBObject> nodes = new MongoNodes().createNodes(graph_id,httpHeaders);
			new MongoEdges().createEdges(nodes, graph_id, graphType, minWeightD, httpHeaders);
			return answer.toString();
		}
		else {
			return new JSONtoReturn().createJSONError(dataToInsert,new NullPointerException()).toString();
		}
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

}

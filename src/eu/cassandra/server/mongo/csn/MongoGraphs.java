package eu.cassandra.server.mongo.csn;

import java.util.Vector;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.cassandra.server.api.exceptions.RestQueryParamMissingException;
import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.JSONValidator;
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.MongoDBQueries;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

public class MongoGraphs {

	public final static String COL_GRAPHS = "graphs";
	public final static String COL_CSN_NODES = "csn_nodes";
	public final static String COL_CSN_EDGES = "csn_edges";
	
	public final static String COL_CSN_CLUSTERS = "csn_clusters";


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
	 * @param httpHeaders
	 * @return
	 */
	public String deleteGraph(String id,HttpHeaders httpHeaders) {
		String dbName = MongoDBQueries.getDbNameFromHTTPHeader(httpHeaders);

		//Delete Nodes and Edges of the graph
		DBConn.getConn(dbName).getCollection(COL_CSN_NODES).findAndRemove(new BasicDBObject("graph_id",id));
		DBConn.getConn(dbName).getCollection(COL_CSN_EDGES).findAndRemove(new BasicDBObject("graph_id",id));
		//Delete the Graph
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
	public String getNodes(String graph_id, HttpHeaders httpHeaders,String filters, 
			int limit, int skip, boolean count) {
		if(graph_id == null) {
			return new JSONtoReturn().createJSONError(
					"Only the Nodes of a particular Graph can be retrieved", 
					new RestQueryParamMissingException("graph_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(httpHeaders,COL_CSN_NODES,"graph_id", 
					graph_id, filters, null, limit, skip, "Nodes retrieved successfully",count,(String[])null).toString();
		}
	}

	/**
	 * 
	 * @param edgeID
	 * @param httpHeaders
	 * @return
	 */
	public String getEdge(String edgeID, HttpHeaders httpHeaders) {
		return new MongoDBQueries().getEntity(httpHeaders,COL_CSN_EDGES,"_id",edgeID,"CSN edges retrieved successfully").toString();
	}

	/**
	 * 
	 * @param graph_id
	 * @param httpHeaders
	 * @return
	 */
	public String getEdges(String graph_id, HttpHeaders httpHeaders,String filters, 
			int limit, int skip, boolean count) {
		if(graph_id == null) {
			return new JSONtoReturn().createJSONError(
					"Only the Edges of a particular Graph can be retrieved", 
					new RestQueryParamMissingException("graph_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(httpHeaders,COL_CSN_EDGES,"graph_id", 
					graph_id, filters, null, limit, skip, "Edges retrieved successfully",count,(String[])null).toString();
		}
	}
}

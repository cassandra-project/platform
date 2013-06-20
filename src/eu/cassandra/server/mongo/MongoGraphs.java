package eu.cassandra.server.mongo;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.JSONValidator;
import eu.cassandra.server.mongo.util.MongoDBQueries;

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
		DBObject answer = new MongoDBQueries().insertData(COL_GRAPHS ,dataToInsert,
				"Graph created successfully",JSONValidator.GRAPH_SCHEMA,httpHeaders);
		String graph_id = ((DBObject)(answer.get("data"))).get("_id").toString();
		createNodes(graph_id,httpHeaders);
		return answer.toString();
	}

	/**
	 * 
	 * @param graph_id
	 * @param httpHeaders
	 * @return
	 */
	private int createNodes(String graph_id,HttpHeaders httpHeaders) {
		int numOfNodes = 0;
		String dbName = MongoDBQueries.getDbNameFromHTTPHeader(httpHeaders);

		DBCursor cursorDoc = DBConn.getConn(dbName).getCollection(MongoInstallations.COL_INSTALLATIONS).find();
		while(cursorDoc.hasNext()) {
			DBObject installationsObj = cursorDoc.next();
			String instID = installationsObj.get("_id").toString();
			DBObject installationNode = new BasicDBObject("inst_id",instID);
			installationNode.put("graph_id", graph_id);
			DBConn.getConn(dbName).getCollection(COL_CSN_NODES).insert(installationNode);
			numOfNodes++;
		}

		return numOfNodes;
	}
}

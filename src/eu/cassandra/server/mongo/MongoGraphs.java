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
	 * curl -k -i --data  @graph.json    --header Content-type:application/json --header dbname:5194cbd3e4b0f0bb234bd64a https://localhost:8443/cassandra/api/csn
	 */
	public String createGraph(String dataToInsert,@Context HttpHeaders httpHeaders) {
		int numOfNodes = createNodes(httpHeaders);
		DBObject answer = new MongoDBQueries().insertData(COL_GRAPHS ,dataToInsert,
				"Graph created successfully with " + numOfNodes + " nodes",JSONValidator.GRAPH_SCHEMA,httpHeaders);
		System.out.println(answer);
		System.out.println(((DBObject)(answer.get("data"))).get("_id").toString());
		return answer.toString();
	}

	/**
	 * 
	 * @param scn_id
	 * @param httpHeaders
	 */
	private int createNodes(HttpHeaders httpHeaders) {
		int numOfNodes = 0;
		String dbName = MongoDBQueries.getDbNameFromHTTPHeader(httpHeaders);

		DBCursor cursorDoc = DBConn.getConn(dbName).getCollection(MongoInstallations.COL_INSTALLATIONS).find();
		while(cursorDoc.hasNext()) {
			DBObject installationsObj = cursorDoc.next();
			String instID = installationsObj.get("_id").toString();
			DBObject installationNode = new BasicDBObject("inst_id",instID);
			DBConn.getConn(dbName).getCollection(COL_CSN_NODES).insert(installationNode);
			numOfNodes++;
		}

		return numOfNodes;
	}
}

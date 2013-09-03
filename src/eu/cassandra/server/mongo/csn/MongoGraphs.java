/*   
   Copyright 2011-2013 The Cassandra Consortium (cassandra-fp7.eu)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package eu.cassandra.server.mongo.csn;

import java.util.Vector;

import javax.ws.rs.core.HttpHeaders;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.cassandra.server.api.csn.SaveGraphImg;
import eu.cassandra.server.api.exceptions.RestQueryParamMissingException;
import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.JSONValidator;
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.MongoDBQueries;

public class MongoGraphs {

	public final static String COL_GRAPHS = "csn_graphs";
	public final static String COL_CSN_NODES = "csn_nodes";
	public final static String COL_CSN_EDGES = "csn_edges";

	public final static String COL_CSN_CLUSTERS = "csn_clusters";
	public final static String COL_CSN_NODES2CLUSTERS = "csn_nodes2clusters";

	public final static String COL_CSN_EDGES_REMOVED = "csn_edges_removed";

	/**
	 * 
	 * @param dataToInsert
	 * @param httpHeaders
	 * @return
	 */
	public String createGraph(String dataToInsert) {
		if(dataToInsert != null) {
			try {
				DBObject answer = new MongoDBQueries().insertData(COL_GRAPHS ,dataToInsert,
						"Graph created successfully",JSONValidator.GRAPH_SCHEMA);
				if(!answer.get("success").toString().equalsIgnoreCase("true")) {
					return new JSONtoReturn().createJSONError(answer.toString(),new Exception()).toString();
				}
				String run_id = ((DBObject)answer.get("data")).get("run_id").toString();
				String graph_id = ((DBObject)(answer.get("data"))).get("_id").toString();
				String graphType = ((DBObject)(answer.get("data"))).get("graphType").toString();
				String minWeight = ((DBObject)(answer.get("data"))).get("minWeight").toString();
				Double minWeightD = null;
				if(minWeight != null) 
					minWeightD = Double.parseDouble(minWeight);

				Vector<DBObject> nodes = new MongoNodes().createNodes(graph_id,run_id);
				((DBObject)answer.get("data")).put("NumberOfNodes", nodes.size());

				Vector<DBObject> edges = new Vector<DBObject>();
				if(!(((DBObject)(answer.get("data"))).containsField("noedges") && ((DBObject)(answer.get("data"))).get("noedges").toString().toLowerCase().equalsIgnoreCase("true"))){
					edges = new MongoEdges().createEdges(nodes, graph_id, graphType, minWeightD, run_id);
					((DBObject)answer.get("data")).put("NumberOfEdges", edges.size());
				}
				else{
					((DBObject)answer.get("data")).put("NumberOfEdges", 0);
				}

				String img = new SaveGraphImg().saveImg(nodes,edges);
				DBObject d = DBConn.getConn().getCollection(COL_GRAPHS).findOne(new BasicDBObject("_id",new ObjectId(graph_id)));
				d.put("img", img);
				DBConn.getConn().getCollection(COL_GRAPHS).save(d);
				((DBObject)answer.get("data")).put("img", img);

				return answer.toString();
			}catch(Exception e) {
				e.printStackTrace();
				return new JSONtoReturn().createJSONError(dataToInsert,e).toString();
			}
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
	public String getGraphs(String run_id,String prj_id) {
		if(run_id != null)
			return new MongoDBQueries().getEntity((HttpHeaders)null,COL_GRAPHS,"run_id",run_id,"CSN graphs retrieved successfully").toString();
		else if(prj_id != null)
			return new MongoDBQueries().getEntity((HttpHeaders)null,COL_GRAPHS,"prj_id",prj_id,"CSN graphs retrieved successfully").toString();
		else
			return new MongoDBQueries().getEntity((HttpHeaders)null, COL_GRAPHS,null,null,"CSN graphs retrieved successfully").toString();
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
		return new MongoDBQueries().deleteDocument(dbName, COL_GRAPHS, id).toString();
	}


	/**
	 * 
	 * @param nodeID
	 * @param httpHeaders
	 * @return
	 */
	public String getNode(String nodeID) {
		return new MongoDBQueries().getEntity((HttpHeaders)null,COL_CSN_NODES,"_id",nodeID,"CSN nodes retrieved successfully").toString();
	}

	/**
	 * 
	 * @param graph_id
	 * @param httpHeaders
	 * @return
	 */
	public String getNodes(String graph_id, String filters, 
			int limit, int skip, boolean count) {
		if(graph_id == null) {
			return new JSONtoReturn().createJSONError(
					"Only the Nodes of a particular Graph can be retrieved", 
					new RestQueryParamMissingException("graph_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(null,COL_CSN_NODES,"graph_id", 
					graph_id, filters, null, limit, skip, "Nodes retrieved successfully",count,(String[])null).toString();
		}
	}

	/**
	 * 
	 * @param edgeID
	 * @param httpHeaders
	 * @return
	 */
	public String getEdge(String edgeID) {
		return new MongoDBQueries().getEntity((HttpHeaders)null,COL_CSN_EDGES,"_id",edgeID,"CSN edges retrieved successfully").toString();
	}


	public String getEdgesRemoved(String clustersid) {
		return new MongoDBQueries().getEntity((HttpHeaders)null,COL_CSN_EDGES_REMOVED,"clustersid",clustersid,
				"CSN edges removed from edge betweeness clusterer retrieved successfully").toString();
	}

	/**
	 * 
	 * @param graph_id
	 * @param httpHeaders
	 * @return
	 */
	public String getEdges(String graph_id, String filters, 
			int limit, int skip, boolean count) {
		if(graph_id == null) {
			return new JSONtoReturn().createJSONError(
					"Only the Edges of a particular Graph can be retrieved", 
					new RestQueryParamMissingException("graph_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(null,COL_CSN_EDGES,"graph_id", 
					graph_id, filters, null, limit, skip, "Edges retrieved successfully",count,(String[])null).toString();
		}
	}
}

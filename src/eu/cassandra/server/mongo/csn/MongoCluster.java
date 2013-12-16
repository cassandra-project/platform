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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.bson.types.ObjectId;

import weka.clusterers.HierarchicalClusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.EuclideanDistance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import eu.cassandra.server.api.csn.SaveGraphImg;
import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.JSONValidator;
import eu.cassandra.server.mongo.util.JSONtoReturn;

public class MongoCluster {

	private Vector<String> nodeIDs = new Vector<String>();

	/**
	 * 
	 * @param message
	 * @param httpHeaders
	 * @return
	 */
	public DBObject cluster(String message) {
		JSONtoReturn jSON2Rrn = new JSONtoReturn();
		try {
			new JSONValidator().isValid(message, JSONValidator.CLUSTER_PARAM_SCHEMA);
			DBObject params = (DBObject)JSON.parse(message);
			String graph_id  = params.get("graph_id").toString();
			String run_id = null;
			String name = params.get("name").toString();
			String clusterbasedon = params.get("clusterbasedon").toString();

			DBObject r = DBConn.getConn().getCollection(MongoGraphs.COL_GRAPHS).findOne(new BasicDBObject("_id",new ObjectId(graph_id)));
			if(r.containsField("run_id")) {
				run_id = r.get("run_id").toString();
			}

			String clusterBasedOn  = params.get("clusterbasedon").toString();
			Integer numberOfClusters = Integer.parseInt(params.get("n").toString());
			String clusterMethod = params.get("clustermethod").toString();
			if(clusterMethod.equalsIgnoreCase("kmeans")) {
				return clusterKmeans(message, graph_id, run_id,clusterBasedOn, numberOfClusters, name, clusterbasedon);
			}
			else if(clusterMethod.equalsIgnoreCase("hierarchical")) {
				return clusterHierarchical(message, graph_id, run_id,clusterBasedOn, numberOfClusters, name, clusterbasedon);
			}
			else if(clusterMethod.equalsIgnoreCase("graphedgebetweenness")) {
				return clusterGraphEgdetweenness(message, graph_id, run_id,clusterBasedOn, numberOfClusters, name, clusterbasedon);
			}
			else 
				return null;
		}catch(Exception e) {
			e.printStackTrace();
			return jSON2Rrn.createJSONError(message,e);
		}
	}

	/**
	 * 
	 * @param clusterBasedOn
	 * @param graph_id
	 * @param httpHeaders
	 * @return
	 */
	private Instances getInstances(String clusterBasedOn, String graph_id) {
		FastVector attributes = new FastVector();
		if(clusterBasedOn.equalsIgnoreCase("hoursP") || clusterBasedOn.equalsIgnoreCase("hoursQ") || clusterBasedOn.equalsIgnoreCase("hoursE"))  {
			for(int i=0;i<24;i++) {
				attributes.addElement(new Attribute("att" + i));
			}
		}else {
			attributes.addElement(new Attribute("att0"));
		}
		Instances instances = new Instances("data", attributes, 0);

		DBCursor nodes = DBConn.getConn().getCollection(
				MongoGraphs.COL_CSN_NODES).find(new BasicDBObject("graph_id",graph_id));
		//Get all nodes
		while(nodes.hasNext()) {
			double[] values = null;
			DBObject installationDBObj = nodes.next();
			nodeIDs.add(installationDBObj.get("_id").toString());
			//If graph was build based on Person or Installation Type do nothing
			if(clusterBasedOn.equalsIgnoreCase(MongoEdges.PersonType) || 
					clusterBasedOn.equalsIgnoreCase(MongoEdges.InstallationType) ||
					clusterBasedOn.equalsIgnoreCase(MongoEdges.TransformerID)	||     
					clusterBasedOn.equalsIgnoreCase(MongoEdges.TopologicalDistance) ||
					clusterBasedOn.equalsIgnoreCase(MongoEdges.Location) ||
					clusterBasedOn.equalsIgnoreCase(MongoEdges.Location) ||
					clusterBasedOn.equalsIgnoreCase(MongoEdges.SocialDistance)) {
				continue;
			}
			else {
				Object vS =  installationDBObj.get(CSNTypes.getCsnTypes(clusterBasedOn));
				if(vS != null) {
					if(clusterBasedOn.equalsIgnoreCase("hoursP") || clusterBasedOn.equalsIgnoreCase("hoursQ") || 
							clusterBasedOn.equalsIgnoreCase("hoursE"))  {
						if(vS instanceof BasicDBList) {
							BasicDBList v = (BasicDBList)vS;
							values = new double[v.size()];
							for(int i=0;i<v.size();i++) {
								Object d = v.get(i);
								if(d instanceof Double) {
									values[i] = (Double)d;
								}
							}
						}
					}
					else {
						Double v = Double.parseDouble(vS.toString());
						values = new double[1];
						values[0] = v;
					}
				}
			}
			if(values != null) {
				Instance instance = new Instance(1,values);
				instances.add(instance);
			}
		}
		nodes.close();
		return instances;
	}

	/**
	 * 
	 * @param message
	 * @param graph_id
	 * @param clusterBasedOn
	 * @param numberOfClusters
	 * @param httpHeaders
	 * @return
	 */
	private DBObject clusterKmeans(String message, String graph_id, String run_id, String clusterBasedOn, int numberOfClusters, String name, String clusterbasedon) {
		try {
			Instances instances = getInstances(clusterBasedOn, graph_id);
			if(instances.numInstances() < 2) {
				return new JSONtoReturn().createJSONError(message,new Exception("Number of CSN Nodes is < 2"));
			}

			SimpleKMeans kmeans = new SimpleKMeans();
			kmeans.setSeed((int)Calendar.getInstance().getTimeInMillis());
			// This is the important parameter to set
			kmeans.setPreserveInstancesOrder(true);
			kmeans.setNumClusters(numberOfClusters);
			kmeans.buildClusterer(instances);

			// This array returns the cluster number (starting with 0) for each instance
			// The array has as many elements as the number of instances
			int[] assignments = kmeans.getAssignments();

			int i=0;
			HashMap<Integer,Vector<String>> clusters = new HashMap<Integer,Vector<String>>();
			for(int clusterNum : assignments) {
				if(clusters.containsKey(clusterNum)) {
					Vector<String> cluster = clusters.get(clusterNum);
					cluster.add(nodeIDs.get(i));
					clusters.put(clusterNum, cluster);
				}
				else {
					Vector<String> cluster = new Vector<String>();
					cluster.add(nodeIDs.get(i));
					clusters.put(clusterNum, cluster);
				}
				i++;
			}
			nodeIDs.clear();
			return saveClusters(graph_id, run_id, "kmeans", clusters, null, name, clusterbasedon);
		}catch(Exception e) {
			e.printStackTrace();
			return new JSONtoReturn().createJSONError(message,e);
		}
	}

	public DBObject clusterHierarchical(String message, String graph_id, String run_id, String clusterBasedOn, int numberOfClusters, String name, String clusterbasedon) {
		try {
			Instances instances = getInstances(clusterBasedOn, graph_id);
			if(instances.numInstances() < 2) {
				return new JSONtoReturn().createJSONError(message,new Exception("Number of CSN Nodes is < 2"));
			}

			HierarchicalClusterer h = new HierarchicalClusterer();
			h.setOptions(new String[] {"-L", "AVERAGE"});
			h.setDistanceFunction(new EuclideanDistance());
			if(numberOfClusters > 0)
				h.setNumClusters(numberOfClusters);
			h.buildClusterer(instances);

			HashMap<Integer,Vector<String>> clusters = new HashMap<Integer,Vector<String>>();
			double[] arr;
			for(int i=0; i<instances.numInstances(); i++) {
				String nodeId = nodeIDs.get(i);
				arr = h.distributionForInstance(instances.instance(i));
				for(int j=0; j< arr.length; j++) {
					if(arr[j] == 1.0) {
						if(!clusters.containsKey(j)) {
							Vector<String> nodes = new Vector<String>();
							nodes.add(nodeId);
							clusters.put(j, nodes);
						}else {
							Vector<String> nodes = clusters.get(j);
							nodes.add(nodeId);
							clusters.put(j, nodes);
						}
					}
				}
			}
			return saveClusters(graph_id, run_id, "hierarchical", clusters, null, name, clusterbasedon);
		}catch(Exception e) {
			e.printStackTrace();
			return new JSONtoReturn().createJSONError(message,e);
		}
	}


	public DBObject clusterGraphEgdetweenness(String message, String graph_id, String run_id, String clusterBasedOn, int numberOfEdgesToRemove, String name, String clusterbasedon) {
		try {
			UndirectedSparseGraph<String, CEdge> graph = new UndirectedSparseGraph<String, CEdge>();

			DBCursor nodes = DBConn.getConn().getCollection(
					MongoGraphs.COL_CSN_NODES).find(new BasicDBObject("graph_id",graph_id));
			while(nodes.hasNext()) {
				DBObject installationDBObj = nodes.next();
				graph.addVertex(installationDBObj.get("_id").toString());
			}
			nodes.close();

			DBCursor edges = DBConn.getConn().getCollection(
					MongoGraphs.COL_CSN_EDGES).find(new BasicDBObject("graph_id",graph_id));
			while(edges.hasNext()) {
				DBObject edgeObj = edges.next();
				String edgeId = edgeObj.get("_id").toString();
				String node1 = edgeObj.get("inst_id1").toString();
				String node2 = edgeObj.get("inst_id2").toString();
				CEdge CEdge = new CEdge(edgeId,node1,node2);
				graph.addEdge(CEdge, node1,node2);
			}
			edges.close();

			EdgeBetweennessClusterer<String, CEdge> clusterer = new EdgeBetweennessClusterer<String, CEdge>(numberOfEdgesToRemove);
			Set<Set<String>> allClusters = clusterer.transform(graph);
			Iterator<Set<String>> allClustersIter = allClusters.iterator();

			HashMap<Integer,Vector<String>> clusters = new HashMap<Integer,Vector<String>>();
			int clusterN = 0;
			while(allClustersIter.hasNext()) {
				Set<String> cluster = allClustersIter.next();
				Iterator<String> clusterIter = cluster.iterator();
				while(clusterIter.hasNext()) {
					String node = clusterIter.next();

					if(clusters.containsKey(clusterN)) {
						Vector<String> c = clusters.get(clusterN);
						c.add(node);
						clusters.put(clusterN, c);
					}
					else {
						Vector<String> c = new  Vector<String>();
						c.add(node);
						clusters.put(clusterN, c);
					}
				}
				clusterN++;
			}

			List<CEdge> edgesRemoved = clusterer.getEdgesRemoved();

			return saveClusters(graph_id, run_id, "graphedgebetweenness", clusters, edgesRemoved, name, clusterbasedon);
		}catch(Exception e) {
			e.printStackTrace();
			return new JSONtoReturn().createJSONError(message,e);
		}
	}

	/**
	 * 
	 * @param graph_id
	 * @param method
	 * @param clusters
	 * @param httpHeaders
	 * @return
	 */
	private DBObject saveClusters(String graph_id, String run_id, String method, HashMap<Integer,Vector<String>> clusters, List<CEdge> edgesRemoved, String name, String clusterbasedon) {
		JSONtoReturn jSON2Rrn = new JSONtoReturn();
		DBObject clusterObject = new BasicDBObject("clustermethod",method);
		try {
			ObjectId objectId = new ObjectId();
			clusterObject.put("_id", objectId);
			clusterObject.put("graph_id", graph_id);
			clusterObject.put("run_id", run_id);
			clusterObject.put("name", name);
			clusterObject.put("clusterbasedon", clusterbasedon);

			Vector<DBObject> nodes = new Vector<DBObject>();
			HashMap<String,String> map = new HashMap<String, String>();
			for(Vector<String> cluster : clusters.values()) {
				for(int i=0;i<cluster.size();i++) {
					String id = cluster.get(i);
					DBObject node = new BasicDBObject("_id",new ObjectId());
					map.put(id, node.get("_id").toString());
					nodes.add(node);
				}
			}
			Vector<DBObject> edges = new Vector<DBObject>();
			for(Vector<String> cluster : clusters.values()) {
				for(int i=0;i<cluster.size();i++) {
					for(int j=i+1;j<cluster.size();j++) {
						String id = cluster.get(i);
						String id2 = cluster.get(j);
						DBObject edge = new BasicDBObject("_id",new ObjectId());
						edge.put("node_id2", map.get(id2) );
						edge.put("node_id1", map.get(id));
						edges.add(edge);
					}
				}
			}
			String img = new SaveGraphImg().saveImg(nodes, edges);
			clusterObject.put("img", img);
			clusterObject.put("pajek", img + ".pajek");

			Vector<DBObject> allClusters = new Vector<DBObject>();

			clusterObject.put("n", clusters.keySet().size());
			for(int j=0;j<clusters.keySet().size();j++) {
				DBObject cl = new BasicDBObject();
				cl.put("name", "cluster" + j);
				cl.put("pricing_id", "");
				cl.put("installations", clusters.get(j));

				allClusters.add(cl);
				//clusterObject.put("cluster_" + j, clusters.get(j));
			}
			clusterObject.put("clusters",allClusters);
			DBConn.getConn().getCollection(MongoGraphs.COL_CSN_CLUSTERS).insert(clusterObject);

			//Inverse (node to cluster) 
			for(int j=0;j<clusters.keySet().size();j++) {
				Vector<String> nodeIDs = clusters.get(j);
				for(String nodeID : nodeIDs) {
					DBObject ob = new BasicDBObject("node_id",nodeID);
					ob.put("cluster", "cluster_" + j);
					ob.put("graph_id", graph_id);
					ob.put("clustersid", objectId.toString());
					if(run_id != null)
						ob.put("run_id", run_id);
					DBConn.getConn().getCollection(MongoGraphs.COL_CSN_NODES2CLUSTERS).insert(ob);
				}
			}

			//Edges Removed
			if(edgesRemoved != null) {
				for(int i=0;i<edgesRemoved.size();i++) {
					String edgeID = edgesRemoved.get(i).getEdgeId();
					DBObject ob = new BasicDBObject("edge_id",edgeID);
					ob.put("graph_id", graph_id);
					ob.put("clustersid", objectId.toString());
					if(run_id != null)
						ob.put("run_id", run_id);
					DBConn.getConn().getCollection(MongoGraphs.COL_CSN_EDGES_REMOVED).insert(ob);
				}
			}
			return jSON2Rrn.createJSONInsertPostMessage("Clusters Created", clusterObject) ;
		}catch(com.mongodb.util.JSONParseException e) {
			return jSON2Rrn.createJSONError("Error parsing JSON input", e.getMessage());
		}catch(Exception e) {
			return jSON2Rrn.createJSONError(clusterObject.toString(),e);
		}
	}
}

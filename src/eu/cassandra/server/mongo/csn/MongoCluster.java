package eu.cassandra.server.mongo.csn;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import javax.ws.rs.core.HttpHeaders;

import org.bson.types.ObjectId;

import weka.clusterers.HierarchicalClusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.EuclideanDistance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.JSONValidator;
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.MongoDBQueries;

public class MongoCluster {

	private Vector<String> nodeIDs = new Vector<String>();

	/**
	 * 
	 * @param message
	 * @param httpHeaders
	 * @return
	 */
	public DBObject cluster(String message, HttpHeaders httpHeaders) {
		JSONtoReturn jSON2Rrn = new JSONtoReturn();
		try {
			new JSONValidator().isValid(message, JSONValidator.CLUSTER_PARAM_SCHEMA);
			DBObject params = (DBObject)JSON.parse(message);
			String graph_id  = params.get("graph_id").toString();
			String clusterBasedOn  = params.get("clusterbasedon").toString();
			int numberOfClusters = Integer.parseInt(params.get("n").toString());
			String clusterMethod = params.get("clustermethod").toString();
			if(clusterMethod.equalsIgnoreCase("kmeans")) {
				return clusterKmeans(message, graph_id, clusterBasedOn, numberOfClusters, httpHeaders);
			}
			else if(clusterMethod.equalsIgnoreCase("hierarchical")) {
				return clusterHierarchical(message, graph_id, clusterBasedOn, numberOfClusters, httpHeaders);
			}
			else if(clusterMethod.equalsIgnoreCase("graphedgebetweenness")) {
				return null;
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
	private Instances getInstances(String clusterBasedOn, String graph_id, HttpHeaders httpHeaders) {
		FastVector attributes = new FastVector();
		if(!clusterBasedOn.toLowerCase().contains("per")) {
			attributes.addElement(new Attribute("att0"));
		}else {
			for(int i=0;i<24;i++) 
				attributes.addElement(new Attribute("att" + i));
		}
		Instances instances = new Instances("data", attributes, 0);

		DBCursor nodes = DBConn.getConn(MongoDBQueries.getDbNameFromHTTPHeader(httpHeaders)).getCollection(
				MongoGraphs.COL_CSN_NODES).find(new BasicDBObject("graph_id",graph_id));
		//Get all nodes
		while(nodes.hasNext()) {
			double[] values = null;
			DBObject installationDBObj = nodes.next();
			nodeIDs.add(installationDBObj.get("_id").toString());
			//If graph was build based on Person or Installation Type do nothing
			if(clusterBasedOn.equals(MongoEdges.PersonType) || clusterBasedOn.equals(MongoEdges.InstallationType)) {
				continue;
			}
			else {
				Object vS =  installationDBObj.get(CSNTypes.getCsnTypes(clusterBasedOn));
				if(vS != null) {
					if(!clusterBasedOn.toLowerCase().contains("per")) {
						Double v = Double.parseDouble(vS.toString());
						values = new double[1];
						values[0] = v;
					}
				}
				else if(clusterBasedOn.toLowerCase().contains("per")) {
					if(vS instanceof Vector<?>) {
						Vector<?> v = (Vector<?>)vS;
						values = new double[v.size()];
						for(int i=0;i<v.size();i++) {
							Object d = v.get(i);
							if(d instanceof Double) {
								values[i] = (Double)d;
								System.out.println(values[i]);
							}
						}
					}
				}
			}
			if(values != null) {
				Instance instance = new Instance(1,values);
				System.out.println((instance==null?true:false) + "\t" + values.length);
				System.out.println(instance.numAttributes() );
				System.out.println(instance.toString());
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
	private DBObject clusterKmeans(String message, String graph_id, String clusterBasedOn, int numberOfClusters, HttpHeaders httpHeaders) {
		try {
			Instances instances = getInstances(clusterBasedOn, graph_id,httpHeaders);

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
				//System.out.printf("Instance %d -> Cluster %d \n", i, clusterNum);
				i++;
			}
			nodeIDs.clear();
			return saveClusters(graph_id, "kmeans", clusters, httpHeaders);
		}catch(Exception e) {
			e.printStackTrace();
			return new JSONtoReturn().createJSONError(message,e);
		}
	}

	public DBObject clusterHierarchical(String message, String graph_id, String clusterBasedOn, int numberOfClusters, HttpHeaders httpHeaders) {
		try {
			Instances instances = getInstances(clusterBasedOn, graph_id,httpHeaders);

			HierarchicalClusterer h = new HierarchicalClusterer();
			h.setOptions(new String[] {"-L", "AVERAGE"});
			h.setDistanceFunction(new EuclideanDistance());
			if(numberOfClusters > 0)
				h.setNumClusters(numberOfClusters);
			h.buildClusterer(instances);

			double[] arr;
			for(int i=0; i<instances.numInstances(); i++) {
				arr = h.distributionForInstance(instances.instance(i));
				for(int j=0; j< arr.length; j++)
					System.out.print(arr[j]+",");
				System.out.println();

			}

			System.out.println(h.numberOfClusters());


			//////////////////////////////////////////
			//			SimpleKMeans kmeans = new SimpleKMeans();
			//			kmeans.setSeed((int)Calendar.getInstance().getTimeInMillis());
			//			// This is the important parameter to set
			//			kmeans.setPreserveInstancesOrder(true);
			//			kmeans.setNumClusters(numberOfClusters);
			//			kmeans.buildClusterer(instances);
			//
			//			// This array returns the cluster number (starting with 0) for each instance
			//			// The array has as many elements as the number of instances
			//			int[] assignments = kmeans.getAssignments();
			//
			//			int i=0;
			HashMap<Integer,Vector<String>> clusters = new HashMap<Integer,Vector<String>>();
			//			for(int clusterNum : assignments) {
			//				if(clusters.containsKey(clusterNum)) {
			//					Vector<String> cluster = clusters.get(clusterNum);
			//					cluster.add(nodeIDs.get(i));
			//					clusters.put(clusterNum, cluster);
			//				}
			//				else {
			//					Vector<String> cluster = new Vector<String>();
			//					cluster.add(nodeIDs.get(i));
			//					clusters.put(clusterNum, cluster);
			//				}
			//				//System.out.printf("Instance %d -> Cluster %d \n", i, clusterNum);
			//				i++;
			//			}
			//			nodeIDs.clear();
			return saveClusters(graph_id, "kmeans", clusters, httpHeaders);
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
	private DBObject saveClusters(String graph_id, String method, HashMap<Integer,Vector<String>> clusters, HttpHeaders httpHeaders) {
		JSONtoReturn jSON2Rrn = new JSONtoReturn();
		DBObject clusterObject = new BasicDBObject("method",method);
		ObjectId objectId = new ObjectId();
		clusterObject.put("_id", objectId);
		clusterObject.put("graph_id", graph_id);
		try {
			clusterObject.put("n", clusters.keySet().size());
			for(int j=0;j<clusters.keySet().size();j++) {
				clusterObject.put("cluster_" + j, clusters.get(j));
			}
			String dbName = MongoDBQueries.getDbNameFromHTTPHeader(httpHeaders);
			DBConn.getConn(dbName).getCollection(MongoGraphs.COL_CSN_CLUSTERS).insert(clusterObject);

			//Inverse (node to cluster) 
			for(int j=0;j<clusters.keySet().size();j++) {
				Vector<String> nodeIDs = clusters.get(j);
				for(String nodeID : nodeIDs) {
					DBObject ob = new BasicDBObject("node_id",nodeID);
					ob.put("cluster", "cluster_" + j);
					ob.put("graph_id", graph_id);
					ob.put("clustersid", objectId.toString());
					DBConn.getConn(dbName).getCollection(MongoGraphs.COL_CSN_NODES2CLUSTERS).insert(ob);
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

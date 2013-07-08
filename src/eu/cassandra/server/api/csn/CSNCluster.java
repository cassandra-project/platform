package eu.cassandra.server.api.csn;

import java.util.Calendar;
import java.util.Vector;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import eu.cassandra.server.mongo.csn.CSNTypes;
import eu.cassandra.server.mongo.csn.MongoEdges;
import eu.cassandra.server.mongo.csn.MongoGraphs;
import eu.cassandra.server.mongo.csn.MongoNodes;
import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.MongoDBQueries;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.utilities.Utils;

@Path("csncluster")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CSNCluster {

	//curl -k -i --data  @graph.json    --header Content-type:application/json --header dbname:51c34c7a712efe578ab670f6 https://localhost:8443/cassandra/api/csn
	@POST
	public Response clusterGraph(String message,@Context HttpHeaders httpHeaders) {
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new MongoGraphs().createGraph(message,httpHeaders)));
	}


	private void clusterKmeans(String graph_id, String clusterBasedOn, int numberOfClusters, HttpHeaders httpHeaders) throws Exception {
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
		while(nodes.hasNext()) {
			double[] values = null;
			DBObject installationDBObj = nodes.next();
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
					else {
						if(vS instanceof Vector<?>) {
							Vector<?> v = (Vector<?>)vS;
							values = new double[v.size()];
							for(int i=0;i<v.size();i++) {
								Object d = v.get(i);
								if(d instanceof Double) {
									values[i] = (Double)d;
								}
							}
						}
					}
				}
			}
			Instance instance = new Instance(1,values);
			instances.add(instance);
		}
		nodes.close();


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
		for(int clusterNum : assignments) {
			System.out.printf("Instance %d -> Cluster %d", i, clusterNum);
			i++;
		}
	}

}

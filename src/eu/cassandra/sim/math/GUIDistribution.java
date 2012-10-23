package eu.cassandra.sim.math;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.server.api.exceptions.JSONSchemaNotValidException;
import eu.cassandra.server.mongo.MongoActivityModels;
import eu.cassandra.sim.utilities.Utils;

public class GUIDistribution {

	private ProbabilityDistribution prob;	

	/**
	 * 
	 * @param type
	 * @param dbo
	 * @throws JSONSchemaNotValidException 
	 */
	public GUIDistribution(String type, DBObject dbo) throws JSONSchemaNotValidException{
		String distrType = dbo.get("distrType").toString();
		BasicDBList tempList = (BasicDBList)dbo.get("parameters");
		DBObject parameters = (DBObject)JSON.parse(tempList.get(0).toString());

		int endValue = 0;
		switch(type){
		case MongoActivityModels.REF_DISTR_STARTTIME : 
			endValue = 1440;
			break;
		case MongoActivityModels.REF_DISTR_DURATION:	
			endValue = 180;
			break;
		case MongoActivityModels.REF_DISTR_REPEATS :	
			endValue = 10;
			break;
		default:
			throw new JSONSchemaNotValidException("Invalid distribution type: " + type);
		}

		switch(distrType){
		case "Normal Distribution":
			double mean = (double)parameters.get("mean");
			double std = (double)parameters.get("std");
			prob = new Gaussian(mean, std);
			if (type.equalsIgnoreCase("Duration") || type.equalsIgnoreCase("Daily Times")) 
				endValue = (int)(mean + 4*std);
			prob.precompute(endValue);
			break;
		case "Uniform Distribution":
			double start = (double)parameters.get("start");
			double end = (double)parameters.get("end");
			prob = new Uniform(start, end);
			if (type.equalsIgnoreCase("duration")){
				endValue = (int)end + 10;
			}
			prob.precompute(endValue);
			break;

		case "Gaussian Mixture Models":
			double[] pi = Utils.dblist2doubleArr((BasicDBList)parameters.get("pi")); 
			double[] means = Utils.dblist2doubleArr((BasicDBList)parameters.get("means"));
			double[] sigmas = Utils.dblist2doubleArr((BasicDBList)parameters.get("sigmas"));
			prob = new GaussianMixtureModels(pi.length ,pi, means, sigmas);
			prob.precompute(endValue);
			break;
		case "Histogram":
			double[] values = Utils.dblist2doubleArr((BasicDBList)parameters.get("values"));
			prob = new Histogram(values);
			break;
		default:
			throw new JSONSchemaNotValidException("Invalid distribution type: " + type);

		}
	}

	/**
	 * 
	 * @return
	 */
	public double[] getValues(){
		return prob.getHistogram();
	}
}



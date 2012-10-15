package eu.cassandra.sim.math;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.sim.utilities.RNG;
import eu.cassandra.sim.utilities.Utils;

public class GUIDistribution {

	ProbabilityDistribution prob;	
	
	public GUIDistribution(String type, DBObject dbo){
		
		String distrType = dbo.get("distrType").toString();
		BasicDBList tempList = (BasicDBList)dbo.get("parameters");
		DBObject parameters = (DBObject)JSON.parse(tempList.get(0).toString());
		System.out.println(parameters.keySet().toString());
					
		int endValue = 0;
		
		switch(type){
		
		case "Start Time": 
			endValue = 1440;
			break;
			
		case "Duration":	
			endValue = 180;
			break;
			
		case "Daily Times":	
			endValue = 10;
			break;
		
		default:
			System.out.println("Invalid Type of Variable");
			// TODO Throw exception or whatever.
			
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
			
			System.out.println("Invalid Type of Distribution");
			// TODO Throw exception or whatever.
		
		}

	}
	
	public double[] getValues(){
		
		return prob.getHistogram();
		
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		String res = readFile("example.json");
		
		DBObject obj = (DBObject)JSON.parse(res); 
		
		GUIDistribution guid = new GUIDistribution("Duration",obj);
		
	}

	private static String readFile( String file ) throws IOException {
	    BufferedReader reader = new BufferedReader( new FileReader (file));
	    String         line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String         ls = System.getProperty("line.separator");

	    while( ( line = reader.readLine() ) != null ) {
	        stringBuilder.append( line );
	        stringBuilder.append( ls );
	    }

	    return stringBuilder.toString();
	}
	
}



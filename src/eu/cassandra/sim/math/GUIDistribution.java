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
package eu.cassandra.sim.math;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.server.api.exceptions.JSONSchemaNotValidException;
import eu.cassandra.server.mongo.MongoActivityModels;
import eu.cassandra.sim.utilities.Utils;

public class GUIDistribution {

	private ProbabilityDistribution prob = null;	

	/**
	 * 
	 * @param type
	 * @param dbo
	 * @throws JSONSchemaNotValidException 
	 */
	public GUIDistribution(String type, DBObject dbo) throws JSONSchemaNotValidException{
		String distrType = dbo.get("distrType").toString();
		BasicDBList tempList = (BasicDBList)dbo.get("parameters");
		if(tempList.size()==0)
			return;

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
			throw new JSONSchemaNotValidException("Invalid distr type: " + type);
		}
		switch(distrType){
		case "Normal Distribution":
			double mean = Double.parseDouble(parameters.get("mean").toString());
			double std = Double.parseDouble(parameters.get("std").toString());
			prob = new Gaussian(mean, std);
			if (type.equalsIgnoreCase("Duration") || type.equalsIgnoreCase("Daily Times")) 
				endValue = (int)(mean + 4*std);
			prob.precompute(endValue);
			break;
		case "Uniform Distribution":
			double start = Double.parseDouble(parameters.get("start").toString());
			double end = Double.parseDouble(parameters.get("end").toString());
			System.out.println(start + " " + end);
			if (type.equalsIgnoreCase("duration")){
				end = (int)end + 10;
			}
			if (type.equalsIgnoreCase("duration")){
				prob = new Uniform(start, end, false);
			} else {
				prob = new Uniform(start, end, true);
			}
//			prob.precompute(endValue);
			break;
		case "Gaussian Mixture Models":
   			int length = tempList.size();
   			double[] w = new double[length];
         	double[] means = new double[length];
         	double[] stds = new double[length];
         	for(int i = 0; i < tempList.size(); i++) {
         		DBObject tuple = (DBObject)tempList.get(i);
         		w[i] = Double.parseDouble(tuple.get("w").toString()); 
         		means[i] = Double.parseDouble(tuple.get("mean").toString()); 
         		stds[i] = Double.parseDouble(tuple.get("std").toString()); 
    		} 
         	prob = new GaussianMixtureModels(length, w, means, stds);
         	prob.precompute(0, 1439, 1440);
			break;
		case "Histogram":
			double[] values = Utils.dblist2doubleArr((BasicDBList)parameters.get("values"));
			prob = new Histogram(values);
			break;
		default:
			throw new JSONSchemaNotValidException("Invalid distribution2 type: " + distrType);

		}
	}

	/**
	 * 
	 * @return
	 */
	public double[] getValues(){
		if(prob == null)
			return null;
		else
			return prob.getHistogram();
	}
}



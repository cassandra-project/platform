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
package eu.cassandra.server.mongo.util;

import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bson.types.ObjectId;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class MongoQueries {
	
	/** 	Returns energy percentages (based on the simulation experiment's KPI calculations) per appliance/activity type 
	 * 		and, if required, per installation. 
	 * @param collection	The collection, based on which the energy sums will be computed ("appliances" or "activities")
	 * @param inst_id		The id of the target installation. If null computations are about the whole simulation.
	 * @return
	 */
	private static HashMap<String, Double> getEnergyPercentagesPerType(DBCollection collection, String inst_id)
	{
		HashMap<String, Double> results = getEnergySumsPerType(collection, inst_id);
		double sum = 0;
		for (String key : results.keySet())
			sum += results.get(key);  
		for (String key : results.keySet())
			results.put(key, results.get(key)/sum*100);
		return results;
	}
	
	/** 	Returns energy sums (based on the simulation experiment's KPI calculations) per appliance/activity type 
	 * 		and, if required, per installation. 
	 * @param collection	The collection, based on which the energy sums will be computed ("appliances" or "activities")
	 * @param inst_id		The id of the target installation. If null computations are about the whole simulation.
	 * @return
	 */
	private static HashMap<String, Double> getEnergySumsPerType(DBCollection collection, String inst_id)
	{
		if (!collection.getName().equals("appliances") && ! collection.getName().equals("activities"))
		{
			System.err.println("Method only applicable to collections 'appliances' and 'activities'");
			System.exit(5);
		}
		
		HashMap<String, Double> results = new HashMap<String, Double>();
		
		List<String> applTypes = collection.distinct("type");
		
		for (String temp : applTypes)
		{
			String type = temp.equals("")?"other":temp;
			
			DBCursor c;
			
			
			if (inst_id != null)
			{
				BasicDBObject andQuery = new BasicDBObject();
				List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
				obj.add(new BasicDBObject("type", temp));
				if (collection.getName().equals("appliances"))
				{
					obj.add(new BasicDBObject("inst_id", inst_id));
					andQuery.put("$and", obj);
				}
				else 
				{
					BasicDBList persons = new BasicDBList();
					BasicDBObject query = new BasicDBObject("inst_id", inst_id);
					c = collection.getDB().getCollection("persons").find(query);
					while (c.hasNext()) {
						String tempS = ""+c.next().get("_id");
						persons.add(tempS);
					}
					BasicDBObject query2 = new BasicDBObject("pers_id", new BasicDBObject("$in", persons));
					obj.add(query2);
					andQuery.put("$and", obj);
				}
				c = collection.find(andQuery, new BasicDBObject("_id", 1));
			}
			else
			{
				BasicDBObject query = new BasicDBObject("type", temp);
				c = collection.find(query, new BasicDBObject("_id", 1));
			}
			
			BasicDBList list = new BasicDBList();
			while (c.hasNext()) {
				String tempS = ""+c.next().get("_id");
				list.add(tempS);
			}
			
			String kpis_coll_name = collection.getName().equals("appliances")?"app_kpis":"act_kpis";
			String id_name = collection.getName().equals("appliances")?"app_id":"act_id";
			DBCollection coll_kpis = collection.getDB().getCollection(kpis_coll_name);
			
			BasicDBObject query2 = new BasicDBObject(id_name, new BasicDBObject("$in", list));
			DBObject match = new BasicDBObject("$match", query2);  
			
			DBObject fields = new BasicDBObject("energy", 1);
			DBObject project = new BasicDBObject("$project", fields);
			
			DBObject groupFields = new BasicDBObject( "_id", null);
			groupFields.put("sumEnergy", new BasicDBObject( "$sum", "$energy"));
			DBObject group = new BasicDBObject("$group", groupFields);
							
			List<DBObject> pipeline = Arrays.asList(match, project, group);
			AggregationOutput output = coll_kpis.aggregate(pipeline);
			for (DBObject result : output.results()) {
				double sum = Double.parseDouble(""+result.get("sumEnergy"));
				results.put(type, sum);
			}
		}
		return results;
	}
	
	private static HashMap<String, Double[]> getEnergyLimitsPerType(DB db, DBCollection collection)
	{
		if (!collection.getName().equals("appliances") && ! collection.getName().equals("activities"))
		{
			System.err.println("Method only applicable to collections 'appliances' and 'activities'");
			System.exit(5);
		}
		
		HashMap<String, Double[]> limitsPerType = new HashMap<String, Double[]>();
		
		List<String> applTypes = collection.distinct("type");
		for (String type: applTypes)
		{
			Double[] lala = {Double.MIN_VALUE, Double.MAX_VALUE};
			limitsPerType.put(type.equals("")?"other":type, lala);
		}
		
		DBCollection installations = collection.getDB().getCollection("installations");
		List<ObjectId> inst_ids = installations.distinct("_id");
		
		double maxWholeHouse = Double.MIN_VALUE;
		double minWholeHouse = Double.MAX_VALUE;
		
		for (ObjectId inst_id : inst_ids)
		{
			HashMap<String, Double> resultsPerInst;
			if (collection.getName().equals("appliances") )
					resultsPerInst = getEnergySumsPerApplianceType(db, collection.getDB().getName(), inst_id+"");
			else
				resultsPerInst = getEnergySumsPerActivityType(db, collection.getDB().getName(), inst_id+"");
			double wholeHouse = sum(resultsPerInst);
			if (wholeHouse > maxWholeHouse)
				maxWholeHouse = wholeHouse;
			if (wholeHouse < minWholeHouse)
				minWholeHouse = wholeHouse;
			for (String key : resultsPerInst.keySet())
			{
//				if (key.equals(""))
//					key = "other";
				double value = Double.parseDouble(resultsPerInst.get(key).toString());  
				Double[] limits = limitsPerType.get(key);
				if (value > limits[0])
					limits[0] = value;
					limitsPerType.put(key, limits);
				if (value < limits[1])
					limits[1] = value;
					limitsPerType.put(key, limits);
			}
		}
		
		Double[] temp = new Double[2];
		temp[0] = maxWholeHouse;
		temp[1] = minWholeHouse;
		limitsPerType.put("Whole house", temp);
		return limitsPerType;
	}
	
	private static HashMap<String, Double[]> getEnergyLimitsPerActivityType(DB adb, String dbname)
	{
		DB db = adb;
		DBCollection activities = db.getCollection("activities");
		return getEnergyLimitsPerType(db, activities);
	}
	
	private static HashMap<String, Double[]> getEnergyLimitsPerApplianceType(DB adb, String dbname)
	{
		DB db = adb;
		DBCollection appliances = db.getCollection("appliances");
		return getEnergyLimitsPerType(db, appliances);
	}
	
	
	/** 	Get energy sums (based on the simulation experiment's KPI calculations) per appliance type 
	 * 		and, if required, per installation. 
	 * @param collection	The name of the database, where the target simulation is stored.
	 * @param inst_id		The id of the target installation. If null computations are about the whole simulation.
	 * @return
	 */
	public static HashMap<String, Double> getEnergySumsPerApplianceType(DB adb, String dbname, String inst_id)
	{
		HashMap<String, Double> temp = new HashMap<String, Double>();
		DB db = adb;
		DBCollection appliances = db.getCollection("appliances");
		temp =  getEnergySumsPerType(appliances, inst_id);
		return temp;	
	}
	
	/** 	Get energy sums (based on the simulation experiment's KPI calculations) per activity type 
	 * 		and, if required, per installation. 
	 * @param collection	The name of the database, where the target simulation is stored.
	 * @param inst_id		The id of the target installation. If null computations are about the whole simulation.
	 * @return
	 */
	public static HashMap<String, Double> getEnergySumsPerActivityType(DB adb, String dbname, String inst_id)
	{
		HashMap<String, Double> temp = new HashMap<String, Double>();
		DB db = adb;
		DBCollection activities = db.getCollection("activities");
		temp = getEnergySumsPerType(activities, inst_id);
		return temp;	
	}
	
	/** 	Get energy percentages (based on the simulation experiment's KPI calculations) per appliance type 
	 * 		and, if required, per installation. 
	 * @param dbname	The name of the database, where the target simulation is stored.
	 * @param inst_id		The id of the target installation. If null computations are about the whole simulation.
	 * @return
	 */
	public static HashMap<String, Double> getEnergyPercentagesPerApplianceType(DB adb, String dbname, String inst_id)
	{
		HashMap<String, Double> temp = new HashMap<String, Double>();
		DB db = adb;
		DBCollection appliances = db.getCollection("appliances");
		temp = getEnergyPercentagesPerType(appliances, inst_id);
		return temp;	
	}
	
	/** 	Get energy percentages (based on the simulation experiment's KPI calculations) per activity type 
	 * 		and, if required, per installation. 
	 * @param dbname	The name of the database, where the target simulation is stored.
	 * @param inst_id		The id of the target installation. If null computations are about the whole simulation.
	 * @return
	 */
	public static HashMap<String, Double> getEnergyPercentagesPerActivityType(DB adb, String dbname, String inst_id)
	{
		HashMap<String, Double> temp = new HashMap<String, Double>();
		DB db = adb;
		DBCollection activities = db.getCollection("activities");
		temp =  getEnergyPercentagesPerType(activities, inst_id);
		return temp;	
	}
	
	/** 	Get energy sums (based on the simulation experiment's KPI calculations) per appliance type and installation,
	 * 		along with the corresponding limits for defining the "efficient", "average" and "inefficient" ranges (per appliance type).
	 * @param dbname	The name of the database, where the target simulation is stored.
	 * @param inst_id		The id of the target installation. Cannot be null.
	 * @return
	 */
	public static HashMap<String, Double[]> getEnergySumsWithLimitsPerApplianceType(DB adb, String dbname, String inst_id)
	{
		if (inst_id == null)
		{
			System.err.println("The id of the target installation cannot be null.");
			System.exit(2);
		}
		
		HashMap<String, Double[]> results = new HashMap<String, Double[]>();
		HashMap<String, Double[]> limits = getEnergyLimitsPerApplianceType(adb, dbname);
		HashMap<String, Double> values = getEnergySumsPerApplianceType(adb, dbname, inst_id);
		values.put("Whole house",  sum(values));
		for (String key : values.keySet())
		{	
			Double[] temp = new Double[4];
			temp[0] = values.get(key);
			double dist = Double.parseDouble(limits.get(key)[0].toString()) - Double.parseDouble(limits.get(key)[1].toString());
			temp[1] = Double.parseDouble(limits.get(key)[1].toString()) + dist*.2;  
			temp[2] = Double.parseDouble(limits.get(key)[1].toString()) + dist*.5;
			temp[3] = Double.parseDouble(limits.get(key)[0].toString());
			results.put(key, temp);
		}
		return results;
	}
	
	/** 	Get energy sums (based on the simulation experiment's KPI calculations) per activity type and installation,
	 * 		along with the corresponding limits for defining the "efficient", "average" and "inefficient" ranges (per activity type).
	 * @param dbname	The name of the database, where the target simulation is stored.
	 * @param inst_id		The id of the target installation. Cannot be null.
	 * @return
	 */
	public static HashMap<String, Double[]> getEnergySumsWithLimitsPerActivityType(DB db, String dbname, String inst_id)
	{
		if (inst_id == null)
		{
			System.err.println("The id of the target installation cannot be null.");
			System.exit(2);
		}
		
		HashMap<String, Double[]> results = new HashMap<String, Double[]>();
		HashMap<String, Double[]> limits = getEnergyLimitsPerActivityType(db, dbname);
		HashMap<String, Double> values = getEnergySumsPerActivityType(db, dbname, inst_id);
		values.put("Whole house",  sum(values));
		for (String key : values.keySet())
		{	
			Double[] temp = new Double[4];
			temp[0] = values.get(key);
			double dist = Double.parseDouble(limits.get(key)[0].toString()) - Double.parseDouble(limits.get(key)[1].toString());
			temp[1] = Double.parseDouble(limits.get(key)[1].toString()) + dist*.2;  
			temp[2] = Double.parseDouble(limits.get(key)[1].toString()) + dist*.5;
			temp[3] = Double.parseDouble(limits.get(key)[0].toString());
			results.put(key, temp);
		}
		return results;
	}
	
	private static double sum(Map<String, Double> m) {
	    double sum = 0;
	    for (String key : m.keySet()) {
	        sum += m.get(key);
	    }
	    return sum;
	}
	
//	public static void main(String args[])
//	{
//		DecimalFormat df = new DecimalFormat("#0.000"); 
//		Mongo m;
//		try {
//			
//			String dbname = "53b27869300460bd8c6825ea";
//
//			System.out.println("Energy sum per appliance type for simulation");
//			HashMap<String, Double> results = getEnergySumsPerApplianceType(dbname, null);
//			for (String key : results.keySet())
//			{
//	            double value = Double.parseDouble(results.get(key).toString());  
//	            System.out.println(key + ": \t" + df.format(value)); 
//			}	 
//			
//			System.out.println();
//			
//			System.out.println("Energy sum per activity type for simulation");
//			HashMap<String, Double> results2 = getEnergySumsPerActivityType(dbname, null);
//			for (String key : results2.keySet())
//			{
//				double value = Double.parseDouble(results2.get(key).toString());  
//	            System.out.println(key + ": \t" + df.format(value)); 
//			}	
//			
//			System.out.println();
//			
//			
//			m = new Mongo();
//			DB db = m.getDB(dbname);
//			DBCollection installations = db.getCollection("installations");
//			List<ObjectId> inst_ids = installations.distinct("_id");
//			
//			for (ObjectId inst_id : inst_ids)
//			{
//				System.out.println("Energy sum per appliance type for installation " + inst_id);
//				HashMap<String, Double> resultsPerInst = getEnergySumsPerApplianceType(dbname, inst_id+"");
//				for (String key : resultsPerInst.keySet())
//				{
//					double value = Double.parseDouble(resultsPerInst.get(key).toString());  
//		            System.out.println(key + ": \t" + df.format(value));  
//				}	 
//				
//				System.out.println();
//				
//				System.out.println("Energy sum per activity type for installation " + inst_id);
//				HashMap<String, Double> resultsPerInst2 = getEnergySumsPerActivityType(dbname, inst_id+"");
//				for (String key : resultsPerInst2.keySet())
//				{
//					double value = Double.parseDouble(resultsPerInst2.get(key).toString());  
//		            System.out.println(key + ": \t" + df.format(value)); 
//				}	
//				
//				System.out.println();
//			}
//			
//			for (ObjectId inst_id : inst_ids)
//			{
//				System.out.println("Energy sum and limits per appliance type for installation " + inst_id);
//				HashMap<String, Double[]> resultsPerInst = getEnergySumsWithLimitsPerApplianceType(dbname, inst_id+"");
//				for (String key : resultsPerInst.keySet())
//				{
//					Double[] values = resultsPerInst.get(key);  
//		            System.out.println(key + ": \t" + df.format(values[0]) + ", \tefficient<=" + df.format(values[1]) + ", \taverage<=" + df.format(values[2]) + ", \tinefficient<=" + df.format(values[3]) );  
//				}	 
//				
//				System.out.println();
//				
//				System.out.println("Energy sum and limits per activity type for installation " + inst_id);
//				HashMap<String, Double[]> resultsPerInst2 = getEnergySumsWithLimitsPerActivityType(dbname, inst_id+"");
//				for (String key : resultsPerInst2.keySet())
//				{
//					Double[] values = resultsPerInst2.get(key);  
//		            System.out.println(key + ": \t" + df.format(values[0]) + ", \tefficient<=" + df.format(values[1]) + ", \taverage<=" + df.format(values[2]) + ", \tinefficient<=" + df.format(values[3]) );  
//				}	
//				
//				System.out.println();
//			}
//			
//			
//			
//			
//			
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (MongoException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}

}

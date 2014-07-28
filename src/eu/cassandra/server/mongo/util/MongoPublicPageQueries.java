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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
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

public class MongoPublicPageQueries {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy MM");
	private static Calendar c1 = Calendar.getInstance();
	
	private static double getBarGraphValuesWithIndices(DBCollection collection, boolean resultsAreEnergy, String inst_id, Date[] dates, int startIndex, int numOfRecords)
	{
//		DBObject skip = new BasicDBObject("$skip", recordsToSkip);
//		DBObject limit = new BasicDBObject("$limit", N);
//		DBObject sort = new BasicDBObject("$sort", new BasicDBObject("tick", -1));
		
		BasicDBObject query = new BasicDBObject();
		query = new BasicDBObject("tick", new BasicDBObject("$gte", startIndex).append("$lt", startIndex+numOfRecords));	
		
		DBObject groupFields = new BasicDBObject( "_id", null);
		if (resultsAreEnergy)
			groupFields.put("aggrValue", new BasicDBObject( "$sum", "$p"));
		else	
			groupFields.put("aggrValue", new BasicDBObject( "$avg", "$p"));
//		groupFields.put("cnt", new BasicDBObject("$sum", 1));
		
		DBObject group = new BasicDBObject("$group", groupFields);
		
		List<DBObject> pipeline; 
		DBObject match;
		if (inst_id != null)
		{
			BasicDBObject andQuery = new BasicDBObject();
			List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
			obj.add(query);
			obj.add(new BasicDBObject("inst_id", inst_id));
			andQuery.put("$and", obj);
			match = new BasicDBObject("$match", andQuery);  
		}
		else
			match = new BasicDBObject("$match", query);  
		pipeline	 = Arrays.asList(match, group);
		AggregationOutput output = collection.aggregate(pipeline);
		double value = 0;
		for (DBObject result : output.results()) {
			value = Double.parseDouble(""+result.get("aggrValue"));
			if (resultsAreEnergy)
				value /= 60.0;
//			System.out.println(value + " " +  Double.parseDouble(""+result.get("cnt")));
		}
		return value;
	}
	
	private static double[] getMostRecentBlockOfBarGraphValues(DBCollection collection, String aggregateBy, boolean resultsAreEnergy, String inst_id, Date[] dates)
	{
		aggregateBy = aggregateBy.trim();
		double divideBy = 60;  	//  aggregateBy = "hour";
		double[] divideBys = null;
		switch(aggregateBy) {
		case "day":
			divideBy = 60 * 24;
			break;
		case "week":
			divideBy = 60 * 24 * 7;
			break;
		case "month":
			divideBy = Double.NaN;
			break;
		default:
			break;
		}
		
		int block_size = 24;
		switch(aggregateBy) {
		case "day":
			block_size = 15;
			break;
		case "week":
			block_size = 12;
			break;
		case "month":
			block_size = 12;	
			break;
		default:
			break;
		}
		
		int numOfResults = 1; 
		if (!aggregateBy.equals("month"))
		{
			if (inst_id != null)
				numOfResults = (int) Math.ceil(collection.count(new BasicDBObject("inst_id", inst_id))/ divideBy);
			else
				numOfResults = (int) Math.ceil(collection.count() / divideBy);
		}
		else
		{
			if (dates[0].getYear() == dates[1].getYear())
				numOfResults = dates[1].getMonth() - dates[0].getMonth() +1;
			else
				numOfResults = (12 - dates[0].getMonth()) + dates[1].getMonth() + 1;
			divideBys = new double[numOfResults+1];
			divideBys[0] = 0;
			int year = dates[0].getYear();			
			int index = 1;
			for (int m=dates[0].getMonth(); ; m++)
			{
				Calendar mycal = new GregorianCalendar(year, m, 1);
				int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH); 
				divideBys[index]= 60 * 24 * daysInMonth;
				index++;
				if (m==11)
				{
					m=-1;
					year++;
				}
				if (m==dates[1].getMonth() && year==dates[1].getYear() )
					break;
			}
		}
			
		double[] results = new double[block_size];
		int index = 0;
		if (numOfResults<block_size)
			index = block_size-numOfResults;
		for (int i=numOfResults<block_size?0:(numOfResults-block_size); i<numOfResults; i++)
		{
			int recordsToSkip = (int) (aggregateBy.equals("month") ? divideBys[i] : divideBy*i);
			int recordsToAggregate = (int)(aggregateBy.equals("month") ? divideBys[i+1] : divideBy);
			results[index] = getBarGraphValuesWithIndices(collection, resultsAreEnergy, inst_id, dates, recordsToSkip, recordsToAggregate);
			index++;
		}
		return results;
	}
	
	private static Date[] getSimulationDates(DB db) throws Exception
	{
		Date[] dates = new Date[2];
		DBCursor c = db.getCollection("sim_param").find();
		
		if (c.size() == 0)
			throw new Exception("Target collection \"sim_param\" is empty.");
		
		while (c.hasNext()) {
			DBObject sim_params = c.next();
			DBObject temp = (DBObject)sim_params.get("calendar");
			String year = ""+temp.get("year");
			String month = ""+temp.get("month");
			String dayOfMonth = ""+temp.get("dayOfMonth");
			
			int numOfSimDays = Integer.parseInt(""+sim_params.get("numberOfDays"));
			
			String dt = year + "/" + month + "/" + dayOfMonth;  // Start date
			try {
				c1.setTime(sdf.parse(dt));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			dates[0] = c1.getTime();
			c1.add(Calendar.DATE, numOfSimDays);  // number of days to add
			dates[1] = c1.getTime();
		}
		return dates;
	}

	private static double mean(TreeMap<String, Double> m) {
	    double sum = 0;
	    for (String key : m.keySet()) {
	        sum += m.get(key);
	    }
	    return sum / m.size();
	}
	
	private static double sum(TreeMap<String, Double> m) {
	    double sum = 0;
	    for (String key : m.keySet()) {
	        sum += m.get(key);
	    }
	    return sum;
	}
	
	
	/** Get the most recent "block" of values for the consumption plot. Returned values may refer to power or energy consumption.
	 *   They are aggregated by the selected unit (hour, day, week or month) and, if required, by installation.
	 *   "Block" length is 24 when aggregating by hour, 15 when aggregating by day and 12 when aggregating by week or month.
	 * @param dbname				The name of the database, where the target simulation is stored.
	 * @param aggregateBy			The aggregation unit. Possible values are: hour, day, week and month.
	 * @param resultsAreEnergy	Whether returned values refer to power or energy consumption.
	 * @param inst_id					The id of the target installation. If null computations are about the whole simulation.
	 * @return
	 * @throws Exception 
	 */
	public static TreeMap<String, Double> getConsumptionPlotData(DB adb, String aggregateBy, boolean resultsAreEnergy, String inst_id) throws Exception
	{
		TreeMap<String, Double> results = new TreeMap<String, Double>();
		try {
			DB db = adb;
			if (db.getCollectionNames().isEmpty())
				throw new Exception("Target database contains no collections.");
			
			Date[] dates  = getSimulationDates(db);
			DBCollection collection;
			if (inst_id!=null)
				collection = db.getCollection("inst_results");
			else
				collection = db.getCollection("aggr_results");
			
			if (collection.count() == 0)
				throw new Exception("Target collection \"inst/aggr_results\" is empty.");
								
			if ( !aggregateBy.equals("hour") && !aggregateBy.equals("day") && !aggregateBy.equals("week") && !aggregateBy.equals("month") )
				throw new Exception("ERROR: Possible values for the aggregation unit are: hour, day, week and month.");
			
			double[] results2 = getMostRecentBlockOfBarGraphValues(collection, aggregateBy, resultsAreEnergy, inst_id, dates);
			
			int block_size = 24;
			String dt;
			switch(aggregateBy) {
			case "day":
				block_size = 15;
				dt = sdf.format(dates[1]);
				try {
					c1.setTime(sdf.parse(dt));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				for (int i=block_size-1; i>=0; i--)
				{
					c1.add(Calendar.DAY_OF_MONTH, -1);  // number of days to add
					results.put(sdf.format(c1.getTime()), results2[i]);
				}
				break;
			case "week":
				block_size = 12;
				dt = sdf.format(dates[1]);
				try {
					c1.setTime(sdf.parse(dt));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				for (int i=block_size-1; i>=0; i--)
				{
					c1.add(Calendar.DAY_OF_MONTH, -7);  // number of days to add
					results.put(sdf.format(c1.getTime()), results2[i]);
				}
				break;
			case "month":
				block_size = 12;	
				dt = sdf.format(dates[1]);
				try {
					c1.setTime(sdf.parse(dt));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				results.put(sdf2.format(c1.getTime()), results2[block_size-1]);
				for (int i=block_size-2; i>=0; i--)
				{
					c1.add(Calendar.MONTH, -1);  // number of days to add
					results.put(sdf2.format(c1.getTime()), results2[i]);
				}
				break;
			default:
				DecimalFormat df = new DecimalFormat("00"); 
				for (int i=0; i<24; i++)
					results.put(df.format(i) + ":00", results2[i]);
				break;
			}
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
	
	/** Get the values for the comparison bars. Returned values may refer to power or energy consumption.
	 *   They are aggregated by the selected unit (hour, day, week or month) and, if required, by installation.
	 *   "Block" length is 24 when aggregating by hour, 15 when aggregating by day and 12 when aggregating by week or month.
	 * @param dbname				The name of the database, where the target simulation is stored.
	 * @param aggregateBy			The aggregation unit. Possible values are: hour, day, week and month.
	 * @param resultsAreEnergy	Whether returned values refer to power or energy consumption.
	 * @param aInst_id				The id of the target installation. Cannot be null.
	 * @return
	 * @throws Exception 
	 */
	public static TreeMap<String, Double> getComparisonBarsData(DB adb, String aggregateBy, boolean resultsAreEnergy, String aInst_id) throws Exception
	{
		if (aInst_id == null)
			throw new Exception("The id of the target installation cannot be null.");
			
		TreeMap<String, Double> barsData = new TreeMap<String, Double>();
		try {
			DB db = adb;
			if (db.getCollectionNames().isEmpty())
				throw new Exception("Target database contains no collections.");
			DBCollection installations = db.getCollection("installations");
			if (installations.count() == 0)
				throw new Exception("Target collection \"installations\" is empty.");
			List<ObjectId> inst_ids = installations.distinct("_id");
			
			double maxInst = Double.MIN_VALUE;
			double minInst = Double.MAX_VALUE;
			double avgInst = 0;
			
		
			for (ObjectId inst_id : inst_ids)
			{
				TreeMap<String, Double> resultsInst = getConsumptionPlotData(adb, aggregateBy, resultsAreEnergy, inst_id+"");
				double value = 0;
				if (!resultsAreEnergy)
					value = mean(resultsInst);
				else
					value = sum(resultsInst);
				if (value > maxInst)
					maxInst = value;
				if (value < minInst)
					minInst = value;
				avgInst += value;
				if ((inst_id+"").equals(aInst_id))
					barsData.put("you", value);
			}
			avgInst /= (double)inst_ids.size();
			
			barsData.put("max", maxInst);
			barsData.put("min", minInst);
			barsData.put("avg", avgInst);
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return barsData;
	}
	
	
	public static void main(String[] args) {
		
		DecimalFormat df = new DecimalFormat("#0.000"); 
		Mongo m;
		try {
			String dbname = "53cf665030043b3208584016";
			String aggregateBy = "month";		
			boolean resultsAreEnergy = false;
			

			m = new Mongo("localhost");
			DB db = m.getDB(dbname);
				
			System.out.println("\"Block\" length is 24 when aggregating by hour, 15 when aggregating by day, "
					+ "and 12 when aggregating by week or month.");
			System.out.println();
			
			System.out.println("Results per " + aggregateBy + " for all installations in the simulation (showing the most recent \"block\" of values)");		     
			TreeMap<String, Double> results = getConsumptionPlotData(db, aggregateBy, resultsAreEnergy, null);
			for (String key : results.keySet())
			{
				double value = Double.parseDouble(results.get(key).toString());  
	            System.out.println(key + " \t" +value); 
			}	
			System.out.println();
			
			
			DBCollection installations = db.getCollection("installations");
			List<ObjectId> inst_ids = installations.distinct("_id");

			System.out.println("Results per " + aggregateBy + " and per installation (showing the most recent \"block\" of values)");
			for (ObjectId inst_id : inst_ids)
			{
				System.out.println("Installation: "+ inst_id);
				TreeMap<String, Double> results2 = getConsumptionPlotData(db, aggregateBy, resultsAreEnergy, inst_id+"");
				for (String key : results2.keySet())
				{
					double value = Double.parseDouble(results2.get(key).toString());  
		            System.out.println(key + " \t" +value); 
				}	
//			    System.out.println((!resultsAreEnergy?"avg: "+ mean(results):"sum: "+sum(results)));
			}
			
			System.out.println();
			
			for (ObjectId inst_id : inst_ids)
			{
				System.out.println("Installation: "+ inst_id);
				TreeMap<String, Double> results2 = getComparisonBarsData(db, aggregateBy, resultsAreEnergy, inst_id+"");
				for (String key : results2.keySet())
				{
					double value = Double.parseDouble(results2.get(key).toString());  
		            System.out.print(key + " \t" +value + " \t"); 
				}	
				 System.out.println();
			}
			m.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	

}
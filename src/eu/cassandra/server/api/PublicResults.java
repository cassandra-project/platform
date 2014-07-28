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
package eu.cassandra.server.api;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.TreeMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;

import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.MongoDBQueries;
import eu.cassandra.server.mongo.util.MongoPublicPageQueries;
import eu.cassandra.server.mongo.util.MongoQueries;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.utilities.Utils;

@Path("publicresults")
@Produces(MediaType.APPLICATION_JSON)
public class PublicResults {

	@GET
	public Response getResults(
			@QueryParam("inst_id") String inst_id,
			@QueryParam("timeRange") String timeRange,
			@QueryParam("unit") String unit,
			@Context HttpHeaders httpHeaders) {
		JSONtoReturn jSON2Rrn = new JSONtoReturn();
		try {
			DecimalFormat df = new DecimalFormat("#0.00"); 
			String dbname = MongoDBQueries.getDbNameFromHTTPHeader(httpHeaders);
			DB db = DBConn.getConn(dbname);
			boolean resultsAreEnergy = false;
			if(unit.equalsIgnoreCase("kwh")) resultsAreEnergy = true;
			String aggregateBy = timeRange;
			TreeMap<String, Double> results = MongoPublicPageQueries.getConsumptionPlotData(db, aggregateBy, resultsAreEnergy, inst_id);
			BasicDBList consumptionPlotData = new BasicDBList();
			for (String key : results.keySet()) {
				double value = Double.parseDouble(results.get(key).toString());  
//	            System.out.println(key + " \t" +value); 
	            DBObject dbo = new BasicDBObject();
				dbo.put("x", key);
				dbo.put("y", ""+df.format(value));
				consumptionPlotData.add(dbo);
			}
			DBObject retObj = new BasicDBObject();
			retObj.put("consumptionPlotData", consumptionPlotData);
			TreeMap<String, Double> results2 = MongoPublicPageQueries.getComparisonBarsData(db, aggregateBy, resultsAreEnergy, inst_id);
			BasicDBList energyBarsData = new BasicDBList();
			for (String key : results2.keySet()) {
				double value = Double.parseDouble(results2.get(key).toString());  
//	            System.out.print(key + " \t" +value + " \t"); 
	            DBObject dbo = new BasicDBObject();
				dbo.put("name", key);
				dbo.put("value", ""+df.format(value));
				energyBarsData.add(dbo);
			}	
			retObj.put("energyBarsData", energyBarsData);
			retObj.put("unit", unit);
			HashMap<String, Double> results3 = MongoQueries.getEnergyPercentagesPerActivityType(db, dbname, inst_id);
			BasicDBList pieChartDataActivities = new BasicDBList();
			for (String key : results3.keySet()) {
				double value = Double.parseDouble(results3.get(key).toString());  
//	            System.out.print(key + " \t" +value + " \t"); 
	            DBObject dbo = new BasicDBObject();
				dbo.put("name", key);
				dbo.put("consumptionPercentage", ""+df.format(value));
				pieChartDataActivities.add(dbo);
			}	
			retObj.put("pieChartDataActivities", pieChartDataActivities);
			HashMap<String, Double> results4 = MongoQueries.getEnergyPercentagesPerApplianceType(db, dbname, inst_id);
			BasicDBList pieChartDataAppliances = new BasicDBList();
			for (String key : results4.keySet()) {
				double value = Double.parseDouble(results4.get(key).toString());  
//	            System.out.print(key + " \t" +value + " \t"); 
	            DBObject dbo = new BasicDBObject();
				dbo.put("name", key);
				dbo.put("consumptionPercentage", ""+df.format(value));
				pieChartDataAppliances.add(dbo);
			}	
			retObj.put("pieChartDataAppliances", pieChartDataAppliances);
			
			HashMap<String, Double[]> results5 = MongoQueries.getEnergySumsWithLimitsPerActivityType(db, dbname, inst_id);
			HashMap<String, Double[]> results6 = MongoQueries.getEnergySumsWithLimitsPerApplianceType(db, dbname, inst_id);
			BasicDBList consumptionCategoryData = new BasicDBList();
			for (String key : results5.keySet()) {
				Double[] values = results5.get(key);  
//	            System.out.println(key + ": \t" + df.format(values[0]) + ", \tefficient<=" + df.format(values[1]) + ", \taverage<=" + df.format(values[2]) + ", \tinefficient<=" + df.format(values[3]) );
	            DBObject dbo = new BasicDBObject();
	            dbo.put("name", key);
	            if(key.equalsIgnoreCase("Whole House")) {
	            	dbo.put("type", "Installation");
	            } else {
	            	dbo.put("type", "Activity");
	            }
				dbo.put("consumption", ""+df.format(values[0]));
				dbo.put("efficient", ""+df.format(values[1]));
				dbo.put("average", ""+df.format(values[2]));
				dbo.put("inefficient", ""+df.format(values[3]));
				dbo.put("description", "Demo Description");
				consumptionCategoryData.add(dbo);
			}	
			for (String key : results6.keySet()) {
				Double[] values = results6.get(key);  
//	            System.out.println(key + ": \t" + df.format(values[0]) + ", \tefficient<=" + df.format(values[1]) + ", \taverage<=" + df.format(values[2]) + ", \tinefficient<=" + df.format(values[3]) );
	            if(key.equalsIgnoreCase("Whole House")) continue;
	            DBObject dbo = new BasicDBObject();
				dbo.put("name", key);
				dbo.put("type", "Appliance");
				dbo.put("consumption", ""+df.format(values[0]));
				dbo.put("efficient", ""+df.format(values[1]));
				dbo.put("average", ""+df.format(values[2]));
				dbo.put("inefficient", ""+df.format(values[3]));
				dbo.put("description", "Demo Description");
				consumptionCategoryData.add(dbo);
			}	
			retObj.put("consumptionCategoryData", consumptionCategoryData);
			return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(jSON2Rrn.createJSON(retObj, "Consumption per type for appliances and activities.")));
		}catch(Exception e) {
			e.printStackTrace();
			return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(jSON2Rrn.createJSONError("Error in retrieving results", e.getMessage())));
		}
	}
	
}
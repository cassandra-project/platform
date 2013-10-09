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
package eu.cassandra.server.mongo;

import java.util.List;
import java.util.Vector;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.server.api.exceptions.RestQueryParamMissingException;
import eu.cassandra.server.mongo.util.JSONValidator;
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.MongoDBQueries;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.utilities.Utils;

public class MongoAppliances {

	public final static String COL_APPLIANCES = "appliances";
	public final static String REF_INSTALLATION = "inst_id";

	/**
	 * curl -i http://localhost:8080/cassandra/api/app/4ff1d9d4e4b0ddb832a310bc
	 * 
	 * @param cid
	 * @return
	 */
	public String getAppliance(HttpHeaders httpHeaders,String id) {
		return new MongoDBQueries().getEntity(httpHeaders,COL_APPLIANCES,"_id", 
				id, "Appliance retrieved successfully").toString();
	}
	
	public static String getParentId(String id) {
		BasicDBList list = ((BasicDBList)((DBObject)JSON.parse(new MongoAppliances().getAppliance(null, id))).get("data"));
		if(list == null || list.isEmpty()) return null;
		return (String)((DBObject)list.get(0)).get(REF_INSTALLATION);	
	}

	/**
	 * curl -i http://localhost:8080/cassandra/api/app?inst_id=4ff1d9d4e4b0ddb832a310bc
	 * 
	 * @param inst_id
	 * @return
	 */
	public String getAppliances(HttpHeaders httpHeaders,String inst_id, String scn_id, boolean count, boolean pertype) {
		if(inst_id != null)
			return new MongoDBQueries().getEntity(httpHeaders,COL_APPLIANCES,"inst_id", 
					inst_id, "Appliances retrieved successfully",count).toString();
		else if(scn_id != null && count) {
			return new MongoDBQueries().getSecondLevelCounts(httpHeaders,scn_id, COL_APPLIANCES).toString(); 
		}
		else if(scn_id != null && pertype) {
			return new MongoDBQueries().getCountsPerType(httpHeaders, scn_id, COL_APPLIANCES).toString();
		}
		else  {
			return new JSONtoReturn().createJSONError(
					"Only the Appliances of a particular Installation can be retrieved", 
					new RestQueryParamMissingException("inst_id QueryParam is missing")).toString();
		}
	}
	
	// For search
	public Response getAppliances(HttpHeaders httpHeaders, String scn_id, 
			String filters, String sort, int limit, int skip, boolean count, 
			boolean pertype, boolean lib) {
		// Search for the installations based on the scenario
		String installations = 
				(new MongoInstallations())
				.getInstallations(httpHeaders,scn_id,null,null,0,0,false,false);
		DBObject jsonResponse = (DBObject) JSON.parse(installations);
		BasicDBList list = (BasicDBList)jsonResponse.get("data");
		// Retrieve the ids
		BasicDBList appsList = new BasicDBList();
		int totalCount = 0;
		for(Object o : list) {
			DBObject dbo = (DBObject)o;
			String inst_id = (String)dbo.get("_id");
			// For each one gather the data and load a DBCursor
			totalCount += addToList(inst_id, appsList, filters, sort, count, httpHeaders);
		}
		if(lib) {
			// search also in the corresponding lib
			totalCount += addToList(scn_id, appsList, filters, sort, count, httpHeaders);
		}
		// Use limit and skip for creating a sublist => return it
		Vector<DBObject> vec = new Vector<DBObject>();
		for(Object o : appsList) {
			DBObject dbo = (DBObject)o;
			vec.add(dbo);
		}
		JSONtoReturn jSON2Rrn = new JSONtoReturn();
		String page = jSON2Rrn.createJSON(vec, "Appliances retrieved successfully").toString();
		return Utils.returnResponseWithAppend(page, "total_size", new Integer(totalCount));
	}
	
	private int addToList(String ref_id, BasicDBList list, String filters, String sort, boolean count, HttpHeaders httpHeaders) {
		String apps = new MongoDBQueries().getEntity(httpHeaders,
				COL_APPLIANCES, "inst_id", ref_id, filters, sort, 0, 0, "Appliances retrieved successfully", count).toString();
		String countResponse = new MongoDBQueries().getEntity(httpHeaders,
				COL_APPLIANCES, "inst_id", ref_id, null, null, 0, 0, "Appliances retrieved successfully", true).toString();
		DBObject response = (DBObject) JSON.parse(countResponse);
		BasicDBList alist = (BasicDBList)response.get("data");
		DBObject object = (DBObject)alist.get(0);
		Integer aint = (Integer)object.get("count");
		DBObject jsonResponse = (DBObject) JSON.parse(apps);
		BasicDBList appsInstList = (BasicDBList)jsonResponse.get("data");
		list.addAll(appsInstList);
		return aint.intValue();
	}

	/**
	 * curl -i http://localhost:8080/cassandra/api/app?actmod_id=5040bca5e4b03aeba2cd907
	 * 
	 * @param actmod_id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getApplianceFromActivityModel(HttpHeaders httpHeaders, String actmod_id) {
		DBObject o =  new MongoDBQueries().getEntity(httpHeaders,MongoActivityModels.COL_ACTMODELS,"_id", 
				actmod_id, "Activity Model retrieved successfully");
		Vector<DBObject> appliances = new Vector<DBObject>();
		Vector<DBObject> dataVec = (Vector<DBObject>)o.get("data");
		if(dataVec != null && dataVec.size() > 0) {
			DBObject data = dataVec.get(0);
			com.mongodb.BasicDBList apps = (com.mongodb.BasicDBList)data.get("containsAppliances");
			if(apps != null) {
				for(int i=0;i<apps.size();i++) {
					String appID = apps.get(i).toString();
					DBObject appObj = new MongoDBQueries().getEntity(httpHeaders,COL_APPLIANCES,"_id", appID, 
							"Appliance retrieved successfully");
					Vector<DBObject> appIn = (Vector<DBObject>)appObj.get("data");
					if(appIn != null && appIn.size() > 0) {
						appliances.add(appIn.get(0));
					}
				}
			}
			return new JSONtoReturn().createJSON(appliances, "Appliance Retrieved from activity Model").toString();
		}
		else {
			return PrettyJSONPrinter.prettyPrint(o.toString());
		}
	}

	/**
	 * curl -i --data  @appliance.json    --header Content-type:application/json http://localhost:8080/cassandra/api/app
	 * 
	 * @param dataToInsert
	 * @return
	 */
	public String createAppliance(String dataToInsert) {
		String response = createApplianceObj(dataToInsert).toString();
		return withAddedWarnings(response, false);
	}
	
	public static DBObject createApplianceObj(String dataToInsert) {
		MongoDBQueries q = new MongoDBQueries();
		DBObject returnObj = q.insertData(COL_APPLIANCES ,dataToInsert,
				"Appliance created successfully", MongoInstallations.COL_INSTALLATIONS ,
				"inst_id",JSONValidator.APPLIANCE_SCHEMA);
		if(Utils.failed(returnObj.toString())) {
			returnObj = q.insertData(COL_APPLIANCES ,dataToInsert,
					"Appliance created successfully", "users" ,
					"inst_id",JSONValidator.APPLIANCE_SCHEMA);
		}
		return returnObj;
	}
	
	private String withAddedWarnings(String response, boolean ary) {
		if(Utils.failed(response)) return response;
		DBObject jsonResponse = (DBObject) JSON.parse(response);
		DBObject data = (DBObject) jsonResponse.get("data");
		String objID = new String();
		if(ary) {
			objID = (String)((DBObject)((BasicDBList)data).get(0)).get("_id");
		} else {
			objID = (String)data.get("_id");
		}
		DBObject returnQuery = 
				new MongoDBQueries().getEntity(MongoConsumptionModels.COL_CONSMODELS, "app_id", objID);
		if(returnQuery == null) {
			BasicDBList list = new BasicDBList();
			String warning = "Add a consumption model for this appliance.";
			list.add(warning);
			jsonResponse.put("warnings", list);
		}
		return jsonResponse.toString();
	}

	/**
	 * curl -i -X DELETE http://localhost:8080/cassandra/api/app/4ff153dbe4b0c855ac36d9a7
	 * 
	 * @param id
	 * @return
	 */
	public String deleteAppliance(String id) {
		return new MongoDBQueries().deleteDocument(COL_APPLIANCES, id).toString();
	}

	/**
	 * curl -X PUT -d @appliance.json   --header Content-type:application/json   http://localhost:8080/cassandra/api/app/4fec8b53df4ffdb8d1d1ce57
	 * 
	 * @param cid
	 * @param jsonToUpdate
	 * @return
	 */
	public String updateAppliance(String id,String jsonToUpdate) {
		MongoDBQueries q = new MongoDBQueries();
		String returnMsg = q.updateDocument("_id", id, jsonToUpdate,
				COL_APPLIANCES, "Appliance updated successfully",
				MongoInstallations.COL_INSTALLATIONS ,"inst_id",JSONValidator.APPLIANCE_SCHEMA).toString();
		if(Utils.failed(returnMsg)) {
			returnMsg = q.updateDocument("_id", id,jsonToUpdate,
					COL_APPLIANCES, "Appliance updated successfully",
					"users" ,"inst_id",JSONValidator.APPLIANCE_SCHEMA).toString();
		}
		return withAddedWarnings(returnMsg, true);
	}
}

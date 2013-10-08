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
import eu.cassandra.sim.utilities.Utils;

public class MongoPersons {
	public final static String COL_PERSONS = "persons";
	public final static String REF_INSTALLATION = "inst_id";

	/**
	 * 
	 * @param cid
	 * @return
	 */
	public String getPerson(HttpHeaders httpHeaders,String id) {
		return new MongoDBQueries().getEntity(httpHeaders,COL_PERSONS,"_id", 
				id, "Person retrieved successfully").toString();
	}
	
	public static String getParentId(String id) {
		BasicDBList list = ((BasicDBList)((DBObject)JSON.parse(new MongoPersons().getPerson(null, id))).get("data"));
		if(list == null || list.isEmpty()) return null;
		return (String)((DBObject)list.get(0)).get(REF_INSTALLATION);
	}

	/**
	 * 
	 * @param httpHeaders
	 * @param inst_id
	 * @param scn_id
	 * @param count
	 * @return
	 */
	public String getPersons(HttpHeaders httpHeaders,String inst_id, String scn_id, boolean count, boolean pertype) {
		if(inst_id != null) {
			return new MongoDBQueries().getEntity(httpHeaders,COL_PERSONS,"inst_id", 
					inst_id, "Persons retrieved successfully",count).toString();
		}
		else if(scn_id != null && count) {
			return new MongoDBQueries().getSecondLevelCounts(httpHeaders,scn_id, COL_PERSONS).toString(); 
		}
		else if(scn_id != null && pertype) {
			return new MongoDBQueries().getCountsPerType(httpHeaders,scn_id, COL_PERSONS).toString(); 
		}
		else {
			return new JSONtoReturn().createJSONError(
					"Only the Persons of a particular Installation can be retrieved", 
					new RestQueryParamMissingException("inst_id QueryParam is missing")).toString();
		}
	}

	/**
	 * @param dataToInsert
	 * @return
	 */
	public String createPerson(String dataToInsert) {
		String response = createPersonObj(dataToInsert).toString();
		return withAddedWarnings(response, false);
	}
	
	private String withAddedWarnings(String response, boolean ary) {
		if(Utils.failed(response)) return response;
		DBObject jsonResponse = (DBObject) JSON.parse(response);
		DBObject data = (DBObject) jsonResponse.get("data");
		String objID =  new String();
		if(ary) {
			objID = (String)((DBObject)((BasicDBList)data).get(0)).get("_id");
		} else {
			objID = (String)data.get("_id");
		}
		BasicDBList list = new BasicDBList();
		DBObject returnQuery = 
				new MongoDBQueries().getEntity(MongoActivities.COL_ACTIVITIES, "pers_id", objID);
		if(returnQuery == null) {
			String warning = "Add at least one activity for this person.";
			list.add(warning);
		}
		
		if(!list.isEmpty()) {
			jsonResponse.put("warnings", list);
		}
		return jsonResponse.toString();
	}
	
	public static DBObject createPersonObj(String dataToInsert) {
		MongoDBQueries q = new MongoDBQueries();
		DBObject returnObj = q.insertData(COL_PERSONS ,dataToInsert,
				"Person created successfully", MongoInstallations.COL_INSTALLATIONS ,
				"inst_id",JSONValidator.PERSON_SCHEMA );
		if(Utils.failed(returnObj.toString())) {
			returnObj = q.insertData(COL_PERSONS ,dataToInsert,
					"Person created successfully", "users" ,
					"inst_id",JSONValidator.PERSON_SCHEMA);
		}
		return returnObj;
	}

	/**
	 * @param cid
	 * @return
	 */
	public String deletePerson(String id) {
		return new MongoDBQueries().deleteDocument(COL_PERSONS, id).toString();
	}

	/**
	 * @param cid
	 * @param jsonToUpdate
	 * @return
	 */
	public String updatePerson(String id,String jsonToUpdate) {
		MongoDBQueries q = new MongoDBQueries();
		String returnMsg = q.updateDocument("_id", id,jsonToUpdate,
				COL_PERSONS, "Person updated successfully",
				MongoInstallations.COL_INSTALLATIONS ,"inst_id",JSONValidator.PERSON_SCHEMA).toString();
		if(Utils.failed(returnMsg)) {
			returnMsg = q.updateDocument("_id", id,jsonToUpdate,
					COL_PERSONS, "Person updated successfully",
					"users" ,"inst_id",JSONValidator.PERSON_SCHEMA).toString();
		}
		return withAddedWarnings(returnMsg, true);
	}
	
	// For search
		public Response getPersons(HttpHeaders httpHeaders, String scn_id, 
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
			String page = jSON2Rrn.createJSON(vec, "Persons retrieved successfully").toString();
			return Utils.returnResponseWithAppend(page, "total_size", new Integer(totalCount));
		}
		
		private int addToList(String ref_id, BasicDBList list, String filters, String sort, boolean count, HttpHeaders httpHeaders) {
			String apps = new MongoDBQueries().getEntity(httpHeaders,
					COL_PERSONS, "inst_id", ref_id, filters, sort, 0, 0, "Appliances retrieved successfully",count).toString();
			String countResponse = new MongoDBQueries().getEntity(httpHeaders,
					COL_PERSONS, "inst_id", ref_id, null, null, 0, 0, "Appliances retrieved successfully", true).toString();
			DBObject response = (DBObject) JSON.parse(countResponse);
			BasicDBList alist = (BasicDBList)response.get("data");
			DBObject object = (DBObject)alist.get(0);
			Integer aint = (Integer)object.get("count");
			
			DBObject jsonResponse = (DBObject) JSON.parse(apps);
			BasicDBList appsInstList = (BasicDBList)jsonResponse.get("data");
			list.addAll(appsInstList);
			return aint.intValue();
		}
}

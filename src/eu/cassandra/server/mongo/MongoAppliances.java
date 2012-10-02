/*   
   Copyright 2011-2012 The Cassandra Consortium (cassandra-fp7.eu)


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

import java.util.Vector;

import javax.ws.rs.core.HttpHeaders;

import com.mongodb.DBObject;

import eu.cassandra.server.api.exceptions.RestQueryParamMissingException;
import eu.cassandra.server.mongo.util.JSONValidator;
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.MongoDBQueries;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

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

	/**
	 * curl -i http://localhost:8080/cassandra/api/app?inst_id=4ff1d9d4e4b0ddb832a310bc
	 * 
	 * @param inst_id
	 * @return
	 */
	public String getAppliances(HttpHeaders httpHeaders,String inst_id, boolean count) {
		if(inst_id == null) {
			return new JSONtoReturn().createJSONError(
					"Only the Appliances of a particular Installation can be retrieved", 
					new RestQueryParamMissingException("inst_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(httpHeaders,COL_APPLIANCES,"inst_id", 
					inst_id, "Appliances retrieved successfully",count).toString();
		}
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
			for(int i=0;i<apps.size();i++) {
				String appID = apps.get(i).toString();
				DBObject appObj = new MongoDBQueries().getEntity(httpHeaders,COL_APPLIANCES,"_id", appID, 
						"Appliance retrieved successfully");
				Vector<DBObject> appIn = (Vector<DBObject>)appObj.get("data");
				if(appIn != null && appIn.size() > 0) {
					appliances.add(appIn.get(0));
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
		return new MongoDBQueries().insertData(COL_APPLIANCES ,dataToInsert,
				"Appliance created successfully", MongoInstallations.COL_INSTALLATIONS ,
				"inst_id",JSONValidator.APPLIANCE_SCHEMA).toString();
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
		return new MongoDBQueries().updateDocument("_id", id,jsonToUpdate,
				COL_APPLIANCES, "Appliance updated successfully",
				MongoInstallations.COL_INSTALLATIONS ,"inst_id",JSONValidator.APPLIANCE_SCHEMA).toString();
	}
}

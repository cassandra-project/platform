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

public class MongoLighting {
	public final static String COL_LIGHTING = "lighting";
	public final static String REF_INSTALLATION = "inst_id";

	/**
	 * 
	 * @param cid
	 * @return
	 */
	public String get(HttpHeaders httpHeaders,String id) {
		return new MongoDBQueries().getEntity(httpHeaders, COL_LIGHTING,"_id", 
				id, "Lighting params retrieved successfully").toString();
	}

	/**
	 * @param dataToInsert
	 * @return
	 */
	public String create(String dataToInsert) {
		return createPersonObj(dataToInsert).toString();
	}
	
	public static DBObject createPersonObj(String dataToInsert) {
		MongoDBQueries q = new MongoDBQueries();
		DBObject returnObj = q.insertData(COL_LIGHTING ,dataToInsert,
				"Lighting params created successfully", MongoInstallations.COL_INSTALLATIONS ,
				"inst_id",JSONValidator.LIGHTING_SCHEMA );
		return returnObj;
	}

	/**
	 * @param cid
	 * @return
	 */
	public String delete(String id) {
		return new MongoDBQueries().deleteDocument(COL_LIGHTING, id).toString();
	}

	/**
	 * @param cid
	 * @param jsonToUpdate
	 * @return
	 */
	public String update(String id,String jsonToUpdate) {
		MongoDBQueries q = new MongoDBQueries();
		String returnMsg = q.updateDocument("_id", id,jsonToUpdate,
				COL_LIGHTING, "Lighting params updated successfully",
				MongoInstallations.COL_INSTALLATIONS ,"inst_id",JSONValidator.LIGHTING_SCHEMA).toString();
		return returnMsg;
	}
	
}

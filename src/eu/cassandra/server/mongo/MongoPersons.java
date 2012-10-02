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


import javax.ws.rs.core.HttpHeaders;

import eu.cassandra.server.api.exceptions.RestQueryParamMissingException;
import eu.cassandra.server.mongo.util.JSONValidator;
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.MongoDBQueries;

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
		return new MongoDBQueries().insertData(COL_PERSONS ,dataToInsert,
				"Person created successfully", MongoInstallations.COL_INSTALLATIONS ,
				"inst_id",JSONValidator.PERSON_SCHEMA ).toString();
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
		return new MongoDBQueries().updateDocument("_id", id,jsonToUpdate,
				COL_PERSONS, "Person updated successfully",
				MongoInstallations.COL_INSTALLATIONS ,"inst_id",JSONValidator.PERSON_SCHEMA).toString();
	}
}

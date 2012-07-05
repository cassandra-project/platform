package cassandra.mongo;

import cassandra.exceptions.RestQueryParamMissingException;
import cassandra.mongo.util.MongoDBQueries;

public class MongoPersons {
	protected final static String COL_PERSONS = "persons";

	/**
	 * 
	 * @param cid
	 * @return
	 */
	public String getPerson(String id) {
		return new MongoDBQueries().getEntity(COL_PERSONS,"_id", 
				id, "Person retrieved successfully").toString();
	}

	/**
	 * @param inst_id
	 * @return
	 */
	public String getPersons(String pers_id) {
		if(pers_id == null) {
			return new MongoDBQueries().createJSONError(
					"Only the Persons of a particular Installation can be retrieved", 
					new RestQueryParamMissingException("pers_id QueryParam is missing")).toString();
		}
		else {
			return new MongoDBQueries().getEntity(COL_PERSONS,"pers_id", 
					pers_id, "Persons retrieved successfully").toString();
		}
	}

	/**
	 * @param dataToInsert
	 * @return
	 */
	public String createPerson(String dataToInsert) {
		return new MongoDBQueries().insertData(COL_PERSONS ,dataToInsert,
				"Person created successfully", MongoInstallations.COL_INSTALLATIONS ,"inst_id" ).toString();
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
				MongoInstallations.COL_INSTALLATIONS ,"inst_id" ).toString();
	}
}

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
package eu.cassandra.server.mongo.util;

import java.util.Vector;

import org.bson.types.ObjectId;

import eu.cassandra.server.api.exceptions.MongoInvalidObjectId;
import eu.cassandra.server.api.exceptions.MongoRefNotFoundException;
import eu.cassandra.server.api.exceptions.RestQueryParamMissingException;
import eu.cassandra.server.mongo.MongoActivities;
import eu.cassandra.server.mongo.MongoActivityModels;
import eu.cassandra.server.mongo.MongoAppliances;
import eu.cassandra.server.mongo.MongoConsumptionModels;
import eu.cassandra.server.mongo.MongoDemographics;
import eu.cassandra.server.mongo.MongoDistributions;
import eu.cassandra.server.mongo.MongoInstallations;
import eu.cassandra.server.mongo.MongoPersons;
import eu.cassandra.server.mongo.MongoProjects;
import eu.cassandra.server.mongo.MongoResults;
import eu.cassandra.server.mongo.MongoScenarios;
import eu.cassandra.server.mongo.MongoSimParam;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class MongoDBQueries {

	public final static int ACTIVE_POWER_P = 0;
	public final static int REACTIVE_POWER_Q = 1;

	private JSONtoReturn jSON2Rrn = new JSONtoReturn();


	/**
	 * 
	 * @param dataToInsert
	 * @param coll
	 * @param keyName
	 * @param refKeyName
	 * @return
	 */
	public DBObject insertArrayDocument(
			DBObject dataToInsert, 
			String coll, 
			String qKey,
			String oKey,
			String refKeyName) 
	{
		ObjectId newObjId;
		String refKey = dataToInsert.get(refKeyName).toString();
		try {
			newObjId =  new ObjectId();
			dataToInsert.put("cid", newObjId );
			//ensureThatRefKeyExists(entityToInsert, coll, refKeyName);

			//new BasicDBObject("dddd", entityToInsert ))
			//refKey = (String)entityToInsert.get(refKeyName);


			BasicDBObject q = new BasicDBObject();
			q.put( qKey, new ObjectId(refKey) );

			BasicDBObject o = new BasicDBObject();
			o.put( "$push", new BasicDBObject(oKey, dataToInsert));
			DBConn.getConn().getCollection(coll).update( q, o, true, true );
		}catch(Exception e) {
			return jSON2Rrn.createJSONError("Data to insert: " + dataToInsert ,e);
		}
		return jSON2Rrn.createJSONInsertPostMessage(qKey + " with cid=" + newObjId  + 
				" added successfully in " + coll + " with _id=" + 
				refKey,dataToInsert) ;

	}


	public DBObject insertNestedDocument(String dataToInsert, String coll, 
			String refKeyName, int schemaType) {
		DBObject data;
		String _id;
		try {
			data = (DBObject)JSON.parse(dataToInsert);
			new JSONValidator().isValid(dataToInsert, schemaType);
			if(!data.containsField("cid"))
				data.put("cid", new ObjectId());
			ensureThatRefKeyExists(data, coll, refKeyName, false);
			_id = data.get(refKeyName).toString();
			DBObject q = new BasicDBObject("_id", new ObjectId(_id));
			DBObject o =  new BasicDBObject().append("$set", new BasicDBObject("sim_param", data));
			DBConn.getConn().getCollection(coll).update(q,o);
		}catch(Exception e) {
			return jSON2Rrn.createJSONError(dataToInsert,e);
		}
		return jSON2Rrn.createJSONInsertPostMessage(coll + " with _id=" + _id + " updated with the following data", data) ;
	}

	/**
	 * 
	 * @param coll
	 * @param qKey
	 * @param qValue
	 * @param successMsg
	 * @param fieldNames
	 * @return
	 */
	public DBObject getEntity(String coll, String qKey, String qValue, 
			String successMsg, String...fieldNames) {
		return getEntity(coll, qKey, qValue, null, null, 0, 0, successMsg, false, fieldNames);
	}

	/**
	 * 
	 * @param coll
	 * @param qKey
	 * @param qValue
	 * @param successMsg
	 * @param counter
	 * @param fieldNames
	 * @return
	 */
	public DBObject getEntity(String coll, String qKey, String qValue, 
			String successMsg,  boolean counter, String...fieldNames) {
		return getEntity(coll, qKey, qValue, null, null, 0, 0, successMsg, counter, fieldNames);
	}

	/**
	 * 
	 * @param coll
	 * @param qKey
	 * @param qValue
	 * @param filters
	 * @param sort
	 * @param limit
	 * @param skip
	 * @param successMsg
	 * @param fieldNames
	 * @return
	 */
	public DBObject getEntity(String coll, String qKey, String qValue, 
			String filters, String sort, int limit, int skip,
			String successMsg, int counter, String...fieldNames) {
		return getEntity(coll, qKey, qValue, filters, sort, limit, skip,
				successMsg, counter, fieldNames);
	}

	/**
	 * @param collection
	 * @param id
	 * @param fieldNames
	 * @return
	 */
	public DBObject getEntity(String coll, String qKey, String qValue, 
			String filters, String sort, int limit, int skip,
			String successMsg,  boolean count, String...fieldNames) {
		DBObject query;
		BasicDBObject fields;
		try {
			query = new BasicDBObject();
			if(qKey != null && qValue != null && 
					(qKey.equalsIgnoreCase("_id") || qKey.endsWith(".cid"))) {
				query.put(qKey, new ObjectId(qValue));
			}
			else if(qKey != null && qValue != null) {
				try{
					if(filters != null)
						query = (DBObject)JSON.parse(filters);
					else
						query = new BasicDBObject();
				}catch(Exception e) {
					return jSON2Rrn.createJSONError("Cannot get entity for collection: " + coll + 
							", error in filters: " + filters ,e);
				}
				query.put(qKey, qValue);
			}
			fields = new BasicDBObject();
			for(String fieldName: fieldNames) {
				fields.put(fieldName, 1);
			}
		}catch(Exception e) {
			return jSON2Rrn.createJSONError("Cannot get entity for collection: " + coll + 
					" with qKey=" + qKey + " and qValue=" + qValue,e);
		}
		return new MongoDBQueries().executeFindQuery(
				coll,query,fields, successMsg, sort, limit, skip, count);
	}

	//	
	//	public DBObject getEntity(String coll, String qKey, String qValue, 
	//				String successMsg, int limit, int offset, Vector<SortingInfo> sortingInfo, String...fieldNames) {
	//		BasicDBObject query;
	//		BasicDBObject fields;
	//		try {
	//			query = new BasicDBObject();
	//			if(qKey != null && qValue != null && 
	//					(qKey.equalsIgnoreCase("_id") || qKey.endsWith(".cid"))) {
	//				query.put(qKey, new ObjectId(qValue));
	//			}
	//			else if(qKey != null && qValue != null) {
	//				query.put(qKey, qValue);
	//			}
	//			fields = new BasicDBObject();
	//			for(String fieldName: fieldNames) {
	//				fields.put(fieldName, 1);
	//			}
	//		}catch(Exception e) {
	//			return createJSONError("Cannot get entity for collection: " + coll + 
	//					" with qKey=" + qKey + " and qValue=" + qValue,e);
	//		}
	//		return new MongoDBQueries().executeFindQuery(
	//				coll,query,fields, successMsg);
	//	}

	/**
	 * 
	 * @param collection
	 * @param id
	 * @return
	 */
	BasicDBObject query = new BasicDBObject();
	public DBObject getEntity(String collection, String id) {
		query.put("_id", new ObjectId(id));
		return DBConn.getConn().getCollection(collection).findOne(query);
	}

	/**
	 * 
	 * @param coll
	 * @param entityName
	 * @return
	 */
	public DBObject getInternalEntities(String coll, String entityName, String parentID) {
		try {
			BasicDBObject query = new BasicDBObject("_id", new ObjectId(parentID));
			BasicDBObject fields = new BasicDBObject(entityName,1);
			DBObject result = new MongoDBQueries().executeFindQuery(coll,query, fields, 
					"Get " + entityName + " with " +  " from " + coll + " with _id=" + parentID);
			@SuppressWarnings("unchecked")
			Vector<DBObject> data = (Vector<DBObject>)result.get("data");
			BasicDBList internalEntities = (BasicDBList)data.get(0).get(entityName);
			Vector<DBObject> recordsVec = new Vector<DBObject>();
			for(int i=0;i<internalEntities.size();i++) {
				BasicDBObject entity = (BasicDBObject)internalEntities.get(i);
				recordsVec.add( entity);
			}
			return jSON2Rrn.createJSON(recordsVec,"Internal entites " + entityName + 
					" from " + coll + " with _id=" + parentID);
		}catch(Exception e) {
			return jSON2Rrn.createJSONError("Cannot get internal entities " +  entityName + 
					" from " + coll + " with _id=" + parentID, e);
		}
	}

	/**
	 * 
	 * @param coll
	 * @param entityName
	 * @param cid
	 * @return
	 */
	public DBObject getInternalEntity(String coll, String entityName, String cid) {
		return getInternalEntity(coll, entityName, cid,null); 
	}

	/**
	 * 
	 * @param coll
	 * @param entityName
	 * @param cid
	 * @param successMsg
	 * @return
	 */
	public DBObject getInternalEntity(String coll, String entityName, 
			String cid,String successMsg) {
		BasicDBObject internalEntity = null;
		try {
			BasicDBObject query = new BasicDBObject(entityName + ".cid", new ObjectId(cid));
			BasicDBObject fields = new BasicDBObject(entityName,1);
			DBObject result = new MongoDBQueries().executeFindQuery(coll,query,
					fields, "Get " + entityName + " with cid=" +  " from " + coll);
			@SuppressWarnings("unchecked")
			Vector<DBObject> data = (Vector<DBObject>)result.get("data");
			BasicDBList internalEntities = (BasicDBList)data.get(0).get(entityName);
			for(int i=0;i<internalEntities.size();i++) {
				BasicDBObject entity = (BasicDBObject)internalEntities.get(i);
				if(cid.equals(entity.get("cid").toString())) {
					internalEntity = entity;
					if(successMsg == null)
						successMsg = "Internal entity " + entityName + " with cid=" + 
								cid + " from collection=" + coll + " successfully retrieved";
					return jSON2Rrn.createJSON(internalEntity,successMsg) ;
				}
			}
			throw new MongoRefNotFoundException("Cannot get internal entity " +  entityName + 
					" with cid=" + cid + " from collection: " + coll);
		}catch(Exception e) {
			return jSON2Rrn.createJSONError("Cannot get internal entity " +  entityName + 
					" with cid=" + cid + " from collection: " + coll, e);
		}
	}

	/**
	 * 
	 * @param collection
	 * @param dbObj1
	 * @param dbObj2
	 * @return
	 */
	public DBObject executeFindQuery(String collection, 
			BasicDBObject dbObj1, BasicDBObject dbObj2, String successMsg) {
		DBCursor cursorDoc;
		if(dbObj2 == null) {
			cursorDoc = DBConn.getConn().getCollection(collection).find(dbObj1);
		}
		else {
			cursorDoc = DBConn.getConn().getCollection(collection).find(dbObj1,dbObj2);
		}
		return jSON2Rrn.createJSON(cursorDoc,successMsg);
	}

	public DBObject executeFindQuery(String collection, 
			DBObject dbObj1, DBObject dbObj2, String successMsg,
			String sort, int limit, int skip, boolean count) {
		DBCursor cursorDoc = null;
		if(count) {
			BasicDBObject dbObject = new BasicDBObject(); 
			dbObject.put("count", DBConn.getConn().getCollection(collection).find(dbObj1).count());
			return 	 jSON2Rrn.createJSON(dbObject, successMsg);
		}
		else {
			cursorDoc = DBConn.getConn().getCollection(collection).find(dbObj1);
		}
		if(sort != null)	{
			try{
				DBObject sortObj = (DBObject)JSON.parse(sort);
				cursorDoc = cursorDoc.sort(sortObj);
			}catch(Exception e) {
				return jSON2Rrn.createJSONError("Error in filtering JSON sorting object: " + sort, e);
			}
		}
		if(skip != 0)
			cursorDoc =	cursorDoc.skip(skip);
		if(limit != 0)
			cursorDoc =	cursorDoc.limit(limit);
		return jSON2Rrn.createJSON(cursorDoc,successMsg);
	}

	/**
	 * 
	 * @param qKey
	 * @param qValue
	 * @param jsonToUpdate
	 * @param collection
	 * @param successMsg
	 * @return
	 */
	public DBObject updateDocument(String qKey, String qValue, String jsonToUpdate, 
			String collection, String successMsg, int schemaType) {
		return updateDocument(qKey, qValue, jsonToUpdate, 
				collection, successMsg, null,null, schemaType);
	}

	/**
	 * 
	 * @param qKey
	 * @param qValue
	 * @param jsonToUpdate
	 * @param collection
	 * @param successMsg
	 * @param refColl
	 * @param refKeyName
	 * @return
	 */
	public DBObject updateDocument(String qKey, String qValue, String jsonToUpdate, 
			String collection, String successMsg, String refColl, 
			String refKeyName, int schemaType) {
		return updateDocument(qKey, qValue, jsonToUpdate, collection, successMsg, 
				refColl, refKeyName, null, schemaType); 
	}

	/**
	 * 
	 * @param qKey
	 * @param qValue
	 * @param jsonToUpdate
	 * @param collection
	 * @param successMsg
	 * @param refColl
	 * @param refKeyName
	 * @param intDocKey
	 * @return
	 */
	public DBObject updateDocument(String qKey, String qValue, String jsonToUpdate, 
			String collection, String successMsg, String refColl, String refKeyName, 
			String intDocKey, int schemaType) {
		Vector<String> keysUpdated = new Vector<String>();
		try {
			DBObject dbObject = (DBObject) JSON.parse(jsonToUpdate);
			if(dbObject.containsField("_id")){
				dbObject.removeField("_id");
				jsonToUpdate = dbObject.toString();
			}
			new JSONValidator().isValid(jsonToUpdate, schemaType,true);

			if(intDocKey != null && refKeyName != null && dbObject.containsField(refKeyName) ) {
				ensureThatRefKeysMatch(dbObject, collection, refKeyName, intDocKey, qValue);
			}
			else if((refColl != null || refKeyName != null) && dbObject.get(refKeyName) != null) {
				ensureThatRefKeyExists(dbObject, refColl, refKeyName, false);
			}
			for(String key : dbObject.keySet()) {
				if(!key.equalsIgnoreCase("id")) {
					keysUpdated.add(key);
					BasicDBObject keyToUpdate;
					if(qKey.equalsIgnoreCase("_id") || qKey.endsWith(".cid")) {
						keyToUpdate = new BasicDBObject().append(qKey, new ObjectId(qValue));
					}
					else {
						keyToUpdate = new BasicDBObject().append(qKey, qValue);
					}
					String keyName = key;
					if(intDocKey != null)
						keyName = intDocKey + "." + key;
					DBConn.getConn().getCollection(collection).update(
							keyToUpdate, new BasicDBObject().append("$set", 
									new BasicDBObject(keyName, dbObject.get(key)) ));
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			return jSON2Rrn.createJSONError("Update Failed for " + jsonToUpdate,e);
		}
		return getEntity(collection,qKey, qValue,successMsg,
				false,keysUpdated.toArray(new String[keysUpdated.size()]));
	}

	/**
	 * 
	 * db.installations.update({"appliances.cid":ObjectId("4ff16606e4b0f2b1e00c4d49")},{    $set :  {"appliances.$.description":"ff"}     })
	 * 
	 * @param coll
	 * @param refColl
	 * @param cid
	 * @param jsonToUpdate
	 * @param successMsg
	 * @return
	 */
	public DBObject updateArrayDocument(String coll, String entityName, String cid, 
			String refColl, String refKeyName, String jsonToUpdate) {
		Vector<String> keysUpdated = new Vector<String>();
		try {
			DBObject dbObject = (DBObject) JSON.parse(jsonToUpdate);

			if( (refColl != null || refKeyName != null) && dbObject.get(refKeyName) != null) {
				ensureThatRefKeyExists(dbObject, refColl, refKeyName,false);
			}
			DBObject q = new BasicDBObject(entityName + ".cid",new ObjectId(cid));

			for(String key : dbObject.keySet()) {
				if(!key.equalsIgnoreCase("cid")) {
					keysUpdated.add(key);
					DBObject o = new BasicDBObject("$set",new BasicDBObject(
							entityName + ".$." + key, dbObject.get(key)));
					DBConn.getConn().getCollection(coll).update(q,o);
				}
			}
		}catch(Exception e) {
			return jSON2Rrn.createJSONError("Update Failed for " + jsonToUpdate,e);
		}
		return getInternalEntity(coll,entityName, cid,"Internal document " + coll + "." + 
				entityName + " with cid=" + cid + " was successfullylly updated");
	}


	public DBObject addArrayDocumentDump(String coll, String parentEntityName,
			DBObject qObj, String cid, DBObject objToPush) {
		try {
			DBObject dObj = new BasicDBObject("$push", objToPush);
			DBConn.getConn().getCollection(coll).update(qObj,dObj);
		}catch(Exception e) {
			return jSON2Rrn.createJSONError("Update Failed for " + objToPush.toString() ,e);
		}
		return getInternalEntity(coll,"activities", cid,"Internal document " + coll + "." + 
				parentEntityName + " with cid=" + cid + " was successfullylly updated");
	}


	public DBObject updateInternalDocumentDump(String coll, String qField, String parentKeyFieldName, String updateFieldName, String newData) {
		DBObject newObject = (DBObject) JSON.parse(newData);
		String parentID = newObject.get(parentKeyFieldName).toString();
		DBObject q = new BasicDBObject(qField, new ObjectId(parentID));
		DBObject o = new BasicDBObject("$set",new BasicDBObject(updateFieldName,newObject));
		DBConn.getConn().getCollection(coll).update(q,o,false,true);
		return new BasicDBObject("a","b");
	}


	/**
	 * 
	 * @param coll
	 * @param dataToInsert
	 * @param successMessage
	 * @return
	 */
	public DBObject insertData(String coll, String dataToInsert, 
			String successMessage, int schemaType) {
		return insertData(coll, dataToInsert, successMessage,(String[])null,
				(String[])null,null, schemaType);
	}

	/**
	 * 
	 * @param coll
	 * @param dataToInsert
	 * @param successMessage
	 * @param refColl
	 * @param refKeyName
	 * @return
	 */
	public DBObject insertData(String coll, String dataToInsert, 
			String successMessage,String refColl, String refKeyName, int schemaType) {
		return insertData(coll, dataToInsert, successMessage, new String[] {refColl},
				new String[] {refKeyName}, new boolean[] {false}, schemaType);
	}

	/**
	 * 
	 * @param queryMessage
	 * @param successMessage
	 * @return
	 */
	public DBObject insertData(String coll, String dataToInsert, 
			String successMessage, String[] refColl, String[] refKeyName, 
			boolean[] canBeNull, int schemaType) {
		DBObject data;
		try {
			data = (DBObject)JSON.parse(dataToInsert);
			if(data.containsField("_id")){
				data.removeField("_id");
				dataToInsert = data.toString();
			}
			new JSONValidator().isValid(dataToInsert, schemaType);
			if(refColl != null && refKeyName != null ) {
				for(int i=0;i<refColl.length;i++) {
					ensureThatRefKeyExists(data, refColl[i], refKeyName[i],canBeNull[i]);
				}
			}
			DBConn.getConn().getCollection(coll).insert(data);
		}catch(com.mongodb.util.JSONParseException e) {
			return jSON2Rrn.createJSONError("Error parsing JSON input",e.getMessage());
		}catch(Exception e) {
			return jSON2Rrn.createJSONError(dataToInsert,e);
		}
		return jSON2Rrn.createJSONInsertPostMessage(successMessage,data) ;
	}

	/**
	 * 
	 * @param data
	 * @param refColl
	 * @param refKeyName
	 * @throws Exception
	 */
	private DBObject ensureThatRefKeyExists(DBObject data, String refColl, 
			String refKeyName, boolean canBeNull) 
					throws MongoRefNotFoundException {
		String refKey = (String)data.get(refKeyName);
		if(canBeNull && ((refKey == null)||(refKey.length()==0)))
			return null;
		DBObject obj = getEntity(refColl, refKey);
		if(obj == null)
			throw new MongoRefNotFoundException("RefID: " + refKeyName + 
					" not found for collection: " + refColl);
		else
			return obj;
	}

	/**
	 * 
	 * @param data
	 * @param coll
	 * @param refKeyName
	 * @param intDocKey
	 * @param cid
	 * @throws MongoRefNotFoundException
	 */
	private void ensureThatRefKeysMatch(DBObject data, String coll, String refKeyName, 
			String intDocKey, String cid) throws MongoRefNotFoundException{
		String parentKey = data.get(refKeyName).toString();
		DBObject q = new BasicDBObject(intDocKey + ".cid",new ObjectId(cid));
		DBCursor cursor =  DBConn.getConn().getCollection(coll).find(q);
		while(cursor.hasNext()) {
			DBObject parent = cursor.next();
			ObjectId objID =  (ObjectId)parent.get("_id");
			if(!objID.toString().equalsIgnoreCase(parentKey)) {
				throw new MongoRefNotFoundException("Error in reference IDs (" + 
						parentKey + "!=" + objID + ")");
			}
		}
	}

	/**
	 * 
	 * @param coll
	 * @param id
	 * @return
	 */
	public DBObject deleteDocument(String coll, String id) {
		DBObject objRemoved;
		try {
			DBObject deleteQuery = new BasicDBObject("_id", new ObjectId(id));
			objRemoved = DBConn.getConn().getCollection(coll).findAndRemove(deleteQuery);
			objRemoved = cascadeDeletes(coll, id,objRemoved);
		}catch(Exception e) {
			return jSON2Rrn.createJSONError("remove db." + coll + " with id=" + id,e);
		}
		return jSON2Rrn.createJSONRemovePostMessage(coll,id,objRemoved) ;
	}

	/**
	 * 
	 * @param coll
	 * @param id
	 * @param refKey
	 */
	private DBObject getAndDeleteReferencedDocuments(String coll, String id, String refKey, DBObject objRemoved) {
		BasicDBObject q = new BasicDBObject(refKey, id); 
		int deleted = DBConn.getConn().getCollection(coll).remove(q).getN();
		BasicDBObject line = new BasicDBObject("deleted",deleted);
		line.put(refKey, id);
		objRemoved.put("cascadeDel_"+coll, line );
		objRemoved = cascadeDeletes(coll, id, objRemoved); 
		return objRemoved;
	}

	/**
	 * 
	 * @param coll
	 * @param id
	 * @param objRemoved
	 * @return
	 */
	private DBObject cascadeDeletes(String coll, String id, DBObject objRemoved) {
		if(coll.equalsIgnoreCase(MongoProjects.COL_PROJECTS)) {
			objRemoved = getAndDeleteReferencedDocuments(MongoScenarios.COL_SCENARIOS,id,MongoScenarios.REF_PROJECT,objRemoved);
		}
		else if(coll.equalsIgnoreCase(MongoScenarios.COL_SCENARIOS)) {
			objRemoved = getAndDeleteReferencedDocuments(MongoInstallations.COL_INSTALLATIONS,id,MongoInstallations.REF_SCENARIO,objRemoved);
			objRemoved = getAndDeleteReferencedDocuments(MongoDemographics.COL_DEMOGRAPHICS,id,MongoDemographics.REF_SCENARIO,objRemoved);
			objRemoved = getAndDeleteReferencedDocuments(MongoSimParam.COL_SIMPARAM,id,MongoSimParam.REF_SCENARIO,objRemoved);
		}
		else if(coll.equalsIgnoreCase(MongoInstallations.COL_INSTALLATIONS)) {
			//objRemoved = getAndDeleteReferencedDocuments(MongoInstallations.COL_INSTALLATIONS,id,MongoInstallations.REF_BELONGS_TO_INST,objRemoved);
			objRemoved = getAndDeleteReferencedDocuments(MongoAppliances.COL_APPLIANCES,id,MongoAppliances.REF_INSTALLATION,objRemoved);
			objRemoved = getAndDeleteReferencedDocuments(MongoPersons.COL_PERSONS,id,MongoPersons.REF_INSTALLATION,objRemoved);
			objRemoved = deleteInternalDocument(MongoDemographics.COL_DEMOGRAPHICS, "generators", MongoDemographics.REF_ENTITY,id,objRemoved);
		}
		else if(coll.equalsIgnoreCase(MongoPersons.COL_PERSONS)) {
			objRemoved = getAndDeleteReferencedDocuments(MongoActivities.COL_ACTIVITIES,id,MongoActivities.REF_PERSON,objRemoved);
			objRemoved = deleteInternalDocument(MongoDemographics.COL_DEMOGRAPHICS, "generators", MongoDemographics.REF_ENTITY,id,objRemoved);
		}
		else if(coll.equalsIgnoreCase(MongoAppliances.COL_APPLIANCES)) {
			objRemoved = getAndDeleteReferencedDocuments(MongoConsumptionModels.COL_CONSMODELS,id,MongoConsumptionModels.REF_APPLIANCE,objRemoved);
			objRemoved = deleteArrayElements(MongoActivityModels.COL_ACTMODELS, MongoActivityModels.REF_CONTAINSAPPLIANCES,id,objRemoved);
			objRemoved = deleteInternalDocument(MongoDemographics.COL_DEMOGRAPHICS, "generators", MongoDemographics.REF_ENTITY,id,objRemoved);
		}
		else if(coll.equalsIgnoreCase(MongoActivities.COL_ACTIVITIES)) {
			objRemoved = getAndDeleteReferencedDocuments(MongoActivityModels.COL_ACTMODELS,id,MongoActivityModels.REF_ACTIVITY,objRemoved);
		}
		else if(coll.equalsIgnoreCase(MongoActivityModels.COL_ACTMODELS)) {
			objRemoved = getAndDeleteReferencedDocuments(MongoDistributions.COL_DISTRIBUTIONS,id,MongoDistributions.REF_ACTIVITYMODEL,objRemoved);
		}
		else if(coll.equalsIgnoreCase(MongoDistributions.COL_DISTRIBUTIONS)) {
			objRemoved = deleteArrayElements(MongoActivityModels.COL_ACTMODELS, MongoActivityModels.REF_DISTR_DURATION ,id,objRemoved);
			objRemoved = deleteArrayElements(MongoActivityModels.COL_ACTMODELS, MongoActivityModels.REF_DISTR_REPEATS ,id,objRemoved);
			objRemoved = deleteArrayElements(MongoActivityModels.COL_ACTMODELS, MongoActivityModels.REF_DISTR_STARTTIME ,id,objRemoved);
		}
		return objRemoved;
	}

	/**
	 * 
	 * db.installations.update({},{$pull : {"appliances": { "cid" : ObjectId("4ff153dde4b0c855ac36d9a9") }}},false,true)
	 * 
	 * @param coll
	 * @param keyName
	 * @param refKeyName
	 * @return
	 */
	public DBObject deleteInternalDocument(String coll, String keyName, String keyName2, String id, DBObject objRemoved) {
			DBObject q = new BasicDBObject();
			DBObject o = new BasicDBObject(new BasicDBObject("$pull",new BasicDBObject(
					keyName, new BasicDBObject(keyName2,id))));
			int removed = DBConn.getConn().getCollection(coll).update(q, o, false, true).getN();
			objRemoved.put("cascadeDel_"+coll + "[" +keyName + "]", removed );
			return objRemoved;
	}

	/**
	 * 
	 * @param coll
	 * @param keyName
	 * @param id
	 * @param objRemoved
	 * @return
	 */
	public DBObject deleteArrayElements(String coll, String keyName, String id, DBObject objRemoved) {
		DBObject q = new BasicDBObject();
		DBObject o = new BasicDBObject("$pull",new BasicDBObject(keyName,id));
		int removed = DBConn.getConn().getCollection(coll).update(q, o, false, true).getN();
		objRemoved.put("cascadeDel_"+coll + "[" +keyName + "]", removed );
		return objRemoved;
	}

	/**
	 * 
	 * @param coll
	 * @param fieldName
	 * @param cid
	 * @return
	 */
	public DBObject deleteDocumentField(String coll,  String fieldName, String cid) {
		DBObject deletedField;
		try {
			deletedField = getEntity(coll,fieldName + ".cid", cid, "Simulation Parameter " +
					"with cid=" + cid + " removed successfully", false, new String[]{ fieldName});
			if(!deletedField.containsField("data"))
				throw new MongoInvalidObjectId("invalid ObjectId [" + cid + "]");
			Vector<?> data = (Vector<?>)deletedField.get("data");
			if(data.size()==0)
				deletedField = null;
			DBObject q = new BasicDBObject(fieldName + ".cid",new ObjectId(cid));
			DBObject o = new BasicDBObject("$unset",new BasicDBObject(fieldName,1));
			DBConn.getConn().getCollection(coll).update(q, o, false, true);
		}catch(Exception e) {
			return jSON2Rrn.createJSONError("remove field db." + coll + "." + fieldName + 
					" with cid=" + cid + "failed",e);
		}
		return jSON2Rrn.createJSONRemovePostMessage(coll + "." + fieldName + ".cid",cid,deletedField) ;
	}

	/**
	 * curl -i 'http://localhost:8080/cassandra/api/results?inst_id=instID_&aggr_unit=3&metric=1&from=3&to=100'
	 * 
	 * @param installationId
	 * @param metric
	 * @param aggregationUnit
	 * @param fromTick
	 * @param toTick
	 * @return
	 */
	public DBObject mongoResultQuery(String runId, String installationId, String metricS, String aggregationUnitS, String fromTickS, String toTickS) {
		try {
			if(runId != null && installationId != null)
				throw new RestQueryParamMissingException(
						"Either run_id or installation_id should be null");
			else if(runId == null && installationId == null)
				throw new RestQueryParamMissingException(
						"Both run_id and installation_id are null");

			Integer aggregationUnit = null;
			if(aggregationUnitS != null)
				aggregationUnit = Integer.parseInt(aggregationUnitS);
			Integer metric = null;
			if(metricS != null)
				metric = Integer.parseInt(metricS);
			Integer fromTick = null;
			if(fromTickS != null)
				fromTick = Integer.parseInt(fromTickS);
			Integer toTick = null;
			if(toTickS != null)
				toTick = Integer.parseInt(toTickS);
			String coll = MongoResults.COL_AGGRRESULTS;
			if(aggregationUnit == null || aggregationUnit <= 0)
				aggregationUnit = 1;
			if(installationId != null)
				coll = MongoResults.COL_INSTRESULTS;

			String yMetric = "p";
			if(metric != null && metric == REACTIVE_POWER_Q)
				yMetric = "q";
			//db.inst_results.find({inst_id:"dszfs123",tick:{$gt:1}}).sort({tick:1}).pretty()
			//db.inst_results.group(
			//	{
			//	 keyf:function(doc)
			//		{var key=new NumberInt(doc.tick/4); return {x:key}
			//		} , 
			//	 cond:{inst_id:"instID_"}, 
			//	 reduce:function(obj,prev)
			//		{prev.csum+=obj.p},
			//	 initial:{csum:0}
			//	}
			//)
			BasicDBObject condition = null; 
			if(installationId != null || fromTick != null || toTick != null)
				condition =	new BasicDBObject();
			if(runId != null)
				condition.append("run_id",runId);
			if(installationId != null)
				condition.append("inst_id",installationId);
			if(fromTick != null)
				condition.append("tick",new BasicDBObject("$gte",fromTick));
			if(toTick != null)
				condition.append("tick",new BasicDBObject("$lte",toTick));

			BasicDBObject groupCmd = new BasicDBObject("ns",coll);
			groupCmd.append("$keyf", "function(doc){var key=new NumberInt(doc.tick/" + aggregationUnit + "); return {x:key}}");
			if(condition != null)
				groupCmd.append("cond", condition); 
			groupCmd.append("$reduce", "function(obj,prev){prev.y+=obj." + yMetric + "}");
			groupCmd.append("initial",  new BasicDBObject("y",0));
			@SuppressWarnings("deprecation")
			BasicDBList dbList = (BasicDBList)DBConn.getConn().getCollection(coll).group(groupCmd);
			return jSON2Rrn.createJSONPlot(dbList, "Data for plot retrieved successfully", 
					"title", "xAxisLabel", "yAxisLabel"); 

		}catch(Exception e) {
			e.printStackTrace();
			return jSON2Rrn.createJSONError("Error in retrieving results", e.getMessage());
		}
	}


	/**
	 * 
	 * @param key
	 * @param data
	 * @return
	 * @throws MongoRefNotFoundException
	 */
	public String getRefKey(String key, DBObject data) throws MongoRefNotFoundException {
		if(!data.containsField(key))
			throw new MongoRefNotFoundException(key + " not found");
		return data.get(key).toString();
	}

//	/**
//	 * 
//	 * @param collection
//	 * @param id
//	 * @param keyName
//	 */
//	public void deleteReferencedObject(String collection, String id, String keyName) {
//		DBObject deleteQuery = new BasicDBObject(keyName, id);
//		DBObject objRemoved = DBConn.getConn().getCollection(collection).findAndRemove(deleteQuery);
//	}


}

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

import java.util.HashMap;
import java.util.Vector;

import javax.ws.rs.core.HttpHeaders;

import org.bson.types.ObjectId;

import eu.cassandra.server.api.exceptions.BadParameterException;
import eu.cassandra.server.api.exceptions.JSONSchemaNotValidException;
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
import eu.cassandra.server.mongo.MongoPricingPolicy;
import eu.cassandra.server.mongo.MongoProjects;
import eu.cassandra.server.mongo.MongoResults;
import eu.cassandra.server.mongo.MongoScenarios;
import eu.cassandra.server.mongo.MongoSimParam;
import eu.cassandra.sim.PricingPolicy;
import eu.cassandra.sim.entities.appliances.GUIConsumptionModel;
import eu.cassandra.sim.math.GUIDistribution;
import eu.cassandra.sim.utilities.Utils;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class MongoDBQueries {

	public final static String ACTIVE_POWER_P = "p";
	public final static String REACTIVE_POWER_Q = "q";

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


	/**
	 * 
	 * @param dataToInsert
	 * @param coll
	 * @param refKeyName
	 * @param schemaType
	 * @return
	 */
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
	public DBObject getEntity(HttpHeaders httpHeaders,String coll, String qKey, String qValue, 
			String successMsg, String...fieldNames) {
		return getEntity(httpHeaders,coll, qKey, qValue, null, null, 0, 0, successMsg, false, fieldNames);
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
	public DBObject getEntity(String dbName, String coll, String qKey, String qValue, 
			String successMsg, String...fieldNames) {
		return getEntity(null,dbName, coll, qKey, qValue, null, null, 0, 0, successMsg, false, fieldNames);
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
	public DBObject getEntity(HttpHeaders httpHeaders,String coll, String qKey, String qValue, 
			String successMsg,  boolean counter, String...fieldNames) {
		return getEntity(httpHeaders,coll, qKey, qValue, null, null, 0, 0, successMsg, counter, fieldNames);
	}


	/**
	 * 
	 * @param httpHeaders
	 * @param scn_id
	 * @param obj2Get
	 * @return
	 */
	public DBObject getCountsPerType(HttpHeaders httpHeaders,String scn_id, String obj2Get) {
		HashMap<String,Integer> counterMap  = new HashMap<String,Integer>();
		BasicDBObject q = new BasicDBObject();
		q.put(MongoInstallations.REF_SCENARIO , scn_id);

		DBCursor cursorDoc = DBConn.getConn(getDbNameFromHTTPHeader(httpHeaders)).
				getCollection(MongoInstallations.COL_INSTALLATIONS).find(q);
		while (cursorDoc.hasNext()) { //Iterate installations
			DBObject obj = cursorDoc.next();
			if(obj2Get.equalsIgnoreCase(MongoInstallations.COL_INSTALLATIONS)) {
				String type = obj.get("type").toString();
				addToMap(type,counterMap);
			}
			else {
				BasicDBObject q2 = new BasicDBObject();
				q2.put("inst_id", obj.get("_id").toString());
				DBCursor cursorDoc2 = DBConn.getConn(getDbNameFromHTTPHeader(httpHeaders)).getCollection(obj2Get).find(q2);
				while (cursorDoc2.hasNext()) { //Iterate Persons or Appliances
					DBObject obj2 = cursorDoc2.next();
					String type = obj2.get("type").toString();
					addToMap(type,counterMap);
				}
				cursorDoc2.close();
			}
		}
		cursorDoc.close();

		Vector<DBObject> data = new Vector<DBObject>();
		for(String type : counterMap.keySet()) {
			BasicDBObject d = new BasicDBObject();
			d.put("type", type);
			d.put("count", counterMap.get(type));
			data.add(d);
		}
		return jSON2Rrn.createJSON(data, "Counters per type for " + obj2Get + " of Scenario: " + scn_id);
	}


	private HashMap<String,Integer> addToMap(String type, HashMap<String,Integer> map){
		if(map.containsKey(type))
			map.put(type, map.get(type)+1);
		else
			map.put(type, 1);
		return map;
	}


	/**
	 * 
	 * @param httpHeaders
	 * @param scn_id
	 * @param obj2Get
	 * @return
	 */
	public DBObject getSecondLevelCounts(HttpHeaders httpHeaders,String scn_id, String obj2Get) {
		int counter = 0;
		BasicDBObject q = new BasicDBObject();
		q.put(MongoInstallations.REF_SCENARIO , scn_id);
		DBCursor cursorDoc = DBConn.getConn(getDbNameFromHTTPHeader(httpHeaders)).
				getCollection(MongoInstallations.COL_INSTALLATIONS).find(q);
		while (cursorDoc.hasNext()) { //Iterate installations
			DBObject obj = cursorDoc.next();
			BasicDBObject q2 = new BasicDBObject();
			q2.put("inst_id", obj.get("_id").toString());
			counter += DBConn.getConn(getDbNameFromHTTPHeader(httpHeaders)).getCollection(obj2Get).find(q2).count();
		}
		cursorDoc.close();

		BasicDBObject data = new BasicDBObject();
		data.put("count", counter);
		return 	 jSON2Rrn.createJSON(data, "Number of " + obj2Get + " of Scenario: " + scn_id + " retrieved");
	}

	/**
	 * 	
	 * @param httpHeaders
	 * @param coll
	 * @param qKey
	 * @param qValue
	 * @param filters
	 * @param sort
	 * @param limit
	 * @param skip
	 * @param successMsg
	 * @param count
	 * @param fieldNames
	 * @return
	 */
	public DBObject getEntity(HttpHeaders httpHeaders,String coll, String qKey, String qValue, 
			String filters, String sort, int limit, int skip,
			String successMsg,  boolean count, String...fieldNames) {
		return getEntity(httpHeaders,null, coll, qKey, qValue, filters, sort, 
				limit, skip, successMsg, count, fieldNames);
	}


	/**
	 * 
	 * @param httpHeaders
	 * @param dbName
	 * @param coll
	 * @param qKey
	 * @param qValue
	 * @param filters
	 * @param sort
	 * @param limit
	 * @param skip
	 * @param successMsg
	 * @param count
	 * @param fieldNames
	 * @return
	 */
	public DBObject getEntity(HttpHeaders httpHeaders,String dbName, String coll, String qKey, String qValue, 
			String filters, String sort, int limit, int skip,
			String successMsg,  boolean count, String...fieldNames) {
		DBObject query;
		BasicDBObject fields = null;
		try {
			query = new BasicDBObject();
			if(qKey != null && qValue != null && 
					(qKey.equalsIgnoreCase("_id") || qKey.endsWith(".cid"))) {
				query.put(qKey, new ObjectId(qValue));
			}
			else if(qKey != null && qValue != null) {
				try{
					if(filters != null) {
						System.out.println(PrettyJSONPrinter.prettyPrint(filters)); 
						query = (DBObject)JSON.parse(filters);
					}
					else
						query = new BasicDBObject();
				}catch(Exception e) {
					return jSON2Rrn.createJSONError("Cannot get entity for collection: " + coll + 
							", error in filters: " + filters ,e);
				}
				query.put(qKey, qValue);
			}
			if(fieldNames != null) {
				fields = new BasicDBObject();
				for(String fieldName: fieldNames) {
					fields.put(fieldName, 1);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			return jSON2Rrn.createJSONError("Cannot get entity for collection: " + coll + 
					" with qKey=" + qKey + " and qValue=" + qValue,e);
		}
		return new MongoDBQueries().executeFindQuery(httpHeaders, dbName, 
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
	public DBObject getEntity(String collection, String id) {
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		return DBConn.getConn().getCollection(collection).findOne(query);
	}

	/**
	 * 
	 * @param collection
	 * @param id
	 * @return
	 */
	public DBObject getEntity(String collection, String key, String value) {
		BasicDBObject query = new BasicDBObject();
		query.put(key, value);
		return DBConn.getConn().getCollection(collection).findOne(query);
	}

	/**
	 * 
	 * @param coll
	 * @param entityName
	 * @return
	 */
	public DBObject getInternalEntities(HttpHeaders httpHeaders, String coll, String entityName, String parentID) {
		try {
			BasicDBObject query = new BasicDBObject("_id", new ObjectId(parentID));
			BasicDBObject fields = new BasicDBObject(entityName,1);
			DBObject result = new MongoDBQueries().executeFindQuery(httpHeaders,coll,query, fields, 
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
	public DBObject getInternalEntity(HttpHeaders httpHeaders, String coll, String entityName, String cid) {
		return getInternalEntity(httpHeaders,coll, entityName, cid,null); 
	}

	/**
	 * 
	 * @param coll
	 * @param entityName
	 * @param cid
	 * @param successMsg
	 * @return
	 */
	public DBObject getInternalEntity(HttpHeaders httpHeaders, String coll, String entityName, 
			String cid,String successMsg) {
		BasicDBObject internalEntity = null;
		try {
			BasicDBObject query = new BasicDBObject(entityName + ".cid", new ObjectId(cid));
			BasicDBObject fields = new BasicDBObject(entityName,1);
			DBObject result = new MongoDBQueries().executeFindQuery(httpHeaders,coll,query,
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
			throw new MongoRefNotFoundException("RefNotFound: Cannot get internal entity " +  entityName + 
					" with cid=" + cid + " from collection: " + coll);
		}catch(Exception e) {
			return jSON2Rrn.createJSONError("GetInternalEntityError: Cannot get internal entity " +  entityName + 
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
	public DBObject executeFindQuery(HttpHeaders httpHeaders, String collection, 
			BasicDBObject dbObj1, BasicDBObject dbObj2, String successMsg) {
		return executeFindQuery(httpHeaders,collection,dbObj1, dbObj2, 
				successMsg,null, 0, 0, false); 
	}


	/**
	 * 
	 * @param httpHeaders
	 * @param collection
	 * @param dbObj1
	 * @param dbObj2
	 * @param successMsg
	 * @param sort
	 * @param limit
	 * @param skip
	 * @param count
	 * @return
	 */
	public DBObject executeFindQuery(HttpHeaders httpHeaders,String collection, 
			DBObject dbObj1, DBObject dbObj2, String successMsg,
			String sort, int limit, int skip, boolean count) {
		return executeFindQuery(httpHeaders,null, collection, 
				dbObj1, dbObj2, successMsg,sort, limit, skip, count);
	}

	/**
	 * 
	 * @param httpHeaders
	 * @param collection
	 * @param dbObj1
	 * @param dbObj2
	 * @param successMsg
	 * @param sort
	 * @param limit
	 * @param skip
	 * @param count
	 * @return
	 */
	public DBObject executeFindQuery(HttpHeaders httpHeaders,String dbName, String collection, 
			DBObject dbObj1, DBObject dbObj2, String successMsg,
			String sort, int limit, int skip, boolean count) {
		try {
			if(dbName == null)
				dbName = getDbNameFromHTTPHeader(httpHeaders);
			DBCursor cursorDoc = null;
			if(count) {
				BasicDBObject dbObject = new BasicDBObject(); 
				dbObject.put("count", DBConn.getConn(dbName).
						getCollection(collection).find(dbObj1).count());
				return 	 jSON2Rrn.createJSON(dbObject, successMsg);
			}
			else {
				if(dbObj2 == null) {
					cursorDoc = DBConn.getConn(dbName).
							getCollection(collection).find(dbObj1);
				}
				else {
					cursorDoc = DBConn.getConn(dbName).
							getCollection(collection).find(dbObj1,dbObj2);
				}
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

			Vector<DBObject> recordsVec = new Vector<DBObject>();
			while (cursorDoc.hasNext()) {
				DBObject obj = cursorDoc.next();
				if(collection.equalsIgnoreCase(MongoDistributions.COL_DISTRIBUTIONS) &&
						dbObj1.containsField("_id")) {
					obj = getValues(obj,httpHeaders,dbName, obj.get("_id").toString(),MongoDistributions.COL_DISTRIBUTIONS);
				}
				if(collection.equalsIgnoreCase(MongoDistributions.COL_DISTRIBUTIONS) &&
						dbObj1.containsField("actmod_id")) {
					if(((String)obj.get("distrType")).equalsIgnoreCase("Histogram")) {
						BasicDBList t = (BasicDBList)obj.get("values");
						if(t != null) {
							double[] values = Utils.dblist2doubleArr(t);
							if(values.length <= 100) {
								obj.put("chartType", "bars");
							} else {
								obj.put("chartType", "line");
							}
						}
					}
				}
				else if(collection.equalsIgnoreCase(MongoConsumptionModels.COL_CONSMODELS) &&
						dbObj1.containsField("_id")) {
					obj = getValues(obj,httpHeaders,dbName, obj.get("_id").toString(),MongoConsumptionModels.COL_CONSMODELS);
				} else if(collection.equalsIgnoreCase(MongoPricingPolicy.COL_PRICING)) {
					PricingPolicy pp = new PricingPolicy(obj);
					double oneKw24Cost = pp.calcOneKw24();
					obj.put("onekw24", oneKw24Cost);
				}

				if(obj.containsField("_id"))
					obj = addChildrenCounts(httpHeaders,collection, obj);
				recordsVec.add(obj);
			}
			cursorDoc.close();
			return jSON2Rrn.createJSON(recordsVec,successMsg);
		}
		catch(Exception e) {
			e.printStackTrace();
			return jSON2Rrn.createJSONError("MongoQueryError: Cannot execute find query for collection: " + collection + 
					" with qKey=" + dbObj1 + " and qValue=" + dbObj2,e);
		}
	}


	/**
	 * 
	 * @param dBObject
	 * @param httpHeaders
	 * @param id
	 * @return
	 * @throws JSONSchemaNotValidException 
	 * @throws BadParameterException 
	 */
	private DBObject getValues(DBObject dBObject, HttpHeaders httpHeaders, String dbName,
			String id, String coll) throws JSONSchemaNotValidException, BadParameterException {
		System.out.println("Here");
		if(dbName == null)
			dbName = getDbNameFromHTTPHeader(httpHeaders);
		if(coll.equalsIgnoreCase(MongoDistributions.COL_DISTRIBUTIONS)) {
			double values[] = null;
			String type = null;
			if(dBObject.containsField("parameters")){
				type = MongoActivityModels.REF_DISTR_STARTTIME ;
				if(DBConn.getConn(dbName).getCollection("act_models").
						findOne(new BasicDBObject("duration", id)) != null) {
					type = MongoActivityModels.REF_DISTR_DURATION;
				}
				else if (DBConn.getConn(dbName).getCollection("act_models").
						findOne(new BasicDBObject("repeatsNrOfTime", id)) != null) {
					type = MongoActivityModels.REF_DISTR_REPEATS;
				}
				values = new GUIDistribution(type, dBObject).getValues();
			}
			if(values == null && dBObject.containsField("values")) {
				BasicDBList t = (BasicDBList)dBObject.get("values");
				values = Utils.dblist2doubleArr(t); 
			}
			if(values != null) {
				int exp = Utils.checkExp(values);
				Utils.upscale(values, exp);
				BasicDBList list = new BasicDBList();
				for(int i=0;i<values.length;i++) {
					BasicDBObject dbObj = new BasicDBObject("x",i);
					dbObj.put("y", values[i]);
					list.add(dbObj);
				}
				dBObject.put("values", list);
				dBObject.put("exp", exp);
				System.out.println((String)dBObject.get("distrType"));
				if(((String)dBObject.get("distrType")).equalsIgnoreCase("Histogram")) {
					if(values.length <= 100) {
						dBObject.put("chartType", "bars");
					} else {
						dBObject.put("chartType", "line");
					}
				}
			}
		} else if(coll.equalsIgnoreCase(MongoConsumptionModels.COL_CONSMODELS)  ) {
			double pvalues[] = null;
			double qvalues[] = null;
			double ppoints[] = null;
			double qpoints[] = null;
			BasicDBList list = new BasicDBList();
			if(dBObject.containsField("pmodel")) {
				if(((DBObject)dBObject.get("pmodel")).containsField("params")) {
					GUIConsumptionModel p = new GUIConsumptionModel((DBObject) dBObject.get("pmodel"), "p");
					Double[] pvaluesConsModel = p.getValues(GUIConsumptionModel.P);
					Double[] pointsConsModel = p.getPoints(pvaluesConsModel.length);
					pvalues = new double[pvaluesConsModel.length];
					ppoints = new double[pointsConsModel.length];
					for(int i=0; i< pvaluesConsModel.length; i++) {
						pvalues[i] = pvaluesConsModel[i];
						ppoints[i] = pointsConsModel[i];
					}
				}
			}
			if(dBObject.containsField("qmodel")) {
				if(((DBObject)dBObject.get("qmodel")).containsField("params")) {
					GUIConsumptionModel q = new GUIConsumptionModel((DBObject) dBObject.get("qmodel"), "q");
					Double[] qvaluesConsModel = q.getValues(GUIConsumptionModel.Q);
					Double[] pointsConsModel = q.getPoints(qvaluesConsModel.length);
					qvalues = new double[qvaluesConsModel.length];
					qpoints = new double[pointsConsModel.length];
					for(int i=0; i< qvaluesConsModel.length; i++) {
						qvalues[i] = qvaluesConsModel[i];
						qpoints[i] = pointsConsModel[i];
					}
				}
			}
			if(pvalues != null && qvalues != null) {
				for(int i = 0; i < Math.min(pvalues.length, qvalues.length); i++) {
					BasicDBObject dbObj = new BasicDBObject("x", ppoints[i]);
					dbObj.put("p", pvalues[i]);
					dbObj.put("q", qvalues[i]);
					list.add(dbObj);
				}
			}
			dBObject.put("values", list);
		}

		return dBObject;
	}


	/**
	 * 
	 * @param httpHeaders
	 * @param coll
	 * @param data
	 * @return
	 */
	private DBObject addChildrenCounts(HttpHeaders httpHeaders,String coll, DBObject data) {
		return addChildrenCounts(httpHeaders,null , coll, data);
	}


	/**
	 * 
	 * @param httpHeaders
	 * @param dbName
	 * @param coll
	 * @param data
	 * @return
	 */
	private DBObject addChildrenCounts(HttpHeaders httpHeaders,String dbName, String coll, DBObject data) {
		SchemaInfo schemaInfo = MongoSchema.getSchemaInfo(coll);
		if(dbName == null)
			dbName = getDbNameFromHTTPHeader(httpHeaders);
		if(schemaInfo != null) {
			String id = ((ObjectId)data.get("_id")).toString();
			for(int i=0;i<schemaInfo.childCollection.length;i++) {
				BasicDBObject q = new BasicDBObject(schemaInfo.refKeys[i], id);
				int counter = DBConn.getConn(dbName).getCollection(
						schemaInfo.getChildCollection()[i]).find(q).count();
				data.put(schemaInfo.getChildCollection()[i] + "_counter", counter);
			}
		}
		return data;
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
		} catch(Exception e) {
			return jSON2Rrn.createJSONError("Update Failed for " + jsonToUpdate,e);
		}
		return getEntity(null,collection, qKey, qValue,successMsg,
				false, keysUpdated.toArray(new String[keysUpdated.size()]));
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
		return getInternalEntity(null,coll,entityName, cid,"Internal document " + coll + "." + 
				entityName + " with cid=" + cid + " was successfullylly updated");
	}

	//	/**
	//	 * 
	//	 * @param coll
	//	 * @param parentEntityName
	//	 * @param qObj
	//	 * @param cid
	//	 * @param objToPush
	//	 * @return
	//	 */
	//	public DBObject addArrayDocumentDump(String coll, String parentEntityName,
	//			DBObject qObj, String cid, DBObject objToPush) {
	//		try {
	//			DBObject dObj = new BasicDBObject("$push", objToPush);
	//			DBConn.getConn().getCollection(coll).update(qObj,dObj);
	//		}catch(Exception e) {
	//			return jSON2Rrn.createJSONError("Update Failed for " + objToPush.toString() ,e);
	//		}
	//		return getInternalEntity(null,coll,"activities", cid,"Internal document " + coll + "." + 
	//				parentEntityName + " with cid=" + cid + " was successfullylly updated");
	//	}


	/**
	 * 
	 * @param coll
	 * @param qField
	 * @param parentKeyFieldName
	 * @param updateFieldName
	 * @param newData
	 * @return
	 */
	public DBObject updateInternalDocumentDump(String coll, String qField, String parentKeyFieldName, String updateFieldName, String newData) {
		DBObject newObject = (DBObject) JSON.parse(newData);
		String parentID = newObject.get(parentKeyFieldName).toString();
		DBObject q = new BasicDBObject(qField, new ObjectId(parentID));
		DBObject o = new BasicDBObject("$set",new BasicDBObject(updateFieldName,newObject));
		DBConn.getConn().getCollection(coll).update(q,o,false,true);
		return new BasicDBObject("a","b");
	}


	public DBObject insertData(String coll, String dataToInsert, 
			String successMessage, int schemaType) {
		return insertData(coll, dataToInsert, successMessage,(String[])null,
				(String[])null,null, schemaType,null);
	}

	/**
	 * 
	 * @param coll
	 * @param dataToInsert
	 * @param successMessage
	 * @param schemaType
	 * @param httpHeaders
	 * @return
	 */
	public DBObject insertData(String coll, String dataToInsert, 
			String successMessage, int schemaType,HttpHeaders httpHeaders) {
		return insertData(coll, dataToInsert, successMessage,(String[])null,
				(String[])null,null, schemaType,httpHeaders);
	}

	public DBObject insertData(String coll, String dataToInsert, 
			String successMessage,String refColl, String refKeyName, int schemaType) {
		return insertData(coll, dataToInsert, successMessage, new String[] {refColl},
				new String[] {refKeyName}, new boolean[] {false}, schemaType,null);
	}

	/**
	 * 
	 * @param coll
	 * @param dataToInsert
	 * @param successMessage
	 * @param refColl
	 * @param refKeyName
	 * @param schemaType
	 * @param httpHeaders
	 * @return
	 */
	public DBObject insertData(String coll, String dataToInsert, 
			String successMessage,String refColl, String refKeyName, int schemaType,HttpHeaders httpHeaders) {
		return insertData(coll, dataToInsert, successMessage, new String[] {refColl},
				new String[] {refKeyName}, new boolean[] {false}, schemaType, httpHeaders);
	}


	/**
	 * 
	 * @param coll
	 * @param dataToInsert
	 * @param successMessage
	 * @param refColl
	 * @param refKeyName
	 * @param canBeNull
	 * @param schemaType
	 * @return
	 */
	public DBObject insertData(String coll, String dataToInsert, 
			String successMessage, String[] refColl, String[] refKeyName, 
			boolean[] canBeNull, int schemaType) {
		return insertData(coll, dataToInsert, successMessage, refColl, refKeyName, canBeNull, schemaType,null); 
	}

	/**
	 * 
	 * @param coll
	 * @param dataToInsert
	 * @param successMessage
	 * @param refColl
	 * @param refKeyName
	 * @param canBeNull
	 * @param schemaType
	 * @param httpHeaders
	 * @return
	 */
	public DBObject insertData(String coll, String dataToInsert, 
			String successMessage, String[] refColl, String[] refKeyName, 
			boolean[] canBeNull, int schemaType,HttpHeaders httpHeaders) {
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
			if(httpHeaders == null)
				DBConn.getConn().getCollection(coll).insert(data);
			else
				DBConn.getConn(MongoDBQueries.getDbNameFromHTTPHeader(httpHeaders)).getCollection(coll).insert(data);
		}catch(com.mongodb.util.JSONParseException e) {
			return jSON2Rrn.createJSONError("Error parsing JSON input", e.getMessage());
		}catch(Exception e) {
			return jSON2Rrn.createJSONError(dataToInsert,e);
		}
		return jSON2Rrn.createJSONInsertPostMessage(successMessage, data) ;
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
			throw new MongoRefNotFoundException("RefNotFound: " + refKeyName + 
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
		DBObject q = new BasicDBObject(intDocKey + ".cid", new ObjectId(cid));
		DBCursor cursor =  DBConn.getConn().getCollection(coll).find(q);
		while(cursor.hasNext()) {
			DBObject parent = cursor.next();
			ObjectId objID =  (ObjectId)parent.get("_id");
			if(!objID.toString().equalsIgnoreCase(parentKey)) {
				throw new MongoRefNotFoundException("RefNotFound: Error in reference IDs (" + 
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
		return deleteDocument(null,coll, id);	
	}


	/**
	 * 
	 * @param coll
	 * @param id
	 * @return
	 */
	public DBObject deleteDocument(String dbName, String coll, String id) {
		DBObject objRemoved;
		try {
			DBObject deleteQuery = new BasicDBObject("_id", new ObjectId(id));
			if(dbName != null)
				objRemoved = DBConn.getConn(dbName).getCollection(coll).findAndRemove(deleteQuery);
			else {
				objRemoved = DBConn.getConn().getCollection(coll).findAndRemove(deleteQuery);
				System.out.println(objRemoved.toString());
				objRemoved = cascadeDeletes(coll, id, objRemoved);
				System.out.println(objRemoved.toString());
			}
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
		System.out.println(keyName + " " + removed);
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
			deletedField = getEntity(null,coll,fieldName + ".cid", cid, "Simulation Parameter " +
					"with cid=" + cid + " removed successfully", false, new String[]{ fieldName});
			if(!deletedField.containsField("data"))
				throw new MongoInvalidObjectId("InvalidObjectid: [" + cid + "]");
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
	 * curl -i  --header "dbname:run_id" 'http://localhost:8080/cassandra/api/results?inst_id=instID_&aggr_unit=3&metric=1&from=3&to=100'
	 * 
	 * @param installationId
	 * @param metric
	 * @param aggregationUnit
	 * @param fromTick
	 * @param toTick
	 * @return
	 */
	public DBObject mongoResultQuery(HttpHeaders httpHeaders, String installationId, 
			String metricS, String aggregationUnitS, String fromTickS, String toTickS) {
		try {
			String runId = getDbNameFromHTTPHeader(httpHeaders);
			if(runId == null && installationId == null)
				throw new RestQueryParamMissingException(
						"QueryParamMissing: Both run_id and installation_id are null");

			String aggrUnit = " (Minute)";
			String defaultAggrUnit = " (Minute)";
			Integer aggregationUnit = null;
			Integer defaultAggregationUnit = null;
			if(aggregationUnitS != null) {
				aggregationUnit = Integer.parseInt(aggregationUnitS);
				aggrUnit = " " + aggregationUnit + " Minute" + (aggregationUnit==1?"":"s") + ")";
			}
			int numberOfDays = Integer.parseInt(DBConn.getConn(runId).
					getCollection("sim_param").findOne().get("numberOfDays").toString());
			if(numberOfDays == 1) {
				defaultAggregationUnit = 5;
				defaultAggrUnit = " (5 Minutes)";
			}
			else if(numberOfDays <= 5) {
				defaultAggregationUnit = 15;
				defaultAggrUnit = " (15 Minutes)";
			}
			else if(numberOfDays <= 20) {
				defaultAggregationUnit = 60;
				defaultAggrUnit = " (1 Hour)";
			}
			else if(numberOfDays <= 60) {
				defaultAggregationUnit = 180;
				defaultAggrUnit = " (3 Hours)";
			}
			else if(numberOfDays <= 360) {
				defaultAggregationUnit = 720;
				defaultAggrUnit = " (12 Hours)";
			}
			if(aggregationUnit == null) {
				aggregationUnit = defaultAggregationUnit;
				aggrUnit = defaultAggrUnit;
			}

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

			String yMetric = ACTIVE_POWER_P;
			if(metricS != null && metricS.equalsIgnoreCase(REACTIVE_POWER_Q))
				yMetric = REACTIVE_POWER_Q;
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
			if( installationId != null || fromTick != null || toTick != null)
				condition =	new BasicDBObject();
			if(installationId != null)
				condition.append("inst_id",installationId);

			if(fromTick != null && toTick != null)
				condition.append("tick",BasicDBObjectBuilder.start("$gte", fromTick).add("$lte", toTick).get());
			else if(fromTick != null)
				condition.append("tick",new BasicDBObject("$gte",fromTick));
			else if(toTick != null)
				condition.append("tick",new BasicDBObject("$lte",toTick));

			BasicDBObject groupCmd = new BasicDBObject("ns",coll);
			groupCmd.append("$keyf", "function(doc){var key=new NumberInt(doc.tick/" + aggregationUnit + "); return {x:key}}");
			if(condition != null)
				groupCmd.append("cond", condition); 
			groupCmd.append("$reduce", "function(obj,prev){prev.y+=obj." + yMetric + "}");
			groupCmd.append("initial",  new BasicDBObject("y",0));

			@SuppressWarnings("deprecation")
			BasicDBList dbList = (BasicDBList)DBConn.getConn(getDbNameFromHTTPHeader(httpHeaders)
					).getCollection(coll).group(groupCmd);

			if(aggregationUnit > 1) {
				for(int i=0;i<dbList.size();i++) {
					BasicDBObject obj = (BasicDBObject)dbList.get(i);
					obj.put("y", Double.parseDouble(obj.get("y").toString())/aggregationUnit);
				}
			}
			return jSON2Rrn.createJSONPlot(dbList, "Data for plot retrieved successfully", 
					"Consumption " + (yMetric.equalsIgnoreCase(REACTIVE_POWER_Q)?"Reactive Power":"Active Power"), 
					"Time" + aggrUnit, yMetric.equalsIgnoreCase(REACTIVE_POWER_Q)?"VAr":"W",defaultAggregationUnit,numberOfDays); 

		}catch(Exception e) {
			e.printStackTrace();
			return jSON2Rrn.createJSONError("Error in retrieving results", e.getMessage());
		}
	}

	/**
	 * curl -i  --header "dbname:run_id" 'http://localhost:8080/cassandra/api/kpis?inst_id=instID'
	 * curl -i  --header "dbname:run_id" 'http://localhost:8080/cassandra/api/kpis'
	 * 
	 * @param installationId
	 * @return
	 */
	public DBObject mongoKPIsQuery(HttpHeaders httpHeaders, String installationId, String appId, String actId) {
		try {
			String runId = getDbNameFromHTTPHeader(httpHeaders);
			if(runId == null && (installationId == null || appId == null || actId == null))
				throw new RestQueryParamMissingException(
						"QueryParamMissing: Both run_id and installation_id are null");
			String coll = MongoResults.COL_AGGRKPIS;
			if(installationId != null) coll = MongoResults.COL_INSTKPIS;
			if(appId != null) coll = MongoResults.COL_APPKPIS;
			if(actId != null) coll = MongoResults.COL_ACTKPIS;
			DBObject condition = new BasicDBObject();
			if( installationId != null) {
				condition.put("inst_id",installationId);
			}
			if( appId != null) {
				condition.put("app_id",appId);
			}
			if( actId != null) {
				condition.put("act_id",actId);
			}
			DBObject result = DBConn.getConn(runId).getCollection(coll).findOne(condition);
			if(result == null) throw new Exception("KPIs not found");
			return jSON2Rrn.createJSON(result, "KPIs retrieved succesfully.");
		}catch(Exception e) {
			e.printStackTrace();
			return jSON2Rrn.createJSONError("Error in retrieving results", e.getMessage());
		}
	}

	public DBObject mongoExpectedQuery(HttpHeaders httpHeaders, String installationId, String actId) {
		try {
			String runId = getDbNameFromHTTPHeader(httpHeaders);
			if(runId == null) {
				throw new RestQueryParamMissingException("QueryParamMissing: run_id is null");
			}
			DBObject condition = new BasicDBObject();
			String collection = new String();
			if( installationId != null) {
				condition.put("id", installationId);
				collection = MongoResults.COL_INSTRESULTS_EXP;
			} else if( actId != null) {
				condition.put("id", actId);
				collection = MongoResults.COL_ACTRESULTS_EXP;
			} else {
				condition.put("id", "aggr");
				collection = MongoResults.COL_AGGRRESULTS_EXP;
			}
			DBCursor cursor = DBConn.getConn(runId).getCollection(collection).find(condition);
			BasicDBList dbList = new BasicDBList();
			while(cursor.hasNext()) {
				DBObject o = cursor.next();
				DBObject dbo = new BasicDBObject();
				dbo.put("x", o.get("tick"));
				dbo.put("y", o.get("p"));
				dbList.add(dbo);
			}
			return jSON2Rrn.createJSONPlot(dbList, "Data for plot retrieved successfully", 
					"Expected Active Power", "Time", "W", 1, 1); 
			//return jSON2Rrn.createJSON(dbList, "Expected power retrieved succesfully.");
			
		} catch(Exception e) {
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
			throw new MongoRefNotFoundException("RefNotFound: " + key + " not found");
		return data.get(key).toString();
	}

	/**
	 * 
	 * @param httpHeaders
	 * @return
	 */
	public static String getDbNameFromHTTPHeader(HttpHeaders httpHeaders) {
		if(httpHeaders == null || httpHeaders.getRequestHeaders() == null || 
				!httpHeaders.getRequestHeaders().containsKey("dbname") )
			return null;
		else
			return httpHeaders.getRequestHeaders().getFirst("dbname");
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

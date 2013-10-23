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

import java.util.Vector;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class JSONtoReturn {


	/**
	 * 
	 * @param data
	 * @param ex
	 * @return
	 */
	public DBObject createJSONError(String data, Exception ex) {
		return  createJSONError(data, ex.getMessage() );
	}

	/**
	 * 
	 * @param data
	 * @param ex
	 * @return
	 */
	public DBObject createJSONError(String data, String ex) {
		DBObject errorMessage = new BasicDBObject();
		errorMessage.put("success", false);
		String error = "Exception";
		String errorDescr = ex == null ? "Null": ex;
		if(ex!= null && ex.matches("(\\S)+:(\\s)(.)*")) {
			String d[] = ex.split(": ",2);
			error = d[0].replace("$.", "");
			errorDescr = d[1];
		}
		errorMessage.put("errors", new BasicDBObject(error,errorDescr));
		errorMessage.put("message", data);
		return errorMessage;
	}

	/**
	 * 
	 * @param successMessage
	 * @param dataToInsert
	 * @param answer
	 * @return
	 */
	public DBObject createJSONInsertPostMessage(String successMessage, DBObject answer ) {
		DBObject postSuccessMessage = new BasicDBObject();
		postSuccessMessage.put("success", true);
		postSuccessMessage.put("message", successMessage);
		postSuccessMessage.put("data", changeObjectIdToString(answer));
		return postSuccessMessage;
	}

	/**
	 * 
	 * @param successMessage
	 * @param dataToInsert
	 * @param answer
	 * @return
	 */
	public DBObject createJSONRemovePostMessage(String coll, String idToRemove,
			Object objRemoved ) {
		DBObject postSuccessMessage = new BasicDBObject();
		postSuccessMessage.put("success", (objRemoved==null)?false:true);
		postSuccessMessage.put("message", (objRemoved==null)?"Object not found":"Object Removed");
		postSuccessMessage.put("idToRemove", idToRemove);
		if(objRemoved instanceof DBObject)
			postSuccessMessage.put("objectRemoved", changeObjectIdToString((DBObject)objRemoved));
		else
			postSuccessMessage.put("objectRemoved", objRemoved);
		return postSuccessMessage;
	}

	//	/**
	//	 * 
	//	 * @param cursorDoc
	//	 * @param descr
	//	 * @return
	//	 */
	//	public DBObject createJSON(DBCursor cursorDoc, String descr) {
	//		DBObject successMessage = new BasicDBObject();
	//		successMessage.put("success", true);
	//		successMessage.put("message", descr + ((cursorDoc.size()==0)?" (No data were found though)":""));
	//		successMessage.put("size", cursorDoc.size());
	//		Vector<DBObject> recordsVec = new Vector<DBObject>();
	//		while (cursorDoc.hasNext()) {
	//			DBObject obj = cursorDoc.next();
	//			recordsVec.add(changeObjectIdToString(obj));
	//		}
	//		cursorDoc.close();
	//		successMessage.put("data", recordsVec);
	//		return successMessage;
	//	}

	/**
	 * 
	 * @param dbObject
	 * @param descr
	 * @return
	 */
	public DBObject createJSON(DBObject dbObject, String descr) {
		Vector<DBObject> dbObjects = new Vector<DBObject>();
		dbObjects.add(dbObject);
		return createJSON(dbObjects, descr);
	}

	/**
	 * 
	 * @param dbObjects
	 * @param descr
	 * @return
	 */
	public DBObject createJSON(Vector<DBObject> dbObjects, String descr) {
		for(int i=0;i<dbObjects.size();i++) {
			dbObjects.set(i, changeObjectIdToString(dbObjects.get(i)));
		}
		DBObject successMessage = new BasicDBObject();
		successMessage.put("success", true);
		successMessage.put("message", descr + ((dbObjects.size()==0)?" (No data were found though)":""));
		successMessage.put("size", dbObjects.size());
		successMessage.put("data", dbObjects);
		return successMessage;
	}

	/**
	 * 
	 * @param dbObjects
	 * @param descr
	 * @param title
	 * @param xAxisLabel
	 * @param yAxisLabel
	 * @return
	 */
	public DBObject createJSONPlot(BasicDBList dbObjects, String descr, 
			String title, String xAxisLabel, String yAxisLabel, 
			int defaultAggrUnit,Integer numberOfDays) {
		DBObject successMessage = new BasicDBObject();
		successMessage.put("success", true);
		successMessage.put("message", descr);
		successMessage.put("title", title);
		successMessage.put("xAxisLabel", xAxisLabel);
		successMessage.put("yAxisLabel", yAxisLabel);
		successMessage.put("aggregationUnit", defaultAggrUnit);
		successMessage.put("numberOfDays", numberOfDays);
		successMessage.put("size", dbObjects.size());
		successMessage.put("data", dbObjects);
		//System.out.println(PrettyJSONPrinter.prettyPrint(successMessage));
		return successMessage;
	}

	/**
	 * 
	 * @param obj
	 * @return
	 */
	private DBObject changeObjectIdToString(DBObject obj) {
		if(obj.containsField("_id") && (obj.get("_id") instanceof ObjectId)) {
			obj.put("_id", obj.get("_id").toString());
		}
		return obj;
	}
}

package eu.cassandra.server.mongo.util;

import java.util.Vector;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
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
		errorMessage.put("exception", ex);
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

	/**
	 * 
	 * @param cursorDoc
	 * @param descr
	 * @return
	 */
	public DBObject createJSON(DBCursor cursorDoc, String descr) {
		DBObject successMessage = new BasicDBObject();
		successMessage.put("success", true);
		successMessage.put("message", descr + ((cursorDoc.size()==0)?" (No data were found though)":""));
		successMessage.put("size", cursorDoc.size());
		Vector<DBObject> recordsVec = new Vector<DBObject>();
		while (cursorDoc.hasNext()) {
			DBObject obj = cursorDoc.next();
			recordsVec.add(changeObjectIdToString(obj));
		}
		cursorDoc.close();
		successMessage.put("data", recordsVec);
		return successMessage;
	}

	/**
	 * 
	 * @param dbObject
	 * @param descr
	 * @return
	 */
	public DBObject createJSON(DBObject dbObject, String descr) {
		DBObject successMessage = new BasicDBObject();
		successMessage.put("success", true);
		successMessage.put("message", descr);
		successMessage.put("size", 1);
		successMessage.put("data", changeObjectIdToString(dbObject));
		return successMessage;
	}

	/**
	 * 
	 * @param dbObjects
	 * @param descr
	 * @return
	 */
	public DBObject createJSON(Vector<DBObject> dbObjects, String descr) {
		DBObject successMessage = new BasicDBObject();
		successMessage.put("success", true);
		successMessage.put("message", descr);
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
			String title, String xAxisLabel, String yAxisLabel) {
		DBObject successMessage = new BasicDBObject();
		successMessage.put("success", true);
		successMessage.put("message", descr);
		successMessage.put("title", title);
		successMessage.put("xAxisLabel", xAxisLabel);
		successMessage.put("yAxisLabel", yAxisLabel);
		successMessage.put("size", dbObjects.size());
		successMessage.put("data", dbObjects);
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

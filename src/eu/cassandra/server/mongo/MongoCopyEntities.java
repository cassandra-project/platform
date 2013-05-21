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

import java.util.Vector;

import javax.ws.rs.core.HttpHeaders;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.JSONValidator;
import eu.cassandra.server.mongo.util.MongoDBQueries;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

public class MongoCopyEntities {

	public MongoCopyEntities(HttpHeaders httpHeaders) {
		
	}
	
	private void addInfoForCascadedCopy(DBObject res,DBObject answer,String newID) {
		if(answer == null)
			answer = res;
		else {
			Object cascaded = answer.get("CascadedCopiedInstallations");
			if(cascaded == null) {
				Vector<String> vec = new Vector<String>();
				vec.add(newID);
				answer.put("CascadedCopiedInstallations",vec);
			}
			else {
				
				Object obj = answer.get("CascadedCopiedInstallations");
				@SuppressWarnings("unchecked")
				Vector<String> vec = (Vector<String>)obj;
				vec.add(newID);
				answer.put("CascadedCopiedInstallations",vec);
			}
			res = answer;
		}
	}
	
	/**
	 * 
	 * @param fromInstID
	 * @param toScnID
	 * @return
	 */
	public String copyInstallationToScenario(String instID, String toScnID ,DBObject answer) {
		DBObject fromObj = DBConn.getConn().getCollection(MongoInstallations.COL_INSTALLATIONS).findOne(new BasicDBObject("_id", new ObjectId(instID)));
		String oldInstallationID = ((ObjectId)fromObj.get("_id")).toString();
		fromObj.put(MongoInstallations.REF_SCENARIO, toScnID);
		if(answer == null) {
			copyOf(fromObj);
		}
		DBObject res = MongoInstallations.createInstallationObj(fromObj.toString());
		String newID = ((DBObject)res.get("data")).get("_id").toString();
		addInfoForCascadedCopy(res,answer,newID);
		//Copy Persons of the Installation
		DBObject q = new BasicDBObject(MongoPersons.REF_INSTALLATION, oldInstallationID);
		System.out.println(q);
		DBCursor cursorDoc = DBConn.getConn().getCollection(MongoPersons.COL_PERSONS).find(q);
		while (cursorDoc.hasNext()) {
			DBObject obj = cursorDoc.next();
			String childID = obj.get("_id").toString();
			copyPersonToInstallation(childID, newID,res);
		}
		//Copy Appliances of the Installation
		q = new BasicDBObject(MongoAppliances.REF_INSTALLATION, oldInstallationID);
		cursorDoc = DBConn.getConn().getCollection(MongoAppliances.COL_APPLIANCES).find(q);
		while (cursorDoc.hasNext()) {
			DBObject obj = cursorDoc.next();
			String childID = obj.get("_id").toString();
			copyApplianceToInstallation(childID, newID,res);
		}
		return PrettyJSONPrinter.prettyPrint(res);
	}

	/**
	 * 
	 * @param fromPersID
	 * @param toInstID
	 * @return
	 */
	public String copyPersonToInstallation(String persID, String toInstID, DBObject answer) {
		DBObject fromObj = DBConn.getConn().getCollection(
				MongoPersons.COL_PERSONS).findOne(new BasicDBObject("_id", new ObjectId(persID)));
		fromObj.put(MongoPersons.REF_INSTALLATION, toInstID);
		if(answer == null) {
			copyOf(fromObj);
		}
		String oldID = ((ObjectId)fromObj.get("_id")).toString();
		DBObject res =  MongoPersons.createPersonObj(fromObj.toString());
		String newID = ((DBObject)res.get("data")).get("_id").toString();
		addInfoForCascadedCopy(res,answer,newID);
		//Copy Activities of the Person
		DBObject q = new BasicDBObject(MongoActivities.REF_PERSON, oldID);
		DBCursor cursorDoc = DBConn.getConn().getCollection(MongoActivities.COL_ACTIVITIES).find(q);
		while (cursorDoc.hasNext()) {
			DBObject obj = cursorDoc.next();
			String childID = obj.get("_id").toString();
			copyActivityToPerson(childID, newID,res);
		}
		return PrettyJSONPrinter.prettyPrint(res.toString());
	}

	/**
	 * 
	 * @param fromAppID
	 * @param toInstID
	 * @return
	 */
	public String copyApplianceToInstallation(String appID, String toInstID, DBObject answer) {
		DBObject fromObj = DBConn.getConn().getCollection(
				MongoAppliances.COL_APPLIANCES).findOne(new BasicDBObject("_id", new ObjectId(appID)));
		fromObj.put(MongoAppliances.REF_INSTALLATION, toInstID);
		if(answer == null) {
			copyOf(fromObj);
		}
		String oldID = ((ObjectId)fromObj.get("_id")).toString();
		DBObject res =  MongoAppliances.createApplianceObj(fromObj.toString());
		String newID = ((DBObject)res.get("data")).get("_id").toString();
		addInfoForCascadedCopy(res,answer,newID);
		//Copy Consumption Model of the Appliance
		DBObject q = new BasicDBObject(MongoConsumptionModels.REF_APPLIANCE, oldID);
		DBCursor cursorDoc = DBConn.getConn().getCollection(MongoConsumptionModels.COL_CONSMODELS).find(q);
		while (cursorDoc.hasNext()) {
			DBObject obj = cursorDoc.next();
			String childID = obj.get("_id").toString();
			copyConsModelToAppliance(childID, newID,res);
		}
		return PrettyJSONPrinter.prettyPrint(res);
	}

	/**
	 * 
	 * @param fromActID
	 * @param toPersID
	 * @return
	 */
	public String copyActivityToPerson(String actID, String toPersID, DBObject answer) {
		DBObject fromObj = DBConn.getConn().getCollection(
				MongoActivities.COL_ACTIVITIES).findOne(new BasicDBObject("_id", new ObjectId(actID)));
		fromObj.put(MongoActivities.REF_PERSON, toPersID);
		String oldID = ((ObjectId)fromObj.get("_id")).toString();
		if(answer == null) {
			copyOf(fromObj);
		}
		DBObject res =  new MongoDBQueries().insertData(MongoActivities.COL_ACTIVITIES ,fromObj.toString() ,
				"Activity copied successfully", MongoPersons.COL_PERSONS ,
				MongoActivities.REF_PERSON,JSONValidator.ACTIVITY_SCHEMA );
		String newID = ((DBObject)res.get("data")).get("_id").toString();

		addInfoForCascadedCopy(res,answer,newID);
		
		//Copy Activity Models of the Activity
		DBObject q = new BasicDBObject(MongoActivityModels.REF_ACTIVITY, oldID);
		DBCursor cursorDoc = DBConn.getConn().getCollection(MongoActivityModels.COL_ACTMODELS).find(q);
		while (cursorDoc.hasNext()) {
			DBObject obj = cursorDoc.next();
			String childID = obj.get("_id").toString();
			copyActivityModelToActivity(childID, newID,res);
		}

		return PrettyJSONPrinter.prettyPrint(res);
	}

	/**
	 * 
	 * @param fromActmodID
	 * @param toActID
	 * @return
	 */
	public String copyActivityModelToActivity(String actmodID, String toActID, DBObject answer) {
		DBObject fromObj = DBConn.getConn().getCollection(
				MongoActivityModels.COL_ACTMODELS).findOne(new BasicDBObject("_id", new ObjectId(actmodID)));
		fromObj.put(MongoActivityModels.REF_ACTIVITY, toActID);
		String oldID = ((ObjectId)fromObj.get("_id")).toString();
		if(answer == null) {
			copyOf(fromObj);
		}
		DBObject res =  new MongoDBQueries().insertData(MongoActivityModels.COL_ACTMODELS ,fromObj.toString() ,
				"Activity Model copied successfully", MongoActivities.COL_ACTIVITIES ,
				MongoActivityModels.REF_ACTIVITY, JSONValidator.ACTIVITYMODEL_SCHEMA );
		String newID = ((DBObject)res.get("data")).get("_id").toString();

		addInfoForCascadedCopy(res, answer, newID);
		
		//Copy Distributions of the Activity Model
		DBObject q = new BasicDBObject(MongoDistributions.REF_ACTIVITYMODEL, oldID);
		DBCursor cursorDoc = DBConn.getConn().getCollection(MongoDistributions.COL_DISTRIBUTIONS).find(q);
		while (cursorDoc.hasNext()) {
			DBObject obj = cursorDoc.next();
			String childID = ((ObjectId)obj.get("_id")).toString();
			String distrClass = null;
			if(childID.equalsIgnoreCase(fromObj.get(MongoActivityModels.REF_DISTR_DURATION).toString())) {
				distrClass = MongoActivityModels.REF_DISTR_DURATION;
			} else if(childID.equalsIgnoreCase(fromObj.get(MongoActivityModels.REF_DISTR_REPEATS).toString())) {
				distrClass = MongoActivityModels.REF_DISTR_REPEATS;
			} else {
				distrClass = MongoActivityModels.REF_DISTR_STARTTIME;
			}
			copyDistributionToActivityModel(childID, newID, res, distrClass);
		}

		return PrettyJSONPrinter.prettyPrint(res);
	}

	/**
	 * 
	 * @param fromConsmodID
	 * @param toAppID
	 * @return
	 */
	public String copyConsModelToAppliance(String consmodID, String toAppID, DBObject answer) {
		DBObject fromObj = DBConn.getConn().getCollection(
				MongoConsumptionModels.COL_CONSMODELS).findOne(new BasicDBObject("_id", new ObjectId(consmodID)));
		fromObj.put(MongoConsumptionModels.REF_APPLIANCE, toAppID);
		if(answer == null) {
			copyOf(fromObj);
		}
		DBObject res =  new MongoDBQueries().insertData(MongoConsumptionModels.COL_CONSMODELS ,fromObj.toString() ,
				"Consumption Model copied successfully", MongoAppliances.COL_APPLIANCES ,
				MongoConsumptionModels.REF_APPLIANCE,JSONValidator.CONSUMPTIONMODEL_SCHEMA );
		String newID = ((DBObject)res.get("data")).get("_id").toString();

		addInfoForCascadedCopy(res,answer,newID);

		return PrettyJSONPrinter.prettyPrint(res.toString());
	}
	
	/**
	 * 
	 * @param fromDistrID
	 * @param toActmodID
	 * @return
	 */
	public String copyDistributionToActivityModel(String distrID, String toActmodID, DBObject answer, String distrClass) {
		DBObject fromObj = DBConn.getConn().getCollection(
				MongoDistributions.COL_DISTRIBUTIONS).findOne(new BasicDBObject("_id", new ObjectId(distrID)));
		fromObj.put(MongoDistributions.REF_ACTIVITYMODEL, toActmodID);
		if(answer == null) {
			copyOf(fromObj);
		}
		DBObject res =  new MongoDBQueries().insertData(MongoDistributions.COL_DISTRIBUTIONS ,fromObj.toString() ,
				"Distribution copied successfully", MongoActivityModels.COL_ACTMODELS ,
				MongoDistributions.REF_ACTIVITYMODEL,JSONValidator.DISTRIBUTION_SCHEMA );
		String newID = ((DBObject)res.get("data")).get("_id").toString();
		
		// Update relevant activity model
		DBObject actMod = DBConn.getConn().getCollection(
				MongoActivityModels.COL_ACTMODELS).findOne(new BasicDBObject("_id", new ObjectId(toActmodID)));
		actMod.put(distrClass, newID);
		String actModID = actMod.get("_id").toString();
		new MongoDBQueries().updateDocument("_id", actModID, actMod.toString(),
						MongoActivityModels.COL_ACTMODELS,  
						"Activity Model updated successfully",
						MongoActivities.COL_ACTIVITIES ,"act_id",
						JSONValidator.ACTIVITYMODEL_SCHEMA);

		addInfoForCascadedCopy(res, answer, newID);
		
		return PrettyJSONPrinter.prettyPrint(res.toString());
	}

	//	/**
	//	 * 
	//	 * @param fromDemogID
	//	 * @param toScnID
	//	 * @return
	//	 */
	//	private String copyDemographicsToScenario(String fromDemogID, String toScnID) {
	//		System.out.println(fromDemogID + "\t" + toScnID);
	//
	//		DBObject fromObj = DBConn.getConn().getCollection(
	//				MongoDemographics.COL_DEMOGRAPHICS).findOne(new BasicDBObject("_id", new ObjectId(fromDemogID)));
	//		System.out.println(PrettyJSONPrinter.prettyPrint(fromObj));
	//		String oldID = ((ObjectId)fromObj.get("_id")).toString();
	//		
	//		fromObj.put(MongoSimParam.REF_SCENARIO, toScnID);
	//		System.out.println(PrettyJSONPrinter.prettyPrint(fromObj));
	//
	//		DBObject res =  new MongoDBQueries().insertData(MongoDemographics.COL_DEMOGRAPHICS ,fromObj.toString() ,
	//				"Demographics copied successfully", MongoScenarios.COL_SCENARIOS, 
	//				MongoDemographics.REF_SCENARIO,JSONValidator.DEMOGRAPHICS_SCHEMA );
	//		String newID = ((DBObject)res.get("data")).get("_id").toString();
	//		System.out.println(PrettyJSONPrinter.prettyPrint(res));
	//
	//		//Copy Installations of Demographics
	//		DBObject q = new BasicDBObject(MongoDemographics.REF_ENTITY, oldID);
	//		System.out.println(PrettyJSONPrinter.prettyPrint(q));
	//		DBCursor cursorDoc = DBConn.getConn().getCollection(MongoDemographics.COL_DEMOGRAPHICS).find(q);
	//		while (cursorDoc.hasNext()) {
	//			DBObject obj = cursorDoc.next();
	//			String childID = obj.get("_id").toString();
	//			copyInstallationToScenario(childID, newID);
	//		}
	//
	//		return PrettyJSONPrinter.prettyPrint(res.toString());
	//	}


	public String copySimParamsToScenario(String smpID, String toScnID, DBObject answer) {
		DBObject fromObj = DBConn.getConn().getCollection(
				MongoSimParam.COL_SIMPARAM).findOne(new BasicDBObject("_id", new ObjectId(smpID)));
		fromObj.put(MongoSimParam.REF_SCENARIO, toScnID);
		if(answer == null) {
			copyOf(fromObj);
		}
		DBObject res =  new MongoDBQueries().insertData(MongoSimParam.COL_SIMPARAM ,fromObj.toString() ,
				"Simulation Parameters copied successfully", MongoScenarios.COL_SCENARIOS, 
				MongoSimParam.REF_SCENARIO,JSONValidator.SIMPARAM_SCHEMA );
		String newID = ((DBObject)res.get("data")).get("_id").toString();

		addInfoForCascadedCopy(res,answer,newID);

		return PrettyJSONPrinter.prettyPrint(res);
	}


	/**
	 * Copy Scenario
	 * 
	 * @param fromScnID
	 * @param toPrjID
	 * @return
	 */
	public String copyScenarioToProject(String scnID, String toPrjID) {
		DBObject fromObj = DBConn.getConn().getCollection(
				MongoScenarios.COL_SCENARIOS).findOne(new BasicDBObject("_id", new ObjectId(scnID)));
		fromObj.put(MongoScenarios.REF_PROJECT, toPrjID);
		copyOf(fromObj); // Change name starting with "Copy of "
		String oldScenarioID = ((ObjectId)fromObj.get("_id")).toString();

		DBObject res =  new MongoDBQueries().insertData(MongoScenarios.COL_SCENARIOS ,fromObj.toString() ,
				"Scenario copied successfully", MongoProjects.COL_PROJECTS , MongoScenarios.REF_PROJECT,JSONValidator.SCENARIO_SCHEMA );
		String newID = ((DBObject)res.get("data")).get("_id").toString();
		
		//Copy Installations of the Scenario
		DBObject q = new BasicDBObject(MongoInstallations.REF_SCENARIO, oldScenarioID);
		DBCursor cursorDoc = DBConn.getConn().getCollection(MongoInstallations.COL_INSTALLATIONS).find(q);
		while (cursorDoc.hasNext()) {
			DBObject obj = cursorDoc.next();
			String childID = obj.get("_id").toString();
			copyInstallationToScenario(childID, newID, res);
		}

		//		//Copy Demographics of the Scenario
		//		q = new BasicDBObject(MongoDemographics.REF_SCENARIO, oldScenarioID);
		//		System.out.println(PrettyJSONPrinter.prettyPrint(q));
		//		cursorDoc = DBConn.getConn().getCollection(MongoDemographics.COL_DEMOGRAPHICS).find(q);
		//		while (cursorDoc.hasNext()) {
		//			DBObject obj = cursorDoc.next();
		//			String childID = obj.get("_id").toString();
		//			copyDemographicsToScenario(childID, newID);
		//		}

		//Copy Simulation Parameters of the Scenario
		q = new BasicDBObject(MongoSimParam.REF_SCENARIO, oldScenarioID);
		cursorDoc = DBConn.getConn().getCollection(MongoSimParam.COL_SIMPARAM).find(q);
		while (cursorDoc.hasNext()) {
			DBObject obj = cursorDoc.next();
			String childID = obj.get("_id").toString();
			copySimParamsToScenario(childID, newID, res);
		}

		return PrettyJSONPrinter.prettyPrint(res);
	}
	
	private static void copyOf(DBObject obj) {
		String name = (String)obj.get("name");
		if(name != null) {
			obj.put("name", "Copy of " + name);
		}
	}
}

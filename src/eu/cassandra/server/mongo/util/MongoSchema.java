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

import eu.cassandra.server.mongo.MongoActivities;
import eu.cassandra.server.mongo.MongoActivityModels;
import eu.cassandra.server.mongo.MongoAppliances;
import eu.cassandra.server.mongo.MongoConsumptionModels;
import eu.cassandra.server.mongo.MongoDemographics;
import eu.cassandra.server.mongo.MongoDistributions;
import eu.cassandra.server.mongo.MongoInstallations;
import eu.cassandra.server.mongo.MongoPersons;
import eu.cassandra.server.mongo.MongoProjects;
import eu.cassandra.server.mongo.MongoRuns;
import eu.cassandra.server.mongo.MongoScenarios;
import eu.cassandra.server.mongo.MongoSimParam;

public class MongoSchema {

	private static final HashMap<String, SchemaInfo> CHILD_REF_KEYS = new  HashMap<String, SchemaInfo>();
	public static boolean initialized = false;

	/**
	 * 
	 * @return
	 */
	public static final SchemaInfo getSchemaInfo(String coll) {
		if(!initialized)
			init();
		return CHILD_REF_KEYS.get(coll);
	}

	/**
	 * 
	 */
	private static void init() {
		CHILD_REF_KEYS.put(MongoActivities.COL_ACTIVITIES ,
				new SchemaInfo(new String[] {
						MongoActivityModels.COL_ACTMODELS},new String[] {
						MongoActivityModels.REF_ACTIVITY}));

		CHILD_REF_KEYS.put(MongoActivityModels.COL_ACTMODELS ,
				new SchemaInfo(new String[] {MongoDistributions.COL_DISTRIBUTIONS},new String[] {
						MongoDistributions.REF_ACTIVITYMODEL}));

		CHILD_REF_KEYS.put(MongoAppliances.COL_APPLIANCES ,
				new SchemaInfo(new String[] {
						MongoActivityModels.COL_ACTMODELS,
						MongoConsumptionModels.COL_CONSMODELS},new String[] {
						MongoActivityModels.REF_CONTAINSAPPLIANCES,
						MongoConsumptionModels.REF_APPLIANCE}));

		CHILD_REF_KEYS.put(MongoInstallations.COL_INSTALLATIONS ,
				new SchemaInfo(new String[] {
						MongoAppliances.COL_APPLIANCES,
						MongoPersons.COL_PERSONS,
						MongoInstallations.COL_INSTALLATIONS},new String[] {
						MongoAppliances.REF_INSTALLATION,
						MongoPersons.REF_INSTALLATION,
						MongoInstallations.REF_BELONGS_TO_INST}));

		CHILD_REF_KEYS.put(MongoPersons.COL_PERSONS ,
				new SchemaInfo(new String[] {
						MongoActivities.COL_ACTIVITIES},new String[] {
						MongoActivities.REF_PERSON}));

		CHILD_REF_KEYS.put(MongoProjects.COL_PROJECTS ,
				new SchemaInfo(new String[] {
						MongoScenarios.COL_SCENARIOS,
						MongoRuns.COL_RUNS},new String[] {
						MongoScenarios.REF_PROJECT,
						MongoRuns.REF_PROJECT}));

		CHILD_REF_KEYS.put(MongoScenarios.COL_SCENARIOS ,
				new SchemaInfo(new String[] {
						MongoDemographics.COL_DEMOGRAPHICS,
						MongoInstallations.COL_INSTALLATIONS,
						MongoSimParam.COL_SIMPARAM},new String[] {
						MongoDemographics.REF_SCENARIO,
						MongoInstallations.REF_SCENARIO,
						MongoSimParam.REF_SCENARIO}));

		initialized = true;
	}




}

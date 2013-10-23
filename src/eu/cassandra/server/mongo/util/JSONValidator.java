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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.server.IServletContextListener;
import eu.cassandra.server.api.exceptions.JSONSchemaNotValidException;
import eu.vahlas.json.schema.JSONSchema;
import eu.vahlas.json.schema.JSONSchemaProvider;
import eu.vahlas.json.schema.impl.JacksonSchemaProvider;

public class JSONValidator {

	public static final int ACTIVITY_SCHEMA = 0;
	public static final int ACTIVITYMODEL_SCHEMA = 1;
	public static final int APPLIANCE_SCHEMA = 2;
	public static final int CONSUMPTIONMODEL_SCHEMA = 3;
	public static final int DEMOGRAPHICS_SCHEMA = 4;
	public static final int DISTRIBUTION_SCHEMA = 5;
	public static final int INSTALLATION_SCHEMA = 6;
	public static final int PERSON_SCHEMA = 7;
	public static final int PROJECT_SCHEMA = 8;
	public static final int SCENARIO_SCHEMA = 9;
	public static final int SIMPARAM_SCHEMA = 10;
	public static final int PRICING_SCHEMA = 11;
	public static final int GRAPH_SCHEMA = 12;
	public static final int LIGHTING_SCHEMA = 13;
	public static final int THERMAL_SCHEMA = 14;

	public static final int CLUSTER_SCHEMA = 100;
	public static final int CLUSTER_PARAM_SCHEMA = 101;

	/**
	 * 
	 * @param schemaType
	 * @return
	 */
	private String getSchemaFileName(int schemaType) {
		String fileName = new String();
		switch (schemaType) {
		case ACTIVITY_SCHEMA:  fileName += "Activity.schema";
		break;
		case ACTIVITYMODEL_SCHEMA:  fileName += "ActivityModel.schema";
		break;
		case APPLIANCE_SCHEMA:  fileName += "Appliance.schema";
		break;
		case CONSUMPTIONMODEL_SCHEMA:  fileName += "ConsumptionModel.schema";
		break;
		case DEMOGRAPHICS_SCHEMA:  fileName += "Demographics.schema";
		break;
		case DISTRIBUTION_SCHEMA:  fileName += "Distribution.schema";
		break;
		case INSTALLATION_SCHEMA:  fileName += "Installation.schema";
		break;
		case PERSON_SCHEMA:  fileName += "Person.schema";
		break;
		case PROJECT_SCHEMA:  fileName += "Project.schema";
		break;
		case SCENARIO_SCHEMA:  fileName += "Scenario.schema";
		break;
		case SIMPARAM_SCHEMA:  fileName += "SimParam.schema";
		break;
		case PRICING_SCHEMA:  fileName += "Pricing.schema";
		break;
		case GRAPH_SCHEMA:  fileName += "Graph.schema";
		break;
		case CLUSTER_SCHEMA: fileName += "Cluster.schema";
		break;
		case CLUSTER_PARAM_SCHEMA: fileName += "ClusterParameters.schema";
		break;
		case LIGHTING_SCHEMA: fileName += "Lighting.schema";
		break;
		case THERMAL_SCHEMA: fileName += "Thermal.schema";
		break;
		}
		return fileName;
	}

	/**
	 * 
	 * @param jsonText
	 * @param schemaType
	 * @return
	 * @throws IOException
	 * @throws JSONSchemaNotValidException
	 */
	public boolean isValid(String jsonText,int schemaType) 
			throws IOException, JSONSchemaNotValidException {
		return isValid(jsonText,schemaType,false); 
	}

	private DBObject removeKeyFromInternalFields(DBObject obj, String key) {
		String[] keys = obj.keySet().toArray(new String[0]);
		for(String k : keys) {
			if(k.equalsIgnoreCase(key) &&  obj.get(k) instanceof Boolean &&  !(Boolean)obj.get(k)) {
				obj.put(k,true);
			}
			else if (obj.get(k) instanceof BasicDBObject) {
				DBObject intObj = (DBObject) obj.get(k); 
				removeKeyFromInternalFields(intObj, key);
			}
		}
		return obj;
	}

	/**
	 * 
	 * @param jsonText
	 * @param schemaType
	 * @param isUpdate
	 * @return
	 * @throws IOException
	 * @throws JSONSchemaNotValidException
	 */
	public boolean isValid(String jsonText,int schemaType, boolean isUpdate) 
			throws IOException, JSONSchemaNotValidException {
		ObjectMapper mapper = new ObjectMapper();
		JSONSchemaProvider schemaProvider = new JacksonSchemaProvider(mapper);

		String jsonSchema = readFile(getSchemaFileName(schemaType));
		if(isUpdate) {

			//			DBObject jsonSchemaObj = (DBObject)JSON.parse(jsonSchema);
			//			DBObject oidObject = new BasicDBObject("type","string").append("optional", "false").append("properties", new BasicDBObject("$oid",new BasicDBObject("type","string").append("optional", "false")));
			//			DBObject schemaProperties = (DBObject)jsonSchemaObj.get("properties");
			//			schemaProperties.put("_id", oidObject);
			//			jsonSchemaObj.put("properties", schemaProperties);
			//			jsonSchema = jsonSchemaObj.toString();
			DBObject jsonSchemaObj = (DBObject)JSON.parse(jsonSchema);
			jsonSchema =  removeKeyFromInternalFields(jsonSchemaObj, "optional").toString();
		}
		JSONSchema schema = schemaProvider.getSchema(jsonSchema);
		List<String> errors = schema.validate(jsonText);
		StringBuilder errorMessage = new StringBuilder();
		for (int i=0;i<errors.size();i++) {
			String s = errors.get(i);
			if(i == errors.size()-1)
				errorMessage.append(s);
			else
				errorMessage.append(s + "\n");
		}
		if(errors.size() != 0)
			throw new JSONSchemaNotValidException(errorMessage.toString());
		else
			return true;
	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private String readFile(String file) throws IOException {
		String schema = IServletContextListener.schemas.getAbsolutePath() + "/" + file;
		InputStream inStream = new FileInputStream(schema);
		InputStreamReader inputStream = new InputStreamReader(inStream);
		BufferedReader reader = new BufferedReader(inputStream);
		String line = null;
		StringBuilder  stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");
		while( ( line = reader.readLine() ) != null ) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}
		reader.close();
		inputStream.close();
		inStream.close();
		return stringBuilder.toString();
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			System.out.println(new JSONValidator().isValid("{\"name\":\"My project\"," +
					"\"description\":\"A project\"}",PROJECT_SCHEMA,true));
		} catch (IOException | JSONSchemaNotValidException e) {
			e.printStackTrace();
		}
	}
}

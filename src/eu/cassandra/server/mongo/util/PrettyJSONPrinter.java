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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.DBObject;

public class PrettyJSONPrinter {


	private static boolean PRETTY_PRINT = true; 
	
	/**
	 * 
	 * @param json
	 * @return
	 */
	public static String prettyPrint(DBObject json) {
		return prettyPrint(json.toString());
	}
	
	/**
	 * 
	 * @param uglyJSONString
	 * @return
	 */
	public static String prettyPrint(String jsonString) {
		if(PRETTY_PRINT) {
			Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(jsonString);
			jsonString = gson.toJson(je);

		}
		return jsonString;
	}
}

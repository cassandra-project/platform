package eu.cassandra.server.mongo.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class PrettyJSONPrinter {


	private static boolean PRETTY_PRINT = true; 
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

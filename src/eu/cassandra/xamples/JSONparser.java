package eu.cassandra.xamples;

import java.util.Iterator;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

public class JSONparser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String jsonstring = new String();
		jsonstring += "{";
		jsonstring += "\"stringType\":\"A\",";
		jsonstring += "\"intType\":1,";
		jsonstring += "\"numericType\":1.0,";
		jsonstring += "\"ary\":[1.0, 2.0, 3.0]";
		jsonstring += "}";
		System.out.println(PrettyJSONPrinter.prettyPrint(jsonstring));
		DBObject dbo = (DBObject)JSON.parse(jsonstring);
		String a = (String)dbo.get("stringType");
		System.out.println(a);
		int b = ((Integer)dbo.get("intType")).intValue();
		System.out.println(b);
		double c = ((Double)dbo.get("numericType")).doubleValue();
		System.out.println(c);
		BasicDBList dbList = (BasicDBList)dbo.get("ary");
		Iterator<Object> iter = dbList.iterator();
		while(iter.hasNext()) {
			double d = ((Double)iter.next()).doubleValue();
			System.out.println(d);
		}

	}

}

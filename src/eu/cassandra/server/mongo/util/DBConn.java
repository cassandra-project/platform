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

import java.net.UnknownHostException;

import java.util.HashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class DBConn {

	private static String DB_NAME = "test";

	private static String DB_HOST = "localhost";

	private static DBConn dbConn = new DBConn();

	private static HashMap<String,DB> dbRuns = new HashMap<String,DB>();

	private Mongo m;

	private DB db;

	private DBConn() {
		try {
			InitialContext ic = new InitialContext();
			DB_NAME = (String) ic.lookup("java:/comp/env/mongo.db");
			DB_HOST = (String) ic.lookup("java:/comp/env/mongo.host.address");
			m = new Mongo(DB_HOST);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		db = m.getDB(DB_NAME);
	}

	public static DB getConn() {
		return dbConn.db;
	}

	public static DB getConn(String dbname) {
		if(dbname == null)
			return getConn();
		else if(dbRuns.containsKey(dbname)) {
			return dbRuns.get(dbname);
		} else {
			try {
				Mongo m = new Mongo(DB_HOST);
				dbRuns.put(dbname, m.getDB(dbname));
				return dbRuns.get(dbname);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (MongoException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}

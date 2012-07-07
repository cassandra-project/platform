package cassandra;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class DBConn {
	
	private static Logger logger = Logger.getLogger(DBConn.class);
	
	private final static String DB_NAME = "test";
	
	private final static String DB_HOST = "localhost";
	
	private static DBConn dbConn = new DBConn();
	
	private Mongo m;
	
	private DB db;
	
	private DBConn() {
		try {
			m = new Mongo(DB_HOST);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db = m.getDB(DB_NAME);
		logger.debug("DB connection instantiated.");
	}
	
	public static DB getConn() {
		return dbConn.db;
	}

}

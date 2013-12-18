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

package eu.cassandra.sim.utilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadPoolExecutor;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

import eu.cassandra.sim.math.ProbabilityDistribution;
import eu.cassandra.sim.math.Gaussian;
import eu.cassandra.sim.math.GaussianMixtureModels;
import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.bson.types.ObjectId;

public class Utils {
	
	public static boolean getBoolean(Object o) {
		if(o == null) {
			return false;
		} else {
			return ((Boolean)o).booleanValue();
		}
	}
	
	public static boolean getEquality(Object o, String s, boolean onnull) {
		if(o == null) {
			return onnull;
		} else {
			if(((String)o).equalsIgnoreCase(s)) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public static double getDouble(Object o) {
		if(o == null) {
			return 0.0;
		} else {
			return Double.parseDouble(o.toString());
		}
	}
	
	public static float getFloat(Object o) {
		if(o == null) {
			return 0.0f;
		} else {
			return Float.parseFloat(o.toString());
		}
	}
	
	public static int getInt(Object o) {
		if(o == null) {
			return 0;
		} else {
			return (int)Math.round(getFloat(o));
		}
	}
	
	public static String stackTraceToString(StackTraceElement[] s) {
		String returnMessage = new String();
		for(int i = 0; i < s.length; i++) {
			returnMessage += s[i].toString() + "\n";
		}
		return returnMessage;
	}
	
	public static void printExecutorSummary(ThreadPoolExecutor executor) {
		System.out.println(
				String.format("[monitor] [%d/%d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s",
						executor.getPoolSize(),
                        executor.getCorePoolSize(),
                        executor.getActiveCount(),
                        executor.getCompletedTaskCount(),
                        executor.getTaskCount(),
                        executor.isShutdown(),
                        executor.isTerminated()));
	}
	
	public static String inject(String message, String field, String value) {
		DBObject data = (DBObject)JSON.parse(message);
		data.put(field, value);
		return data.toString();
	}
	
	public static void upscale(double[] values, int exp) {
		for(int i = 0; i < values.length; i++) {
			values[i] = values[i] * Math.pow(10, exp * -1);
		}
	}
	
	public static int checkExp(double[] values) {
		boolean flag_1 = false;
		boolean flag_2 = false;
		boolean flag_3 = false;
		boolean flag_4 = false;
		boolean flag_5 = false;
		boolean flag_6 = false;
		boolean flag_7 = false;
		for(int i = 0; i < values.length; i++) {
			if(values[i] > 0.1) flag_1 = true;
			if(values[i] > 0.01) flag_2 = true;
			if(values[i] > 0.001) flag_3 = true;
			if(values[i] > 0.0001) flag_4 = true;
			if(values[i] > 0.00001) flag_5 = true;
			if(values[i] > 0.000001) flag_6 = true;
			if(values[i] > 0.0000001) flag_7 = true;
		}
		if(flag_1) {
			return 0;
		} 
		if(flag_2) {
			return -1;
		} 
		if(flag_3) {
			return -2;
		}
		if(flag_4) {
			return -3;
		}
		if(flag_5) {
			return -4;
		}
		if(flag_6) {
			return -5;
		}
		if(flag_7) {
			return -6;
		}
		return 0;
	}

  public static double[] dblist2doubleArr (BasicDBList list)
  {
    double[] arr = new double[list.size()];
    for (int i = 0; i < list.size(); i++) {
      arr[i] = Double.parseDouble(list.get(i).toString());
    }
    return arr;
  }

  public static float[] dblist2floatArr (BasicDBList list)
  {
    float[] arr = new float[list.size()];
    for (int i = 0; i < list.size(); i++) {
      arr[i] = ((Float) list.get(i)).floatValue();
    }
    return arr;
  }

  public static int[] dblist2intArr (BasicDBList list)
  {
    int[] arr = new int[list.size()];
    for (int i = 0; i < list.size(); i++) {
      arr[i] = ((Integer) list.get(i)).intValue();
    }
    return arr;
  }

  public static String hashcode (String message)
  {
    String hash = null;
    try {
      MessageDigest cript = MessageDigest.getInstance("SHA-1");
      cript.reset();
      cript.update(message.getBytes("utf8"));
      hash = new BigInteger(1, cript.digest()).toString(16);
    }
    catch (NoSuchAlgorithmException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return hash;
  }
  
  public static DBObject getUser(String username, DB db) {
	  DBObject query = new BasicDBObject();
	  query.put("username", username);
	  return db.getCollection("users").findOne(query);
  }
  
  public static DBObject getUserWithId(String id) {
	  DB db = DBConn.getConn();
	  ObjectId objid = new ObjectId(id);
	  DBObject query = new BasicDBObject();
	  query.put("_id", objid);
	  return db.getCollection("users").findOne(query);
  }
  
  public static String extractCredentials(HttpHeaders httpHeaders) {
	  String authorizationPart = httpHeaders.getRequestHeader("Authorization").get(0);
	  if(authorizationPart.equalsIgnoreCase("undefined")) {
		  return null;
	  } else {
		  return httpHeaders.getRequestHeader("Authorization").get(0).split(" ")[1];
	  }
  }
  
  public static String extractUsername(String headerMessage) {
	  byte[] bytes = Base64.decodeBase64(headerMessage);
	  if(bytes == null) return null;
	  String decodedHeader = new String(bytes);
	  String[] tokens = decodedHeader.trim().split(":"); // Remove new line char
	  return tokens[0];
  }
  
  public static String extractPassword(String headerMessage) {
	  byte[] bytes = Base64.decodeBase64(headerMessage);
	  if(bytes == null) return null;
	  String decodedHeader = new String(bytes);
	  String[] tokens = decodedHeader.trim().split(":"); // Remove new line char
	  return tokens[1];
  }
  
  public static boolean authenticate(String headerMessage, DB db) {
	  String username = extractUsername(headerMessage);
	  String password = extractPassword(headerMessage);
	  if(username == null || password == null) return false;
	  DBObject user = getUser(username, db);
	  if(user == null) return false;
	  String user_id = user.get("_id").toString();
	  String passwordHash = user.get("password").toString();
	  MessageDigest m = DigestUtils.getMd5Digest();
	  m.update((password + user_id).getBytes(), 0, (password + user_id).length());
	  String output = new BigInteger(1, m.digest()).toString(16);
	  return passwordHash.equals(output);
  }
  
  public static String userChecked(HttpHeaders httpHeaders) {
	  if(httpHeaders == null || httpHeaders.getRequestHeaders() == null ||
			  httpHeaders.getRequestHeader("Authorization") == null) {
		  return null;
	  }
	  DB db = DBConn.getConn();
	  if(Utils.authenticate(Utils.extractCredentials(httpHeaders), db)) {
		  String username = Utils.extractUsername(Utils.extractCredentials(httpHeaders));
		  String usr_id = Utils.getUser(username, db).get("_id").toString();
		  return usr_id;
	  } else {
		  return null;
	  }
	}
	
//  public static double parseDouble(String s) {
//	  try {
//		  return Double.parseDouble(s);
//	  } catch(Exception e) {
//		  e.printStackTrace();
//	  }
//  }

  /*
    public static void createHistogram (String title, String x, String y,
                                        Double[] data)
    {

      XYSeries series = new XYSeries("Data");

      for (int i = 0; i < data.length; i++) {
        series.add(i, data[i]);
      }

      final XYDataset dataset = new XYSeriesCollection(series);
      PlotOrientation orientation = PlotOrientation.VERTICAL;
      boolean show = false;
      boolean toolTips = false;
      boolean urls = false;

      JFreeChart chart =
        ChartFactory.createXYLineChart(title, x, y, dataset, orientation, show,
                                       toolTips, urls);
      int width = 1024;
      int height = 768;

      try {
        ChartUtilities.saveChartAsPNG(new File(title + ".PNG"), chart, width,
                                      height);
      }
      catch (IOException e) {
      }

    }
  */
  public static String readFile (String file) throws IOException
  {
    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line = null;
    StringBuilder stringBuilder = new StringBuilder();
    String ls = System.getProperty("line.separator");

    while ((line = reader.readLine()) != null) {
      stringBuilder.append(line);
      stringBuilder.append(ls);
    }

    return stringBuilder.toString();
  }
  
  public static Response returnResponse(String json) {
	  DBObject jsonResponse = (DBObject) JSON.parse(json);
	  if(Boolean.parseBoolean(jsonResponse.get("success").toString())) {
		  return Response.ok(json, MediaType.APPLICATION_JSON).build();
	  } else {
		  return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
	  }
  }
  
  public static Response returnBadRequest(String msg) {
	  JSONtoReturn jsonMsg = new JSONtoReturn();
	  String json = PrettyJSONPrinter.prettyPrint(jsonMsg.createJSONError(msg, new Exception(msg)));
	  Response r = Response.status(Response.Status.BAD_REQUEST).entity(json).build();
	  return r; 
  }
  
  public static Response returnResponseWithAppend(String json, String key, Integer value) {
	  DBObject jsonResponse = (DBObject) JSON.parse(json);
	  if(Boolean.parseBoolean(jsonResponse.get("success").toString())) {
		  jsonResponse.put(key, value);
		  return Response.ok(PrettyJSONPrinter.prettyPrint(jsonResponse.toString()), MediaType.APPLICATION_JSON).build();
	  } else {
		  return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
	  }
  }
  
  public static boolean failed(String json) {
	  DBObject jsonResponse = (DBObject) JSON.parse(json);
	  if(Boolean.parseBoolean(jsonResponse.get("success").toString())) {
		  return false;
	  } else {
		  return true;
	  }
  }

  // Almost copy of the corresponding functions of the Mallet toolkit,
  // http://mallet.cs.umass.edu
  /**
   * Function copied by the Mallet toolbox: http://mallet.cs.umass.edu
   *
   * Returns the KL divergence, K(p1 || p2).
   *
   * The log is w.r.t. base 2. <p>
   *
   * *Note*: If any value in <tt>p2</tt> is <tt>0.0</tt> then the KL-divergence
   * is <tt>infinite</tt>. 
   * 
   */
  public static double klDivergence(double[] p1, double[] p2) {
    assert(p1.length == p2.length);
    double klDiv = 0.0;
    for (int i = 0; i < p1.length; ++i) {
      if (p1[i] == 0) {
        continue;
      }
      if (p2[i] == 0) {
        return Double.POSITIVE_INFINITY;
      }
      klDiv += p1[i] * Math.log(p1[i] / p2[i]);
    }
    return klDiv / Math.log(2);
  }

  /**
   * Function copied by the Mallet toolbox: http://mallet.cs.umass.edu
   *
   * Returns the Jensen-Shannon divergence.
   */
  public static double jensenShannonDivergence(double[] p1, double[] p2) {
    assert(p1.length == p2.length);
    double[] average = new double[p1.length];
    for (int i = 0; i < p1.length; ++i) {
      average[i] += (p1[i] + p2[i])/2;
    }
    return (klDivergence(p1, average) + klDivergence(p2, average))/2;
  }

  public static double histogramDistance(double[] histogram1, double[] histogram2)
  {
    return jensenShannonDivergence(histogram1, histogram2);
  }

  /**
   * @param args
 * @throws MongoException 
 * @throws UnknownHostException 
 * @throws NoSuchAlgorithmException 
 * @throws UnsupportedEncodingException 
   */
  public static void main (String[] args) throws UnknownHostException, MongoException, NoSuchAlgorithmException
  {
//	  System.out.println((0.9 * Math.pow(10.0, -1 * -1)) + " ");
//	  double[] values = {0.55385,0.2,0.09231,0.06154,0.04615,0.01538,0,0,0.01538,0,0.01538};
	  Gaussian probDist1 = new Gaussian(30, 10);
	  probDist1.precompute(0, 1439, 1440);
	  double[] values = probDist1.getHistogram();
	  int exp = Utils.checkExp(values);
	  System.out.println("Exp: " + exp);
	  Utils.upscale(values, exp);
	  for(int i = 0; i < values.length; i++) {
		  System.out.println(values[i]);
	  }
	  
    //System.out.println(hashcode((new Long(System.currentTimeMillis()).toString())));
	  //Mongo m = new Mongo("localhost");
	  //DB db = m.getDB("test");
	  //System.out.println(authenticate("a3lyY2hhOmxhbGExMjM=", db));
	  //System.out.println(MD5HashGenerator.generateMd5Hash("demo", "511cf876bf13fde604000000"));
	  //System.out.println(MD5HashGenerator.generateMd5Hash("demo", "512df4d4bd32fc4c0c000000"));

          // Test for probability distribution
/*          Gaussian probDist1 = new Gaussian(800, 200);
          double[] p2 = {0.25, 0.6, 0.15};
          double[] m2 = {400, 800, 1200};
          double[] s2 = {100, 200, 10};
          GaussianMixtureModels probDist2 = new GaussianMixtureModels(3, p2, m2, s2);

          probDist1.precompute(0, 1440, 1440);
          probDist2.precompute(0, 1440, 1440);          
          double dist = histogramDistance(probDist1.getHistogram(),
                                          probDist2.getHistogram());
          System.out.println("Distribution distance: " + Double.toString(dist));
 */
  }
}

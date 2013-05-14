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

import eu.cassandra.server.mongo.util.DBConn;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class Utils
{
	
	public static String inject(String message, String field, String value) {
		DBObject data = (DBObject)JSON.parse(message);
		data.put(field, value);
		return data.toString();
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
  
  public static boolean failed(String json) {
	  DBObject jsonResponse = (DBObject) JSON.parse(json);
	  if(Boolean.parseBoolean(jsonResponse.get("success").toString())) {
		  return false;
	  } else {
		  return true;
	  }
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
    //System.out.println(hashcode((new Long(System.currentTimeMillis()).toString())));
	  Mongo m = new Mongo("localhost");
	  DB db = m.getDB("test");
	  System.out.println(authenticate("a3lyY2hhOmxhbGExMjM=", db));
	  System.out.println(MD5HashGenerator.generateMd5Hash("demo", "511cf876bf13fde604000000"));
	  System.out.println(MD5HashGenerator.generateMd5Hash("demo", "512df4d4bd32fc4c0c000000"));
  }

}

/*   
   Copyright 2011-2012 The Cassandra Consortium (cassandra-fp7.eu)


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
package eu.cassandra.server.mongo.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.server.mongo.util.PrettyJSONPrinter;

public class TestMongo {

	private static final String PROJECTS = "projects";
	private static final String SCENARIOS = "scenarios";
	private static final String INSTALLATIONS = "installations";
	private static final String ACTIVITIES = "activities";
	private static final String ACT_MODELS = "act_models";
	private static final String APPLIANCES = "appliances";
	private static final String PERSONS = "persons";
	private static final String DISTRIBUTIONS = "distributions";

	public static void main(String args[]) {
		new TestMongo();
	}

	public TestMongo() {
		testCreate();
		getData("http://localhost:8080/cassandra/api/prj/","4ff410c8e4b0c338f131de9e",PROJECTS);
		getData("http://localhost:8080/cassandra/api/distr/","4ff46ab0e4b0560065300d36",DISTRIBUTIONS);
	}

	/**
	 * 
	 */
	public void testCreate() {
		System.out.println("Creating Projects");
		httpConnection("http://localhost:8080/cassandra/api/prj","POST","tests/project2.json",(String[])null,(String[])null);
		String res = httpConnection("http://localhost:8080/cassandra/api/prj","POST","tests/project.json",(String[])null,(String[])null);
		DBObject obj = (DBObject)JSON.parse(res); 
		String id = ((DBObject)obj.get("objectCreated")).get("_id").toString();

		System.out.println("\n\nCreating Scenarios");
		httpConnection("http://localhost:8080/cassandra/api/scn","POST","tests/scenario.json","project_id",id);
		res = httpConnection("http://localhost:8080/cassandra/api/scn","POST","tests/scenario2.json","project_id",id);
		obj = (DBObject)JSON.parse(res);
		id = ((DBObject)obj.get("objectCreated")).get("_id").toString();

		System.out.println("\n\nCreating Parameters");
		httpConnection("http://localhost:8080/cassandra/api/smp","POST","tests/simparam.json","scn_id",id);
		httpConnection("http://localhost:8080/cassandra/api/smp","POST","tests/simparam2.json","scn_id",id);

		System.out.println("\n\nCreating Installations");
		String t = httpConnection("http://localhost:8080/cassandra/api/inst","POST","tests/installation.json",new String[] {"scenario_id","belongsToInstallation"},new String[] {id,id});
		DBObject objT = (DBObject)JSON.parse(t);
		String idT = ((DBObject)objT.get("objectCreated")).get("_id").toString();
		res = httpConnection("http://localhost:8080/cassandra/api/inst","POST","tests/installation2.json",new String[] {"scenario_id","belongsToInstallation"},new String[] {id,idT});
		obj = (DBObject)JSON.parse(res);
		id = ((DBObject)obj.get("objectCreated")).get("_id").toString();

		System.out.println("\n\nCreating Appliances");
		httpConnection("http://localhost:8080/cassandra/api/app","POST","tests/appliance.json",new String[] {"inst_id",},new String[] {id});
		httpConnection("http://localhost:8080/cassandra/api/app","POST","tests/appliance2.json",new String[] {"inst_id",},new String[] {id});
		
		System.out.println("\n\nCreating Persons");
		System.out.println(id);
		String pers = httpConnection("http://localhost:8080/cassandra/api/pers","POST","tests/person.json","inst_id",id);
		DBObject persObj = (DBObject)JSON.parse(pers);
		System.out.println(persObj);
		String  persID = ((DBObject)persObj.get("objectCreated")).get("_id").toString();

		System.out.println("\n\nCreating Activities");
		httpConnection("http://localhost:8080/cassandra/api/act","POST","tests/activity.json","pers_id",persID);
		res = httpConnection("http://localhost:8080/cassandra/api/act","POST","tests/activity2.json","pers_id",persID);
		obj = (DBObject)JSON.parse(res);
		System.out.println(obj);
		id = ((DBObject)obj.get("objectCreated")).get("_id").toString();

		System.out.println("\n\nCreating Activity Models");
		httpConnection("http://localhost:8080/cassandra/api/actmod","POST","tests/activitymodel.json","act_id",id);
		res = httpConnection("http://localhost:8080/cassandra/api/actmod","POST","tests/activitymodel2.json","act_id",id);
		obj = (DBObject)JSON.parse(res);
		System.out.println("\n\nTest: " + obj);
		id = ((DBObject)obj.get("objectCreated")).get("_id").toString();

		res = httpConnection("http://localhost:8080/cassandra/api/distr","POST","tests/distribution2.json",(String[])null,(String[])null);
		obj = (DBObject)JSON.parse(res);
		System.out.println(obj);
		id = ((DBObject)obj.get("objectCreated")).get("_id").toString();
		res =httpConnection("http://localhost:8080/cassandra/api/distr","POST","tests/distribution.json",new String[] {"duration","startTime","repeatsNrOfTimes"},new String[] {id,id,id});
		System.out.println(res);

	}

	/**
	 * 
	 * @param url
	 * @param id
	 * @param type
	 * @return
	 */
	private BasicDBList getData(String url, String id, String type) {
		String res = httpConnection(url +id,"GET");
		DBObject obj = (DBObject)JSON.parse(res);
		BasicDBList data = (BasicDBList)obj.get("data");
		for(int i=0;i<data.size();i++) {
			System.out.println(PrettyJSONPrinter.prettyPrint(data.get(i).toString()) + "\n");
			String intID = ((DBObject)data.get(i)).get("_id").toString();

			if(type.equalsIgnoreCase(PROJECTS))
				getData("http://localhost:8080/cassandra/api/scn?prj_id=", intID,SCENARIOS);

			if(type.equalsIgnoreCase(SCENARIOS))
				getData("http://localhost:8080/cassandra/api/inst?scn_id=", intID,INSTALLATIONS);

			if(type.equalsIgnoreCase(SCENARIOS))
				getData("http://localhost:8080/cassandra/api/smp?scn_id=", intID,"");

			if(type.equalsIgnoreCase(PERSONS))
				getData("http://localhost:8080/cassandra/api/act?pers_id=", intID,ACTIVITIES);

			if(type.equalsIgnoreCase(INSTALLATIONS))
				getData("http://localhost:8080/cassandra/api/app?inst_id=", intID,APPLIANCES);
			
			if(type.equalsIgnoreCase(INSTALLATIONS))
				getData("http://localhost:8080/cassandra/api/pers?inst_id=", intID,PERSONS);

			if(type.equalsIgnoreCase(ACTIVITIES))
				getData("http://localhost:8080/cassandra/api/actmod?act_id=", intID,ACT_MODELS);
		}
		return data;
	}


	/**
	 * 
	 * @param url
	 * @param method
	 * @return
	 */
	private String httpConnection(String url,String method) {
		return httpConnection(url,method,null,(String[])null,(String[])null);
	}

	/**
	 * 
	 * @param url
	 * @param method
	 * @param fileToSend
	 * @param keyToReplace
	 * @param valueToReplace
	 * @return
	 */
	private String httpConnection(String url,String method, String fileToSend, 
			String keyToReplace, String valueToReplace) {
		return httpConnection(url,method, fileToSend, 
				new String[] {keyToReplace}, new String[] {valueToReplace});
	}

	/**
	 * 
	 * @param url
	 * @param method
	 * @param fileToSend
	 * @param keyToReplace
	 * @param valueToReplace
	 * @return
	 */
	private String httpConnection(String url,String method, String fileToSend, 
			String[] keyToReplace, String valueToReplace[]) {
		System.out.println(method + " on: " + url);
		String response = "";
		HttpURLConnection httpCon = null;
		try {
			httpCon = (HttpURLConnection) new URL(url).openConnection();
			httpCon.setDoOutput(true);
			if(fileToSend != null) {
				httpCon.setDoInput(true);
				httpCon.setRequestMethod(method);
				String urlParameters = readFile(fileToSend,keyToReplace, valueToReplace); 
				httpCon.setRequestProperty("content-type", "application/json");
				httpCon.setRequestProperty("Content-Length", "" +  Integer.toString(urlParameters.getBytes().length));
				httpCon.connect();
				DataOutputStream wr = new DataOutputStream (httpCon.getOutputStream ());
				wr.writeBytes (urlParameters);
				wr.flush ();
				wr.close ();
			}
			else {
				httpCon.setRequestMethod(method);
			}
			response = convertStreamToString(httpCon.getInputStream());
			//System.out.println(response);
		}catch(Exception e) {
			e.printStackTrace();
		}
		finally{
			if(httpCon != null)
				httpCon.disconnect();
		}
		return response;
	}

	/**
	 * 
	 * @param fileName
	 * @return
	 */
	private String readFile(String fileName, String keyToReplace[], String valueToReplace[]) {
		StringBuilder sb = new StringBuilder();
		String strLine = null;
		try{
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			while ((strLine = br.readLine()) != null)   {
				if(keyToReplace != null && valueToReplace != null) {
					strLine = strLine.trim();
					for(int i=0;i<keyToReplace.length;i++) {
						if(strLine.startsWith(keyToReplace[i])) {
							String comma = "";
							if(strLine.endsWith(","))
								comma = ",";
							strLine = keyToReplace[i] + " : \"" + valueToReplace[i] + "\"" + comma;
							break;
						}
					}
				}
				sb.append(strLine);
			}
			in.close();
		}catch (Exception e){
			e.printStackTrace();
		}
		System.out.println("\n\n" + sb + "\n\n");
		return sb.toString();
	}

	/**
	 * 
	 * @param is
	 * @return
	 */
	private String convertStreamToString(java.io.InputStream is) {
		try {
			return new java.util.Scanner(is).useDelimiter("\\A").next();
		} catch (java.util.NoSuchElementException e) {
			return "";
		}
	}

}

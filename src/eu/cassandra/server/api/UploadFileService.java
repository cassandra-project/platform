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
package eu.cassandra.server.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import eu.cassandra.server.mongo.MongoResults;
import eu.cassandra.server.mongo.MongoRuns;
import eu.cassandra.server.mongo.util.DBConn;
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
 
@Path("file")
public class UploadFileService {
	
	@javax.ws.rs.core.Context 
	ServletContext context;
 
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {

		String filename = fileDetail.getFileName();
		String prj_id = new String(); //TODO
		String uploadedFileLocation = context.getRealPath("/resources") + 
				"/" + filename;
		
		
		System.out.println(uploadedFileLocation);
 
		try {
			// Save it
			writeToFile(uploadedInputStream, uploadedFileLocation);
			// TODO: Create a Run and return the id in the response
			ObjectId objid = ObjectId.get();
			DBObject run = new BasicDBObject();
			String dbname = objid.toString();
			Mongo m = new Mongo("localhost");
			DB db = m.getDB(dbname);
			MongoResults mr = new MongoResults(dbname);
			mr.createIndexes();
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
			String runName = "Run for " + filename + " on " + sdf.format(calendar.getTime());
			run.put("_id", objid);
			run.put("name", runName);
			run.put("started", System.currentTimeMillis());
			run.put("ended", System.currentTimeMillis());
			run.put("type", "file");
			run.put("prj_id", prj_id);
			run.put("percentage", 100);
			DBConn.getConn().getCollection(MongoRuns.COL_RUNS).insert(run);
			// TODO: Parse and calculate KPIs
			File f = new File(uploadedFileLocation);
			Scanner sc = new Scanner(f);
			String header = sc.next();
			String[] headerTokens = header.split(",");
			int numOfInstallations = headerTokens.length - 1;
			double maxPower = 0;
			double[] maxPowerInst = new double[numOfInstallations];
	  		double avgPower = 0;
	  		double[] avgPowerInst = new double[numOfInstallations];
	  		double energy = 0;
	  		double[] energyInst = new double[numOfInstallations];
	  		int tick = 0;
			while(sc.hasNext()) {
				tick++;
				String line = sc.next();
				String[] tokens = line.split(",");
				double powerSum = 0;
				for(int i = 1; i < tokens.length; i++) {
					double power = Double.parseDouble(tokens[i]);
					energyInst[i-1] += power;
					avgPowerInst[i-1] += power;
					if(maxPowerInst[i-1] < power) {
						maxPowerInst[i-1] = power; 
					}
					powerSum += power;
					mr.addTickResultForInstallation(tick, 
	  						headerTokens[i], 
	  						power, 
	  						0, 
	  						MongoResults.COL_INSTRESULTS);
				}
				mr.addAggregatedTickResult(tick, 
						powerSum, 
	  					0, 
	  					MongoResults.COL_AGGRRESULTS);
				energy += powerSum;
				avgPower += powerSum;
				if(maxPower < powerSum) {
					maxPower = powerSum; 
				}
			}
			
			// TODO: Add ticks and KPIs in the db
			for(int i = 0; i < numOfInstallations; i++) {
				mr.addKPIs(headerTokens[i+1], 
							maxPowerInst[i], 
							avgPowerInst[i]/tick, 
							energyInst[i], 
							0,
							0);
			}
			mr.addKPIs(MongoResults.AGGR, 
	  					maxPower, 
	  					avgPower/tick, 
	  					energy, 
	  					0,
	  					0);
		
			String output = "File uploaded to : " + uploadedFileLocation;
			return Response.status(200).entity(output).build();
		} catch(Exception exp) {
			JSONtoReturn jsonMsg = new JSONtoReturn();
			String json = PrettyJSONPrinter.prettyPrint(jsonMsg.createJSONError("Error", exp));
			Response r = Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			return r; 
		}
 
	}
 
	private void writeToFile(InputStream uploadedInputStream,
		String uploadedFileLocation) {
 
		try {
			OutputStream out = new FileOutputStream(new File(
					uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];
 
			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
 
			e.printStackTrace();
		}
 
	}
 
}

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

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import eu.cassandra.server.api.exceptions.MongoInvalidObjectId;
import eu.cassandra.server.api.exceptions.RestQueryParamMissingException;
import eu.cassandra.server.mongo.MongoCopyEntities;
import eu.cassandra.server.mongo.util.JSONtoReturn;
import eu.cassandra.server.mongo.util.PrettyJSONPrinter;
import eu.cassandra.sim.utilities.Utils;

@Path("copy")
@Produces(MediaType.APPLICATION_JSON)
public class CopyEntities {

	/**
	 * curl -i --data  @activity.json    --header Content-type:application/json 'http://localhost:8080/cassandra/api/copy?fromScnID=5006a706e4b05ff53eb83ff3&toPrjID=4ff42c26e4b0ee32fd10d3d6'
	 * 
	 * @param actID
	 * @param actmodID
	 * @param appID
	 * @param consmodID
	 * @param distrID
	 * @param instID
	 * @param persID
	 * @param scnID
	 * @param smpID
	 * 
	 * @param toPrjID
	 * @param toActID
	 * @param toActmodID
	 * @param toAppID
	 * @param toInstID
	 * @param toPersID
	 * @param toScnID
	 * @return
	 */
	@POST
	public Response createConsumptionModel(
			@QueryParam("actID") String actID,
			@QueryParam("actmodID") String actmodID,
			@QueryParam("appID") String appID,
			@QueryParam("consmodID") String consmodID,
			@QueryParam("distrID") String distrID,
			@QueryParam("instID") String instID,
			@QueryParam("persID") String persID,
			@QueryParam("scnID") String scnID,
			@QueryParam("smpID") String smpID,

			@QueryParam("toPrjID") String toPrjID,
			@QueryParam("toActID") String toActID,
			@QueryParam("toActmodID") String toActmodID,
			@QueryParam("toAppID") String toAppID,
			@QueryParam("toInstID") String toInstID,
			@QueryParam("toPersID") String toPersID,
			@QueryParam("toScnID") String toScnID,
			
			@Context HttpHeaders httpHeaders)
	{
		//Check if the number of parameters is correct
		int counter1 = 0;
		if(actID != null) counter1++;
		if(actmodID != null) counter1++;
		if(appID != null) counter1++;
		if(consmodID != null) counter1++;
		if(distrID != null) counter1++;
		if(instID != null) counter1++;
		if(persID != null) counter1++;
		if(scnID != null) counter1++;
		if(smpID != null) counter1++;

		int counter2 = 0;
		if(toPrjID != null) counter2++;
		if(toAppID != null) counter2++;
		if(toActID != null) counter2++;
		if(toActmodID != null) counter2++;
		if(toInstID != null) counter2++;
		if(toPersID != null) counter2++;
		if(toScnID != null) counter2++;
		
		if(counter1 != 1 || counter2 != 1) {
			return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new JSONtoReturn().createJSONError("Invalid ObjectId Parameters",
					new RestQueryParamMissingException("You should provide two and only two valid ObjectId"))));
		}

		String answer = "";
		MongoCopyEntities copy = new MongoCopyEntities(httpHeaders);
		try {
		if(scnID != null && toPrjID != null) //Scenario to Project
			answer = copy.copyScenarioToProject(scnID, toPrjID);
		else if(instID != null && toScnID != null) { //Installation to Scenario
			answer = copy.copyInstallationToScenario(instID, toScnID, null, true);
		} else if(smpID != null && toScnID != null) //Simulation Parameter to Scenario
			answer = copy.copySimParamsToScenario(smpID, toScnID, null);
		else if(appID != null && toInstID != null) //Appliance to Installation
			answer = copy.copyApplianceToInstallation(appID, toInstID, null);
		else if(persID != null && toInstID != null) //Person to Installation
			answer = copy.copyPersonToInstallation(persID, toInstID, null, false, null);
		else if(actID != null && toPersID != null) //Activities to Person
			answer = copy.copyActivityToPerson(actID, toPersID, null, false, null);
		else if(consmodID != null && toAppID != null) //Consumption Model to Appliance
			return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new JSONtoReturn().createJSONError("Invalid copy command",
					new RestQueryParamMissingException("Please check documentation for valid copy commands"))));
			// obsolete
			//answer = copy.copyConsModelToAppliance(consmodID, toAppID,null);
		else if(actmodID != null && toActID != null) //Activity Model to Activity
			// return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new JSONtoReturn().createJSONError("Invalid copy command",
					//new RestQueryParamMissingException("Please check documentation for valid copy commands"))));
			// obsolete
			answer = copy.copyActivityModelToActivity(actmodID, toActID, null, false, null); 
		else if(distrID != null && toActmodID != null) //Distribution to Activity Model
			return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new JSONtoReturn().createJSONError("Invalid copy command",
					new RestQueryParamMissingException("Please check documentation for valid copy commands"))));
			// obsolete
			// answer = copy.copyDistributionToActivityModel(distrID, toActmodID, null); obsolete
		else {
			return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new JSONtoReturn().createJSONError("Invalid copy command",
					new RestQueryParamMissingException("Please check documentation for valid copy commands"))));
		}
		}catch (Exception e){
			e.printStackTrace();
			return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(new JSONtoReturn().createJSONError("Invalid copy command",
					new MongoInvalidObjectId("The ObjecID provided is probably invalid or not existing"))));
		}
		return Utils.returnResponse(PrettyJSONPrinter.prettyPrint(answer));
	}
}

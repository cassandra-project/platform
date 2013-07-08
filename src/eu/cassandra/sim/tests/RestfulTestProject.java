/*   
   Copyright 2011-2013 The Cassandra Consortium (cassandra-fp7.el)


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
package eu.cassandra.sim.tests;

import java.io.BufferedReader;
import java.io.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.lang.Runtime;



public class RestfulTestProject
{
	
	private static final String CMD = "C:\\workspace\\RestTEsting\\src\\TestAHouseScenario\\RestTestProject.bat";
	private static final String CMD2 = "C:\\workspace\\RestTEsting\\src\\TestAHouseScenario\\RestTestScenario.bat";
	private static final String CMD3 = "C:\\workspace\\RestTEsting\\src\\TestAHouseScenario\\RestTestInstallation.bat";
	private static final String CMD4 = "C:\\workspace\\RestTEsting\\src\\TestAHouseScenario\\RestTestPerson.bat";
	private static final String CMD5 = "C:\\workspace\\RestTEsting\\src\\TestAHouseScenario\\RestTestActivity.bat";
	private static final String CMD6 = "C:\\workspace\\RestTEsting\\src\\TestAHouseScenario\\RestTestAppliance.bat";
	private static final String CMD7 = "C:\\workspace\\RestTEsting\\src\\TestAHouseScenario\\RestTestConsumptionModel.bat";
	private static final String CMD8 = "C:\\workspace\\RestTEsting\\src\\TestAHouseScenario\\RestTestDistr.bat";
	private static final String CMD9 = "C:\\workspace\\RestTEsting\\src\\TestAHouseScenario\\RestTestActivityModel.bat";
	private static final String CMD10 = "C:\\workspace\\RestTEsting\\src\\TestAHouseScenario\\RestTestSimParams.bat";
	private static final String CMD11 = "C:\\workspace\\RestTEsting\\src\\TestAHouseScenario\\RestTestRun.bat";
	  public static void main(String args[]) {

	        try {
	            // Run "netsh" Windows command
	            Process process = Runtime.getRuntime().exec(CMD11);

	            // Get input streams
	            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
	            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
	            System.out.println("Before");
	            URL location = RestfulTestProject.class.getProtectionDomain().getCodeSource().getLocation();
	        	System.out.println("CWD : "+ location);

	            // Read command standard output
	            String s;
	            System.out.println("Standard output: ");
	            while ((s = stdInput.readLine()) != null) {
	                System.out.println(s);
	            }

	            // Read command errors
	            System.out.println("Standard error: ");
	            while ((s = stdError.readLine()) != null) {
	                System.out.println(s);
	            }
	        } catch (Exception e) {
	            e.printStackTrace(System.err);
	        }
	    }
	
	
	
	  /*	  	public  static void main(String[] args) throws IOException{
		//String cmd= "./tests/TestAHouseScenario/script";
	System.out.println("Before");
	
	URL location = RestfulTestProject.class.getProtectionDomain().getCodeSource().getLocation();
	System.out.println("CWD : "+ location);
	
	Process proc = Runtime.getRuntime().exec("cmd.exe /c TestAHouseScenario\\script2.bat" );
	
	InputStream stderr = proc.getErrorStream();
	InputStreamReader isr = new InputStreamReader(stderr);
BufferedReader br = new BufferedReader(isr);
	
	
	String line = null;
		
	try {
	
		while ((line = br.readLine()) != null ) {
			System.out.println(line);
		}
		int rc = proc.waitFor();
		System.out.println("Process returned " + rc);
		
	} catch (InterruptedException e) {
			// ignore for now
		System.out.println("Error executing script: " + e);
	}
	System.out.println("After");
	}*/

}

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
package eu.cassandra.sim.tests;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


public class RestfulTest
{
	
	public  static void main(String[] args) throws IOException{
		//String cmd= "./tests/TestAHouseScenario/script";
	System.out.println("Before");
	
	URL location = RestfulTest.class.getProtectionDomain().getCodeSource().getLocation();
	System.out.println("CWD : "+ location);
	
	Process proc = Runtime.getRuntime().exec("tests\\TestAHouseScenario\\script2.bat" );
	
/*	InputStream stderr = proc.getErrorStream();
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
	}*/
	System.out.println("After");
	}

}

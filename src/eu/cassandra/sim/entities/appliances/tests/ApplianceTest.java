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
package eu.cassandra.sim.entities.appliances.tests;
/**
 * Class modeling an electric appliance. The appliance has a stand by
 * consumption otherwise there are a number of periods along with their
 * consumption rates.
 * 
 * @author Konstantina Valogianni
 * @version platform
 */
import static org.junit.Assert.*;

import org.junit.Test;

import eu.cassandra.sim.entities.appliances.Appliance;
import eu.cassandra.sim.entities.installations.Installation;
import eu.cassandra.sim.utilities.RNG;
import eu.cassandra.sim.utilities.Constants;



public class ApplianceTest {

	@Test
	public void testgetId() {
		double[] power = {1f,1f};
		int[] period = {1, 1};
		Constants cons=new Constants();
		RNG.init();
		Appliance tester=new Appliance.Builder("TestApp", "testDesc", "testType", null, power, period, 1f, true).build();
		assertEquals(0,tester.getId());
		
	}
	
	@Test
	public void testgetName() {
		double[] power = {1f,1f};
		int[] period = {1, 1};
		RNG.init();
		Appliance tester=new Appliance.Builder("TestApp", "testDesc", "testType", null, power, period, 1f, true).build();
		assertTrue("Result1", "TestApp"==tester.getName());
		
	}
	
	@Test
	public void testgetInstallation() {
		double[] power = {1f,1f};
		int[] period = {1, 1};
		RNG.init();
		Installation inst= new Installation.Builder(1, "TestInst", "TestInstDesc", "TestType").build();
		Appliance tester=new Appliance.Builder("TestApp", "testDesc", "testType", inst, power, period, 1f, true).build();
		assertTrue("Result2", inst==tester.getInstallation());
		
	}
	
	@Test
	public void testgetisInUse() {
		double[] power = {1f,1f};
		int[] period = {1, 1};
		RNG.init();
		Installation inst= new Installation.Builder(1, "TestInst", "TestInstDesc", "TestType").build();
		Appliance tester=new Appliance.Builder("TestApp", "testDesc", "testType", inst, power, period, 1f, true).build();
		assertTrue("Result3", true==tester.isInUse());
		
	}
	
	@Test
	public void testgetOnTick() {
		double[] power = {1f,1f};
		int[] period = {1, 1};
		RNG.init();
		Installation inst= new Installation.Builder(1, "TestInst", "TestInstDesc", "TestType").build();
		Appliance tester=new Appliance.Builder("TestApp", "testDesc", "testType", inst, power, period, 1f, true).build();
		tester.turnOn((long)1, "TestApp");
		assertTrue("Result4", 1==tester.getOnTick());
		
	}
	
	@Test
	public void testgetWho() {
		double[] power = {1f,1f};
		int[] period = {1, 1};
		RNG.init();
		Installation inst= new Installation.Builder(1, "TestInst", "TestInstDesc", "TestType").build();
		Appliance tester=new Appliance.Builder("TestApp", "testDesc", "testType", inst, power, period, 1f, true).build();
		assertTrue("Result5", null==tester.getWho());
		
	}
	
	@Test
	public void testgetPower() {
		double[] power = {2f,2f};
		int[] period = {1, 1};
		RNG.init();
		Installation inst= new Installation.Builder(1, "TestInst", "TestInstDesc", "TestType").build();
		Appliance tester=new Appliance.Builder("TestApp", "testDesc", "testType", inst, power, period, 1f, true).build();
		
		for(int i = 0; i < 100; i++){
			assertTrue("Result6",2f==tester.getPower(i));
		}
		
	}
}

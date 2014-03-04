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

import eu.cassandra.server.api.exceptions.BadParameterException;
import eu.cassandra.sim.entities.appliances.Appliance;
import eu.cassandra.sim.entities.appliances.ConsumptionModel;
import eu.cassandra.sim.entities.installations.Installation;
import eu.cassandra.sim.utilities.ORNG;
import eu.cassandra.sim.utilities.RNG;
import eu.cassandra.sim.utilities.Constants;



public class ApplianceTest {

	@Test
	public void testgetId() throws BadParameterException {
		
		String s = "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" : 200.0, \"d\" :8, \"s\": 0.0}, {\"p\" : 120.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 0.0, \"d\" : 80, \"s\": 0.0}]},{ \"n\" : 1, \"values\" : [ {\"p\" : 14.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 11.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 5.0, \"d\" : 73, \"s\": 0.0}]}]}";
		RNG.init();
		String id = "111";
		Appliance tester=new Appliance.Builder(id,"TestApp", "testDesc", "testType", null, new ConsumptionModel(s, "p"), null, 1f, true).build(new ORNG());
		assertEquals("111",tester.getId());
		
	}
	
	@Test
	public void testgetName() throws BadParameterException {
		String s = "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" : 200.0, \"d\" :8, \"s\": 0.0}, {\"p\" : 120.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 0.0, \"d\" : 80, \"s\": 0.0}]},{ \"n\" : 1, \"values\" : [ {\"p\" : 14.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 11.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 5.0, \"d\" : 73, \"s\": 0.0}]}]}";
		RNG.init();
		String id = "111";
		Appliance tester=new Appliance.Builder(id,"TestApp", "testDesc", "testType", null, new ConsumptionModel(s, "p"), null, 1f, true).build(new ORNG());
		assertTrue("Result1", "TestApp"==tester.getName());
		
	}
	
@Test
	public void testgetInstallation() throws BadParameterException {
		String s = "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" : 200.0, \"d\" :8, \"s\": 0.0}, {\"p\" : 120.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 0.0, \"d\" : 80, \"s\": 0.0}]},{ \"n\" : 1, \"values\" : [ {\"p\" : 14.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 11.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 5.0, \"d\" : 73, \"s\": 0.0}]}]}";
		RNG.init();
		String id = "111";
		String InstId="222";
		Installation inst= new Installation.Builder(InstId, "TestInst", "TestInstDesc", "TestType").build();
		Appliance tester=new Appliance.Builder(id,"TestApp", "testDesc", "testType", inst, new ConsumptionModel(s, "p"), null, 1f, true).build(new ORNG());
		assertTrue("Result2", inst==tester.getInstallation());
		
	}
	
@Test
	public void testgetisInUse() throws BadParameterException {
	String s = "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" : 200.0, \"d\" :8, \"s\": 0.0}, {\"p\" : 120.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 0.0, \"d\" : 80, \"s\": 0.0}]},{ \"n\" : 1, \"values\" : [ {\"p\" : 14.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 11.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 5.0, \"d\" : 73, \"s\": 0.0}]}]}";
	RNG.init();
	String id = "111";
	String InstId="222";
	Installation inst= new Installation.Builder(InstId, "TestInst", "TestInstDesc", "TestType").build();
	Appliance tester=new Appliance.Builder(id,"TestApp", "testDesc", "testType", inst, new ConsumptionModel(s, "p"), null, 1f, true).build(new ORNG());
	assertTrue("Result3", true==tester.isInUse());
		
	}
	
@Test
	public void testgetOnTick() throws BadParameterException {
	String s = "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" : 200.0, \"d\" :8, \"s\": 0.0}, {\"p\" : 120.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 0.0, \"d\" : 80, \"s\": 0.0}]},{ \"n\" : 1, \"values\" : [ {\"p\" : 14.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 11.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 5.0, \"d\" : 73, \"s\": 0.0}]}]}";
	RNG.init();
	String id = "111";
	String InstId="222";
	Installation inst= new Installation.Builder(InstId, "TestInst", "TestInstDesc", "TestType").build();
	Appliance tester=new Appliance.Builder(id,"TestApp", "testDesc", "testType", inst, new ConsumptionModel(s, "p"), null, 10, true).build(new ORNG());
	tester.turnOn((long)1, "TestApp", null);
	assertTrue("Result4", 1==tester.getOnTick());
		
	}
	
@Test
	public void testgetWho() throws BadParameterException {
	String s = "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" : 200.0, \"d\" :8, \"s\": 0.0}, {\"p\" : 120.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 0.0, \"d\" : 80, \"s\": 0.0}]},{ \"n\" : 1, \"values\" : [ {\"p\" : 14.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 11.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 5.0, \"d\" : 73, \"s\": 0.0}]}]}";
	RNG.init();
	String id = "111";
	String InstId="222";
	Installation inst= new Installation.Builder(InstId, "TestInst", "TestInstDesc", "TestType").build();
	Appliance tester=new Appliance.Builder(id,"TestApp", "testDesc", "testType", inst, new ConsumptionModel(s, "p"), null, 1f, true).build(new ORNG());
	assertTrue("Result5", null==tester.getWho());
		
	}
	
@Test
	public void testgetPower() throws BadParameterException {
	String s = "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" : 200.0, \"d\" :8, \"s\": 0.0}, {\"p\" : 120.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 0.0, \"d\" : 80, \"s\": 0.0}]},{ \"n\" : 1, \"values\" : [ {\"p\" : 14.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 11.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 5.0, \"d\" : 73, \"s\": 0.0}]}]}";
	RNG.init();
	String id = "111";
	String InstId="222";
	Installation inst= new Installation.Builder(InstId, "TestInst", "TestInstDesc", "TestType").build();
	Appliance tester=new Appliance.Builder(id,"TestApp", "testDesc", "testType", inst, new ConsumptionModel(s, "p"), null, 10, true).build(new ORNG());
		
	
			tester.turnOn((long)1, "TestApp", null);
			assertTrue("Result6",0==tester.getPower(100, "p"));
			
			tester.turnOn((long)10, "TestApp", null);
			assertTrue("Result7",0==tester.getPower(100, "p"));
			
			tester.turnOn((long)100, "TestApp", null);
			assertTrue("Result8",200==tester.getPower(100, "p"));
			
			tester.turnOn((long)60, "TestApp", null);
			assertTrue("Result9",120==tester.getPower(70, "p"));
			
			Appliance tester2=new Appliance.Builder(id,"TestApp", "testDesc", "testType", inst, new ConsumptionModel(s, "p"), null, 10, false).build(new ORNG());
			assertTrue("Result10",10==tester2.getPower(100, "p"));
	}
}

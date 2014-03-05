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
/**
 * 
 * @author Konstantina Valogianni
 * 
 */
package eu.cassandra.sim.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.cassandra.server.api.exceptions.BadParameterException;
import eu.cassandra.sim.Event;
import eu.cassandra.sim.entities.appliances.Appliance;
import eu.cassandra.sim.entities.appliances.ConsumptionModel;
import eu.cassandra.sim.entities.installations.Installation;
import eu.cassandra.sim.utilities.ORNG;
import eu.cassandra.sim.utilities.RNG;
import eu.cassandra.sim.utilities.Constants;

public class EventTest {

	@Test
	public void testgetAppliance() throws BadParameterException {
		String s = "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" : 200.0, \"d\" :8, \"s\": 0.0}, {\"p\" : 120.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 0.0, \"d\" : 80, \"s\": 0.0}]},{ \"n\" : 1, \"values\" : [ {\"p\" : 14.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 11.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 5.0, \"d\" : 73, \"s\": 0.0}]}]}";
		RNG.init();
		String id = "111";
		String InstId="222";
		Installation inst= new Installation.Builder(InstId, "TestInst", "TestInstDesc", "TestType").build();
		Appliance app=new Appliance.Builder(id,"TestApp", "testDesc", "testType", inst, new ConsumptionModel(s, "p"), null, 10, true).build(new ORNG());
		String hash="0000";
		Event tester=new Event(1,1,app,hash, null);
		assertTrue("Result1", app==tester.getAppliance());
	}
	
	@Test
	public void testgetAction() throws BadParameterException {
		String s = "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" : 200.0, \"d\" :8, \"s\": 0.0}, {\"p\" : 120.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 0.0, \"d\" : 80, \"s\": 0.0}]},{ \"n\" : 1, \"values\" : [ {\"p\" : 14.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 11.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 5.0, \"d\" : 73, \"s\": 0.0}]}]}";
		RNG.init();
		String id = "111";
		String InstId="222";
		Installation inst= new Installation.Builder(InstId, "TestInst", "TestInstDesc", "TestType").build();
		Appliance app=new Appliance.Builder(id,"TestApp", "testDesc", "testType", inst, new ConsumptionModel(s, "p"), null, 10, true).build(new ORNG());
		String hash="0000";
		Event tester=new Event(1,1,app,hash, null);
		assertTrue("Result2", 1==tester.getAction());
	}
	
	@Test
	public void testgetTick() throws BadParameterException {
		String s = "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" : 200.0, \"d\" :8, \"s\": 0.0}, {\"p\" : 120.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 0.0, \"d\" : 80, \"s\": 0.0}]},{ \"n\" : 1, \"values\" : [ {\"p\" : 14.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 11.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 5.0, \"d\" : 73, \"s\": 0.0}]}]}";
		RNG.init();
		String id = "111";
		String InstId="222";
		Installation inst= new Installation.Builder(InstId, "TestInst", "TestInstDesc", "TestType").build();
		Appliance app=new Appliance.Builder(id,"TestApp", "testDesc", "testType", inst, new ConsumptionModel(s, "p"), null, 10, true).build(new ORNG());
		String hash="0000";
		Event tester=new Event(3,1,app,hash, null);
		assertTrue("Result3", 3==tester.getTick());
	}
	@Test
	public void testApply() throws BadParameterException {
		String s = "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" : 200.0, \"d\" :8, \"s\": 0.0}, {\"p\" : 120.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 0.0, \"d\" : 80, \"s\": 0.0}]},{ \"n\" : 1, \"values\" : [ {\"p\" : 14.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 11.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 5.0, \"d\" : 73, \"s\": 0.0}]}]}";
		RNG.init();
		String id = "111";
		String InstId="222";
		Installation inst= new Installation.Builder(InstId, "TestInst", "TestInstDesc", "TestType").build();
		Appliance app=new Appliance.Builder(id,"TestApp", "testDesc", "testType", inst, new ConsumptionModel(s, "p"), null, 10, true).build(new ORNG());
		String hash="0000";
		Event tester=new Event(3,1,app,hash, null);
		assertTrue("Result4", false==tester.apply());
	}
	
	@Test
	public void testcompareTo() throws BadParameterException {
		String s = "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" : 200.0, \"d\" :8, \"s\": 0.0}, {\"p\" : 120.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 0.0, \"d\" : 80, \"s\": 0.0}]},{ \"n\" : 1, \"values\" : [ {\"p\" : 14.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 11.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 5.0, \"d\" : 73, \"s\": 0.0}]}]}";
		RNG.init();
		String id = "111";
		String InstId="222";
		Installation inst= new Installation.Builder(InstId, "TestInst", "TestInstDesc", "TestType").build();
		Appliance app=new Appliance.Builder(id,"TestApp", "testDesc", "testType", inst, new ConsumptionModel(s, "p"), null, 10, true).build(new ORNG());
		String hash="0000";
		Event tester=new Event(3,1,app,hash, null);
		Event tester2=new Event(1,1,app,hash, null);
		assertTrue("Result4", 1==tester.compareTo(tester2));
	}

}

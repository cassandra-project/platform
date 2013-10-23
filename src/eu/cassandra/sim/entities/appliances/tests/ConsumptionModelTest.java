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
 * 
 * @author Konstantina Valogianni
 * @version platform
 */
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import eu.cassandra.server.api.exceptions.BadParameterException;
import eu.cassandra.sim.entities.appliances.Appliance;
import eu.cassandra.sim.entities.appliances.ConsumptionModel;
import eu.cassandra.sim.utilities.RNG;

public class ConsumptionModelTest {

	
	@Test
	public void testgetTotalDuration() throws BadParameterException {
		
		String s = "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" : 200.0, \"d\" :9, \"s\": 0.0}, {\"p\" : 120.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 0.0, \"d\" : 80, \"s\": 0.0}]},{ \"n\" : 1, \"values\" : [ {\"p\" : 14.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 11.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 5.0, \"d\" : 73, \"s\": 0.0}]}]}";
		ConsumptionModel tester=new ConsumptionModel(s, "p");
		assertEquals(220,tester.getTotalDuration());
		
		
	}
	
	@Test
	public void testgetOuterN() throws BadParameterException {
		
		String s = "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" : 200.0, \"d\" :9, \"s\": 0.0}, {\"p\" : 120.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 0.0, \"d\" : 80, \"s\": 0.0}]},{ \"n\" : 1, \"values\" : [ {\"p\" : 14.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 11.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 5.0, \"d\" : 73, \"s\": 0.0}]}]}";
		ConsumptionModel tester=new ConsumptionModel(s, "p");
		assertEquals(0,tester.getOuterN());
		
		
	}
	
	@Test
	public void testgetPatternN() throws BadParameterException {
		
		String s = "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" : 200.0, \"d\" :9, \"s\": 0.0}, {\"p\" : 120.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 0.0, \"d\" : 80, \"s\": 0.0}]},{ \"n\" : 1, \"values\" : [ {\"p\" : 14.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 11.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 5.0, \"d\" : 73, \"s\": 0.0}]}]}";
		ConsumptionModel tester=new ConsumptionModel(s, "p");
		assertEquals(2,tester.getPatternN());
		
		
	}
	
	@Test
	public void testgetN() throws BadParameterException {
		
		String s = "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" : 200.0, \"d\" :9, \"s\": 0.0}, {\"p\" : 120.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 0.0, \"d\" : 80, \"s\": 0.0}]},{ \"n\" : 1, \"values\" : [ {\"p\" : 14.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 11.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 5.0, \"d\" : 73, \"s\": 0.0}]}]}";
		ConsumptionModel tester=new ConsumptionModel(s, "p");
		assertEquals(1,tester.getN(1));
		assertEquals(1,tester.getN(0));
		
		
	}
	
	@Test
	public void testgetPatternDuration() throws BadParameterException {
		
		String s = "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" : 200.0, \"d\" :9, \"s\": 0.0}, {\"p\" : 120.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 0.0, \"d\" : 80, \"s\": 0.0}]},{ \"n\" : 1, \"values\" : [ {\"p\" : 14.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 11.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 5.0, \"d\" : 73, \"s\": 0.0}]}]}";
		ConsumptionModel tester=new ConsumptionModel(s, "p");
		assertEquals(111,tester.getPatternDuration(1));
		assertEquals(109,tester.getPatternDuration(0));
		
		
	}
	

}

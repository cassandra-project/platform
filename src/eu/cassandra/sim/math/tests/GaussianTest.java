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
package eu.cassandra.sim.math.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.cassandra.sim.math.Gaussian;
import eu.cassandra.sim.utilities.RNG;


/**
 * @author Konstantina Valogianni 
 * @version platform
 * @since 2012-11-07
 */
public class GaussianTest {

	@Test
	public void testgetDescription() {
		Gaussian tester=new Gaussian();
		assertTrue("Result", "Gaussian probability density function"==tester.getDescription());
	}
	  
	@Test
	public void testGetNumberOfParameters() {
		Gaussian tester=new Gaussian();
		assertEquals("Result", 2, tester.getNumberOfParameters());
	}
	@Test
	public void testGetParameter() {
		Gaussian tester=new Gaussian(3,2);
		assertEquals("Result", (long)3, (long)tester.getParameter(0));
		assertEquals("Result", (long)2, (long)tester.getParameter(1));
	}
	
	
	
	@Test
	public void testGetProbability() {
		Gaussian tester=new Gaussian();
		assertTrue("Result3",0.3989422804014327==tester.getProbability(0));
	}
	
	@Test
	public void testGetPrecomputedProbability() {
		Gaussian tester=new Gaussian(2, 3);
		tester.precompute (1, 10, 10);
		assertTrue("Result",0.1501655201415712==tester.getPrecomputedProbability(1));
	}
	
	/* This part is not deterministic and therefore the outcome is not the same after repeated trials
	 * @Test
	public void testGetPrecomputedBin() {
		Gaussian tester=new Gaussian(2, 3);
		tester.precompute (1, 10, 10);
		RNG.init();
		assertTrue("Result", true==tester.precomputed);
		assertTrue("Result", 1==tester.getPrecomputedBin());
	}*/
}

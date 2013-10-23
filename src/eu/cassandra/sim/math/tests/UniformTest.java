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

import eu.cassandra.sim.math.Uniform;
import eu.cassandra.sim.utilities.RNG;

/**
 * @author Konstantina Valogianni 
 * @version platform
 * @since 2012-11-07
 */


public class UniformTest {

		@Test
		public void testGetParameter() {
			Uniform tester=new Uniform(1,10);
			assertEquals("Result", (long)1, (long)tester.getParameter(0));
			assertEquals("Result", (long)10, (long)tester.getParameter(1));
		}
		@Test
		public void testGetProbability() {
			Uniform tester=new Uniform(1, 10);
			tester.precompute (1, 10, 10);
			assertTrue("Result",0.1 ==tester.getProbability(3));
			assertTrue("Result",0 ==tester.getProbability(11));
		}
		
		@Test
		public void testGetPrecomputedProbability() {
			Uniform tester=new Uniform(1, 10);
			tester.precompute (1, 10,15);
			assertTrue("Result", 0.1==tester.getPrecomputedProbability (2));
			assertTrue("Result", 0==tester.getPrecomputedProbability (13));
		}
		
		/* This part is not deterministic and therefore the outcome is not the same after repeated trials
		@Test
		public void testGetPrecomputedBin() {
			Uniform tester=new Uniform(1, 10);
			RNG.init();
			tester.precompute (1, 2,2);
			
			assertEquals("Result", (long)-1,(long)tester.getPrecomputedBin());
		}*/
}

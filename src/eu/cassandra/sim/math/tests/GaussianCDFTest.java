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


import eu.cassandra.sim.math.GaussianCDF;

public class GaussianCDFTest {
	
	@Test
	public void testgetDescription() {
		GaussianCDF tester=new GaussianCDF();
		assertTrue("Result", "Gaussian cumulative probability density function"==tester.getDescription());
	}
		
	@Test
	public void testGetProbability() {
		GaussianCDF tester=new GaussianCDF(5,10);
		assertTrue("Result",0.3445783620526301== tester.getProbability(1));
	}

}

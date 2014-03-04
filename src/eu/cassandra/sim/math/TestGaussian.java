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
package eu.cassandra.sim.math;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.cassandra.sim.utilities.RNG;

public class TestGaussian {
	
	@Test
	public void test_gm() {
		int num = (int) (10 * Math.random());
	    double[] pi = new double[num];
	    double[] means = new double[num];
	    double[] sigmas = new double[num];

	    for (int i = 0; i < num; i++) {
	      pi[i] = 1.0 / (double) num;
	      means[i] = 1440 * Math.random();
	      sigmas[i] = 144 * Math.random();
	    }

	    GaussianMixtureModels g = new GaussianMixtureModels(num, pi, means, sigmas);
	    assertTrue(g.getPrecomputedBin(RNG.nextDouble()) == -1);
	    
	}
	

}

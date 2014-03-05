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
import eu.cassandra.sim.math.GaussianMixtureModels;

import eu.cassandra.sim.utilities.RNG;


/**
 * @author Konstantina Valogianni 
 * @version platform
 * @since 2012-11-07
 */



public class GaussianMixtureModelsTest 
{
	
	@Test
	public void testgetDescription() {
		int num = 2;
	     double[] pi = new double[num];
	     double[] means = new double[num];
	     double[] sigmas = new double[num];

	     for (int i = 0; i < num; i++) {
	       pi[i] = 1.0 / (double) num;
	       means[i] = 1440 * Math.random();
	       sigmas[i] = 144 * Math.random();
	     }
		GaussianMixtureModels tester=new GaussianMixtureModels(num, pi, means, sigmas);
		
		assertTrue("Result", "Gaussian Mixture Models probability density function"==tester.getDescription());
	}
	
	@Test
	public void testGetNumberOfParameters() {
		int num = 2;
	     double[] pi = new double[num];
	     double[] means = new double[num];
	     double[] sigmas = new double[num];

	     for (int i = 0; i < num; i++) {
	       pi[i] = 1.0 / (double) num;
	       means[i] = 1440 * Math.random();
	       sigmas[i] = 144 * Math.random();
	     }
		GaussianMixtureModels tester=new GaussianMixtureModels(num, pi, means, sigmas);
		assertEquals("Result",6,tester.getNumberOfParameters());
	}
	
	@Test
	public void testGetParameters() {
		int num = 2;
	     double[] pi = new double[num];
	     double[] means = new double[num];
	     double[] sigmas = new double[num];

	     for (int i = 0; i < num; i++) {
	       pi[i] = 1.0 / (double) num;
	       means[i] = 1440;
	       sigmas[i] = 144;
	     }
		GaussianMixtureModels tester=new GaussianMixtureModels(num, pi, means, sigmas);
		assertTrue("Result",1440==tester.getParameters(1)[0]);
		assertTrue("Result",144==tester.getParameters(1)[1]);
		assertTrue("Result",0.5==tester.getParameters(1)[2]);
		assertTrue("Result",1440==tester.getParameters(0)[0]);
		assertTrue("Result",144==tester.getParameters(0)[1]);
		assertTrue("Result",0.5==tester.getParameters(0)[2]);
	}
	
	@Test
	public void testGetProbability() {
		int num =2;
	     double[] pi = new double[num];
	     double[] means = new double[num];
	     double[] sigmas = new double[num];

	     for (int i = 0; i < num; i++) {
	       pi[i] = 1.0 / (double) num;
	       means[i] = 1440 ;
	       sigmas[i] = 144 ;
	     }
		GaussianMixtureModels tester=new GaussianMixtureModels(num, pi, means, sigmas);
		assertTrue("Result",6.139037250930967E-25==tester.getProbability(2));
	}
	@Test
	public void testGetCumulativeProbability() {
		int num =2;
	     double[] pi = new double[num];
	     double[] means = new double[num];
	     double[] sigmas = new double[num];

	     for (int i = 0; i < num; i++) {
	       pi[i] = 1.0 / (double) num;
	       means[i] = 1440;
	       sigmas[i] = 144;
	     }
		GaussianMixtureModels tester=new GaussianMixtureModels(num, pi, means, sigmas);
		assertTrue("Result",0.0==tester.getCumulativeProbability(2));
	}
	
	@Test
	public void testGetPrecomputedProbability() {
		int num =2;
	     double[] pi = new double[num];
	     double[] means = new double[num];
	     double[] sigmas = new double[num];

	     for (int i = 0; i < num; i++) {
	       pi[i] = 1.0 / (double) num;
	       means[i] = 1440;
	       sigmas[i] = 144;
	     }
		GaussianMixtureModels tester=new GaussianMixtureModels(num, pi, means, sigmas);
		tester.precompute(0, 1439, 1440);
		assertEquals((long)0.5055384072564862,(long)tester.getPrecomputedProbability(1439));
		assertEquals((long)0,(long)tester.getPrecomputedProbability(2));
	}
	
	@Test
	public void testGetPrecomputedBin() {
		int num = (int) (10 * Math.random());
	     double[] pi = new double[num];
	     double[] means = new double[num];
	     double[] sigmas = new double[num];
	     RNG.init();
	     for (int i = 0; i < num; i++) {
	       pi[i] = 1.0 / (double) num;
	       means[i] = 1440 * Math.random();
	       sigmas[i] = 144 * Math.random();
	     }
		GaussianMixtureModels tester=new GaussianMixtureModels(num, pi, means, sigmas);
		assertTrue("Result",-1==tester.getPrecomputedBin(RNG.nextDouble()));
		tester.precompute(0, 1439, 1440);
		assertTrue("Result2",0<tester.getPrecomputedBin(RNG.nextDouble()));
	}
	
	
	@Test
	public void testgetParameter() {
		int num =2;
	     double[] pi = new double[num];
	     double[] means = new double[num];
	     double[] sigmas = new double[num];

	     for (int i = 0; i < num; i++) {
	       pi[i] = 1.0 / (double) num;
	       means[i] = 1440;
	       sigmas[i] = 144 ;
	     }
		GaussianMixtureModels tester=new GaussianMixtureModels(num, pi, means, sigmas);
		
		assertTrue("Result",0==tester.getParameter(1));
	}
}

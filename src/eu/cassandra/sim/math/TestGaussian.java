package eu.cassandra.sim.math;

import static org.junit.Assert.*;

import org.junit.Test;

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
	    assertTrue(g.getPrecomputedBin() == -1);
	    
	}
	

}

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

/**
 * @author Christos Diou <diou remove this at iti dot gr>
 * @version prelim
 * @since 2012-22-01
 */
public class GaussianCDF extends Gaussian implements ProbabilityDistribution {

    /**
     * Constructor. Sets the parameters of the standard normal
     * distribution, with mean 0 and standard deviation 1.
     */
    public GaussianCDF() {
    	super();
    }

    /**
     * @param mu Mean value of the Gaussian distribution.
     * @param s Standard deviation of the Gaussian distribution.
     */
    public GaussianCDF(double mu, double s) {
    	super(mu,s);
    }

    public String getDescription() {
        return "Gaussian cumulative probability density function";
    }

    public void precompute(double startValue, double endValue, int nBins) {
        if (startValue >= endValue) {
            // TODO Throw an exception or whatever.
            return;
        }
        precomputeFrom = startValue;
        precomputeTo = endValue;
        numberOfBins = nBins;

        double div = (endValue - startValue) / (double) nBins;
        histogram = new double[nBins];

        for (int i = 0; i < nBins; i ++) {
            double x = startValue + i * div;
            // Value of bin is the probability at the beginning of the
            // value range.
            histogram[i] = bigPhi(x, mean, sigma);
        }
        precomputed = true;
    }

    public double getProbability(double x) {
        return bigPhi(x, mean, sigma);
    }

}

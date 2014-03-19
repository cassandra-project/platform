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

import eu.cassandra.sim.utilities.RNG;

/**
 * @author Antonios Chrysopoulos
 * @version prelim
 * @since 2012-26-06
 */
public class GaussianMixtureModels implements ProbabilityDistribution
{
  protected double[] pi;
  protected Gaussian[] gaussians;

  // For precomputation
  protected boolean precomputed;
  protected int numberOfBins;
  protected double precomputeFrom;
  protected double precomputeTo;
  protected double[] histogram;
  
  public String getType()
  {
    return "GMM";
  }

  /**
   * Constructor. Sets the parameters of the standard normal
   * distribution, with mean 0 and standard deviation 1.
   */
  public GaussianMixtureModels (int n)
  {
    pi = new double[n];
    for (int i = 0; i < n; i++) {
      pi[i] = (1.0 / n);
      gaussians[i] = new Gaussian();
    }
    precomputed = false;
  }

  /**
   * @param mu
   *          Mean value of the Gaussian distribution.
   * @param s
   *          Standard deviation of the Gaussian distribution.
   */
  public GaussianMixtureModels (int n, double[] pi, double[] mu, double[] s)
  {
    gaussians = new Gaussian[n];
    this.pi = new double[n];
    for (int i = 0; i < n; i++) {
      this.pi[i] = pi[i];
      gaussians[i] = new Gaussian(mu[i], s[i]);
    }
    precomputed = false;
  }

  public String getDescription ()
  {
    String description = "Gaussian Mixture Models probability density function";
    return description;
  }

  public int getNumberOfParameters ()
  {
    return 3 * pi.length;
  }

  public double[] getParameters (int index)
  {
    double[] temp = new double[3];

    temp[0] = gaussians[index].getParameter(0);
    temp[1] = gaussians[index].getParameter(1);
    temp[2] = pi[index];

    return temp;
  }

  public void setParameters (int index, double[] values)
  {

    gaussians[index].setParameter(0, values[0]);
    gaussians[index].setParameter(1, values[1]);
    pi[index] = values[2];

  }

  public void precompute (double startValue, double endValue, int nBins)
  {
    if (startValue >= endValue) {
      // TODO Throw an exception or whatever.
      return;
    }
    precomputeFrom = startValue;
    precomputeTo = endValue;
    numberOfBins = nBins;
    histogram = new double[nBins];

    for (int i = 0; i < gaussians.length; i++) {
      gaussians[i].precompute(startValue, endValue, nBins);
    }

    for (int i = 0; i < nBins; i++) {

      for (int j = 0; j < gaussians.length; j++) {
        histogram[i] += pi[j] * gaussians[j].getHistogram()[i];
      }
      
    }

    precomputed = true;
  }

  public double getProbability (double x)
  {
    double sum = 0;
    for (int j = 0; j < pi.length; j++) {
      sum += pi[j] * gaussians[j].getProbability(x);
    }
    return sum;
  }

  public double getCumulativeProbability (double z)
  {
    double sum = 0;
    for (int j = 0; j < pi.length; j++) {
      sum +=
        pi[j]
                * Gaussian.bigPhi(z, gaussians[j].getParameter(0),
                                  gaussians[j].getParameter(1));
    }
    return sum;
  }

  public double getPrecomputedProbability (double x)
  {
    if (!precomputed) {
      return -1;
    }
    double div = (precomputeTo - precomputeFrom) / (double) numberOfBins;
    int bin = (int) Math.floor((x - precomputeFrom) / div);
    if (bin == numberOfBins) {
      bin--;
    }
    return histogram[bin];
  }

  public int getPrecomputedBin (double rn)
  {
    if (!precomputed) {
      return -1;
    }
    // double div = (precomputeTo - precomputeFrom) / (double) numberOfBins;
    double dice = rn;
    double sum = 0;
    for (int i = 0; i < numberOfBins; i++) {
      sum += histogram[i];
      // if(dice < sum) return (int)(precomputeFrom + i * div);
      if (dice < sum) {
        return i;
      }
    }
    return -1;
  }
  
  public void precompute (int endValue)
  {
    if (endValue == 0) {
      // TODO Throw an exception or whatever.
      return;
    }
    int startValue = 0;
    int nBins = endValue;
    
    precompute(startValue, endValue,nBins);
    
  }
  
  public double[] getHistogram ()
  {
	if (precomputed == false){
		System.out.println("Not computed yet!");
		return null;		
	}
	  
    return histogram;
  }

  public void status ()
  {

    System.out.print("Gaussian Mixture with");
    System.out.println(" Number of Mixtures:" + pi.length);
    for (int i = 0; i < pi.length; i++) {
      System.out.print("Mixture " + i);
      System.out.print(" Mean: " + gaussians[i].getParameter(0));
      System.out.print(" Sigma: " + gaussians[i].getParameter(1));
      System.out.print(" Weight: " + pi[i]);
      System.out.println();
    }
    System.out.println("Precomputed: " + precomputed);
    if (precomputed) {
      System.out.print("Number of Beans: " + numberOfBins);
      System.out.print(" Starting Point: " + precomputeFrom);
      System.out.println(" Ending Point: " + precomputeTo);
    }
    System.out.println();
  }
  
  @Override
  public double getProbabilityGreaterEqual (int x)
  {
    double prob = 0;

    int start = (int) x;

    for (int i = start; i < histogram.length; i++)
      prob += histogram[i];

    return prob;
  }

  public static void main (String[] args)
  {
    System.out.println("Testing Mixture Creation.");

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
    g.precompute(0, 1439, 1440);

    g.status();

//    Charts.createHistogram("TestHist", "minute", "Possibility", g.histogram);
//    Charts.createMixtureDistribution("TestMix", "minute", "Possibility",
//                                     g.histogram);

    RNG.init();
    System.out.println("Testing Random Bins");
    for (int i = 0; i < num; i++) {
      int temp = g.getPrecomputedBin(RNG.nextDouble());
      System.out.println("Random Bin: " + temp + " Possibility Value: "
                         + g.getPrecomputedProbability(temp));
    }
    
    System.out.println("======Test 2======");

    double[] pi2 = {0.5, 0.5};
    double[] means2 = {1382.81587, 444.884615};
    double[] sigmas2 = {7.46468, 66.580967};
    
    
    GaussianMixtureModels g2 = new GaussianMixtureModels(pi2.length, pi2, means2, sigmas2);
    g2.precompute(0, 1439, 1440);
    g2.status();
    RNG.init();
    for (int i = 0; i < 10; i++) {
    	int temp = g2.getPrecomputedBin(RNG.nextDouble());
    	System.out.println(temp + " " + g2.getPrecomputedProbability(temp));
    }
    double[] h = g2.getHistogram();
    double sum = 0;
    for (int i = 0; i < h.length; i++) {
    	//System.out.println(i + " " + h[i]);
    	sum += h[i];
    }
    System.out.println("Sum: " + sum);
    
  }

  public double getParameter (int index)
  {
    return 0;
  }

  public void setParameter (int index, double value)
  {
  }

}

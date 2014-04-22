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
 * @since 2012-28-06
 */
public class Uniform implements ProbabilityDistribution {
	  /**
	   * The name of the Normal distribution.
	   */
	  private String name = "";

	  /**
	   * The type of the Normal distribution.
	   */
	  private String type = "";

	  /**
	   * A boolean variable that shows if the values of the Normal distribution
	   * histogram
	   * has been precomputed or not.
	   */
	  protected boolean precomputed;

	  /**
	   * A variable presenting the number of bins that are created for the histogram
	   * containing the values of the Normal distribution.
	   */
	  protected int numberOfBins;

	  /**
	   * The starting point of the bins for the precomputed values.
	   */
	  protected double precomputeFrom;

	  /**
	   * The ending point of the bins for the precomputed values.
	   */
	  protected double precomputeTo;

	  /**
	   * An array containing the probabilities of each bin precomputed for the
	   * Normal distribution.
	   */
	  protected double[] histogram;

	  /**
	   * This is an array that contains the probabilities that the distribution has
	   * value over a threshold.
	   */
	  private double[] greaterProbability;

	  /** The id of the distribution as given by the Cassandra server. */
	  private String distributionID = "";

	  /**
	   * @param start
	   *          Starting value of the Uniform distribution.
	   * @param end
	   *          Ending value of the Uniform distribution.
	   */
	  public Uniform (double start, double end)
	  {
	    name = "Generic";
	    type = "Uniform Distribution";
	    precomputeFrom = start;
	    precomputeTo = end;
	    precomputed = false;
	    estimateGreaterProbability();
	  }

	  /**
	   * @param start
	   *          Starting value of the Uniform distribution.
	   * @param end
	   *          Ending value of the Uniform distribution.
	   * @param startTime
	   *          variable that shows if this is a start time distribution or not.
	   */
	  public Uniform (double start, double end, boolean startTime)
	  {
	    name = "Generic";
	    type = "Uniform Distribution";
	    precomputeFrom = start;
	    precomputeTo = end;

	    if (startTime)
	      precompute((int) start, (int) end, 1440);
	    else
	      precompute((int) start, (int) end, (int) (end + 1));

	    estimateGreaterProbability();
	  }

	  public String getType ()
	  {
	    return "Uniform";
	  }

	  public String getDescription ()
	  {
	    String description = "Uniform probability density function";
	    return description;
	  }

	  public int getNumberOfParameters ()
	  {
	    return 2;
	  }

	  public double getParameter (int index)
	  {
	    switch (index) {
	    case 0:
	      return precomputeFrom;
	    case 1:
	      return precomputeTo;
	    default:
	      return 0.0;
	    }

	  }

	  public void setParameter (int index, double value)
	  {
	    switch (index) {
	    case 0:
	      precomputeFrom = value;
	      break;
	    case 1:
	      precomputeTo = value;
	      break;
	    default:
	      return;
	    }
	  }

	  public double getProbability (double x)
	  {
	    if (x > precomputeTo || x < precomputeFrom) {
	      return 0.0;
	    }
	    else {
	      if (precomputeTo == precomputeFrom && x == precomputeTo)
	        return 1.0;
	      else
	        return 1.0 / (double) (precomputeTo - precomputeFrom + 1);
	    }
	  }

	  public double getPrecomputedProbability (double x)
	  {
	    if (!precomputed) {
	      return -1;
	    }
	    else if (x > precomputeTo || x < precomputeFrom) {
	      return -1;
	    }
	    return histogram[(int) (x - precomputeFrom)];
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
	      if (dice < sum)
	        return i;
	    }
	    return -1;
	  }

	  public void precompute (int endValue)
	  {
	    precompute(0, endValue, endValue + 1);
	  }

	  public double[] getHistogram ()
	  {
	    if (precomputed == false) {
	      System.out.println("Not computed yet!");
	      return null;
	    }

	    return histogram;
	  }

	  public double[] getGreaterProbability ()
	  {
	    return greaterProbability;
	  }

	  public void status ()
	  {
	    System.out.print("Uniform Distribution with ");
	    System.out.println("Precomputed: " + precomputed);
	    if (precomputed) {
	      System.out.print("Number of Beans: " + numberOfBins);
	      System.out.print(" Starting Point: " + precomputeFrom);
	      System.out.println(" Ending Point: " + precomputeTo);
	    }
	    System.out.println();

	  }

	  public static void main (String[] args)
	  {
	    System.out.println("Testing Start Time Uniform Distribution Creation.");

	    int start = (int) (1440 * Math.random());
	    int end = (int) (start + (1440 - start) * Math.random());

	    Uniform u = new Uniform(start, end);
	    u.precompute(start, end, 1440);

	    u.status();
	    RNG.init();

	    System.out.println("Testing Random Bins");
	    for (int i = 0; i < 10; i++) {
	      int temp = u.getPrecomputedBin(RNG.nextDouble());
	      System.out.println("Random Bin: " + temp + " Possibility Value: "
	                         + u.getPrecomputedProbability(temp));
	    }

	    System.out.println("Testing Duration Uniform Distribution Creation.");

	    start = 10;
	    end = 12;

	    u = new Uniform(start, end);
	    u.precompute(start, end, (int) end + 1);

	    u.status();

	    System.out.println("Testing Random Bins");
	    for (int i = 0; i < 10; i++) {
	      int temp = u.getPrecomputedBin(RNG.nextDouble());
	      System.out.println("Random Bin: " + temp + " Possibility Value: "
	                         + u.getPrecomputedProbability(temp));
	    }

	  }

	  public String getName ()
	  {
	    return name;
	  }

	  public double getProbability (int x)
	  {
	    if (x < 0)
	      return 0;
	    else
	      return histogram[x];
	  }

	  public double getProbabilityLess (int x)
	  {
	    return 1 - getProbabilityGreater(x);
	  }

	  public double getProbabilityGreater (int x)
	  {
	    double prob = 0;

	    int start = (int) x;

	    for (int i = start+1; i < histogram.length; i++)
	      prob += histogram[i];

	    return prob;
	  }

	  private void estimateGreaterProbability ()
	  {
	    greaterProbability = new double[histogram.length];

	    for (int i = 0; i < histogram.length; i++)
	      greaterProbability[i] = getProbabilityGreater(i);

	  }

	  public double getPrecomputedProbability (int x)
	  {
	    if (!precomputed)
	      return -1;
	    else
	      return histogram[x];

	  }
	  
	  
	  public void precompute (double startValue, double endValue, int nBins) {
		  precompute((int)startValue, (int) endValue, nBins);
	  }
	  
	  
	  public void precompute (int startValue, int endValue, int nBins)
	  {
	    // TODO Auto-generated method stub
	    numberOfBins = nBins;
	    histogram = new double[nBins];

	    if (startValue > endValue) {
	      System.out.println("Starting point greater than ending point");
	      return;
	    }
	    else {

	      for (int i = startValue; i <= endValue; i++) {

	        histogram[i] = 1.0 / (double) (precomputeTo - precomputeFrom + 1);

	      }
	    }
	    precomputed = true;
	  }
}
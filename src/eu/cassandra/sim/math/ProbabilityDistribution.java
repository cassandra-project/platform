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
public interface ProbabilityDistribution
{

  /**
   * Return a string describing the distribution in free text.
   * 
   * @return String with description of distribution.
   */
  public String getDescription();
  
  public String getType();

  /**
   * Return the number of parameters of this distribution.
   * 
   * @return An int with the number of distribution parameters.
   */
  public int getNumberOfParameters ();

  /**
   * Return a distribution parameter.
   * 
   * @param index
   *          Index of the parameter (starting from 0).
   * @return The parameter value.
   */
  public double getParameter (int index);

  /**
   * Set a parameter value
   * 
   * Most implementations are expected to set the parameters at the
   * constructor, but this function should also be implemented.
   * 
   * @param index
   *          Index of the parameter (starting from 0).
   * @return value The parameter value
   */
  public void setParameter (int index, double value);

  /**
   * Precomputes a set of distribution values.
   * 
   * Given a set of "bins" this method computes the probability of
   * randomly drawing a value from each bin, including the bin
   * starting value and not including the end value. The computed
   * value is stored in a histogram vector and can later be directly
   * accessed using the method getPrecomputedProbability(). If the
   * distribution is a probability density function, then this
   * function may compute the integral of the pdf for the bin value
   * range, otherwise the function will only compute the probability
   * at the starting value of the bin.
   * 
   * @param startValue
   *          The starting value of the probability
   *          distribution domain or the lower bound for which probabilities
   *          will be pre-computed.
   * @param endValue
   *          The ending value of the probability
   *          distribution domain or the lower bound for which probabilities
   *          will be pre-computed.
   * @param nBins
   *          The number of bins that will be used for the given
   *          value range.
   */
  public void precompute (double startValue, double endValue, int nBins);

  /**
   * Precomputes a set of distribution values.
   * 
   * Given a set of end point of the distribution this method computes
   * the probability of randomly drawing a value from each bin, including the bin
   * starting value and not including the end value. The computed
   * value is stored in a histogram vector and can later be directly
   * accessed using the method getPrecomputedProbability(). If the
   * distribution is a probability density function, then this
   * function may compute the integral of the pdf for the bin value
   * range, otherwise the function will only compute the probability
   * at the starting value of the bin.
   * 
   * @param endValue
   *          The ending value of the probability
   *          distribution domain or the lower bound for which probabilities
   *          will be pre-computed.
   */
  public void precompute (int endValue);
  
  /**
   * Get the probability value P(x).
   * 
   * @param x
   *          The input value.
   * @return The probability value at x (P(x)).
   */
  public double getProbability (double x);
  
  /**
   * Get the probability value P(X < x).
   * 
   * @param x
   *          The input value.
   * @return The probability value at x (P(x)).
   */
  public double getProbabilityGreater (int x);

  /**
   * Get the precomputed probability value for the x.
   * 
   * @param x
   *          The input value.
   * @return The precomputed probability value of the bin that
   *         contains x. If no probabilities have been pre-computed, then
   *         this function returns -1.
   */
  public double getPrecomputedProbability (double x);

  /**
   * Gets a random integer between 0 and the number of nBins.
   * 
   * @return A random integer following the distribution of the precomputed
   *         histogram
   */
  public int getPrecomputedBin (double rn);
  
  /**
   * Returns the histogram in question.
   * 
   * @return A random integer following the distribution of the precomputed
   *         histogram
   */
  public double[] getHistogram ();

  /**
   * Shows the general attributes of the disribution.
   * 
   */
  public void status ();

}

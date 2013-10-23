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

package eu.cassandra.sim.math.response;

import java.util.ArrayList;

import eu.cassandra.sim.utilities.Constants;

/**
 * This class is used for implementing an array of incentives that are a result
 * of the analysis of the available pricing schemes (Basic and New). The array
 * is then used as an input to the Response Models creation procedure.
 * 
 * @author Antonios Chrysopoulos
 * @version 0.9, Date: 29.07.2013
 */
public class IncentiveVector
{
	/** This is an list of the available incentives for the day. */
	  ArrayList<Incentive> incentives = new ArrayList<Incentive>();

	  /** This variable equals to the number of penalty incentives of the list. */
	  int numberOfPenalties = 0;

	  /** This variable equals to the number of reward incentives of the list. */
	  int numberOfRewards = 0;

	  /** This variable shows the index to the larger penalty incentive of the list. */
	  int indexOfLargerPenalty = -1;

	  /** This variable shows the index to the larger reward incentive of the list. */
	  int indexOfLargerReward = -1;

	  /**
	   * The constructor of the Incentive Vector. It uses the pricing schemes to
	   * fill the array and the rest of the variables.
	   * 
	   * @param basicScheme
	   *          The basic pricing scheme as imported by the user to the Training
	   *          Module GUI.
	   * @param newScheme
	   *          The new pricing scheme as imported by the user to the Training
	   *          Module GUI.
	   */
	  public IncentiveVector (double[] basicScheme, double[] newScheme)
	  {
	    // Initializing auxiliary variables
	    double[] diff = new double[Constants.MIN_IN_DAY];
	    boolean startFlag = false;
	    int start = -1;
	    int end = -1;
	    double base = 0;
	    double bDiff = 0;
	    double aDiff = 0;
	    double previousValue = 0;

	    // For all the minutes of the day the procedure checks the difference
	    // between the two pricing schemes and defines the incentives found.
	    for (int i = 0; i < diff.length; i++) {
	      // System.out.println("Index:" + i);

	      diff[i] = newScheme[i] - basicScheme[i];
	      // System.out.println("Difference:" + diff[i]);

	      if (previousValue != diff[i]) {
	        // System.out
	        // .println("In for difference!" + previousValue + " " + diff[i]);

	        if (startFlag == false) {
	          // System.out.println("In for start!");
	          if (i != 0)
	            bDiff = newScheme[i] - newScheme[i - 1];
	          else
	            bDiff = newScheme[i] - newScheme[Constants.MIN_IN_DAY - 1];
	          base = basicScheme[i];
	          startFlag = true;
	          start = i;
	        }
	        else {
	          // System.out.println("In for end!");
	          aDiff = newScheme[i] - newScheme[i - 1];
	          end = i - 1;
	          startFlag = false;
	          incentives.add(new Incentive(start, end, base, bDiff, aDiff,
	                                       previousValue));

	          if (diff[i] != 0) {
	            // System.out.println("In for start kapaki!");
	            base = basicScheme[i];
	            bDiff = newScheme[i] - newScheme[i - 1];
	            startFlag = true;
	            start = i;

	          }
	        }
	      }
	      previousValue = diff[i];
	    }

	    if (startFlag) {
	      // System.out.println("In for end of index!");
	      aDiff = newScheme[0] - newScheme[Constants.MIN_IN_DAY - 1];
	      end = Constants.MIN_IN_DAY - 1;
	      startFlag = false;
	      incentives.add(new Incentive(start, end, base, bDiff, aDiff,
	                                   previousValue));
	    }

	    analyze();

	    // show();
	  }

	  /**
	   * This function used the newly filled list of incentives and analyses the
	   * results to fill the rest of the variables.
	   */
	  private void analyze ()
	  {

	    double maxPrice = Double.NEGATIVE_INFINITY;
	    double minPrice = Double.POSITIVE_INFINITY;

	    for (int i = 0; i < incentives.size(); i++) {

	      if (incentives.get(i).isPenalty())
	        numberOfPenalties++;
	      else
	        numberOfRewards++;

	      if (maxPrice < incentives.get(i).getPrice()) {
	        maxPrice = incentives.get(i).getPrice();
	        indexOfLargerPenalty = i;
	      }

	      if (minPrice > incentives.get(i).getPrice()) {
	        minPrice = incentives.get(i).getPrice();
	        indexOfLargerReward = i;
	      }

	    }

	  }

	  /**
	   * This function is used for the exhibition of the details of the attributes
	   * contained in the Incentives Vector.
	   */
	  private void show ()
	  {
	    for (int i = 0; i < incentives.size(); i++)
	      incentives.get(i).status();
	    System.out.println("Penalties: " + numberOfPenalties);
	    System.out.println("Rewards: " + numberOfRewards);
	    if (numberOfPenalties > 0)
	      System.out.println("Larger Penalty: "
	                         + incentives.get(indexOfLargerPenalty).getPrice());
	    if (numberOfRewards > 0)
	      System.out.println("Larger Reward: "
	                         + incentives.get(indexOfLargerReward).getPrice());
	  }

	  /**
	   * This is a getter function of the incentives' list.
	   * 
	   * @return the list of incentives.
	   */
	  public ArrayList<Incentive> getIncentives ()
	  {
	    return incentives;
	  }
}

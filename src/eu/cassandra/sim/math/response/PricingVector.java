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
 * This class is used for implementing an array of pricings that are a result
 * of the analysis of the available pricing schemes (Basic and New). The array
 * is then used as an input to the Response Models creation procedure.
 * 
 * @author Antonios Chrysopoulos
 * @version 0.9, Date: 29.07.2013
 */
public class PricingVector
{

  /** This is an list of the available pricings for the day. */
  ArrayList<Pricing> pricings = new ArrayList<Pricing>();

  /** This is an list of the the penalty indices in the pricing list. */
  ArrayList<Integer> penalties = new ArrayList<Integer>();

  /** This is an list of the the penalty indices in the pricing list. */
  ArrayList<Integer> rewards = new ArrayList<Integer>();

  /** This is an list of the the penalty indices in the pricing list. */
  ArrayList<Integer> bases = new ArrayList<Integer>();

  /** This variable shows the index to the cheapest pricing of the list. */
  int indexOfCheapest = -1;

  /**
   * The constructor of the Pricing Vector. It uses the pricing schemes to
   * fill the array and the rest of the variables.
   * 
   * @param basicScheme
   *          The basic pricing scheme as imported by the user to the Training
   *          Module GUI.
   * @param newScheme
   *          The new pricing scheme as imported by the user to the Training
   *          Module GUI.
   */
  public PricingVector (double[] basicScheme, double[] newScheme)
  {
    // Initializing auxiliary variables
    boolean startFlag = false;
    int start = -1;
    int end = -1;
    String type = "";
    double currentValue = 0;
    double previousPricing = 0;
    double previousValue = 0;

    // For all the minutes of the day the procedure checks the difference
    // between the two pricing schemes and defines the incentives found.
    for (int i = 0; i < newScheme.length; i++) {
      // System.out.println("Index:" + i);

      if (previousValue != newScheme[i]) {
        // System.out
        // .println("In for difference!" + previousValue + " " + diff[i]);

        if (startFlag == false) {
          // System.out.println("In for start!");
          currentValue = newScheme[i];
          previousPricing = basicScheme[i];
          if (newScheme[i] == basicScheme[i])
            type = "Base";
          else if (newScheme[i] < basicScheme[i])
            type = "Reward";
          else
            type = "Penalty";

          startFlag = true;
          start = i;
        }
        else {
          // System.out.println("In for end!");
          end = i - 1;
          startFlag = false;
          pricings.add(new Pricing(start, end, previousPricing, currentValue,
                                   type));

          // System.out.println("In for start kapaki!");
          currentValue = newScheme[i];
          previousPricing = basicScheme[i];
          if (newScheme[i] == basicScheme[i])
            type = "Base";
          else if (newScheme[i] < basicScheme[i])
            type = "Reward";
          else
            type = "Penalty";
          startFlag = true;
          start = i;

        }
      }
      previousValue = newScheme[i];
    }

    if (startFlag) {
      // System.out.println("In for end of index!");
      end = Constants.MIN_IN_DAY - 1;
      startFlag = false;
      pricings.add(new Pricing(start, end, previousPricing, currentValue, type));
    }

    analyze();

    // show();
  }

  /**
   * This function used the newly filled list of pricings and analyses the
   * results to fill the rest of the variables.
   */
  private void analyze ()
  {

    double minPrice = Double.POSITIVE_INFINITY;
    int minDur = 0;
    int newDur = 0;

    for (int i = 0; i < pricings.size(); i++) {

      if (pricings.get(i).getType().equalsIgnoreCase("Base"))
        bases.add(i);
      else if (pricings.get(i).getType().equalsIgnoreCase("Reward"))
        rewards.add(i);
      else
        penalties.add(i);

      if (minPrice > pricings.get(i).getCurrentPrice()) {
        minPrice = pricings.get(i).getCurrentPrice();
        minDur =
          pricings.get(i).getEndMinute() - pricings.get(i).getStartMinute();
        indexOfCheapest = i;
      }
      else if (minPrice == pricings.get(i).getCurrentPrice()) {
        newDur =
          pricings.get(i).getEndMinute() - pricings.get(i).getStartMinute();
        if (minDur < newDur) {
          minDur = newDur;
          indexOfCheapest = i;
        }
      }

    }

  }

  /**
   * This function is used for the exhibition of the details of the attributes
   * contained in the Pricings Vector.
   */
  public void show ()
  {
    // for (int i = 0; i < pricings.size(); i++)
    // pricings.get(i).status();
    System.out.println("Penalties: " + penalties.size());
    System.out.println("Rewards: " + rewards.size());
    System.out.println("Base: " + bases.size());

    System.out.println("Cheapest Pricing: ");
    pricings.get(indexOfCheapest).status();

  }

  /**
   * This is a getter function of the pricings' list.
   * 
   * @return the list of pricings.
   */
  public ArrayList<Pricing> getPricings ()
  {
    return pricings;
  }

  /**
   * This is a getter function of the penalties' list.
   * 
   * @return the list of penalties.
   */
  public ArrayList<Integer> getPenalties ()
  {
    return penalties;
  }

  /**
   * This is a getter function of the rewards' list.
   * 
   * @return the list of rewards.
   */
  public ArrayList<Integer> getRewards ()
  {
    return rewards;
  }

  /**
   * This is a getter function of the bases' list.
   * 
   * @return the list of bases.
   */
  public ArrayList<Integer> getBases ()
  {
    return bases;
  }

  /**
   * This is a getter function of the number of penalties.
   * 
   * @return the number of penalties.
   */
  public int getNumberOfPenalties ()
  {
    return penalties.size();
  }

  /**
   * This is a getter function of the number of rewards.
   * 
   * @return the number of rewards.
   */
  public int getNumberOfRewards ()
  {
    return rewards.size();
  }

  /**
   * This is a getter function of the number of bases.
   * 
   * @return the number of bases.
   */
  public int getNumberOfBases ()
  {
    return bases.size();
  }

  /**
   * This is a getter function of the incentives' list for a certain index.
   * 
   * @return a pricing.
   */
  public Pricing getPricings (int index)
  {
    return pricings.get(index);
  }

  /**
   * This is a getter function of the index of the cheapest pricing.
   * 
   * @return the index of the cheapest pricing in the list.
   */
  public int getCheapest ()
  {
    return indexOfCheapest;
  }
}
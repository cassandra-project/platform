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

/**
 * This class is used for implementing the pricing model. This can be easily
 * adopted for more compicated pricing schemes.
 * 
 * @author Antonios Chrysopoulos
 * @version 0.9, Date: 29.07.2013
 */
public class Pricing
{

	/** This variable signifies the start minute of the day for the incentive. */
	  private final int startMinute;

	  /** This variable signifies the end minute of the day for the incentive. */
	  private final int endMinute;

	  /**
	   * This variable is the previous price of the new pricing scheme.
	   */
	  private final double previousPrice;

	  /**
	   * This variable is the current price of the new pricing scheme.
	   */
	  private final double currentPrice;

	  /**
	   * This variable states the type of the pricing (Penalty or Reward).
	   */
	  private final String type;

	  /**
	   * A constructor of a pricing where all of the input variables are known.
	   * 
	   * @param start
	   *          The incentive's start minute of the day
	   * @param end
	   *          The incentive's end minute of the day
	   * @param price
	   *          The monetary value of the pricing
	   * @param type
	   *          The type of pricing policy (Penalty or Reward)
	   */
	  public Pricing (int start, int end, double previous, double current,
	                  String type)
	  {
	    startMinute = start;
	    endMinute = end;
	    previousPrice = previous;
	    currentPrice = current;
	    this.type = type;

	  }

	  /**
	   * This function is used as a getter for the start minute of the pricing.
	   * 
	   * @return pricing's start minute.
	   */
	  public int getStartMinute ()
	  {
	    return startMinute;
	  }

	  /**
	   * This function is used as a getter for the end minute of the pricing.
	   * 
	   * @return pricing's end minute.
	   */
	  public int getEndMinute ()
	  {
	    return endMinute;
	  }

	  /**
	   * This function is used as a getter for the price of the pricing.
	   * 
	   * @return pricing's price.
	   */
	  public double getPreviousPrice ()
	  {
	    return previousPrice;
	  }

	  /**
	   * This function is used as a getter for the price of the pricing.
	   * 
	   * @return pricing's price.
	   */
	  public double getCurrentPrice ()
	  {
	    return currentPrice;
	  }

	  /**
	   * This function is used as a getter for the type of the pricing.
	   * 
	   * @return pricing's type (Penalty or Reward).
	   */
	  public String getType ()
	  {
	    return type;
	  }

	  /**
	   * This function is used to estimate the ratio of the pricing schemes.
	   * 
	   * @return the ratio of the pricing scheme.
	   */
	  public double getGainRatio ()
	  {
	    return previousPrice / currentPrice;
	  }

	  /**
	   * This function is used to estimate the ratio of the pricing schemes.
	   * 
	   * @return the ratio of the pricing scheme.
	   */
	  public double getLossRatio ()
	  {
	    return currentPrice / previousPrice;
	  }

	  /**
	   * This function is used to present the basic attributes of the price.
	   */
	  public void status ()
	  {
	    System.out.println("Start Minute: " + startMinute);
	    System.out.println("End Minute: " + endMinute);
	    System.out.println("Current Pricing: " + currentPrice);
	    System.out.println("Previous Pricing: " + previousPrice);
	    System.out.println("Pricing Type: " + type);
	  }

}

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
 * This class is used for implementing the (monetary) incentives proposed to the
 * consumers in order to respond accordingly. They can be either penalty
 * incentives (meaning an increase in the price of the power) or a reward
 * (meaning a decrease in the price of power).
 * 
 * @author Antonios Chrysopoulos
 * @version 0.9, Date: 29.07.2013
 */
public class Incentive
{

	/** This variable signifies the start minute of the day for the incentive. */
	  private int startMinute;

	  /** This variable signifies the end minute of the day for the incentive. */
	  private int endMinute;

	  /**
	   * This variable states if the incentive is penalty or reward.
	   */
	  private boolean penalty;

	  /** This variable presents the pricing of the basic schema at the same time. */
	  private double base;

	  /**
	   * This variable is the estimated difference between the old pricing value and
	   * the new pricing value.
	   */
	  private double difference;

	  /**
	   * This variable is the estimated difference between the time period before
	   * this time interval and the current incentive pricing value.
	   */
	  private double beforeDifference;

	  /**
	   * This variable is the estimated difference between the time period after
	   * this time interval and the current incentive pricing value.
	   */
	  private double afterDifference;

	  /**
	   * A constructor of an incentive where all of the input variables are known.
	   * 
	   * @param start
	   *          The incentive's start minute of the day
	   * @param end
	   *          The incentive's end minute of the day
	   * @param base
	   *          The base pricing scheme for the same interval
	   * @param bDiff
	   *          The pricing difference with the previous time interval
	   * @param aDiff
	   *          The pricing difference with the next time interval
	   * @param diff
	   *          The pricing difference
	   */
	  public Incentive (int start, int end, double base, double bDiff,
	                    double aDiff, double diff)
	  {
	    startMinute = start;
	    endMinute = end;
	    this.base = base;
	    difference = Math.abs(diff);
	    penalty = (diff > 0);
	    beforeDifference = bDiff;
	    afterDifference = aDiff;
	  }

	  /**
	   * This function is used as a getter for the start minute of the incentive.
	   * 
	   * @return incentive's start minute.
	   */
	  public int getStartMinute ()
	  {
	    return startMinute;
	  }

	  /**
	   * This function is used as a getter for the end minute of the incentive.
	   * 
	   * @return incentive's end minute.
	   */
	  public int getEndMinute ()
	  {
	    return endMinute;
	  }

	  /**
	   * This function is used as a getter for the pricing difference of the
	   * incentive.
	   * 
	   * @return incentive's pricing difference.
	   */
	  public double getDifference ()
	  {
	    return difference;
	  }

	  /**
	   * This function is used as a getter for the type of the incentive.
	   * 
	   * @return incentive's type.
	   */
	  public boolean isPenalty ()
	  {
	    return penalty;
	  }

	  /**
	   * This function is used as a getter for the pricing difference of the
	   * incentive and the previous time interval.
	   * 
	   * @return incentive's pricing difference with the previous time interval.
	   */
	  public double getBeforeDifference ()
	  {
	    return beforeDifference;
	  }

	  /**
	   * This function is used as a getter for the pricing difference of the
	   * incentive and the next time interval.
	   * 
	   * @return incentive's pricing difference with the next time interval.
	   */
	  public double getAfterDifference ()
	  {
	    return afterDifference;
	  }

	  /**
	   * This function is used as a getter for the basic pricing scheme for that
	   * interval.
	   * 
	   * @return basic pricing scheme for that interval.
	   */
	  public double getBase ()
	  {
	    return base;
	  }

	  /**
	   * This function is used as a getter for the price offer from the incentive.
	   * 
	   * @return incentive's price.
	   */
	  public double getPrice ()
	  {

	    if (isPenalty())
	      return base + difference;
	    else
	      return base - difference;

	  }

	  /**
	   * This function is used to present the basic attributes of the incentive.
	   */
	  public void status ()
	  {
	    System.out.println("Start Minute: " + startMinute);
	    System.out.println("End Minute: " + endMinute);
	    System.out.println("Penalty: " + penalty);
	    System.out.println("Base:" + base);
	    System.out.println("Difference: " + difference);
	    System.out.println("Before Difference: " + beforeDifference);
	    System.out.println("After Difference: " + afterDifference);

	  }
}

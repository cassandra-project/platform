package eu.cassandra.sim.math.response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import eu.cassandra.sim.PricingPolicy;
import eu.cassandra.sim.math.Histogram;
import eu.cassandra.sim.math.ProbabilityDistribution;
import eu.cassandra.sim.utilities.Constants;

public class Response {
	
	private static final int SHIFTING_WINDOW_IN_MINUTES = 30;
	
	public static ProbabilityDistribution respond(
			ProbabilityDistribution pd, PricingPolicy policy, PricingPolicy baseline, 
			double awareness, double sensitivity) {
		double[] previousHist = pd.getHistogram();
		double[] newHist = new double[Constants.MIN_IN_DAY];
		double[] policyArr = policy.getTOUArray();
		double[] baseArr = baseline.getTOUArray();
		switch(pd.getType()) {
			case "Uniform":
				return pd;
			case "GMM":
				break;
			case "Gaussian":
				break;
			case "Histrogram":
				break;
			default:
				return pd;
		}
		newHist = shiftingOptimal(previousHist, baseArr, policyArr);
//		newHist = shiftingNormal(previousHist, baseArr, policyArr);
//		newHist = shiftingDiscrete(previousHist, baseArr, policyArr);
		ProbabilityDistribution retPd = new Histogram(newHist);
//		for(int i = 0; i < policyArr.length; i++) {
//			System.out.print(policyArr[i] + ",");
//		}
//		System.out.println("Start");
//		for(int i = 0; i < previousHist.length; i++) {
//			System.out.println(previousHist[i] + "," + retPd.getHistogram()[i]);
//		}
//		System.out.println("End");
		return retPd;
	}
	
	
	  public static double[] shiftingNormal (double[] values, double[] basicScheme, double[] newScheme)
	  {
	    double[] result = Arrays.copyOf(values, values.length);

	    PricingVector pricingVector = new PricingVector(basicScheme, newScheme);

	    IncentiveVector inc = new IncentiveVector(basicScheme, newScheme);

	    if (pricingVector.getPricings().size() > 1)
	      for (Incentive incentive: inc.getIncentives())
	        result = movingAverage(result, incentive);

	    return result;

	  }

	  
	  public static double[] shiftingDiscrete (double[] values, double[] basicScheme, double[] newScheme)
	  {
	    double[] result = Arrays.copyOf(values, values.length);

	    PricingVector pricingVector = new PricingVector(basicScheme, newScheme);

	    if (pricingVector.getPricings().size() > 1)
	      result = discreteAverage(result, pricingVector);

	    return result;
	  }
	
	public static double[] shiftingOptimal (double[] values, double[] basicScheme, double[] newScheme)
	  {

	    double[] result = Arrays.copyOf(values, values.length);

	    PricingVector pricingVector = new PricingVector(basicScheme, newScheme);

	    if (pricingVector.getPricings().size() > 1)
	      result = discreteOptimal(result, pricingVector);

	    return result;

	  }

	
	public static double[] movingAverage (double[] values, Incentive incentive)
  {
    // Initialize the auxiliary variables.
    int side = -1;
    int startIndex = incentive.getStartMinute();
    int endIndex = incentive.getEndMinute();
    double overDiff = 0;
    double temp = 0;
    String type = "";
    // double sum = 0;

    // First, the incentive type is checked (penalty or reward) and then the
    // before and after values are checked to see how the residual percentage
    // will be distributed.
    if (incentive.isPenalty()) {

      if (incentive.getBeforeDifference() > 0
          && incentive.getAfterDifference() < 0)
        type = "Both";

      if (incentive.getBeforeDifference() > 0
          && incentive.getAfterDifference() >= 0)
        type = "Left";

      if (incentive.getBeforeDifference() <= 0
          && incentive.getAfterDifference() < 0)
        type = "Right";

      if (incentive.getBeforeDifference() < 0
          && incentive.getAfterDifference() > 0)
        type = "None";
    }
    else {

      if (incentive.getBeforeDifference() < 0
          && incentive.getAfterDifference() > 0)
        type = "Both";

      if (incentive.getBeforeDifference() < 0
          && incentive.getAfterDifference() <= 0)
        type = "Left";

      if (incentive.getBeforeDifference() >= 0
          && incentive.getAfterDifference() > 0)
        type = "Right";

      if (incentive.getBeforeDifference() > 0
          && incentive.getAfterDifference() < 0)
        type = "None";

    }

    // System.out.println("Penalty: " + incentive.isPenalty() + " Type: " +
    // type);

    if (!type.equalsIgnoreCase("None")) {
      // In case of penalty the residual percentage is moved out of the window
      // to close distance, either on one or both sides accordingly
      if (incentive.isPenalty()) {

        for (int i = startIndex; i < endIndex; i++) {
          temp = incentive.getBase() * values[i] / incentive.getPrice();
          overDiff += values[i] - temp;
          values[i] = temp;

        }
        // System.out.println("Over Difference = " + overDiff);
        double additive = overDiff / SHIFTING_WINDOW_IN_MINUTES;

        switch (type) {

        case "Both":

          side = SHIFTING_WINDOW_IN_MINUTES / 2;

          for (int i = 0; i < side; i++) {

            int before = startIndex - i;
            if (before < 0)
              before += Constants.MIN_IN_DAY;
            int after = endIndex + i;
            if (after > Constants.MIN_IN_DAY - 1)
              after %= Constants.MIN_IN_DAY;

            values[before] += additive;
            values[after] += additive;

          }
          break;

        case "Left":

          side = SHIFTING_WINDOW_IN_MINUTES;

          for (int i = 0; i < side; i++) {

            int before = startIndex - i;
            if (before < 0)
              before += Constants.MIN_IN_DAY;
            values[before] += additive;

          }
          break;

        case "Right":

          side = SHIFTING_WINDOW_IN_MINUTES;

          for (int i = 0; i < side; i++) {

            int after = endIndex + i;
            if (after > Constants.MIN_IN_DAY - 1)
              after %= Constants.MIN_IN_DAY;

            values[after] += additive;
          }
        }
      }
      // In case of reward a percentage of the close distances are moved in the
      // window, either from one or both sides accordingly.
      else {
        side = SHIFTING_WINDOW_IN_MINUTES * 2;
        switch (type) {

        case "Both":

          for (int i = startIndex - side; i < startIndex; i++) {

            int index = i;

            if (index < 0)
              index += Constants.MIN_IN_DAY;

            temp = incentive.getPrice() * values[index] / incentive.getBase();
            overDiff += values[index] - temp;
            values[index] = temp;
          }

          for (int i = endIndex; i < endIndex + side; i++) {

            int index = i;

            if (index > Constants.MIN_IN_DAY - 1)
              index %= Constants.MIN_IN_DAY;

            temp = incentive.getPrice() * values[index] / incentive.getBase();
            overDiff += values[index] - temp;
            values[index] = temp;
          }
          break;

        case "Left":

          for (int i = startIndex - 2 * side; i < startIndex; i++) {

            int index = i;

            if (index < 0)
              index += Constants.MIN_IN_DAY;

            temp = incentive.getPrice() * values[index] / incentive.getBase();
            overDiff += values[index] - temp;
            values[index] = temp;
          }
          break;

        case "Right":

          for (int i = endIndex; i < endIndex + 2 * side; i++) {

            int index = i;

            if (index > Constants.MIN_IN_DAY - 1)
              index %= Constants.MIN_IN_DAY;

            temp = incentive.getPrice() * values[index] / incentive.getBase();
            overDiff += values[index] - temp;
            values[index] = temp;
          }

        }
        // System.out.println("Over Difference = " + overDiff);

        double additive = overDiff / (endIndex - startIndex);

        for (int i = startIndex; i < endIndex; i++)
          values[i] += additive;

      }

    }
    // for (int i = 0; i < values.length; i++)
    // sum += values[i];
    // System.out.println("Summary: " + sum);

    return values;
  }

  
  public static double[] discreteOptimal (double[] values, PricingVector pricing)
  {
    // Initialize the auxiliary variables.
    double temp = 0, additive = 0;
    Pricing tempPricing;
    double sum = 0;
    // double sum = 0;
    double overDiff = 0;
    int start, start2, end, end2, duration;
    double previousPrice, newPrice;

    if (pricing.getNumberOfPenalties() > 0) {

      Map<Integer, Double> percentageMap = new TreeMap<Integer, Double>();
      ArrayList<Integer> tempList = new ArrayList<Integer>(pricing.getBases());
      tempList.addAll(pricing.getRewards());

      for (Integer index: tempList)
        sum += pricing.getPricings(index).getGainRatio();

      for (Integer index: tempList)
        percentageMap.put(index, pricing.getPricings(index).getGainRatio()
                                 / sum);

      // System.out.println("Percentage Map: " + percentageMap.toString());

      for (Integer index: pricing.getPenalties()) {
        overDiff = 0;
        sum = 0;

        tempPricing = pricing.getPricings(index);
        start = tempPricing.getStartMinute();
        end = tempPricing.getEndMinute();
        previousPrice = tempPricing.getPreviousPrice();
        newPrice = tempPricing.getCurrentPrice();

        for (int i = start; i <= end; i++) {
          temp = previousPrice * values[i] / newPrice;
          overDiff += values[i] - temp;
          values[i] = temp;
        }

        // System.out.println("OverDiff for index " + index + ": " + overDiff);

        for (Integer index2: tempList) {
          start2 = pricing.getPricings(index2).getStartMinute();
          end2 = pricing.getPricings(index2).getEndMinute();
          duration = end2 - start2;
          additive = overDiff * percentageMap.get(index2) / duration;
          // System.out.println("Additive for index " + index2 + ": " +
          // additive);
          for (int i = start2; i < end2; i++)
            values[i] += additive;
        }

        for (int i = 0; i < values.length; i++)
          sum += values[i];

        // System.out.println("Summary: " + sum);

      }

    }
    else if (pricing.getNumberOfRewards() > 0) {

      Pricing tempPricing2 = null;
      ArrayList<Pricing> tempList = new ArrayList<Pricing>();

      for (Integer index: pricing.getRewards())
        tempList.add(pricing.getPricings(index));

      Collections.sort(tempList, comp);

      // System.out.println("Rewards List: " + tempList.toString());

      for (int i = 0; i < tempList.size(); i++) {

        tempPricing2 = tempList.get(i);
        newPrice = tempPricing2.getCurrentPrice();
        start2 = tempPricing2.getStartMinute();
        end2 = tempPricing2.getEndMinute();
        duration = end2 - start2;

        for (Integer index: pricing.getBases()) {
          overDiff = 0;
          sum = 0;

          tempPricing = pricing.getPricings(index);
          start = tempPricing.getStartMinute();
          end = tempPricing.getEndMinute();
          previousPrice = tempPricing.getCurrentPrice();

          for (int j = start; j <= end; j++) {
            temp = newPrice * values[j] / previousPrice;
            overDiff += values[j] - temp;
            values[j] = temp;
          }

          // System.out.println("OverDiff for index " + index + ": " +
          // overDiff);

          additive = overDiff / duration;
          System.out.println("Additive for index " + i + ": " + additive);

          for (int j = start2; j < end2; j++)
            values[j] += additive;
        }

        for (int j = 0; j < values.length; j++)
          sum += values[j];

        // System.out.println("Summary: " + sum);

      }

    }

    return values;
  }

  
  public static double[] discreteAverage (double[] values, PricingVector pricing)
  {

    // Initialize the auxiliary variables.
    double temp = 0;
    // double sum = 0;
    double overDiff = 0;
    int start, end;
    double newPrice;

    // Finding the cheapest window in the day.
    int cheapest = pricing.getCheapest();
    int startCheapest =
      pricing.getPricings(pricing.getCheapest()).getStartMinute();
    int endCheapest = pricing.getPricings(pricing.getCheapest()).getEndMinute();
    int durationCheapest = endCheapest - startCheapest;
    double cheapestPrice =
      pricing.getPricings(pricing.getCheapest()).getCurrentPrice();

    // Moving from all the available vectors residual percentages to the
    // cheapest one.
    for (int i = 0; i < pricing.getPricings().size(); i++) {

      if (i != cheapest) {
        // sum = 0;
        overDiff = 0;
        start = pricing.getPricings(i).getStartMinute();
        end = pricing.getPricings(i).getEndMinute();
        newPrice = pricing.getPricings(i).getCurrentPrice();

        for (int j = start; j <= end; j++) {
          temp = cheapestPrice * values[j] / newPrice;
          overDiff += values[j] - temp;
          values[j] = temp;
        }

        double additive = overDiff / durationCheapest;

        for (int j = startCheapest; j <= endCheapest; j++)
          values[j] += additive;

        // for (int j = 0; j < values.length; j++)
        // sum += values[j];
        // System.out.println("Summary after index " + i + ": " + sum);

      }

    }

    return values;
  }

  public static Comparator<Pricing> comp = new Comparator<Pricing>() {
	    @Override
	    public int compare (Pricing poi1, Pricing poi2)
	    {
	      return Double.compare(poi1.getGainRatio(), poi1.getGainRatio());
	    }
	  };

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

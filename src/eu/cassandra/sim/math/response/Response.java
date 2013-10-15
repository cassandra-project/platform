package eu.cassandra.sim.math.response;

import java.util.Arrays;

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
//		newHist = shiftingOptimal(previousHist, policyArr);
		newHist = shiftingNormal(previousHist, baseArr, policyArr);
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
	
	public static double[] shiftingNormal (double[] histogram, double[] basicScheme, double[] newScheme) {
		double[] result = Arrays.copyOf(histogram, histogram.length);

	    PricingVector pricingVector = new PricingVector(basicScheme, newScheme);

	    IncentiveVector inc = new IncentiveVector(basicScheme, newScheme);

	    if (pricingVector.getPrices().size() > 1)
	      for (Incentive incentive: inc.getIncentives())
	        result = movingAverage(result, incentive);
	    return result;
	}
	
	public static double[] shiftingOptimal (double[] histogram, double[] newScheme) {
	    double[] result = new double[Constants.MIN_IN_DAY];

	    double sum = 0;

	    for (int i = 0; i < newScheme.length; i++) {
	      result[i] = histogram[i] / newScheme[i];
	      sum += result[i];
	    }

	    for (int i = 0; i < result.length; i++)
	      result[i] /= sum;

	    return result;

	}
	
	public static double[] movingAverage (double[] values, Incentive incentive) {
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


	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

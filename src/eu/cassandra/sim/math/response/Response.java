package eu.cassandra.sim.math.response;

import eu.cassandra.sim.PricingPolicy;
import eu.cassandra.sim.math.Histogram;
import eu.cassandra.sim.math.ProbabilityDistribution;
import eu.cassandra.sim.utilities.Constants;

public class Response {
	
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
		newHist = shiftingOptimal(previousHist, policyArr);
		ProbabilityDistribution retPd = new Histogram(newHist);
		return retPd;
	}
	
	public static double[] shiftingOptimal (double[] histogram, double[] newScheme)
	  {
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

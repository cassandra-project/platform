package eu.cassandra.sim.math.response;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import eu.cassandra.server.mongo.MongoPricingPolicy;
import eu.cassandra.sim.PricingPolicy;
import eu.cassandra.sim.math.Gaussian;
import eu.cassandra.sim.math.Histogram;
import eu.cassandra.sim.math.ProbabilityDistribution;
import eu.cassandra.sim.utilities.Constants;

public class Response {
	
	private static final int SHIFTING_WINDOW_IN_MINUTES = 60;
	private static final double SMALL_NUMBER = 0.0000001;
	
	public static ProbabilityDistribution respond(ProbabilityDistribution pd, 
			PricingPolicy policy, 
			PricingPolicy baseline, 
			double awareness, 
			double sensitivity, 
			String responseType) {
			double w = 2;
			double[] previousHist = pd.getHistogram();
			double[] newHist = new double[Constants.MIN_IN_DAY];
			double[] policyArr = policy.getTOUArray();
			double[] baseArr = baseline.getTOUArray();
			switch(responseType) {
				case "None":
					return pd;
				case "Optimal":
					newHist = shiftingOptimal(previousHist, baseArr, policyArr, w * awareness, w * sensitivity);
					break;
				case "Normal":
					newHist = shiftingNormal(previousHist, baseArr, policyArr, w * awareness, w * sensitivity);
					break;
				case "Discrete":
					newHist = shiftingDiscrete(previousHist, baseArr, policyArr, w * awareness, w * sensitivity);
					break;
				case "Daily":
					newHist = shiftingDaily(previousHist, baseArr, policyArr, awareness, sensitivity);
					// Clean up NaNs
					for(int i = 0; i < newHist.length; i++) {
						if(Double.isNaN(newHist[i])) {
							newHist[i] = 0.0;
						}
					}
					break;
				default:
					return pd;
			}
			ProbabilityDistribution retPd = new Histogram(newHist);
			return retPd;
	}
	
	
	public static double[] shiftingNormal (double[] values, 
			double[] basicScheme, double[] newScheme,
			double awareness, double sensitivity) {
		double[] result = Arrays.copyOf(values, values.length);
		PricingVector pricingVector = new PricingVector(basicScheme, newScheme);
		pricingVector.show();
		IncentiveVector inc = new IncentiveVector(basicScheme, newScheme);
		if (pricingVector.getPricings().size() > 1)
			for (Incentive incentive: inc.getIncentives())
				result = movingAverage(result, incentive, awareness, sensitivity);
		return result;
	}

	  
	public static double[] shiftingDiscrete (double[] values, 
			double[] basicScheme, double[] newScheme,
			double awareness, double sensitivity) {
		double[] result = Arrays.copyOf(values, values.length);
		PricingVector pricingVector = new PricingVector(basicScheme, newScheme);
		if (pricingVector.getPricings().size() > 1)
			result = discreteAverage(result, pricingVector, awareness, sensitivity);
		return result;
	}
	
	public static double[] shiftingOptimal (double[] values, 
			double[] basicScheme, double[] newScheme,
			double awareness, double sensitivity) {
		double[] result = Arrays.copyOf(values, values.length);
	    PricingVector pricingVector = new PricingVector(basicScheme, newScheme);
	    if (pricingVector.getPricings().size() > 1)
	      result = discreteOptimal(result, pricingVector, awareness, sensitivity);
	    return result;
	}
	
	public static double[] movingAverage (double[] values, Incentive incentive,
			double awareness, double sensitivity) {
		// Initialize the auxiliary variables.
	    int side = -1;
	    int startIndex = incentive.getStartMinute();
	    int endIndex = incentive.getEndMinute();
	    double overDiff = 0, overDiffTemp = 0;
	    double temp = 0;
	    String type = "";

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
	    } else {

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

	    if (!type.equalsIgnoreCase("None")) {
	      // In case of penalty the residual percentage is moved out of the window
	      // to close distance, either on one or both sides accordingly
	      if (incentive.isPenalty()) {

	        for (int i = startIndex; i < endIndex; i++) {

	          temp = incentive.getBase() * values[i] / incentive.getPrice();
	          overDiffTemp = (values[i] - temp) * awareness * sensitivity;
	          overDiff += overDiffTemp;
	          values[i] -= overDiffTemp;

	        }
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
	            overDiffTemp = (values[index] - temp) * awareness * sensitivity;
	            overDiff += overDiffTemp;
	            values[index] -= overDiffTemp;

	          }

	          for (int i = endIndex; i < endIndex + side; i++) {

	            int index = i;

	            if (index > Constants.MIN_IN_DAY - 1)
	              index %= Constants.MIN_IN_DAY;

	            temp = incentive.getPrice() * values[index] / incentive.getBase();
	            overDiffTemp = (values[index] - temp) * awareness * sensitivity;
	            overDiff += overDiffTemp;
	            values[index] -= overDiffTemp;

	          }
	          break;

	        case "Left":

	          for (int i = startIndex - 2 * side; i < startIndex; i++) {

	            int index = i;

	            if (index < 0)
	              index += Constants.MIN_IN_DAY;

	            temp = incentive.getPrice() * values[index] / incentive.getBase();
	            overDiffTemp = (values[index] - temp) * awareness * sensitivity;
	            overDiff += overDiffTemp;
	            values[index] -= overDiffTemp;

	          }
	          break;

	        case "Right":

	          for (int i = endIndex; i < endIndex + 2 * side; i++) {

	            int index = i;

	            if (index > Constants.MIN_IN_DAY - 1)
	              index %= Constants.MIN_IN_DAY;

	            temp = incentive.getPrice() * values[index] / incentive.getBase();
	            overDiffTemp = (values[index] - temp) * awareness * sensitivity;
	            overDiff += overDiffTemp;
	            values[index] -= overDiffTemp;

	          }

	        }

	        double additive = overDiff / (endIndex - startIndex);

	        for (int i = startIndex; i < endIndex; i++)
	          values[i] += additive;

	      }

	    }

	    return values;
  }

  
  public static double[] discreteOptimal (double[] values, PricingVector pricing,
		  double awareness, double sensitivity) {
	// Initialize the auxiliary variables.
	    double temp = 0, additive = 0, overDiff = 0, overDiffTemp = 0, sum = 0;
	    Pricing tempPricing;
	    // double sum = 0;

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

	          temp = ((previousPrice * values[i]) / newPrice);
	          // System.out.println("Temp = " + temp);
	          overDiffTemp = (values[i] - temp) * awareness * sensitivity;
	          overDiff += overDiffTemp;
	          values[i] -= overDiffTemp;
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
	            overDiffTemp = (values[j] - temp) * awareness * sensitivity;
	            overDiff += overDiffTemp;
	            values[j] -= overDiffTemp;

	          }

	          // System.out.println("OverDiff for index " + index + ": " +
	          // overDiff);

	          additive = overDiff / duration;
	          // System.out.println("Additive for index " + i + ": " + additive);

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

  
  	public static double[] discreteAverage (double[] values, 
  			PricingVector pricing,
  			double awareness, double sensitivity) {

	    // Initialize the auxiliary variables.
	    double temp = 0;
	    // double sum = 0;
	    double overDiff = 0, overDiffTemp = 0;
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
	          overDiffTemp = (values[j] - temp) * awareness * sensitivity;
	          overDiff += overDiffTemp;
	          values[j] -= overDiffTemp;

	        }

	        double additive = overDiff / durationCheapest;

	        for (int j = startCheapest; j <= endCheapest; j++)
	          values[j] += additive;

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

	
	public static double estimateEnergyRatio (double[] basicScheme, double[] newScheme) {
		double baseEnergy = 0;
		double newEnergy = 0;
		for (int i = 0; i < basicScheme.length; i++) {
			baseEnergy += basicScheme[i];
			newEnergy += newScheme[i];
		}
		double energyRatio = newEnergy / baseEnergy;
		return energyRatio;
	}
	
	// Daily times
	public static double[] shiftingDaily (double values[], double[] basicScheme, double[] newScheme, 
			double awareness, double sensitivity) {
		double energyRatio = estimateEnergyRatio(basicScheme, newScheme);
		return shiftingDailyPreview(values, energyRatio, awareness, sensitivity);
	}

	public static double[] shiftingDailyPreview (double values[], double energyRatio, double awareness, double sensitivity) {
		double[] temp = Arrays.copyOf(values, values.length);
		double diff = (energyRatio - 1) * (awareness * sensitivity);
		double[] result;
		if (diff > 0){
			if (diff > 1){
			 result = new double[1];
			 result[0] = 1;
			}
			else
			result = reduceUse(temp, diff);
		}
		else
			result = increaseUse(temp, Math.abs(diff));
		return result;
	}

	 private static double[] reduceUse (double[] result, double diff)
	  {

	    int index = result.length - 1;
	    double diffTemp = diff;
	    double sum = 0;

//	    System.out.println("Before:" + Arrays.toString(result));

	    while (diffTemp > 0) {

	      double reduction = Math.min(result[index], diffTemp);

	      result[index] -= reduction;
	      diffTemp -= reduction;

	      // System.out.println("Index: " + index + " Reduction: " + reduction
	      // + " DiffTemp: " + diffTemp);

	      index--;

	    }

	    // Fixes out of bounds error
	    index = Math.max(0, index);

	    // System.out.println("After:" + Arrays.toString(result));

	    for (int i = 0; i <= index; i++)
	      sum += result[i];

	    for (int i = 0; i <= index; i++)
	      result[i] += (result[i] / (sum + SMALL_NUMBER)) * diff;

	    sum = 0;

	    for (int i = 0; i < result.length; i++)
	      sum += result[i];

	    result[0] += (1 - sum);

	    // System.out.println("After Normalization:" + Arrays.toString(result));
	    //
	    // System.out.println("Summary: " + sum);

	    index = result.length - 1;

	    while (result[index] == 0)
	      index--;

	    return Arrays.copyOfRange(result, 0, index + 1);

	  }

	 private static double[] increaseUse (double[] result, double diff)
	  {

	    int index = 0;
	    double diffTemp = diff;
	    double sum = 0;

	    // System.out.println("Before:" + Arrays.toString(result));

	    while (diffTemp > 0) {

	      double reduction = Math.min(result[index], diffTemp);

	      result[index] -= reduction;
	      diffTemp -= reduction;

	      // System.out.println("Index: " + index + " Reduction: " + reduction
	      // + " DiffTemp: " + diffTemp);

	      index++;

	    }

	    index = Math.min(index, result.length - 1);

	    // System.out.println("After:" + Arrays.toString(result));

	    for (int i = index; i < result.length; i++)
	      sum += result[i];

	    for (int i = index; i < result.length; i++)
	      result[i] += (result[i] / (sum + SMALL_NUMBER)) * diff;

	    sum = 0;

	    for (int i = 0; i < result.length; i++)
	      sum += result[i];

	    result[result.length - 1] += (1 - sum);

	    // System.out.println("After Normalization:" + Arrays.toString(result));
	    //
	    // System.out.println("Summary: " + sum);

	    return Arrays.copyOf(result, result.length);
	  }
	 
	 public static void main(String[] args) throws UnknownHostException, MongoException, ParseException {
		    Gaussian g = new Gaussian(840, 100);
		    g.precompute(0, 1439, 1440);
		    System.out.println(Arrays.toString(g.getHistogram()));
		    String prc_id = "52aa0f7f712edbccc313a1b3";
			DBObject query = new BasicDBObject(); // A query
			query.put("_id", new ObjectId(prc_id));
			Mongo m = new Mongo("cassandra.iti.gr");
			DB db = m.getDB("test");
			DBObject pricingPolicy = db.getCollection(MongoPricingPolicy.COL_PRICING).findOne(query);
			PricingPolicy pp1 = new PricingPolicy(pricingPolicy);
			System.out.println(pp1.getTOUArray().length);
			System.out.println(Arrays.toString(pp1.getTOUArray()));
			prc_id = "52aa161b712edbccc31438f2";
			query = new BasicDBObject(); // A query
			query.put("_id", new ObjectId(prc_id));
			m = new Mongo("cassandra.iti.gr");
			db = m.getDB("test");
			pricingPolicy = db.getCollection(MongoPricingPolicy.COL_PRICING).findOne(query);
			PricingPolicy pp2 = new PricingPolicy(pricingPolicy);
			System.out.println(Arrays.toString(pp2.getTOUArray()));
			System.out.println(pp2.getTOUArray().length);
			System.out.println(Arrays.toString(respond(g, pp2, pp1, 1, 1, "Normal").getHistogram()));
	 }
	 
	 

}

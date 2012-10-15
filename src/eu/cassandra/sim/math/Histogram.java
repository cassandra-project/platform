package eu.cassandra.sim.math;

import eu.cassandra.sim.utilities.RNG;

public class Histogram implements ProbabilityDistribution{

	protected int numberOfBins;
	  protected double precomputeFrom;
	  protected double precomputeTo;
	  protected double[] histogram;
	  protected boolean precomputed;
	
	public Histogram(int size){
		
		precomputeFrom = 0;
		precomputeTo = size;
		numberOfBins = size;
		histogram = new double[size];
		precomputed = false;
	}
	
	/**
	  * Constructor. Takes a set of values and put them in the histogram.
	  */
	Histogram(double[] values){
		
		precomputeFrom = 0;
		precomputeTo = values.length;
		numberOfBins = values.length;
		histogram = values;
		precomputed = true;
		
	}

	@Override
	public String getDescription() {
		String description = "Histogram Frequency Probability Density function";
	    return description;
	}

	@Override
	public int getNumberOfParameters() {
		return 1;
	}

	@Override
	public double getParameter(int index) {
		return numberOfBins;
	}

	@Override
	public void setParameter(int index, double value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void precompute(double startValue, double endValue, int nBins) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void precompute(int endValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getProbability(double x) {
		return histogram[(int)x];
	}

	@Override
	public double getPrecomputedProbability(double x) {
		return histogram[(int)x];
	}

	@Override
	public int getPrecomputedBin ()
	  {
	    if (!precomputed) {
	      return -1;
	    }
	    // double div = (precomputeTo - precomputeFrom) / (double) numberOfBins;
	    double dice = RNG.nextDouble();
	    double sum = 0;
	    for (int i = 0; i < numberOfBins; i++) {
	      sum += histogram[i];
	      // if(dice < sum) return (int)(precomputeFrom + i * div);
	      if (dice < sum)
	        return i;
	    }
	    return -1;
	  }

	@Override
	public double[] getHistogram() {
		
		return histogram;
	}

	@Override
	public void status() {
		System.out.print("Histogram");
	    System.out.print(" Number Of Bins: " + getParameter(0));
	    if (precomputed) {
	      System.out.print(" Starting Point: " + precomputeFrom);
	      System.out.println(" Ending Point: " + precomputeTo);
	    }
	    
	    for (int i = 0; i < histogram.length;i++){
	    	System.out.println("Index: " + i + " Value: " + histogram[i]);
	    }
	    System.out.println();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
}

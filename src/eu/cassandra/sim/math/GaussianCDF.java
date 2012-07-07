package eu.cassandra.sim.math;

/**
 * @author Christos Diou <diou remove this at iti dot gr>
 * @version prelim
 * @since 2012-22-01
 */
public class GaussianCDF extends Gaussian implements ProbabilityDistribution {

    /**
     * Constructor. Sets the parameters of the standard normal
     * distribution, with mean 0 and standard deviation 1.
     */
    public GaussianCDF() {
    	super();
    }

    /**
     * @param mu Mean value of the Gaussian distribution.
     * @param s Standard deviation of the Gaussian distribution.
     */
    public GaussianCDF(double mu, double s) {
    	super(mu,s);
    }

    public String getDescription() {
        return "Gaussian cumulative probability density function";
    }

    public void precompute(double startValue, double endValue, int nBins) {
        if (startValue >= endValue) {
            // TODO Throw an exception or whatever.
            return;
        }
        precomputeFrom = startValue;
        precomputeTo = endValue;
        numberOfBins = nBins;

        double div = (endValue - startValue) / (double) nBins;
        histogram = new double[nBins];

        for (int i = 0; i < nBins; i ++) {
            double x = startValue + i * div;
            // Value of bin is the probability at the beginning of the
            // value range.
            histogram[i] = bigPhi(x, mean, sigma);
        }
        precomputed = true;
    }

    public double getProbability(double x) {
        return bigPhi(x, mean, sigma);
    }

}

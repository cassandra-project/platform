package eu.cassandra.sim.utilities;

import java.util.Random;

/**
 * Wrapper class around java.util.Random for easier universal handling and 
 * access.
 * 
 * @author Cassandra developers
 *
 */
public class RNG {
	
	private static Random random;
	
	private RNG() { }
	
	public static void init() {
		random = new Random();
		random.setSeed(System.currentTimeMillis());
		random.nextDouble();
		random.nextDouble();
		random.nextDouble();
		random.nextDouble();
		random.nextDouble();
	}
	
	public static void setSeed(long seed) {
		random.setSeed(seed);
	}
	
	public static int nextInt() {
		return random.nextInt();
	}
	
	public static int nextInt(int n) {
		return random.nextInt(n);
	}
	
	public static double nextDouble() {
		return random.nextDouble();
	}
	
	public static double nextDoublePlusMinus() {
		return 2 * random.nextDouble() - 1;
	}
	
}

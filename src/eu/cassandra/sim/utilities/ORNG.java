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

package eu.cassandra.sim.utilities;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Wrapper class around java.util.Random for easier universal handling and 
 * access.
 * 
 * @author Cassandra developers
 *
 */
public class ORNG {
	
	private Random random;
	
	public ORNG() { 
		random = new Random();
		random.setSeed(System.currentTimeMillis());
		random.nextDouble();
		random.nextDouble();
		random.nextDouble();
		random.nextDouble();
		random.nextDouble();
	}
	
	public ORNG(long seed) { 
		random = new Random();
		random.setSeed(seed);
		random.nextDouble();
		random.nextDouble();
		random.nextDouble();
		random.nextDouble();
		random.nextDouble();
	}
	
	public void setSeed(long seed) {
		random.setSeed(seed);
	}
	
	public int nextInt() {
		return random.nextInt();
	}
	
	public int nextInt(int n) {
		return random.nextInt(n);
	}
	
	public long nextLong() {
		return random.nextLong();
	}
	
	public double nextDouble() {
		return random.nextDouble();
	}
	
	public float nextFloat() {
		return random.nextFloat();
	}
	
	public double nextDoublePlusMinus() {
		return 2 * random.nextDouble() - 1;
	}
	
	public static void main(String[] args) {
		ORNG orng = new ORNG();
		System.out.println(orng.nextLong());
		System.out.println(orng.nextInt());
		System.out.println(orng.randomString());
	}
	
	public String randomString() {
		return new BigInteger(130, random).toString(32);
	}

}

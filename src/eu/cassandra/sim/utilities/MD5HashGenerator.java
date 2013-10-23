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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5HashGenerator {
	
	public static String generateMd5Hash(String password, String salt) throws NoSuchAlgorithmException {
		MessageDigest m = MessageDigest.getInstance("MD5");
		m.update((password + salt).getBytes(), 0, (password + salt).length());
		return new BigInteger(1, m.digest()).toString(16);
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		if(args.length != 2) {
			System.out.println("Needs two arguments. Password and salt (userid)!");
			System.exit(1);
		}
		System.out.println(generateMd5Hash(args[0], args[1]));
	}

}

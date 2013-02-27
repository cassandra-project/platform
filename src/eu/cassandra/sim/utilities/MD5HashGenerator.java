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
			System.out.println("Needs two arguments.");
			System.exit(1);
		}
		System.out.println(generateMd5Hash(args[0], args[1]));
	}

}

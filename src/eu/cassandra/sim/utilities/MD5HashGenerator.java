package eu.cassandra.sim.utilities;

import java.math.BigInteger;
import java.security.MessageDigest;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5HashGenerator {
	
	public static String generateMd5Hash(String password, String salt) {
		MessageDigest m = DigestUtils.getMd5Digest();
		m.update((password + salt).getBytes(), 0, (password + salt).length());
		return new BigInteger(1, m.digest()).toString(16);
	}
	
	public static void main(String[] args) {
		System.out.println(generateMd5Hash(args[0], args[1]));
	}

}

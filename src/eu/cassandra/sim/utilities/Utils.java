package eu.cassandra.sim.utilities;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

	public static String hashcode(String message) {
		String hash = null;
		try {
			MessageDigest cript = MessageDigest.getInstance("SHA-1");
			cript.reset();
			cript.update(message.getBytes("utf8"));
			hash = new BigInteger(1, cript.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hash;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(hashcode((new Long(System.currentTimeMillis()).toString())));
	}

}

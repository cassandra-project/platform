package eu.cassandra.sim.utilities;

import java.io.FileNotFoundException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;
import java.util.Vector;

/**
 * The class Utils provides static methods for helping with various chores. In
 * particular:
 * <li>
 * <ul>Read values and arrays of values from files that follow a Java Properties
 * format.<ul>
 * </li>
 * 
 * @author Cassandra developers
 *
 */
public abstract class FileUtils {
	
	public static Properties loadProperties(String filename) {
		Properties props = new Properties();
		try {
			Reader r = new FileReader(filename);
			props.load(r);
			r.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}
	
	public static boolean getBool(String propsFile, String key) {
		return getBool(loadProperties(propsFile), key);
	}
	
	public static boolean getBool(Properties props, String key) {
		String s = props.getProperty(key);
		return Boolean.parseBoolean(s);
	}
	
	public static double getDouble(String propsFile, String key) {
		return getDouble(loadProperties(propsFile), key);
	}
	
	public static double getDouble(Properties props, String key) {
		String s = props.getProperty(key);
		return Double.parseDouble(s);
	}
	
	public static double[] getDoubleArray(String propsFile, String key) {
		return getDoubleArray(loadProperties(propsFile), key);
	}
	
	public static double[] getDoubleArray(Properties props, String key) {
		String s = props.getProperty(key);
		if(s.equals(null)) return null;
		String[] tokens = s.split("\\,");
		int length = tokens.length;
		double[] arr = new double[length];
		for(int i = 0; i < length; i++) {
			arr[i] = Double.parseDouble(tokens[i]);
		}
		return arr;
	}
	
	public static float getFloat(String propsFile, String key) {
		return getFloat(loadProperties(propsFile), key);
	}
	
	public static float getFloat(Properties props, String key) {
		String s = props.getProperty(key);
		return Float.parseFloat(s);
	}
	
	public static float[] getFloatArray(String propsFile, String key) {
		return getFloatArray(loadProperties(propsFile), key);
	}
	
	public static float[] getFloatArray(Properties props, String key) {
		String s = props.getProperty(key);
		if(s.equals(null)) return null;
		String[] tokens = s.split("\\,");
		int length = tokens.length;
		float[] arr = new float[length];
		for(int i = 0; i < length; i++) {
			arr[i] = Float.parseFloat(tokens[i]);
		}
		return arr;
	}
	
	public static int getInt(String propsFile, String key) {
		return getInt(loadProperties(propsFile), key);
	}
	
	public static int getInt(Properties props, String key) {
		String s = props.getProperty(key);
		return Integer.parseInt(s);
	}
	
	public static int[] getIntArray(String propsFile, String key) {
		return getIntArray(loadProperties(propsFile), key);
	}
	
	public static int[] getIntArray(Properties props, String key) {
		String s = props.getProperty(key);
		if(s.equals(null)) return null;
		String[] tokens = s.split("\\,");
		int length = tokens.length;
		int[] arr = new int[length];
		for(int i = 0; i < length; i++) {
			arr[i] = Integer.parseInt(tokens[i]);
		}
		return arr;
	}
	
	public static String[] getStringArray(String propsFile, String key) {
		return getStringArray(loadProperties(propsFile), key);
	}
	
	public static String[] getStringArray(Properties props, String key) {
		String s = props.getProperty(key);
		if(s.equals(null)) return null;
		String[] arr = s.split("\\,");
		return arr;
	}
	
	public static String getString(String propsFile, String key) {
		return getString(loadProperties(propsFile), key);
	}
	
	public static String getString(Properties props, String key) {
		String s = props.getProperty(key);
		return s;
	}
	
	/**
	 * Create folder structure
	 * 
	 * @param folder
	 * 
	 * TODO Create a directory if it does not exist.
	 */
	public static void createFolderStucture(File folder) {
		File tempFolder = new File(folder.getPath());
		Vector<File> folders = 	new Vector<File>();
		while(tempFolder.getParentFile() != null && 
				tempFolder.getParent() != tempFolder.getPath()) {
			folders.add(tempFolder);
			tempFolder = tempFolder.getParentFile();
		}
		if(folders != null && folders.size() > 0) {
			if(!folders.get(0).getParentFile().exists())
				folders.get(0).getParentFile().mkdir();
		}
		for(int i = folders.size()-1; i>=0; i--) {
			if(!folders.get(i).exists())
				folders.get(i).mkdir();
		}
	}
	
	public static void main(String[] args) {
		Properties props = loadProperties("props/appliances.props");
		System.out.println(props.getProperty("appliances"));
	}

}

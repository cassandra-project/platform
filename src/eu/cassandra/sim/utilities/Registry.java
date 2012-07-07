package eu.cassandra.sim.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Stores float numbers in an array along with some statistical capabilities.
 * 
 * @author Cassandra developers
 * @version prelim
 */
public class Registry {

	private String name;

	private float[] values;

	public Registry(String aName, int size) {
		name = aName;
		values = new float[size];
	}

	public String getName() {
		return name;
	}

	public void setName(String s) {
		name = s;
	}

	public float getValue(int tick) {
		return values[tick];
	}
	
	public float[] getValues() {
		return values;
	}

	public void setValue(int tick, float value) {
		values[tick] = value;
	}

	public double getMean(int startTick, int endTick) {
		double mean = 0.0;
		for (int i = startTick; i <= endTick; i ++) {
			mean += values[i];
		}
		mean /= (endTick - startTick + 1);
		return mean;
	}

	public double getVariance(int startTick, int endTick) {
		double var = 0.0;
		double mean = getMean(startTick, endTick);
		for (int i = startTick; i <= endTick; i ++) {
			var += (values[i] - mean) * (values[i] - mean);
		}
		var /= (endTick - startTick + 1);
		return var;
	}
	
	public void saveRegistry(File parrentFolder) {
		try {
			File file = new File(parrentFolder.getPath() + "/" + 
					getName() + ".csv");
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufWriter = new BufferedWriter(fileWriter);
			for(int i =0; i< values.length; i++) {
				bufWriter.write(i + "," + values[i]);
				bufWriter.newLine();
			}
			bufWriter.flush();
			bufWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
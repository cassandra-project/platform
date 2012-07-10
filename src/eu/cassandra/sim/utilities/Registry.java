/*   
   Copyright 2011-2012 The Cassandra Consortium (cassandra-fp7.eu)

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

	public double getMean() {
		 return getMean(0, values.length-1);
	}
	
	public double getMean(int startTick, int endTick) {
		double mean = getSum(startTick, endTick);
		mean /= (endTick - startTick + 1);
		return mean;
	}

	public double getSum() {
		return getSum(0, values.length-1);
	}
	
	public double getSumKWh() {
		return getSumKWh(0, values.length-1);
	}

	public double getSum(int startTick, int endTick) {
		double sum = 0.0;
		for (int i = startTick; i <= endTick; i ++) {
			sum += values[i];
		}
		return sum;
	}
	
	public double getSumKWh(int startTick, int endTick) {
		double sum = 0.0;
		for (int i = startTick; i <= endTick; i ++) {
			sum += values[i];
		}
		return sum/60000.0;
	}

	public double getVariance() {
		return getVariance(0, values.length-1);
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
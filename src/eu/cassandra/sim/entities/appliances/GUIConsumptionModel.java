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
package eu.cassandra.sim.entities.appliances;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.server.api.exceptions.BadParameterException;
import eu.cassandra.sim.entities.appliances.ConsumptionModel.Tripplet;
import eu.cassandra.sim.utilities.Utils;

public class GUIConsumptionModel {
	
	public static final int P = 0;
	public static final int Q = 1;

  private ConsumptionModel cons = new ConsumptionModel();

  /**
   * 	
   * @param obj
 * @throws BadParameterException 
   */
  public GUIConsumptionModel (DBObject obj, String type) throws BadParameterException
  {
    cons.init(obj, type);
    cons.status();
  }

  /**
   * 
   * @return
   */
  public Double[] getValues(int type)
  {
    ArrayList<Double> temp = new ArrayList<Double>();
    int times = cons.getOuterN();
    if (times == 0)
      times = 2;
    // Number of repeats
    for (int i = 0; i < times; i++) {
      // System.out.println("Time: " + i);
      // Number of patterns in each repeat
      for (int j = 0; j < cons.getPatternN(); j++) {
        // System.out.println("Pattern: " + j);
        int internalTimes = cons.getN(j);
        if (internalTimes == 0)
          internalTimes = 2;
        // System.out.println("Internal Times: " + k);
        for (int k = 0; k < internalTimes; k++) {
          ArrayList<Tripplet> tripplets = cons.getPattern(j);
          for (int l = 0; l < tripplets.size(); l++) {
            // System.out.println("Tripplet: " + l);
            for (int m = 0; m < tripplets.get(l).d; m++) {
            	if(type == Q) {
            		temp.add(tripplets.get(l).v);
            	} else {
            		temp.add(tripplets.get(l).v);
            	}
            }
          }
        }
      }
    }
    ArrayList<Double> values = new ArrayList<Double>();
    for(int i = 0; i < temp.size(); i++) {
    	values.add(temp.get(i));
    	values.add(temp.get(i));
    	values.add(temp.get(Math.min(i+1, temp.size()-1)));
    }
    Double[] result = new Double[values.size()];
    values.toArray(result);
    return result;
  }
  
  public Double[] getPoints(int length)
  {
    ArrayList<Double> temp = new ArrayList<Double>();
    int count = 0;
    for(int i = 0; i < length; i += 3) {
    	temp.add(new Double(count-0.01));
    	temp.add(new Double(count));
    	temp.add(new Double(count+0.01));
    	count++;
    }
    Double[] result = new Double[temp.size()];
    temp.toArray(result);
    return result;
  }

  public static void main (String[] args) throws IOException, BadParameterException
  {

    String s = Utils.readFile("laptopcm.json");
    DBObject dbo = (DBObject) JSON.parse(s);
    System.out.println(dbo.toString());

    GUIConsumptionModel tester = new GUIConsumptionModel(dbo, "p");
    System.out.println(Arrays.toString(tester.getValues(P)));
    // Utils.createHistogram("Test", "Power", "Power", tester.getValues());

  }
}

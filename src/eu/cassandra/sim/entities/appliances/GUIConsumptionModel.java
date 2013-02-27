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

import eu.cassandra.sim.entities.appliances.ConsumptionModel.Tripplet;
import eu.cassandra.sim.utilities.Utils;

public class GUIConsumptionModel
{

  private ConsumptionModel cons = new ConsumptionModel();

  /**
   * 
   * @param obj
   */
  public GUIConsumptionModel (DBObject obj)
  {
    cons.init(obj);
    cons.status();
  }

  /**
   * 
   * @return
   */
  public Double[] getValues ()
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
              temp.add(tripplets.get(l).p);
            }
          }
        }
      }
    }
    Double[] result = new Double[temp.size()];
    temp.toArray(result);
    return result;

  }

  public static void main (String[] args) throws IOException
  {

    String s = Utils.readFile("laptopcm.json");
    DBObject dbo = (DBObject) JSON.parse(s);
    System.out.println(dbo.toString());

    GUIConsumptionModel tester = new GUIConsumptionModel(dbo);
    System.out.println(Arrays.toString(tester.getValues()));
    // Utils.createHistogram("Test", "Power", "Power", tester.getValues());

  }
}

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

package eu.cassandra.sim;

import java.text.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Simulation class can simulate up to 4085 years of simulation.
 * 
 * @author Kyriakos C. Chatzidimitriou (kyrcha [at] iti [dot] gr)
 * 
 */
public class SimulationWorld
{

  private SimCalendar simCalendar;

  public SimulationWorld ()
  {

    simCalendar = new SimCalendar();

  }

  public SimulationWorld (String parametersFile) throws ParseException
  {

    // TODO Read the correct parameter file and fill the variables //
//    String date = FileUtils.getString(parametersFile, "date");
//
//    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
//    Date temp = df.parse(date);
//    String granularity = FileUtils.getString(parametersFile, "granularity");
//    int granularityValue = FileUtils.getInt(parametersFile, "granularityValue");
//    simCalendar = new SimCalendar(temp, granularity, granularityValue);

  }

  public SimCalendar getSimCalendar ()
  {
    return simCalendar;
  }

}

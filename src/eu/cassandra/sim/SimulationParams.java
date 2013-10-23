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

package eu.cassandra.sim;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.sim.utilities.Utils;

/**
 * The Simulation class can simulate up to 4085 years of simulation.
 * 
 * @author Kyriakos C. Chatzidimitriou (kyrcha [at] iti [dot] gr)
 * 
 */
public class SimulationParams
{

  private SimCalendar simCalendar;
  private String name;
  private String locationInfo;
  private String responseType;

  public SimulationParams ()
  {

    simCalendar = new SimCalendar();

  }

  public SimulationParams (DBObject dbo) throws ParseException
  {

	  responseType = dbo.get("responseType").toString();
	  
    int day;
    int month;
    int year;

    name = dbo.get("name").toString();
    locationInfo = dbo.get("locationInfo").toString();
    int duration = Integer.parseInt(dbo.get("numberOfDays").toString());

    DBObject tempList = (DBObject) dbo.get("calendar");

    if (tempList == null) {
      simCalendar = new SimCalendar();
      day = simCalendar.getMyCalendar().get(Calendar.DAY_OF_MONTH);
      month = simCalendar.getMyCalendar().get(Calendar.MONTH);
      year = simCalendar.getMyCalendar().get(Calendar.YEAR);

    }
    else {
      day = ((Integer)tempList.get("dayOfMonth")).intValue();
      month = ((Integer)tempList.get("month")).intValue();
      year = ((Integer)tempList.get("year")).intValue();
    }
    simCalendar = new SimCalendar(day, month, year, duration);

  }

  public SimCalendar getSimCalendar ()
  {
    return simCalendar;
  }

  public String getName ()
  {
    return name;
  }

  public String getResponseType ()
  {
    return responseType;
  }
  
  public String getLocationInfo ()
  {
    return locationInfo;
  }

  /**
   * @param args
   * @throws IOException
   * @throws ParseException
   */
  public static void main (String[] args) throws IOException, ParseException
  {
    String s = Utils.readFile("simparam.json");

    DBObject obj = (DBObject) JSON.parse(s);

    SimulationParams sp = new SimulationParams(obj);

    System.out.println("Name:" + sp.getName());
    System.out.println("Location Info:" + sp.getLocationInfo());
    System.out.println("SimCalendar:" + sp.getSimCalendar().toString());
  }

}

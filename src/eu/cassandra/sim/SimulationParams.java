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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;


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

  public SimulationParams ()
  {

    simCalendar = new SimCalendar();

  }

  public SimulationParams (DBObject dbo) throws ParseException
  {


	  name = dbo.get("name").toString();  
	  locationInfo = dbo.get("locationInfo").toString();  
	  int duration = Integer.parseInt(dbo.get("numberOfDay").toString());  
	  
	  BasicDBObject tempList = (BasicDBObject)dbo.get("calendar");

	  int day = tempList.getInt("dayOfMonth");
	  int month = tempList.getInt("month");
	  int year = tempList.getInt("year");
	  
	  simCalendar = new SimCalendar(day,month,year,duration);
	  
  }

  public SimCalendar getSimCalendar ()
  {
    return simCalendar;
  }
  
  public String getName ()
  {
    return name;
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
	public static void main(String[] args) throws IOException, ParseException {
		String s = readFile("simparam.json");

		DBObject obj = (DBObject)JSON.parse(s); 

		SimulationParams sp = new SimulationParams(obj);
		
		System.out.println("Name:" + sp.getName());
		System.out.println("Location Info:" + sp.getLocationInfo());
		System.out.println("SimCalendar:" + sp.getSimCalendar().toString());
	}


	private static String readFile( String file ) throws IOException {
		BufferedReader reader = new BufferedReader( new FileReader (file));
		String         line = null;
		StringBuilder  stringBuilder = new StringBuilder();
		String         ls = System.getProperty("line.separator");

		while( ( line = reader.readLine() ) != null ) {
			stringBuilder.append( line );
			stringBuilder.append( ls );
		}

		return stringBuilder.toString();
	}
}

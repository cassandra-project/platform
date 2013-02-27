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
package eu.cassandra.sim.entities.installations;

import org.apache.log4j.Logger;

public class GeoLocation
{
  static Logger logger = Logger.getLogger(GeoLocation.class);

  private double latitude;
  private double longitude;
  private String id;

  GeoLocation ()
  {
    id = null;
    longitude = 0;
    latitude = 0;

  }

  GeoLocation (Installation installation, double latitude, double longitude)
  {
    id = installation.getId();
    this.longitude = longitude;
    this.latitude = latitude;
  }

  public double getLongitude ()
  {

    return longitude;

  }

  public double getLatitude ()
  {

    return latitude;

  }

  @Override
  public boolean equals (Object o)
  {
    if (this == o)
      return true;
    if (!(o instanceof GeoLocation))
      return false;

    GeoLocation that = (GeoLocation) o;

    if (Double.compare(that.getLatitude(), latitude) != 0)
      return false;
    if (Double.compare(that.getLongitude(), longitude) != 0)
      return false;

    return true;
  }

  @Override
  public String toString ()
  {

    String temp =
      "Geolocation: Latitude " + latitude + " Longitude " + longitude;

    return temp;

  }

}

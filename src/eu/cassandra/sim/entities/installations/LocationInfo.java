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

public class LocationInfo
{

  static Logger logger = Logger.getLogger(LocationInfo.class);

  private final String id;
  private final String name;
  private GeoLocation location;

  public static class Builder
  {
    // Required variables
    private final String id;
    private final String name;
    // Optional or state related variables
    private GeoLocation location = null;

    public Builder (Installation installation, String aname)
    {
      id = installation.getId();
      name = aname;
    }

    public Builder geoLocation (GeoLocation location)
    {
      this.location = location;
      return this;
    }

    public LocationInfo build ()
    {
      return new LocationInfo(this);
    }

  }

  private LocationInfo (Builder builder)
  {
    id = builder.id;
    name = builder.name;
    location = builder.location;
  }

  public String getId ()
  {
    return id;
  }

  public String getName ()
  {
    return name;
  }

  public GeoLocation getLocation ()
  {
    return location;
  }

  public void setLocation (GeoLocation location)
  {

    this.location = location;

  }

  @Override
  public String toString ()
  {

    return location.toString();

  }

}

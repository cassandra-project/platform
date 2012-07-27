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

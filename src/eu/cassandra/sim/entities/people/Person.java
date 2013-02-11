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
package eu.cassandra.sim.entities.people;

import java.util.Vector;
import java.util.concurrent.PriorityBlockingQueue;

import com.mongodb.BasicDBObject;

import eu.cassandra.server.mongo.MongoPersons;
import eu.cassandra.sim.Event;
import eu.cassandra.sim.entities.Entity;
import eu.cassandra.sim.entities.installations.Installation;

public class Person extends Entity {

  private final Installation house;
  private Vector<Activity> activities;

  public static class Builder
  {
    // Required parameters
    private final String id;
    private final String name;
    private final String description;
    private final String type;
    private final Installation house;
    // Optional parameters: not available
    private Vector<Activity> activities = new Vector<Activity>();

    public Builder (String aid, String aname, String desc, String atype, Installation ahouse)
    {
      id = aid;
      name = aname;
      description = desc;
      type = atype;
      house = ahouse;
    }

    public Person build ()
    {
      return new Person(this);
    }
  }

  private Person (Builder builder)
  {
    id = builder.id;
    name = builder.name;
    description = builder.description;
    type = builder.type;

    house = builder.house;
    activities = builder.activities;
  }

  	public void addActivity(Activity a) {
  		//System.out.println(a.getName());
  		activities.add(a);
  	}
  
    public void updateDailySchedule(int tick, PriorityBlockingQueue<Event> queue) {
    	for(Activity activity: activities) {
    		//System.out.println(activity.getName());
    		activity.updateDailySchedule(tick, queue);
    	}
    }

  public Installation getInstallation ()
  {
    return house;
  }

  public Vector<Activity> getActivities ()
  {
    return activities;
  }

  	@Override
  	public BasicDBObject toDBObject() {
  		BasicDBObject obj = new BasicDBObject();
  		obj.put("name", name);
  		obj.put("type", type);
  		obj.put("description", description);
  		obj.put("inst_id", parentId);
  		return obj;
	}

	@Override
	public String getCollection() {
		return MongoPersons.COL_PERSONS;
	}
}

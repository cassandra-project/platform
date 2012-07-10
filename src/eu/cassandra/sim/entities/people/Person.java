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

import eu.cassandra.sim.Event;
import eu.cassandra.sim.entities.installations.Installation;

public class Person {
	private static int idCounter = 0;
    private final int id;
    private final String name;
    private final int type;
    private final Installation house;
    private Vector<Activity> activities;
    public static class Builder {
    	// Required parameters
    	private final int id;
    	private final String name;
    	private final int type;
    	private final Installation house;
        // Optional parameters: not available
    	private Vector<Activity> activities = new Vector<Activity>();
        public Builder(String aname, int atype, Installation ahouse) {
        	id = idCounter++;
        	name = aname;
        	type = atype;
        	house = ahouse;
        }
        public Person build() {
        	return new Person(this);
        }
    }
    private Person(Builder builder) {
    	id = builder.id;
    	name = builder.name;
    	type = builder.type;
    	house = builder.house;
    	activities = builder.activities;
	}
    
    public void addActivity(Activity a) {
    	activities.add(a);
    }
    
    public void updateDailySchedule(int tick,  PriorityBlockingQueue<Event> queue) {
    	for(Activity activity : activities) {
    		activity.updateDailySchedule(tick, queue);
		}
    }
    
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public int getType() {
    	return type;
    }

    public Installation getInstallation() {
        return house;
    }

    public Vector<Activity> getActivities() {
        return activities;
    }
}

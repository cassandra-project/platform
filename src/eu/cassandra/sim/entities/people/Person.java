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

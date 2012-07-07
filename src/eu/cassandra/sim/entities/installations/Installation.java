package eu.cassandra.sim.entities.installations;

import java.util.Vector;
import java.util.concurrent.PriorityBlockingQueue;

import eu.cassandra.sim.Event;
import eu.cassandra.sim.entities.appliances.Appliance;
import eu.cassandra.sim.entities.people.Person;
import eu.cassandra.sim.utilities.Registry;

public class Installation {
	private final int id;
    private final String name;
    private Vector<Person> persons;
    private Vector<Appliance> appliances;
    private Registry registry;
    
    public static class Builder {
    	private static int idCounter = 0;
    	// Required variables
    	private final int id;
        private final String name;
        // Optional or state related variables
        private Vector<Person> persons = new Vector<Person>();
        private Vector<Appliance> appliances = new Vector<Appliance>();
        private Registry registry = null;
        public Builder(String aname) {
        	id = idCounter++;
			name = aname;
        }
        public Builder registry(Registry aregistry) {
			registry = aregistry; return this;
		}
		public Installation build() {
			return new Installation(this);
		}
    }
    
    private Installation(Builder builder) {
		id = builder.id;
		name = builder.name;
		persons = builder.persons;
		appliances = builder.appliances;
		registry = builder.registry;
	}
    
    public void updateDailySchedule(int tick,  PriorityBlockingQueue<Event> queue) {
    	for(Person person : getPersons()) {
    		person.updateDailySchedule(tick, queue);
		}
    }

	public void nextStep(int tick) {
		updateRegistry(tick);
	}

	public void updateRegistry(int tick) {
		float power = 0f;
		for(Appliance appliance : getAppliances()) {
			power += appliance.getPower(tick);
		}
		getRegistry().setValue(tick, power);
	}

	public float getPower(int tick) {
		return getRegistry().getValue(tick);
	}
	
    public int getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public Registry getRegistry() {
        return registry;
    }
    
    public Vector<Person> getPersons() {
    	return persons;
    }

    public void addPerson(Person person) {
        persons.add(person);
    }

    public Vector<Appliance> getAppliances() {
        return appliances;
    }

    public void addAppliance(Appliance appliance) {
        this.appliances.add(appliance);
    }
    
    public Appliance applianceExists(String name) {
    	for(Appliance a : appliances) {
    		if(a.getName().equalsIgnoreCase(name)) return a;
    	}
    	return null;
    }
}

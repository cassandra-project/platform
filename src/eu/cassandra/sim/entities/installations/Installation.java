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
package eu.cassandra.sim.entities.installations;

import java.util.Vector;

import java.util.concurrent.PriorityBlockingQueue;

import eu.cassandra.sim.entities.appliances.Appliance;
import eu.cassandra.sim.entities.people.Person;
import eu.cassandra.sim.Event;

public class Installation {
	private final int id;
    private final String name;
    private Vector<Person> persons;
    private Vector<Appliance> appliances;
    private double currentPower;
    
    public static class Builder {
    	// Required variables
    	private final int id;
        private final String name;
        // Optional or state related variables
        private Vector<Person> persons = new Vector<Person>();
        private Vector<Appliance> appliances = new Vector<Appliance>();
        private double currentPower = 0.0;
        public Builder(int aid, String aname) {
        	id = aid;
			name = aname;
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
		currentPower = builder.currentPower;
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
		currentPower = power;
	}

	public double getCurrentPower() {
		return currentPower;
	}
	
    public int getId() {
        return this.id;
    }

    public String getName() {
        return name;
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

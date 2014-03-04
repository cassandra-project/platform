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

import java.util.Vector;
import java.util.concurrent.PriorityBlockingQueue;

import com.mongodb.BasicDBObject;

import eu.cassandra.server.mongo.MongoInstallations;
import eu.cassandra.server.mongo.MongoResults;
import eu.cassandra.sim.Event;
import eu.cassandra.sim.PricingPolicy;
import eu.cassandra.sim.entities.Entity;
import eu.cassandra.sim.entities.appliances.Appliance;
import eu.cassandra.sim.entities.external.ThermalModule;
import eu.cassandra.sim.entities.people.Activity;
import eu.cassandra.sim.entities.people.Person;
import eu.cassandra.sim.utilities.Constants;
import eu.cassandra.sim.utilities.ORNG;

public class Installation extends Entity {
	private Vector<Person> persons;
	private Vector<Appliance> appliances;
	private Vector<Installation> subInstallations;
	private LocationInfo locationInfo;
	private double currentPowerP;
	private double currentPowerQ;
	private double maxPower = 0;
	private double cycleMaxPower = 0;
	private double avgPower = 0;
	private double energy = 0;
	private double previousEnergy = 0;
	private double energyOffpeak = 0;
	private double previousEnergyOffpeak = 0;
	private double cost = 0;
	private ThermalModule tm;
	
	public static class Builder {
    	// Required variables
    	private final String id;
        private final String name;
        private final String description;
        private final String type;
        // Optional or state related variables
        private LocationInfo locationInfo;
        private Vector<Person> persons = new Vector<Person>();
        private Vector<Appliance> appliances = new Vector<Appliance>();
        private Vector<Installation> subInstallations;
        private double currentPowerP = 0.0;
        private double currentPowerQ = 0.0;
        
        public Builder(String aid, String aname, String adescription, String atype) {
        	id = aid;
			name = aname;
		    description = adescription;
		    type = atype;
        }
        
		public Builder locationInfo (LocationInfo aLocationInfo) {
			locationInfo = aLocationInfo;
			return this;
	    }
		
	    public Builder subInstallations (Installation... inst) {
	    	for (Installation installation: inst) {
	    		subInstallations.add(installation);
	    	}
	    	return this;
	    }
	    
		public Installation build() {
			return new Installation(this);
		}
    }
    
    private Installation(Builder builder) {
    	id = builder.id;
        name = builder.name;
        description = builder.description;
        type = builder.type;
        persons = builder.persons;
        appliances = builder.appliances;
        subInstallations = builder.subInstallations;
        locationInfo = builder.locationInfo;
	}
    
    public void updateDailySchedule(int tick, PriorityBlockingQueue<Event> queue, 
    		PricingPolicy pricing, PricingPolicy baseline, String responseType, ORNG orng) {
    	for(Person person : getPersons()) {
    		person.updateDailySchedule(tick, queue, pricing, baseline, responseType, orng);
		}
    	if(tm != null) {
    		tm.nextStep();
    	}
    }
    
    public void updateMaxPower(double power) {
    	if(power > maxPower) maxPower = power;
    	if(power > cycleMaxPower) cycleMaxPower = power;
    }
    
    public double getMaxPower() {
    	return maxPower;
    }
    
    public void updateAvgPower(double powerFraction) {
    	avgPower += powerFraction;
    }
    
    public double getAvgPower() {
    	return avgPower;
    }
    
    public void updateEnergy(double power) {
    	energy += (power/1000.0) * Constants.MINUTE_HOUR_RATIO; 
    }
    
    public void updateEnergyOffpeak(double power) {
    	energyOffpeak += (power/1000.0) * Constants.MINUTE_HOUR_RATIO; 
    }
    
    public void updateCost(PricingPolicy pp, int tick) {
    	cost += pp.calculateCost(energy, previousEnergy, energyOffpeak, previousEnergyOffpeak, tick, cycleMaxPower);
    	cycleMaxPower = 0;
    	previousEnergy = energy;
    	previousEnergyOffpeak = energyOffpeak;
    	for(Appliance appliance : getAppliances()) {
    		appliance.updateCost(pp, tick);
    	}
    }
    
    public void addAppliancesKPIs(MongoResults m, double mcrunsRatio, double co2) {
    	for(Appliance appliance : getAppliances()) {
    		m.addAppKPIs(appliance.getId(), 
    			appliance.getMaxPower() * mcrunsRatio, 
    			appliance.getAvgPower() * mcrunsRatio, 
    			appliance.getEnergy() * mcrunsRatio, 
    			appliance.getCost() * mcrunsRatio,
    			appliance.getEnergy() * co2 * mcrunsRatio);
    	}
    }
    
    public void addActivitiesKPIs(MongoResults m, double mcrunsRatio, double co2) {
    	for(Person person : getPersons()) {
    		for(Activity activity: person.getActivities()) {
	    		m.addActKPIs(activity.getId(), 
	    			activity.getMaxPower() * mcrunsRatio, 
	    			activity.getAvgPower() * mcrunsRatio, 
	    			activity.getEnergy() * mcrunsRatio, 
	    			activity.getCost() * mcrunsRatio,
	    			activity.getEnergy() * co2 * mcrunsRatio);
    		}
    	}
    }
    
    public double getEnergy() {
    	return energy;
    }
    
    public double getCost() {
    	return cost;
    }

	public void nextStep(int tick) {
		updateRegistry(tick);
	}

	public void updateAppliancesAndActivitiesConsumptions(int tick, int endTick, PricingPolicy pricing) {
		for(Appliance appliance : getAppliances()) {
			double p = appliance.getPower(tick, "p");
			Activity act = appliance.getWhat();
			if(act != null) {
				act.updateMaxPower(p);
				act.updateAvgPower(p/endTick);
				if(pricing.isOffpeak(tick)) {
					act.updateEnergyOffpeak(p);
				} else {
					act.updateEnergy(p);
				}
			}
			appliance.updateMaxPower(p);
			appliance.updateAvgPower(p/endTick);
			if(pricing.isOffpeak(tick)) {
				appliance.updateEnergyOffpeak(p);
			} else {
				appliance.updateEnergy(p);
			}
		}
	}
	
	public void updateRegistry(int tick) {
		float p = 0f;
		float q = 0f;
		for(Appliance appliance : getAppliances()) {
			p += appliance.getPower(tick, "p");
			q += appliance.getPower(tick, "q");
		}
		if(tm != null) {
			p += tm.getPower(tick);
		}
		currentPowerP = p;
		currentPowerQ = q;
	}

	public double getCurrentPowerP() {
		return currentPowerP;
	}
	
	public double getCurrentPowerQ() {
		return currentPowerQ;
	}
    
    public Vector<Person> getPersons() {
    	return persons;    
    }

    public void addPerson (Person person) {
    	persons.add(person);
    }
    
    public void setThermalModule (ThermalModule atm) {
    	tm = atm;
    }

    public Vector<Appliance> getAppliances () {
    	return appliances;
    }
 
    public Vector<Installation> getSubInstallations () {
    	return subInstallations;
    }

    public void addAppliance (Appliance appliance) {
    	appliances.add(appliance);
    }

    public void addInstallation (Installation installation) {
    	subInstallations.add(installation);
    }

    public Appliance applianceExists (String name) {
    	for (Appliance a: appliances) {
    		if (a.getName().equalsIgnoreCase(name))
    			return a;
    	}
    	return null;
    }

    public LocationInfo getLocationInfo () {
    	return locationInfo;
    }
    
    public BasicDBObject toDBObject() {
    	BasicDBObject obj = new BasicDBObject();
    	obj.put("name", name);
    	obj.put("description", description);
    	obj.put("type", type);
    	obj.put("scenario_id", parentId);
    	if(locationInfo != null) {
    		obj.put("location", locationInfo.getName());
    		obj.put("x", locationInfo.getLocation().getLatitude());
    		obj.put("y", locationInfo.getLocation().getLongitude());
    	}
    	return obj;
    }
    
    public String getCollection() {
    	return MongoInstallations.COL_INSTALLATIONS;
    }

}

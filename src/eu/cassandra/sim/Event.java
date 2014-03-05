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
package eu.cassandra.sim;

import org.apache.log4j.Logger;

import eu.cassandra.sim.entities.appliances.Appliance;
import eu.cassandra.sim.entities.people.Activity;

public class Event implements Comparable<Event> {
	
	static Logger logger = Logger.getLogger(Event.class);
	
	public String hashcode;
	
	public final static int SWITCH_OFF = 0;
	
	public final static int SWITCH_ON = 1; 
	
	private int tick;
	
	private int action;
	
	private Appliance app;
	
	private Activity act;
	
	public Event(int atick, int aaction, Appliance aapp, String ahashcode, Activity aact) {
		tick = atick;
		action = aaction;
		app = aapp;
		hashcode = ahashcode;
		act = aact;
	}
	
	public Appliance getAppliance() {
		return app;
	}
	
	public Activity getActivity() {
		return act;
	}
	
	public int getAction() {
		return action;
	}
	
	public int getTick() {
		return tick;
	}
	
	public boolean apply() {
		switch(action) {
			case SWITCH_ON:
				if(!app.isInUse()) {
					app.turnOn(tick, hashcode, act);
					return true;
				} else {
					logger.warn("Tried to switch on appliance while on.");
					return false;
				}
			case SWITCH_OFF:
//				System.out.println(app.getId() + " " + app.getName() + " " + app.getWho());
				if(app.isInUse() && app.getWho().equalsIgnoreCase(hashcode)) {
					app.turnOff();
					return true;
				} else if(!app.getWho().equalsIgnoreCase(hashcode)){
					logger.warn("Someone else tried to switch off " +
							"appliance while off.");
					return false;
				}
			default:
				return false;
		}
	}

	@Override
	public int compareTo(Event o) {
		if(tick < o.getTick()) {
			return -1;
		} else if(tick > o.getTick()) {
			return 1;
		} else {
			return 0;
		}
	}

}

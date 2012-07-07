package eu.cassandra.sim;

import org.apache.log4j.Logger;

import eu.cassandra.sim.entities.appliances.Appliance;

public class Event implements Comparable<Event> {
	
	static Logger logger = Logger.getLogger(Event.class);
	
	public String hashcode;
	
	public final static int SWITCH_OFF = 0;
	
	public final static int SWITCH_ON = 1; 
	
	private int tick;
	
	private int action;
	
	private Appliance app;
	
	public Event(int atick, int aaction, Appliance aapp, String ahashcode) {
		tick = atick;
		action = aaction;
		app = aapp;
		hashcode = ahashcode;
	}
	
	public Appliance getAppliance() {
		return app;
	}
	
	public int getAction() {
		return action;
	}
	
	public int getTick() {
		return tick;
	}
	
	public void apply() {
		switch(action) {
			case SWITCH_ON:
				if(!app.isInUse()) {
					app.turnOn(tick, hashcode);
				} else {
					logger.warn("Tried to switch on appliance while on.");
				}
				break;
			case SWITCH_OFF:
				if(app.isInUse() && app.getWho().equalsIgnoreCase(hashcode)) {
					app.turnOff();
				} else if(!app.getWho().equalsIgnoreCase(hashcode)){
					logger.warn("Someone else tried to switch off " +
							"appliance while off.");
				}
				break;
			default:
				break;
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

package eu.cassandra.sim.entities.people;

import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.Logger;

import eu.cassandra.sim.Event;
import eu.cassandra.sim.SimCalendar;
import eu.cassandra.sim.entities.appliances.Appliance;
import eu.cassandra.sim.math.ProbabilityDistribution;
import eu.cassandra.sim.utilities.RNG;
import eu.cassandra.sim.utilities.Utils;

public class Activity {
	
	static Logger logger = Logger.getLogger(Activity.class);
	
	private final String name;
	private final HashMap<String, ProbabilityDistribution> nTimesGivenWeakDay;
	private final ProbabilityDistribution probStartTime;
	private final ProbabilityDistribution probDuration;
	private Vector<Appliance> appliances;
	private Vector<Double> probApplianceUsed;
    public static class Builder {
    	// Required parameters
    	private final String name;
    	private final HashMap<String, ProbabilityDistribution> nTimesGivenWeakDay;
    	private final ProbabilityDistribution probStartTime;
    	private final ProbabilityDistribution probDuration;
        // Optional parameters: not available    	
    	private Vector<Appliance> appliances;
    	private Vector<Double> probApplianceUsed;
        public Builder(String aname, ProbabilityDistribution start, 
        		ProbabilityDistribution duration) {
        	name = aname;
        	probStartTime = start;
        	probDuration = duration;
        	appliances = new Vector<Appliance>();
        	probApplianceUsed = new Vector<Double>();
        	nTimesGivenWeakDay = new HashMap<String,ProbabilityDistribution>();
        }
        public Builder appliances(Appliance... apps) {
        	for(Appliance app : apps) {
        		appliances.add(app);
        	}
        	return this;
        }
        public Builder times(String day, ProbabilityDistribution timesPerDay) {
        	nTimesGivenWeakDay.put(day, timesPerDay);
        	return this;
        }
        public Builder applianceUsed(Double... probs) {
        	for(Double prob : probs) {
        		probApplianceUsed.add(prob);
        	}
        	return this;
        }
        public Activity build() {
        	return new Activity(this);
        }
    }
    private Activity(Builder builder) {
    	name = builder.name;
    	appliances = builder.appliances;
    	nTimesGivenWeakDay = builder.nTimesGivenWeakDay;
    	probStartTime = builder.probStartTime;
    	probDuration = builder.probDuration;
    	probApplianceUsed = builder.probApplianceUsed;
	}
    
    public void addAppliance(Appliance a, Double prob) {
    	appliances.add(a);
    	probApplianceUsed.add(prob);
    }
    
    public String getName() {
    	return name;
    }
    
    public void updateDailySchedule(int tick,  PriorityBlockingQueue<Event> queue) {
    	/*
    	 *  Decide on the number of times the activity is going to be activated
    	 *  during a day
    	 */
    	ProbabilityDistribution numOfTimesProb;
    	if(SimCalendar.isWeekend(tick)) {
    		numOfTimesProb = nTimesGivenWeakDay.get("weekend");
    	} else {
    		numOfTimesProb = nTimesGivenWeakDay.get("weekday");
    	}
    	
    	int numOfTimes = numOfTimesProb.getPrecomputedBin();
    	logger.trace(numOfTimes);
    	/*
    	 * Decide the duration and start time for each activity activation
    	 */
    	for(int i = 1; i <= numOfTimes; i++) {
    		int duration = probDuration.getPrecomputedBin();
    		int startTime = probStartTime.getPrecomputedBin();
    		// Select appliances to be switched on
    		for(int j = 0; j < appliances.size(); j++) {
    			if(RNG.nextDouble() < probApplianceUsed.get(j).doubleValue()) {
    				Appliance a = appliances.get(j);
    				int appDuration = duration;
    				int appStartTime = startTime;
    				String hash = 
    						Utils.hashcode((
    								new Long(System.currentTimeMillis()).toString()));
    				Event eOn = 
    						new Event(tick + appStartTime, 
    								Event.SWITCH_ON, 
    								a,
    								hash);
    				queue.offer(eOn);
    				Event eOff = 
    						new Event(tick + appStartTime + appDuration, 
    								Event.SWITCH_OFF, 
    								a,
    								hash);
    				queue.offer(eOff);
    			}
    		}
    	}
    }
    
}

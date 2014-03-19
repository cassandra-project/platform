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
package eu.cassandra.sim.entities.appliances;

import com.mongodb.BasicDBObject;

import eu.cassandra.server.api.exceptions.BadParameterException;
import eu.cassandra.server.mongo.MongoAppliances;
import eu.cassandra.sim.PricingPolicy;
import eu.cassandra.sim.entities.Entity;
import eu.cassandra.sim.entities.appliances.ConsumptionModel.Tripplet;
import eu.cassandra.sim.entities.installations.Installation;
import eu.cassandra.sim.entities.people.Activity;
import eu.cassandra.sim.utilities.Constants;
import eu.cassandra.sim.utilities.ORNG;
import eu.cassandra.sim.utilities.RNG;

/**
 * Class modeling an electric appliance. The appliance has a stand by
 * consumption otherwise there are a number of periods along with their
 * consumption rates.
 * 
 * @author kyrcha
 * @version prelim
 */
public class Appliance extends Entity {
	private final ConsumptionModel pcm;
	private final ConsumptionModel qcm;
	private final double standByConsumption;
	private final boolean base;
	private final Installation installation;
	
	private boolean inUse;
	private long onTick;
	private String who;
	private Activity what;
	
	private double maxPower = 0;
	private double cycleMaxPower = 0;
	private double avgPower = 0;
	private double energy = 0;
	private double previousEnergy = 0;
	private double energyOffpeak = 0;
	private double previousEnergyOffpeak = 0;
	private double cost = 0;
	
	public static class Builder {
		// Required variables
		private final String id;
		private final String description;
		private final String type;
	    private final String name;
		private final Installation installation;
		private final ConsumptionModel pcm;
		private final ConsumptionModel qcm;
		private final double standByConsumption;
		private final boolean base;
		// Optional or state related variables
		private long onTick = -1;
		private String who = null;
		public Builder(
				String aid,
				String aname, 
				String adesc, 
				String atype,
				Installation ainstallation, 
				ConsumptionModel apcm,
				ConsumptionModel aqcm,
				double astandy, 
				boolean abase) {
			id = aid;
			name = aname;
			description = adesc;
			type = atype;		
			installation = ainstallation;
			pcm = apcm;
			qcm = aqcm;
			standByConsumption = astandy;
			base = abase;
		}
		public Appliance build(ORNG orng) {
			return new Appliance(this, orng);
		}
	}
	
	private Appliance(Builder builder, ORNG orng) {
		id = builder.id;
		name = builder.name;
		description = builder.description;
		type = builder.type;
		installation = builder.installation;
		standByConsumption = builder.standByConsumption;
		pcm = builder.pcm;
		qcm = builder.qcm;
		base = builder.base;
		inUse = (base) ? true : false;
		onTick = (base) ? -orng.nextInt(Constants.MIN_IN_DAY) : builder.onTick;
		who = builder.who;
	}

	public Installation getInstallation() {
		return installation;
	}

	public boolean isInUse() {
		return inUse;
	}
	
	public ConsumptionModel getQConsumptionModel() {
		return qcm;
	}
	
	public ConsumptionModel getPConsumptionModel() {
		return pcm;
	}

	public double getPower(long tick, String type) {
		try {
		
		ConsumptionModel cm = null; 
		if(type == "p") {
			cm = pcm;
		} else {
			cm = qcm;
		}
		double power = 0;
		// TODO
		if(isInUse()) {
			long relativeTick = Math.abs(tick - onTick);
			// If the device has a limited operational duration
			long divTick = relativeTick / cm.getTotalDuration();
			if(divTick >= cm.getOuterN() && cm.getOuterN() > 0) {
				power = 0;
			} else {
				int sum = 0;
				long moduloTick = relativeTick % cm.getTotalDuration();
				int index1 = -1;
				for(int i = 0; i < cm.getPatternN(); i++) {
					sum += (cm.getN(i) * cm.getPatternDuration(i));
					long whichPattern = moduloTick / sum;
					if(whichPattern == 0) {
						index1 = i;
						break;
					}
				}
				sum = 0;
				long moduloTick2 = moduloTick % cm.getPatternDuration(index1);
				int index2 = -1;
				for(int j = 0; j < cm.getPattern(index1).size(); j++) {
					sum += ((Tripplet)cm.getPattern(index1).get(j)).d;
					long whichPattern = moduloTick2 / sum;
					if(whichPattern == 0) {
						index2 = j;
						break;
					}
				}
				relativeTick++;		
				power = ((Tripplet)cm.getPattern(index1).get(index2)).v; 
			}
		} else {
			power = standByConsumption;
//			power = 0;
		}
		return power;
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void turnOff() {
		if(!base) {
			inUse = false;
			onTick = -1;
		}
	}

	public void turnOn(long tick, String awho, Activity awhat) {
		inUse = true;
		onTick = tick;
		who = awho;
		what = awhat;
	}

	public long getOnTick() {
		return onTick;
	}
	
	public String getWho() {
		return who;
	}
	
	public Activity getWhat() {
		return what;
	}
	
	public Double[] getActiveConsumption () {
		return pcm.getConsumption();
	}
	
	public boolean isStaticConsumption() {
		return pcm.checkStatic();
	}
	
	public static void main(String[] args) throws BadParameterException {
		// TODO [TEST] check the getPower method
		String p = "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" : 140.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 117.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 0.0, \"d\" : 73, \"s\": 0.0}]},{ \"n\" : 1, \"values\" : [ {\"p\" : 14.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 11.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 5.0, \"d\" : 73, \"s\": 0.0}]}]}";
		String q = "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"q\" : 140.0, \"d\" : 20, \"s\": 0.0}, {\"q\" : 117.0, \"d\" : 18, \"s\": 0.0}, {\"q\" : 0.0, \"d\" : 73, \"s\": 0.0}]},{ \"n\" : 1, \"values\" : [ {\"q\" : 14.0, \"d\" : 20, \"s\": 0.0}, {\"q\" : 11.0, \"d\" : 18, \"s\": 0.0}, {\"q\" : 5.0, \"d\" : 73, \"s\": 0.0}]}]}";
		Appliance freezer = new Appliance.Builder("id2",
				"freezer", 
				"A new freezer", 
				"FreezerA", 
				null,
				new ConsumptionModel(p, "p"),
				new ConsumptionModel(q, "q"),
				2f,
				true).build(new ORNG());
		System.out.println(freezer.getId());
		System.out.println(freezer.getName());
		for(int i = 0; i < 200; i++) {
			System.out.println(freezer.getPower(i, "p"));
		}
	}

	@Override
	public BasicDBObject toDBObject() {
		BasicDBObject obj = new BasicDBObject();
		obj.put("name", name);
		obj.put("description", description);
		obj.put("standby_consumption", standByConsumption);
		obj.put("inst_id", parentId);
		return obj;
	}

	@Override
	public String getCollection() {
		return MongoAppliances.COL_APPLIANCES;
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
    	if(what != null) {
    		what.updateCost(pp, tick);
    	}
    }
    
    public double getEnergy() {
    	return energy;
    }
    
    public double getCost() {
    	return cost;
    }
	
}

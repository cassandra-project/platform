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
package eu.cassandra.sim.entities.appliances;

import eu.cassandra.sim.entities.installations.Installation;
import eu.cassandra.sim.utilities.Constants;
import eu.cassandra.sim.utilities.RNG;

/**
 * Class modeling an electric appliance. The appliance has a stand by
 * consumption otherwise there are a number of periods along with their
 * consumption rates.
 * 
 * @author kyrcha
 * @version prelim
 */
public class Appliance {
	private final int id;
	private final String description;
	private final String type;
    private final String name;
	private final Installation installation;
	private final double[] consumption;
	private final int[] periods;
	private final int totalCycleTime;
	private final double standByConsumption;
	private final boolean base;
	
	private boolean inUse;
	private long onTick;
	private String who;
	
	public static class Builder {
		private static int idCounter = 0;
		// Required variables
		private final int id;
		private final String description;
		private final String type;
	    private final String name;
		private final Installation installation;
		private final double[] consumption;
		private final int[] periods;
		private final int totalCycleTime;
		private final double standByConsumption;
		private final boolean base;
		// Optional or state related variables
		private long onTick = -1;
		private String who = null;
		public Builder(
				String aname, 
				String adesc, 
				String atype,
				Installation ainstallation, 
				double[] aconsumption, 
				int[] aperiods, 
				double astandy, 
				boolean abase) {
			id = idCounter++;
			name = aname;
			description = adesc;
			type = atype;		
			installation = ainstallation;
			consumption = aconsumption;
			periods = aperiods;
			int sum = 0;
			for(int i = 0; i < periods.length; i++) {
				sum += periods[i];
			}
			totalCycleTime = sum;
			standByConsumption = astandy;
			base = abase;
		}
		public Appliance build() {
			return new Appliance(this);
		}
	}
	
	private Appliance(Builder builder) {
		id = builder.id;
		name = builder.name;
		description = builder.description;
		type = builder.type;
		installation = builder.installation;
		standByConsumption = builder.standByConsumption;
		consumption = builder.consumption;
		periods = builder.periods;
		totalCycleTime = builder.totalCycleTime;
		base = builder.base;
		inUse = (base) ? true : false;
		onTick = (base) ? -RNG.nextInt(Constants.MIN_IN_DAY) : builder.onTick;
		who = builder.who;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Installation getInstallation() {
		return installation;
	}

	public boolean isInUse() {
		return inUse;
	}

	public double getPower(long tick) {
		double power;
		if(isInUse()) {
			long relativeTick = Math.abs(tick - onTick);
			long tickInCycle = relativeTick % totalCycleTime;
			int ticks = 0;
			int periodIndex = 0;
			for(int i = 0; i < periods.length; i++) {
				ticks += periods[i];
				if(tickInCycle < ticks) {
					periodIndex = i;
					break;
				}
			}
			power = consumption[periodIndex];
		} else {
			power = standByConsumption;
		}
		return power;
	}

	public void turnOff() {
		if(!base) {
			inUse = false;
			onTick = -1;
		}
	}

	public void turnOn(long tick, String awho) {
		inUse = true;
		onTick = tick;
		who = awho;
	}

	public long getOnTick() {
		return onTick;
	}
	
	public String getWho() {
		return who;
	}
	
	public static void main(String[] args) {
		double[] power = {1f,1f};
		int[] period = {1, 1};
		Appliance fridge = new Appliance.Builder(
				"refrigerator", 
				"A new refrigerator", 
				"FridgeA", 
				null, 
				power, 
				period,
				1f,
				true).build();
		System.out.println(fridge.getId());
		System.out.println(fridge.getName());
		Appliance freezer = new Appliance.Builder(
				"freezer", 
				"A new freezer", 
				"FreezerA", 
				null,
				power,
				period,
				2f,
				true).build();
		System.out.println(freezer.getId());
		System.out.println(freezer.getName());
		for(int i = 0; i < 100; i++) {
			System.out.println(freezer.getPower(i));
		}
	}
	
}
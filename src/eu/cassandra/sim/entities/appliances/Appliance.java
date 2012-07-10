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
import eu.cassandra.sim.utilities.Params;
import eu.cassandra.sim.utilities.RNG;
import eu.cassandra.sim.utilities.FileUtils;

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
	private final String name;
	private final Installation installation;
	private final float[] consumption;
	private final int[] periods;
	private final int totalCycleTime;
	private final float standByConsumption;
	private final boolean base;
	
	private boolean inUse;
	private long onTick;
	private String who;
	
	public static class Builder {
		private static int idCounter = 0;
		// Required variables
		private final int id;
		private final String name;
		private final Installation installation;
		private final float[] consumption;
		private final int[] periods;
		private final int totalCycleTime;
		private final float standByConsumption;
		private final boolean base;
		// Optional or state related variables
		private long onTick = -1;
		private String who = null;
		public Builder(String aname, Installation ainstallation) {
			id = idCounter++;
			name = aname;
			installation = ainstallation;
			consumption = 
					FileUtils.getFloatArray(Params.APPS_PROPS, name+".power");
			periods = FileUtils.getIntArray(Params.APPS_PROPS, name+".periods");
			int sum = 0;
			for(int i = 0; i < periods.length; i++) {
				sum += periods[i];
			}
			totalCycleTime = sum;
			standByConsumption = 
					FileUtils.getFloat(Params.APPS_PROPS, name+".stand-by");
			base = FileUtils.getBool(Params.APPS_PROPS, name+".base");
		}
		public Appliance build() {
			return new Appliance(this);
		}
	}
	
	private Appliance(Builder builder) {
		id = builder.id;
		name = builder.name;
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

	public float getPower(long tick) {
		float power;
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
		Appliance frige = new Appliance.Builder("refrigerator", 
				null).build();
		System.out.println(frige.getId());
		System.out.println(frige.getName());
		Appliance freezer = new Appliance.Builder("freezer", 
				null).build();
		System.out.println(freezer.getId());
		System.out.println(freezer.getName());
		for(int i = 0; i < 100; i++) {
			System.out.println(freezer.getPower(i));
		}
	}
	
}

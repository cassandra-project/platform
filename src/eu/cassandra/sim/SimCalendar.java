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

package eu.cassandra.sim;

import java.util.Calendar;


import eu.cassandra.sim.utilities.Constants;

/**
 * Assumes we start from a Monday in a year.
 * 
 * @author kyrcha
 *
 */
public class SimCalendar {
	
	/**
	 * Calculates the day of week using the java.util.Calendar specifications
	 * 
	 * @param tick
	 * @return
	 */
	public static int getDayOfWeek(int tick) {
		int whole = tick / Constants.MIN_IN_DAY;
		switch(whole) {
			case 0:
				return Calendar.MONDAY;
			case 1:	
				return Calendar.TUESDAY;
			case 2:
				return Calendar.WEDNESDAY;
			case 3:
				return Calendar.THURSDAY;
			case 4:
				return Calendar.FRIDAY;
			case 5:
				return Calendar.SATURDAY;
			case 6:
				return Calendar.SUNDAY;
			default:
				return -1;
		}
	}
	
	public static boolean isWeekend(int tick) {
		int day = getDayOfWeek(tick);
		if(day == Calendar.SATURDAY || day == Calendar.SUNDAY) return true;
		return false;
	}
	
	public static void main(String[] args) {
		System.out.println(Calendar.SUNDAY); 	// 1
		System.out.println(Calendar.MONDAY); 	// 2
		System.out.println(Calendar.TUESDAY); 	// 3
		System.out.println(Calendar.WEDNESDAY); // 4 
		System.out.println(Calendar.THURSDAY); 	// 5
		System.out.println(Calendar.FRIDAY); 	// 6
		System.out.println(Calendar.SATURDAY); 	// 7
		System.out.println(getDayOfWeek(0) == Calendar.MONDAY); // true
		System.out.println(getDayOfWeek(365) == Calendar.MONDAY); // true
		System.out.println(getDayOfWeek(1440) == Calendar.TUESDAY); // true
		System.out.println(getDayOfWeek(5 * 1440) == Calendar.SATURDAY); // true
		System.out.println(getDayOfWeek(6 * 1440) == Calendar.SUNDAY); // true
	}
	

}

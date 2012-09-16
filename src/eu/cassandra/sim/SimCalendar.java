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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SimCalendar {
	private Calendar myCalendar;
	private Date base;
	private String granularity;
	private int granularityValue;
	
	public SimCalendar() {
		granularity = "Minute";
		granularityValue = 1;
		myCalendar = Calendar.getInstance();

		int day = myCalendar.get(Calendar.DAY_OF_MONTH);
		int month = myCalendar.get(Calendar.MONTH);
		int year = myCalendar.get(Calendar.YEAR);

		myCalendar.set(year, month, day, 0, 0, 0);
		base = myCalendar.getTime();

	}

	public SimCalendar (Date date, String granularity, int value) {

		setGranularity(granularity, value);
		myCalendar = Calendar.getInstance();
		myCalendar.setTime(date);

		int day = myCalendar.get(Calendar.DAY_OF_MONTH);
		int month = myCalendar.get(Calendar.MONTH);
		int year = myCalendar.get(Calendar.YEAR);

		myCalendar.set(year, month, day, 0, 0, 0);

		base = myCalendar.getTime();

	}

	public SimCalendar (String season, String granularity, int value) {

		setGranularity(granularity, value);
		myCalendar = Calendar.getInstance();

		int day = 1;
		int month;
		int year = myCalendar.get(Calendar.YEAR);

		switch (season) {
		case "Summer":
			
			month = Calendar.JUNE;
			break;
			
		case "Spring":

			month = Calendar.MARCH;
			break;

		case "Autumn":

			month = Calendar.SEPTEMBER;
			break;

		case "Winter":

			month = Calendar.DECEMBER;
			break;

		default:
			
			month = Calendar.JANUARY;
			
		}

		myCalendar.set(year, month, day, 0, 0, 0);

		base = myCalendar.getTime();

	}

	public SimCalendar (int month, String granularity, int value) {

		setGranularity(granularity, value);
		myCalendar = Calendar.getInstance();

		int day = 1;
		int year = myCalendar.get(Calendar.YEAR);

		myCalendar.set(year, month - 1, day, 0, 0, 0);

		base = myCalendar.getTime();

	}

	public Calendar getMyCalendar () {
		return myCalendar;
	}

	public Date getBase () {
		return base;
	}

	public String getGranularity () {
		return granularity;
	}

	public int getGranularityRaw () {

		switch (granularity) {
		case "Minute":

			return Calendar.MINUTE;

		case "Hour":

			return Calendar.HOUR;

		case "Day":

			return Calendar.DAY_OF_YEAR;

		case "Week":

			return Calendar.WEEK_OF_YEAR;

		case "Month":

			return Calendar.MONTH;

		}

		return 0;
		
	}

	public int getGranularityValue () {
		return granularityValue;
	}

	private void setGranularity (String gran, int value) {
		granularity = gran;
		granularityValue = value;
	}

	public boolean isWeekend (int tick) {
		Calendar temp = Calendar.getInstance();
		int gran = getGranularityRaw();
		System.out.println("Before " + temp.getTime().toString());
		temp.add(gran, tick * granularityValue);
		System.out.println("After " + temp.getTime().toString());
		int day = temp.get(Calendar.DAY_OF_WEEK);
		if (day == Calendar.SATURDAY || day == Calendar.SUNDAY)
			return true;
		return false;
	}

	public String toString () {
		String temp =
				"Base: " + base + " Granularity: " + granularity + 
				" Granularity Value: " + granularityValue;
		return temp;
	}

	public static void main (String[] args) throws ParseException {
		String date = "10/07/2010";
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date temp = df.parse(date);
		System.out.println(temp.toString());
		System.out.println(Calendar.SUNDAY); // 1
		System.out.println(Calendar.MONDAY); // 2
		System.out.println(Calendar.TUESDAY); // 3
		System.out.println(Calendar.WEDNESDAY); // 4
		System.out.println(Calendar.THURSDAY); // 5
		System.out.println(Calendar.FRIDAY); // 6
		System.out.println(Calendar.SATURDAY); // 7
		SimCalendar test = new SimCalendar();
		System.out.println(test.toString());
		test = new SimCalendar(Calendar.getInstance().getTime(), "Minute", 10);
		System.out.println(test.toString());
		test = new SimCalendar("Spring", "Minute", 10);
		System.out.println(test.toString());
		test = new SimCalendar(5, "Day", 1);
		System.out.println(test.toString());
		System.out.println(test.isWeekend(1));
		System.out.println(test.isWeekend(5));
	}
	
}
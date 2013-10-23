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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SimCalendar {
	private Calendar myCalendar;
	private Date base;
	private String granularity = "Minute";
	private int granularityValue = 1;
	private int duration = 0;
	
	private static final String[] ABBR_DAYS = {"NA", "Sun", "Mon", "Tue", "Wed",
		"Thu", "Fri", "Sat"};

	public SimCalendar() {

		myCalendar = Calendar.getInstance();

		int day = myCalendar.get(Calendar.DAY_OF_MONTH);
		int month = myCalendar.get(Calendar.MONTH);
		int year = myCalendar.get(Calendar.YEAR);

		myCalendar.set(year, month, day, 0, 0, 0);
		base = myCalendar.getTime();

	}

	public SimCalendar(Date date, int duration) {

		myCalendar = Calendar.getInstance();

		int day = myCalendar.get(Calendar.DAY_OF_MONTH);
		int month = myCalendar.get(Calendar.MONTH);
		int year = myCalendar.get(Calendar.YEAR);

		myCalendar.set(year, month, day, 0, 0, 0);
		base = myCalendar.getTime();
		this.duration = duration;

	}
	
	public SimCalendar(int day, int month, int year, int duration) {

		myCalendar = Calendar.getInstance();

		myCalendar.set(year, month-1, day, 0, 0, 0);
		base = myCalendar.getTime();
		this.duration = duration;

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
	
	private Calendar copyCal(Calendar cal) {
		Calendar temp = Calendar.getInstance();
		temp.set(myCalendar.get(Calendar.YEAR), 
				myCalendar.get(Calendar.MONTH), 
				myCalendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		return temp;
	}

	public boolean isWeekend (int tick) {
		Calendar temp = copyCal(myCalendar);
		int gran = getGranularityRaw();
		temp.add(gran, tick * granularityValue);
		int day = temp.get(Calendar.DAY_OF_WEEK);
		if (day == Calendar.SATURDAY || day == Calendar.SUNDAY)
			return true;
		return false;
	}
	
	public String getCurrentDate(int tick) {
		Calendar temp = copyCal(myCalendar);
		int gran = getGranularityRaw();
		temp.add(gran, tick * granularityValue);
		int day = temp.get(Calendar.DAY_OF_MONTH);
		int month = temp.get(Calendar.MONTH) + 1;
		return day + "/" + month;
	}
	
	public String getDayOfWeek(int tick) {
		Calendar temp = copyCal(myCalendar);
		int gran = getGranularityRaw();
		temp.add(gran, tick * granularityValue);
		return ABBR_DAYS[temp.get(Calendar.DAY_OF_WEEK)];
	}

	public String toString () {
		String temp =
				"Base: " + base + " Granularity: " + granularity + 
				" Granularity Value: " + granularityValue + " Duration: " + duration;
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
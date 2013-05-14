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
/**
 * 
 * @author Konstantina Valogianni
 * 
 */
package eu.cassandra.sim.tests;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import org.junit.Test;

import eu.cassandra.sim.SimCalendar;

public class SimCalendarTest {
	
	@Test
	public void testgetBase() {
		Date d= new Date();
	    SimCalendar tester = new SimCalendar(d,"Minute",1);
	    Calendar testCal= Calendar.getInstance();
	    testCal.setTime(d);
	    testCal.set(testCal.HOUR_OF_DAY,0);
	    testCal.set(testCal.MINUTE,0);
	    testCal.set(testCal.SECOND,0);
	    assertEquals(testCal.getTime(),tester.getBase());
	    
	}
	
	@Test
	public void testgetMyCalendar() {
		Date d= new Date();
	    SimCalendar tester = new SimCalendar(d,"Minute",1);
	    Calendar testCal= Calendar.getInstance();
	    testCal.setTime(d);
	    testCal.set(testCal.HOUR_OF_DAY,0);
	    testCal.set(testCal.MINUTE,0);
	    testCal.set(testCal.SECOND,0);
	    assertEquals(testCal,tester.getMyCalendar());
	}

	@Test
	public void testtoString () {
		
		Date d= new Date();
	    SimCalendar tester = new SimCalendar(d,"Minute",1);
	    assertEquals("Base: Mon Oct 01 00:00:00 CEST 2012 Granularity: Minute Granularity Value: 1",tester.toString());
	}
	
	@Test
	public void testgetGranularity() {
		Date d= new Date();
	    //Date temp = df.parse(date);
	    SimCalendar tester = new SimCalendar(d,"Minute",1);
	    assertEquals( "Minute",tester.getGranularity());
	}
	
	@Test
	public void testgetGranularityRaw() {
		Date d= new Date();
	    //Date temp = df.parse(date);
	    SimCalendar tester = new SimCalendar(d,"Minute",1);
	    SimCalendar tester2 = new SimCalendar(d,"Hour",1);
	    SimCalendar tester3 = new SimCalendar(d,"Day",1);
	    SimCalendar tester4 = new SimCalendar(d,"Week",1);
	    SimCalendar tester5 = new SimCalendar(d,"Month",1);
	    assertTrue("Result1", 12==tester.getGranularityRaw());
	    assertTrue("Result2", 10==tester2.getGranularityRaw());
	    assertTrue("Result3", 6==tester3.getGranularityRaw());
	    assertTrue("Result4", 3==tester4.getGranularityRaw());
	    assertTrue("Result5", 2==tester5.getGranularityRaw());
	    //the values 12,10,6,3,2 come from the constant field values for the Calendar class
	    
	}
	
	@Test
	public void testgetGranularityValue() {
		
	    SimCalendar tester = new SimCalendar();
	    assertTrue("Result6", 1==tester.getGranularityValue());
	}
	
	@Test
	public void testisWeekend() {
		
	    SimCalendar tester = new SimCalendar();
	    assertEquals( false,tester.isWeekend(4));
	}

}

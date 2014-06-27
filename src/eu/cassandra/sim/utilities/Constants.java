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

package eu.cassandra.sim.utilities;

/**
 * Class to hold constants
 * 
 * @author Cassandra developers
 *
 */
public class Constants {
	
	public static final int SHIFTING_WINDOW_IN_MINUTES = 60;
	
	public final static int MIN_IN_HOUR = 60;
	
	public final static int MINUTES_PER_DAY = 60 * 24;
	
	public final static int MIN_IN_DAY = 60 * 24;
	
	public final static double MINUTE_HOUR_RATIO = 1.0/60.0;
	
	public final static int MU = 0;
	
	public final static int SIGMA = 1;
	
	public final static String AUTHORIZATION_FAIL = 
			"{ \"success\": false, \"message\": \"User authorization failed\", \"errors\": { \"exception\": \"User and or password do not match.\" }}";

}

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

package eu.cassandra.sim.utilities;

/**
 * Class to hold certain values
 * 
 * @author Cassandra developers
 *
 */
public abstract class Params {
	
	// Default properties files
	
	public static String ACT_PROPS;
	
	public static String APPS_PROPS;
	
	public static String DEMOG_PROPS;
	
	public static String SIM_PROPS;
	
	// Configuration files
	
	public static String LOG_CONFIG_FILE = "config/log.conf";
	
	public static String JAVADB_PROPS = "config/javaDB.conf";
	
	/** Defines the registry directory */
	public static String REGISTRIES_DIR = "registries/";
	
	/** Defines the properties directory */
	public static String PROPS_DIR = "props/";
	
}

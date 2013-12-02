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
package eu.cassandra.server;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class IServletContextListener implements ServletContextListener {
	
	static Logger logger = Logger.getLogger(IServletContextListener.class);
	
	public static File schemas;
	public static File graphs;
	public static String resources_path;
	

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		resources_path = arg0.getServletContext().getRealPath("resources/");
		arg0.getServletContext().setAttribute("RESOURCES_PATH", resources_path);
		String path = arg0.getServletContext().getRealPath("resources/jsonSchema/");
		schemas = new File(path);
		graphs = new File(arg0.getServletContext().getRealPath("resources/graphs/"));
		Logger.getRootLogger().getLoggerRepository().resetConfiguration();
		DailyRollingFileAppender drfa = new DailyRollingFileAppender();
		try {
			drfa.setName("R");
			//drfa.setFile("/var/log/tomcat7/cassandra.log");
			drfa.setFile("/home/kyrcha/cassandra/logs/cassandra.log");
			drfa.setLayout(new PatternLayout("%d{MM/dd HH:mm:ss} %-5p %30.30c %x - %m\n"));
			drfa.setDatePattern("'.'yyyy-MM-dd");
			drfa.setThreshold(Level.TRACE);
			drfa.setAppend(true);
			drfa.activateOptions();
		} catch(Exception e) {
			drfa = new DailyRollingFileAppender();
			drfa.setName("R");
			drfa.setFile("/home/kyrcha/cassandra/logs/cassandra.log");
			drfa.setLayout(new PatternLayout("%d{MM/dd HH:mm:ss} %-5p %30.30c %x - %m\n"));
			drfa.setDatePattern("'.'yyyy-MM-dd");
			drfa.setThreshold(Level.TRACE);
			drfa.setAppend(true);
			drfa.activateOptions();
		}
		Logger.getRootLogger().addAppender(drfa);
		logger.info("Cassandra platform started!!!");
	}

}

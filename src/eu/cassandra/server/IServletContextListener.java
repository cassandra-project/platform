package eu.cassandra.server;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class IServletContextListener implements ServletContextListener {
	
	public static File schemas;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		String path = arg0.getServletContext().getRealPath("resources/jsonSchema/");
		schemas = new File(path);
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
	}

}

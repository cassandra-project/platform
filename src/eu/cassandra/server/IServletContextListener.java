package eu.cassandra.server;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

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
	}

}

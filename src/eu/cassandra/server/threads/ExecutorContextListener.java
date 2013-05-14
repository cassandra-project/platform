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
package eu.cassandra.server.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ExecutorContextListener implements ServletContextListener {
	
    private  ExecutorService executor;

    public void contextInitialized(ServletContextEvent arg0) {
        ServletContext context = arg0.getServletContext();
        int nr_executors = 2;
        ThreadFactory daemonFactory = new DaemonThreadFactory();
        try {
            nr_executors = 
            		Integer.parseInt(context.getInitParameter("nr-executors"));
        } catch (NumberFormatException ignore) {
        	
        }
        if(nr_executors <= 1) {
        	executor = Executors.newSingleThreadExecutor(daemonFactory);
        } else {
        	executor = Executors.newFixedThreadPool(nr_executors, daemonFactory);
        }
        context.setAttribute("MY_EXECUTOR", executor);
    }
    
    public void contextDestroyed(ServletContextEvent arg0) {
        //ServletContext context = arg0.getServletContext();
        executor.shutdownNow(); // or process/wait until all pending jobs are done
    }

}


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

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ArrayBlockingQueue;


import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ExecutorContextListener implements ServletContextListener {
    
    private ThreadPoolExecutor executorPool;

    public void contextInitialized(ServletContextEvent arg0) {
        ServletContext context = arg0.getServletContext();
        int nr_executors = 4;
        executorPool = new ThreadPoolExecutor(nr_executors, 10, 24, 
        		TimeUnit.HOURS, new ArrayBlockingQueue<Runnable>(10));
        context.setAttribute("MY_EXECUTOR", executorPool);
    }
    
    public void contextDestroyed(ServletContextEvent arg0) {
        executorPool.shutdown();
    }

}


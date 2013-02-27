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

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Hands out threads from the wrapped threadfactory with setDeamon(true), so the
 * threads won't keep the JVM alive when it should otherwise exit.
 */
public class DaemonThreadFactory implements ThreadFactory {

    private final ThreadFactory factory;

    /**
     * Construct a ThreadFactory with setDeamon(true) using
     * Executors.defaultThreadFactory()
     */
    public DaemonThreadFactory() {
        this(Executors.defaultThreadFactory());
    }

    /**
     * Construct a ThreadFactory with setDeamon(true) wrapping the given factory
     * 
     * @param thread
     *            factory to wrap
     */
    public DaemonThreadFactory(ThreadFactory factory) {
        if (factory == null)
            throw new NullPointerException("factory cannot be null");
        this.factory = factory;
    }

    public Thread newThread(Runnable r) {
        final Thread t = factory.newThread(r);
        t.setDaemon(true);
        return t;
    }
}


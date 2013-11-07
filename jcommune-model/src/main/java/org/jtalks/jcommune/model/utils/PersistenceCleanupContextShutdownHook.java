/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.jcommune.model.utils;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * Cleans up persistence resources on application context close. This is necessary
 * during continuous deployments on Tomcat to avoid memory leaks.
 *
 * @author Evgeniy Naumenko
 */
public class PersistenceCleanupContextShutdownHook {

    private static final Logger logger = LoggerFactory.getLogger(PersistenceCleanupContextShutdownHook.class);

    /**
     * Called on the application context disposal and cleans up persistence
     * resources that may hold ClassLoader references
     */
    public void dispose() {
        this.shutdownDaemonThread();
        this.unregisterDrivers();
        //shutdown EHCache cache manager
        CacheManager.getInstance().shutdown();
    }

    /**
     * Unregistered JDBC drivers may lead to memory leaks on redeploy if run on Tomcat 6.0.23
     * or earlier. Since 6.0.24 Tomcat has memory leak detection system, so on new Tomcat
     * versions this code merely removes a warning from logs.
     */
    private void unregisterDrivers() {
        Enumeration drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = (Driver) drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                logger.debug("Unregistering JDBC driver: {}", driver);
            } catch (SQLException e) {
                logger.warn("Cannot dispose JDBC driver: {}.", driver, e);
            }
        }
    }

    /**
     * The cleanup thread for abandoned connections in the com.mysql.jdbc.NonRegisteringDriver class
     * requires manual shutdown. If not closed properly this thread won't stop on application
     * undeploy. That effectively means holding a ClassLoader reference and potential permgen
     * memory leak. This method shuts the parasite thread down for the great justice.
     */
    private void shutdownDaemonThread() {
        try {
            AbandonedConnectionCleanupThread.shutdown();
        } catch (InterruptedException e) {
            logger.warn("Cannot dispose JDBC driver.", e);
        }
    }
}

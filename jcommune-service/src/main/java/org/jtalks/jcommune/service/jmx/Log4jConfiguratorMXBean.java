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
package org.jtalks.jcommune.service.jmx;

import java.util.List;

/**
 * Taken from <a href="http://www.sureshpw.com/2012/04/dynamic-logging-with-log4j.html">here</a>, used to expose Log4j
 * configuration via JMX so that we can change it during runtime without restarting the app.
 *
 * @author stanislav bashkirtsev
 */
public interface Log4jConfiguratorMXBean {
    /**
     * Lists all the loggers defined in logger configuration. Only these names can be used to change the logging level.
     *
     * @return the list of logger names defined in log4j configuration
     */
    List<String> getLoggers();

    /**
     * Get the level of the logger (INFO, DEBUG, ERROR, etc.). If you don't know the exact logger name, use {@link
     * #getLoggers()} to list all of them.
     *
     * @param logger the name of the logger (from {@link #getLoggers()} to get its current level
     * @return the level specified logger uses or {@code "unavailable"} if such logger does not exist
     */
    String getLogLevel(String logger);

    /**
     * Change the level of the logger in the runtime if you need to see more (or less) information.
     *
     * @param logger the name of the logger to change its level
     * @param level  the level to change logging to, results in no-op if it's the same as the current one. If a logger
     *               with the specified name does not exist, it also results in no-op.
     * @see #getLoggers()
     */
    void setLogLevel(String logger, String level);
}


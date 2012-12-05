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

/** Take from <a href="http://www.sureshpw.com/2012/04/dynamic-logging-with-log4j.html">here</a>. */
public interface Log4jConfiguratorMXBean {
    /** list of all the logger names and their levels */
    List<String> getLoggers();

    /** Get the log level for a given logger */
    String getLogLevel(String logger);

    /** Set the log level for a given logger */
    void setLogLevel(String logger, String level);
}


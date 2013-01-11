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

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Allows exposing Log4j operations/attributes via JMX so that we can change the logging level in runtime.<br/> Taken
 * from <a href="http://www.sureshpw.com/2012/04/dynamic-logging-with-log4j.html">here</a>.
 * @author stanislav bashkirtsev
 */
public class Log4jConfigurator implements Log4jConfiguratorMXBean {
    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getLoggers() {
        List<String> list = new ArrayList<String>();
        for (Logger log : getAllLoggers()) {
            if (log.getLevel() != null) {
                list.add(log.getName() + " = " + log.getLevel().toString());
            }
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLogLevel(String logger) {
        String level = "unavailable";
        if (StringUtils.isNotBlank(logger)) {
            Logger log = getLogger(logger);
            if (log != null) {
                level = log.getLevel().toString();
            }
        }
        return level;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLogLevel(String logger, String level) {
        if (StringUtils.isNotBlank(logger) && StringUtils.isNotBlank(level)) {
            Logger log = getLogger(logger);
            if (log != null) {
                log.setLevel(Level.toLevel(level.toUpperCase()));
            }
        }
    }

    @VisibleForTesting
    List<Logger> getAllLoggers() {
        return Collections.<Logger>list(LogManager.getCurrentLoggers());
    }

    @VisibleForTesting
    Logger getLogger(String logger) {
        return Logger.getLogger(logger);
    }

}

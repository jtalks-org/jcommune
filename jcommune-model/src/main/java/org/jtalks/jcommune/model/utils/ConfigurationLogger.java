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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs the configuration that was used during the app startup. This might be useful to double-check that all the
 * necessary configs are loaded from needed places and we didn't leave some junk props. In the future we can expose this
 * information as JMX MBean.
 *
 * @author stanislav bashkirtsev
 */
public class ConfigurationLogger {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Logs the file location of the EhCache config. By default we take it from the classpath, but on different
     * environments we use {@code $TOMCAT_HOME/conf/jcommune.ehcache.xml} instead. To be more sure about what we're
     * loading and to follow DRY we'll need to use EhCache classes to figure out what configs it loaded instead of
     * setting this property directly as a string.
     *
     * @param ehCacheConfigLocation the location of the cache config file
     */
    public void setEhCacheConfigLocation(String ehCacheConfigLocation) {
        logger.info("Loading EhCache configuration from: [{}]", ehCacheConfigLocation);
    }

}

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

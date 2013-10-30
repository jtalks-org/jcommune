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
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * <p>Extended version of the usual Spring's {@link PropertyPlaceholderConfigurer} that looks into Tomcat JNDI first.
 * Note, that JNDI is the highest priority and if property was found there, then nothing can override it. If property
 * wasn't found there, then usual algorithm of {@link PropertyPlaceholderConfigurer} is used.</p> <b>Justification</b>:
 * in order to simplify the deployment in different environment like DEV, UAT, we'd like to change Tomcat's {@code
 * $CATALINA_HOME/Catalina/localhost[jtalks_app.xml]} file rather than unzip the war file and change properties there.
 * We certainly don't want to use OS env vars because they are available only for Tomcat runtime and will be reset with
 * Tomcat shutting down.
 *
 * @author stanislav bashkirtsev
 */
public class JndiAwarePropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
    /** Tomcat places its own JNDI context into {@link InitialContext} with this key. So we can look it up. */
    private static final String TOMCAT_CONTEXT_NAME = "java:/comp/env";
    /** Intentionally didn't want to use Spring's logger from super class, slf4j is more powerful. */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * <p>Looks for Tomcat JNDI environment first to get variables from there and returns if the value was found, if
     * nothing found then it works as usual configurer: </p> {@inheritDoc}
     */
    @Override
    protected String resolvePlaceholder(String placeholder, Properties props, int systemPropertiesMode) {
        String propValue = resolveJndiProperty(placeholder);
        if (propValue == null) {
            propValue = super.resolvePlaceholder(placeholder, props, systemPropertiesMode);
        }
        return propValue;
    }

    /**
     * Takes a look at Tomcat JNDI environment ({@code java:/comp/env}) and tries to find the placeholder there. Returns
     * {@code null} if nothing found there, otherwise found value is returned as a string.
     *
     * @param placeholder the property key to find its values
     * @return the value of the property from Tomcat JNDI or {@code null} if nothing found there
     */
    private String resolveJndiProperty(String placeholder) {
        String propValue = null;
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup(TOMCAT_CONTEXT_NAME);
            propValue = (String) envContext.lookup(placeholder);
            logger.info("Property {} taken from JNDI.", placeholder, propValue);
        } catch (NamingException e) {
            logger.info("Could not resolve JNDI property [{}]. Will be trying file properties, then System ones and " +
                    "if not found anywhere, then defaults will be taken", placeholder);
        }
        return propValue;
    }

}

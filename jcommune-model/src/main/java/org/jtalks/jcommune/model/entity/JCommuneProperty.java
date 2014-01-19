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
package org.jtalks.jcommune.model.entity;

import org.jtalks.common.model.entity.Component;
import org.jtalks.common.model.entity.Property;
import org.jtalks.jcommune.model.dao.ComponentDao;
import org.jtalks.jcommune.model.dao.PropertyDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Provides access to the JCommune property, which is stored in the database.
 * Each enum value is wired as a separate bean to inject individual
 * properties into other beans.
 *
 * @author Anuar_Nurmakanov
 */
public enum JCommuneProperty {
    /**
     * The property to check the enabling of email notifications to subscribers of topics or branches.
     */
    SENDING_NOTIFICATIONS_ENABLED,

    /**
     * Property for session timeout for logged users.
     */
    SESSION_TIMEOUT,

    /**
     * Property for the maximum size of the avatar.
     */
    AVATAR_MAX_SIZE,

    /**
     * Name of component
     */
    CMP_NAME,

    /**
     * Description of component
     */
    CMP_DESCRIPTION,

    /**
     * JavaSape account ID
     */
    CMP_SAPE_ACCOUNT_ID,

    /**
     * Show javasape content on main page
     */
    CMP_SAPE_ON_MAIN_PAGE_ENABLE,

    /**
     * Sape links count for one request to Sape service
     */
    CMP_SAPE_LINKS_COUNT,

    /**
     * JCommune host
     */
    CMP_HOST_URL,

    /**
     * Sape service timeout
     */
    CMP_SAPE_TIMEOUT,
    
    /**
     * Whether show dummy links for SAPE
     */
    CMP_SAPE_SHOW_DUMMY_LINKS,

    /**
     * Whether enable SAPE service
     */
    CMP_SAPE_ENABLED,
    /**
     * Logo tooltip
     */
    LOGO_TOOLTIP,
    /** Maximum size of the forum logo */
    FORUM_LOGO_MAX_SIZE,
    /**
     * Keeps the date of the last modification of the forum admin information
     * such as logo or favorite icon
     */
    ADMIN_INFO_LAST_UPDATE_TIME,
    /** title prefix - should be displayed at the beginning of the title of the every page */
    ALL_PAGES_TITLE_PREFIX,
    
    COPYRIGHT
    ;


    private static final Logger LOGGER = LoggerFactory.getLogger(JCommuneProperty.class);

    private String name;
    private String defaultValue;
    private PropertyDao propertyDao;
    private ComponentDao componentDao;

    /**
     * Returns a string value of the property. Property values
     * ​​are stored as strings, so this method is main for retrieving
     * a value of property.
     * It is also worth noting that if the property has not been found,
     * it will return a default value.
     *
     * @return a string value of the property
     */
    public String getValue() {

        if (propertyDao != null) {
            Property property = propertyDao.getByName(name);
            if (property != null) {
                return property.getValue();
            } else {
                return getDefaultValue();
            }
        } else {
            return getDefaultValue();
        }
    }
    
    /**
     * Set new value of this property. For this operation <code>componentDao</code>
     * must be specified. In other case new value will be to stored to DB.
     * @param value new value of property
     */
    public void setValue(String value) {
        if (componentDao != null) {
            Component component = componentDao.getComponent();
            component.setProperty(name, value);
            componentDao.saveOrUpdate(component);
        } else {
            LOGGER.warn("Can't set value of property {}. No componentDAO", name);
        }
    }

    /**
     * Returns a string value of the component property.
     * It is also worth noting that if the property has not been found,
     * it will return a default value.
     *
     * @return a string value of component property
     */
    public String getValueOfComponent() {
        try {
            if (componentDao != null) {
                Component cmp = componentDao.getComponent();
                if (cmp != null) {
                    return name.equals("cmp.name") ? cmp.getName() : cmp.getDescription();
                } else {
                    return getDefaultValue();
                }
            } else {
                return getDefaultValue();
            }
        } catch (Exception ex) {
            return getDefaultValue();
        }
    }

    /**
     * Converts a value of the property to boolean and returns it.
     * Keep in mind, if the property isn't boolean, the result will false.
     *
     * @return a boolean value of the property
     */
    public boolean booleanValue() {
        return Boolean.valueOf(getValue());
    }

    /**
     * Converts a value of the property to int and returns it.
     * Keep in mind, if the property isn't integer {@link NumberFormatException}
     * will be thrown
     *
     * @return a boolean value of the property
     */
    public int intValue() {
        return Integer.valueOf(getValue());
    }

    /**
     * @param name the name of the property
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param defaultValue default value for current property
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Default value returns if property not found, or connection to database is down
     *
     * @return defaultValue
     */
    private String getDefaultValue() {
        LOGGER.trace("[{}] property was not found, using default value [{}]", name, defaultValue);
        return defaultValue;
    }

    /**
     * Set an instance of {@link org.jtalks.jcommune.model.dao.PropertyDao} to search properties by name.
     *
     * @param propertyDao an instance of {@link org.jtalks.jcommune.model.dao.PropertyDao}
     */
    public void setPropertyDao(PropertyDao propertyDao) {
        this.propertyDao = propertyDao;
    }

    /**
     * Set an instance of {@link org.jtalks.jcommune.model.dao.ComponentDao} to search properties of Component.
     *
     * @param componentDao an instance of {@link org.jtalks.jcommune.model.dao.ComponentDao}
     */
    public void setComponentDao(ComponentDao componentDao) {
        this.componentDao = componentDao;
    }

}

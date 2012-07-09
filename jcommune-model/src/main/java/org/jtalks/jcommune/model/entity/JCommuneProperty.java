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

import org.jtalks.common.model.entity.Property;
import org.jtalks.jcommune.model.dao.PropertyDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Provides access to the JCommune property, which is stored in the database.
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
    SESSION_TIMEOUT;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JCommuneProperty.class);
    //fields
    private String name;
    private String defaultValue;
    private PropertyDao propertyDao;

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
        Property property = propertyDao.getByName(name);
        if (property != null) {
            return property.getValue();
        } else {
            LOGGER.warn(name + " property was not found.");
            return defaultValue;
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
     * Sets the name of the property.
     * 
     * @param name the name of the property
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets default value for this property.
     * 
     * @param defaultValue default value
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Set an instance of {@link PropertyDao} to search properties by name.
     * 
     * @param propertyDao an instance of {@link PropertyDao}
     */
    public void setPropertyDao(PropertyDao propertyDao) {
        this.propertyDao = propertyDao;
    }
}

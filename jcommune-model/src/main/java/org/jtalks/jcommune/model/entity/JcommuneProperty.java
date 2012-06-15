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
public enum JcommuneProperty {
    /**
     * The property to check the enabling of email notifications to subscribers of topics or branches.
     */
    SENDING_NOTIFICATIONS_ENABLED;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JcommuneProperty.class);
    //fields
    private String name;
    private String defaultValue;
    private PropertyDao propertyDao;

    /**
     * {@inheritDoc}
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

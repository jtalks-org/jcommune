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
package org.jtalks.jcommune.service.nontransactional;

import org.jtalks.common.model.entity.Property;
import org.jtalks.jcommune.model.dao.PropertyDao;
import org.jtalks.jcommune.service.JcommuneProperty;


/**
 * Provides access to the JCommune property, which is stored in the database.
 * 
 * @author Anuar_Nurmakanov
 */
public enum JcommuneDatabaseProperty implements JcommuneProperty {
    /**
     * The property to check the enabling of email notifications to subscribers of topics or branches.
     */
    SENDING_NOTIFICATIONS_ENABLED("jcommune.sending_notifications_enabled", Boolean.TRUE.toString());
    //fields
    private String name;
    private String defaultValue;
    private PropertyDao propertyDao;
    
    /**
     * Constructor with required fields.
     * 
     * @param name a name of a property
     * @param defaultValue default value
     */
    private JcommuneDatabaseProperty(String name, String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    public String getValue() {
        if (propertyDao != null) {
            Property property = propertyDao.getByName(name);
            if (property != null) {
                return property.getValue();
            }
        }
        return defaultValue;
    }
    
    
    /**
     * Gets a name of the property.
     * 
     * @return a name of the property
     */
    String getName() {
        return name;
    }

    /**
     * Gets default value.
     * 
     * @return default value
     */
    String getDefaultValue() {
        return defaultValue;
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

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

import org.jtalks.common.model.entity.Entity;

/**
 * Stores information about the type contacts of user.
 * Used as {@code UserDetails}
 */
public class UserContactType extends Entity {

    /** Placeholder for content in display pattern */
    public static final String CONTACT_MASK_PLACEHOLDER = "%s";
    
    private String typeName;
    private String icon;
    private String mask;
    private String displayPattern;
    private String validationPattern;

    /**
     * Only for hibernate usage.
     */
    public UserContactType() {
    }

    /**
     *
     * @return address of icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     *
     * @param icon address of icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     *
     * @return name type of contact
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     *
     * @param typeName name type of contact
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * @return the mask for editing
     */
    public String getMask() {
        return mask;
    }

    /**
     * @param mask the mask for editing
     */
    public void setMask(String mask) {
        this.mask = mask;
    }

    /**
     * @return the display pattern contact
     */
    public String getDisplayPattern() {
        return displayPattern;
    }

    /**
     * @param displayPattern the display pattern for contact
     */
    public void setDisplayPattern(String displayPattern) {
        this.displayPattern = displayPattern;
    }

    /**
     * @return the validation regexp of contact type
     */
    public String getValidationPattern() {
        return validationPattern;
    }

    /**
     * @param validationPattern validation regexp of contact type
     */
    public void setValidationPattern(String validationPattern) {
        this.validationPattern = validationPattern;
    }

    /**
     * Get value ready to display based on <code>displayPattern</code> and 
     * given contact value
     * @param value contact value
     * @return value of contact ready to display in HTML page
     */
    public String getDisplayValue(String value) {
        return displayPattern.replaceAll(CONTACT_MASK_PLACEHOLDER, value);
    }
    
    
}

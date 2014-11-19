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
 * @author Mikhail Stryzhonok
 */
public class CommentProperty extends Entity {
    private String name;
    private PropertyType type;
    private String value;
    private PostComment comment;

    /**
     * For hibernate usage only
     */
    public CommentProperty() {
    }

    /**
     * Get name of the property
     *
     * @return name of the property
     */
    public String getName() {
        return name;
    }

    /**
     * Sets specified name to the property
     *
     * @param name name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets type of the property
     *
     * @return type of the property
     */
    public PropertyType getType() {
        return type;
    }

    /**
     * Sets specified type to the property
     *
     * @param type type to set
     */
    public void setType(PropertyType type) {
        this.type = type;
    }

    /**
     * Gets value of the property
     *
     * @return value of the property
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets specified value to the property
     *
     * @param value value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets comment which owns the property
     *
     * @return comment which owns the property
     */
    public PostComment getComment() {
        return comment;
    }

    /**
     * Sets specified comment as property owner
     *
     * @param comment comment to set
     */
    public void setComment(PostComment comment) {
        this.comment = comment;
    }
}

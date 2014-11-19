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
public class TopicType extends Entity {

    private String name;

    /**
     * For Hibernate use only
     */
    public TopicType() {
    }

    public TopicType(String name) {
        this.name = name;
    }

    /**
     * Gets name of the type
     *
     * @return name of the type
     */
    public String getName() {
        return name;
    }


    /**
     * Sets specified name to the type
     *
     * @param name name to be set
     */
    public void setName(String name) {
        this.name = name;
    }
}

/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.poulpe.model.entity;

/**
 * Represent topic types on the page of general configuration
 * 
 * @author Pavel Vervenko
 */
public class TopicType extends Persistent {

    private String title;
    private String description;

    /**
     * Default constructor of entity.
     */
    public TopicType() {
    }

    /**
     * Construct TopicType with specified title and description.
     * @param title title
     * @param description description 
     */
    public TopicType(String title, String description) {
        this.title = title;
        this.description = description;
    }

    /**
     * Get the TopicType description.
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of TopicType.
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the title of the TopicType.
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title of TopicType.
     * @param title title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
}

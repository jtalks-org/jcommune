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

import java.util.ArrayList;
import java.util.List;

/**
 * Forum branch that contains topics related to branch theme.
 *
 * @author Pavel Vervenko
 */
public class Branch extends Persistent {

    private String name;
    private String description;
    private boolean deleted;

    /**
     * Set branch name which briefly describes the topics contained in it.
     *
     * @return branch name
     */
    public String getName() {
        return name;
    }

    /**
     * Get branch name.
     *
     * @param name branch name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get branch description.
     *
     * @return branch description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set branch description which contains additional information about the branch.
     *
     * @param description branch description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Check if branch marked is deleted.
     * @return deleted
     */
    public boolean getDeleted() {
        return deleted;
    }

    /**
     * Mark branch as deleted.
     * @param deleted 
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}

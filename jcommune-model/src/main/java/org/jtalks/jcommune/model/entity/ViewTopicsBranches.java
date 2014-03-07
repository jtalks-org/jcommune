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
 * Stores information about the branch on which a group of users are allowed to view
 *
 * @author Mikhail Zaitsev
 */
public class ViewTopicsBranches extends Entity {

    private Long branchId;
    private String sid;
    private Boolean granting;

    /**
     * @return id branch
     */
    public Long getBranchId() {
        return branchId;
    }

    /**
     * Set branch id on which a group of users are allowed to view
     *
     * @param branchId branch id
     */
    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    /**
     * @return sid - group or anonymous users
     */
    public String getSid() {
        return sid;
    }

    /**
     * Set sid - group or anonymous users
     *
     * @param sid sid
     */
    public void setSid(String sid) {
        this.sid = sid;
    }

    /**
     * Get 'Allowed' or 'Restricted' group for branch
     * @return true-allowed, false-restricted
     */
    public Boolean getGranting() {
        return granting;
    }

    /**
     * Set 'Allowed' or 'Restricted' group for branch
     * @param granting true-allowed, false-restricted
     */
    public void setGranting(Boolean granting) {
        this.granting = granting;
    }
}

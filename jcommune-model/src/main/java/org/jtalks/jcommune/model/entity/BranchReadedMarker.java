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

import org.joda.time.DateTime;
import org.jtalks.common.model.entity.Entity;

/**
 * Needed to store time of marking branch as read for every user.
 * Mark every topic in branch is very slow operation.
 * @see org.jtalks.jcommune.model.entity.LastReadPost
 *
 * @author Mikhail Stryzhonok
 */
public class BranchReadedMarker extends Entity {

    private JCUser user;
    private Branch branch;
    private DateTime markTime;

    /**
     * Needed for hibernate usage
     */
    public BranchReadedMarker() {
    }

    /**
     * Constructs marker from user and branch and sets current time
     *
     * @param user user which marks branch as read
     * @param branch branch to be marked
     */
    public BranchReadedMarker(JCUser user, Branch branch) {
        this.user = user;
        this.branch = branch;
        this.markTime = new DateTime();
    }

    /**
     * Gets the user who marked branch as read
     *
     * @return the user who marked branch as read
     */
    public JCUser getUser() {
        return user;
    }

    /**
     * Sets specified user to marker
     *
     * @param user user to be set
     */
    public void setUser(JCUser user) {
        this.user = user;
    }

    /**
     * Gets the branch which was marked as read
     *
     * @return the branch which was marked as read
     */
    public Branch getBranch() {
        return branch;
    }

    /**
     * Sets marked as read branch
     *
     * @param branch branch to be set
     */
    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    /**
     * Gets time of marking the branch as read
     *
     * @return time of marking the branch as read
     */
    public DateTime getMarkTime() {
        return markTime;
    }

    /**
     * Sets time of marking branch as read
     *
     * @param markTime time to be set
     */
    public void setMarkTime(DateTime markTime) {
        this.markTime = markTime;
    }
}

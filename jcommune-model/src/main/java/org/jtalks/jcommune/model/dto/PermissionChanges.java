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
package org.jtalks.jcommune.model.dto;

import com.google.common.collect.ImmutableList;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.permissions.JtalksPermission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * DTO container represents changes which needs to be provided by ACL level for specified {@link org.jtalks.common.model.permissions.JtalksPermission}.
 *
 * @author stanislav bashkirtsev
 * @author Vyacheslav Zhivaev
 */
public class PermissionChanges {
    private final JtalksPermission permission;
    private final List<Group> newlyAddedGroups = new ArrayList<Group>();
    private final List<Group> removedGroups = new ArrayList<Group>();

    /**
     * Constructs the object with given {@link org.jtalks.common.model.permissions.JtalksPermission} instance.
     *
     * @param permission type of permission
     */
    public PermissionChanges(JtalksPermission permission) {
        this.permission = permission;
    }

    /**
     * Constructs the object with given {@link org.jtalks.common.model.permissions.JtalksPermission} instance.
     *
     * @param permission       type of permission
     * @param newlyAddedGroups the collection of newly added groups
     * @param removedGroups    the collection of removed groups
     */
    public PermissionChanges(JtalksPermission permission, Collection<Group> newlyAddedGroups,
                             Collection<Group> removedGroups) {
        this.permission = permission;
        this.newlyAddedGroups.addAll(newlyAddedGroups);
        this.removedGroups.addAll(removedGroups);
    }

    /**
     * Gets newly added permissions.
     *
     * @return Group[] with newly permissions
     */
    public Group[] getNewlyAddedGroupsAsArray() {
        return newlyAddedGroups.toArray(new Group[newlyAddedGroups.size()]);
    }

    /**
     * Sets newly added permissions.
     *
     * @param newlyAddedGroups - list of newly added permissions
     */
    public void addNewlyAddedGroups(Collection<Group> newlyAddedGroups) {
        this.newlyAddedGroups.addAll(newlyAddedGroups);
    }

    /**
     * Gets removed permissions.
     *
     * @return Group[] with removed permissions
     */
    public List<Group> getRemovedGroups() {
        return ImmutableList.copyOf(removedGroups);
    }

    /**
     * Gets removed permissions.
     *
     * @return Group[] with removed permissions
     */
    public Group[] getRemovedGroupsAsArray() {
        return removedGroups.toArray(new Group[removedGroups.size()]);
    }

    /**
     * Sets removed permissions.
     *
     * @param removedGroups - list with removed permissions
     */
    public void addRemovedGroups(Collection<Group> removedGroups) {
        this.removedGroups.addAll(removedGroups);
    }

    /**
     * Gets permission.
     *
     * @return {@link org.jtalks.common.model.permissions.JtalksPermission}
     */
    public JtalksPermission getPermission() {
        return permission;
    }

    /**
     * Checks {@link PermissionChanges} is empty.
     *
     * @return {@code true} if empty, else return {@code false}
     */
    public boolean isEmpty() {
        return removedGroups.isEmpty() && newlyAddedGroups.isEmpty();
    }
}

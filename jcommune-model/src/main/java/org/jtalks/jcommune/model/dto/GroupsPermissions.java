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

import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.common.model.permissions.ProfilePermission;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Container for permissions and related to it access lists. Contains several methods to simple management of access
 * lists.
 *
 * @author Vyacheslav Zhivaev
 */
public class GroupsPermissions {
    private final ConcurrentMap<JtalksPermission, GroupAccessList> accessListMap = new ConcurrentSkipListMap<>(new PermissionComparator());

    /**
     * Constructs {@link GroupsPermissions} with empty internal state. Use add* methods to fill this map.
     */
    public GroupsPermissions() {
        // NOOP
    }

    /**
     * Constructs {@link GroupsPermissions} with given list of permissions and empty restrict\allow data.
     *
     * @param permissions to be added to the access lists
     */
    public <T extends JtalksPermission> GroupsPermissions(List<T> permissions) {
        for (JtalksPermission permission : permissions) {
            accessListMap.put(permission, new GroupAccessList());
        }
    }

    /**
     * Constructs {@link GroupsPermissions} with predefined values to be added to the access lists.
     *
     * @param accessLists values to initialize this container
     */
    public GroupsPermissions(Map<JtalksPermission, GroupAccessList> accessLists) {
        accessListMap.putAll(accessLists);
    }

    /**
     * Adds new permission to the access list.
     *
     * @param permission to be added
     * @param toAllow    group to allow
     * @param toRestrict group to restrict
     * @return {@code this} instance for providing fluent interface
     */
    public GroupsPermissions add(JtalksPermission permission, Group toAllow, Group toRestrict) {
        accessListMap.putIfAbsent(permission, new GroupAccessList());
        accessListMap.get(permission).addAllowed(toAllow).addRestricted(toRestrict);
        return this;
    }

    /**
     * Adds new 'allowed' permission.
     *
     * @param permission the permission to add
     * @param group      the group for which permission added
     * @return {@code this} instance for providing fluent interface
     */
    public GroupsPermissions addAllowed(JtalksPermission permission, Group group) {
        return add(permission, group, null);
    }

    /**
     * Adds new 'restricted' permission.
     *
     * @param permission the permission to add
     * @param group      the group for which permission added
     * @return {@code this} instance for providing fluent interface
     */
    public GroupsPermissions addRestricted(JtalksPermission permission, Group group) {
        return add(permission, null, group);
    }

    /**
     * Based on 'allow' flag, add 'allow' permission (if it's {@code true}), or 'restrict' permission on it
     * (otherwise).
     *
     * @param permission the permission to add
     * @param group      the group for which permission added
     * @param allow      {@code true} if allowance is needed, {@code false} otherwise
     * @return {@code this} instance for providing fluent interface
     */
    public GroupsPermissions add(JtalksPermission permission, Group group, boolean allow) {
        return (allow) ? addAllowed(permission, group) : addRestricted(permission, group);
    }

    /**
     * For given permission, retrieves list of {@link org.jtalks.common.model.entity.Group} that are allowed.
     *
     * @param permission the permission to get for
     * @return list of {@link org.jtalks.common.model.entity.Group}, list instance is UNMODIFIABLE
     */
    public List<Group> getAllowed(JtalksPermission permission) {
        GroupAccessList groupAccessList = accessListMap.get(permission);
        if (groupAccessList == null) {
            return Collections.unmodifiableList(new ArrayList<Group>());
        } else {
            return Collections.unmodifiableList(groupAccessList.getAllowed());
        }
    }

    /**
     * For given permission, retrieves list of {@link org.jtalks.common.model.entity.Group} that are restricted.
     *
     * @param permission the permission to get for
     * @return list of {@link org.jtalks.common.model.entity.Group}, list instance is UNMODIFIABLE
     */
    public List<Group> getRestricted(JtalksPermission permission) {
        GroupAccessList groupAccessList = accessListMap.get(permission);
        if (groupAccessList == null) {
            return Collections.unmodifiableList(new ArrayList<Group>());
        } else {
            return Collections.unmodifiableList(groupAccessList.getRestricted());
        }
    }

    /**
     * For given permission, retrieves list of {@link org.jtalks.common.model.entity.Group} that are allowed or restricted relative to parameter {@code
     * allowed}.
     *
     * @param permission the permission to get for
     * @param allowed    the flag indicating which type of groups needed: allowed (if {@code true}) or restricted
     * @return list of {@link org.jtalks.common.model.entity.Group}, list instance is UNMODIFIABLE
     */
    public List<Group> get(JtalksPermission permission, boolean allowed) {
        return (allowed) ? getAllowed(permission) : getRestricted(permission);
    }

    /**
     * Gets all permissions defined in this map.
     *
     * @return set of all permissions defined in this map, set instance is UNMODIFIABLE
     */
    public Set<JtalksPermission> getPermissions() {
        return Collections.unmodifiableSet(accessListMap.keySet());
    }

    /**
     * Access List Map is a internal object - map where key is the permission of type <T extends JtalksPermission> and
     * the value is GroupAccessList object containing groups which have this permission ("allowed")
     * or do not have ("restricted").
     * This method returns copy of the Access List Map where keys are the same like in the original map.
     * Values (GroupAccessList) are newly created objects based on data from the values in the original map
     * but they were created with unmodified list with data from the original allowed and restricted group lists.
     * @return copy of the Access List Map
     */
    public Map<JtalksPermission, GroupAccessList> getAccessListMap() {
        Map<JtalksPermission, GroupAccessList > accessListMapCopy = new HashMap <>();

        Set<JtalksPermission> permissions = getPermissions();
        for (JtalksPermission permission : permissions) {
            GroupAccessList groupAccessList = new GroupAccessList();
            groupAccessList.setAllowed(getAllowed(permission));
            groupAccessList.setRestricted(getRestricted(permission));
            accessListMapCopy.put(permission, groupAccessList);
        }

        return accessListMapCopy;
    }

    /**
     * Gets groups permissions with all the profile permissions but no groups restricted or allowed.
     *
     * @return groups permissions with all the profile permissions but no groups restricted or allowed
     */
    public static GroupsPermissions profilePermissions() {
        return new GroupsPermissions(ProfilePermission.getAllAsList());
    }

    public static class PermissionComparator implements Comparator<JtalksPermission> {

        @Override
        public int compare(JtalksPermission o1, JtalksPermission o2) {
            return o1.getMask() - o2.getMask();
        }
    }
}

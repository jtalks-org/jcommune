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
package org.jtalks.jcommune.service.security;

import org.jtalks.common.model.entity.Component;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.jcommune.model.dto.GroupsPermissions;
import org.jtalks.jcommune.model.dto.PermissionChanges;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.service.security.acl.AclClassName;
import org.jtalks.jcommune.service.security.acl.AclGroupPermissionEvaluator;

import java.util.List;

/**
 * Service for checking permission like @PreAuthorize('hasPermission(...)') annotation does. Intended to be used when
 * we can't pass required IDs to methods (e.g. branch ID in code review addComment method).
 * <p>See <a href="http://jtalks.org/display/jtalks/Managing+Permissions">Permission Management Vision</a> if you're not
 * familiar with the concept of how the permissions are granted or restricted.</p>
 *
 * @author Vyacheslav Mishcheryakov
 * @see {@link AclGroupPermissionEvaluator}
 */
public interface PermissionService {

    /**
     * Checks if current user is granted with permission
     *
     * @param targetId    the identifier for the object instance
     * @param targetClass the target's ACL class (e.g. 'BRANCH' or 'USER'). Not null.
     * @param permission  permission to check. Not null.
     * @return true if the permission is granted, false otherwise
     */
    boolean hasPermission(long targetId, AclClassName targetClass, JtalksPermission permission);

    /**
     * Checks if current user is granted with permission
     *
     * @param targetId   the identifier for the object instance
     * @param targetType a String representing the target's type (e.g. 'BRANCH' or 'USER'). Not null.
     * @param permission a representation of the permission object as supplied by the expression system. Not null.
     * @return true if the permission is granted, false otherwise
     */
    boolean hasPermission(long targetId, String targetType, String permission);

    /**
     * Checks if current user can create topic provided by plugin
     *
     * @param branchId id of the branch where topic will be created
     * @param type type of the topic
     *
     * @return true if the permission is granted, false otherwise
     */
    boolean canCreatePlugableTopic(long branchId, String type);

    /**
     * Emulates @PreAuthorize('hasPermission(...)')
     *
     * @param targetId    the identifier for the object instance
     * @param targetClass target's ACL class (e.g. 'BRANCH' or 'USER'). Not null.
     * @param permission  permission to check. Not null.
     * @throws org.springframework.security.access.AccessDeniedException
     *          if current user is not granted with permission
     */
    void checkPermission(long targetId, AclClassName targetClass, JtalksPermission permission);

    /**
     * Checks whether current user has a specified branch permission for the specified branch. Note, that you can use
     * other hasXxx() methods, but this one is convenient short-hand.
     *
     * @param branchId   the id of the branch to check permission for
     * @param permission the permission to check whether current user is granted to perform the action
     * @return true if current user is granted to the permission, false if she either she is not granted, or she is
     *         restricted from this permission
     */
    <T extends JtalksPermission> boolean hasBranchPermission(long branchId, T permission);

    /**
     * Return access lists for branch.
     *
     * @param branch branch which will be returned access list
     * @return access list
     */
    GroupsPermissions getPermissionsFor(Branch branch);

    /**
     * Change grants for branch.
     *
     * @param branch  branch to which grants will be changed
     * @param changes grants for branch
     */
    void changeGrants(Branch branch, PermissionChanges changes);

    /**
     * Change restriction for branch.
     *
     * @param branch  branch to which restriction will be changed
     * @param changes new restriction for branch
     */
    void changeRestrictions(Branch branch, PermissionChanges changes);

    /**
     * Gets {@link GroupsPermissions} for defined
     * {@link Component}.
     *
     * @param component the component to get for
     * @return {@link GroupsPermissions} for defined
     *         {@link Component}
     */
    GroupsPermissions getPermissionsMapFor(Component component);

    /**
     * Change grants for component.
     *
     * @param component the component to change for
     * @param changes   the {@link PermissionChanges} which needs to be applied
     * @see PermissionChanges
     */
    void changeGrants(Component component, PermissionChanges changes);

    /**
     * Change restrictions for component.
     *
     * @param component the component to change for
     * @param changes   the {@link PermissionChanges} which needs to be applied
     */
    void changeRestrictions(Component component, PermissionChanges changes);

    /**
     * Return PersonalPermissions access lists for all available {@link org.jtalks.common.model.entity.Group}'s.
     *
     * @param groups all groups
     * @return access list
     */
    GroupsPermissions getPersonalPermissions(List<Group> groups);

    /**
     * Change grants for group.
     *
     * @param group   group to which grants will be changed
     * @param changes grants for group
     */
    void changeGrants(Group group, PermissionChanges changes);

    /**
     * Change restriction for group.
     *
     * @param group   group to which restriction will be changed
     * @param changes new restriction for group
     */
    void changeRestrictions(Group group, PermissionChanges changes);
}

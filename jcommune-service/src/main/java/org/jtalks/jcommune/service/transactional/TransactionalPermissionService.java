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
package org.jtalks.jcommune.service.transactional;

import org.jtalks.common.model.entity.Component;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.common.service.security.SecurityContextFacade;
import org.jtalks.jcommune.model.dto.GroupsPermissions;
import org.jtalks.jcommune.model.dto.PermissionChanges;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.plugin.api.PluginLoader;
import org.jtalks.jcommune.plugin.api.core.Plugin;
import org.jtalks.jcommune.plugin.api.core.TopicPlugin;
import org.jtalks.jcommune.plugin.api.filters.TypeFilter;
import org.jtalks.jcommune.service.security.*;
import org.jtalks.jcommune.service.security.acl.AclClassName;
import org.jtalks.jcommune.service.security.acl.AclGroupPermissionEvaluator;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Implementation of {@link org.jtalks.jcommune.service.security.PermissionService} interface
 *
 * @author Vyacheslav Mishcheryakov
 */
public class TransactionalPermissionService implements PermissionService {
    /**
     * Pattern to build permission full name, here is an example of eventual string:
     * {@code BranchPermissions.EDIT_OWN_POSTS}
     */
    private static final String PERMISSION_FULLNAME_PATTERN = "%s.%s";
    private SecurityContextFacade contextFacade;
    private AclGroupPermissionEvaluator aclEvaluator;
    private PermissionManager permissionManager;
    private PluginLoader pluginLoader;

    /**
     * @param contextFacade to get {@link Authentication} object from security context
     * @param aclEvaluator  to evaluate permissions
     */
    public TransactionalPermissionService(SecurityContextFacade contextFacade,
                                          AclGroupPermissionEvaluator aclEvaluator,
                                          PermissionManager permissionManager) {
        this.contextFacade = contextFacade;
        this.aclEvaluator = aclEvaluator;
        this.permissionManager = permissionManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPermission(long targetId, AclClassName targetClass, JtalksPermission permission) {
        String stringPermission = String.format(PERMISSION_FULLNAME_PATTERN,
                permission.getClass().getSimpleName(), permission.getName());
        return hasPermission(targetId, targetClass.toString(), stringPermission);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPermission(long targetId, String targetType, String permission) {
        Authentication authentication = contextFacade.getContext().getAuthentication();
        return aclEvaluator.hasPermission(authentication, targetId, targetType, permission);
    }

    @Override
    public boolean canCreatePlugableTopic(long targetId, String type) {
        List<Plugin> plugins = pluginLoader.getPlugins(new TypeFilter(TopicPlugin.class));
        for (Plugin plugin : plugins) {
            TopicPlugin topicPlugin = (TopicPlugin) plugin;
            if (topicPlugin.isEnabled() && topicPlugin.getTopicType().equals(type)) {
                return hasBranchPermission(targetId, topicPlugin.getCreateTopicPermission());
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends JtalksPermission> boolean hasBranchPermission(long branchId, T permission) {
        return hasPermission(branchId, AclClassName.BRANCH, permission);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkPermission(long targetId, AclClassName targetClass,
                                JtalksPermission permission) {
        if (!hasPermission(targetId, targetClass, permission)) {
            Authentication authentication = contextFacade.getContext().getAuthentication();
            throw new AccessDeniedException(
                    "Access denied for " + authentication.getName() + ". "
                            + targetClass + ": " + targetId
                            + ", permission - " + permission.getName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupsPermissions getPermissionsFor(Branch branch) {
        return permissionManager.getPermissionsMapFor(branch);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeGrants(Branch branch, PermissionChanges changes) {
        permissionManager.changeGrants(branch, changes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeRestrictions(Branch branch, PermissionChanges changes) {
        permissionManager.changeRestrictions(branch, changes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupsPermissions getPermissionsMapFor(Component component) {
        return permissionManager.getPermissionsMapFor(component);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeGrants(Component component, PermissionChanges changes) {
        permissionManager.changeGrants(component, changes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeRestrictions(Component component, PermissionChanges changes) {
        permissionManager.changeRestrictions(component, changes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupsPermissions getPersonalPermissions(List<Group> groups) {
        return permissionManager.getPermissionsMapFor(groups);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeGrants(Group group, PermissionChanges changes) {
        permissionManager.changeGrants(group, changes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeRestrictions(Group group, PermissionChanges changes) {
        permissionManager.changeRestrictions(group, changes);
    }


}

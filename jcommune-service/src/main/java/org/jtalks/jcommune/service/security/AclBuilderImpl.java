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

import org.jtalks.common.model.entity.Entity;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kirill Afonin
 */
public class AclBuilderImpl implements AclBuilder {
    /**
     * Possible actions that will be performed when builder finished.
     */
    public enum Action {
        DELETE, GRANT
    }

    private List<Sid> sids = new ArrayList<Sid>();
    private List<Permission> permissions = new ArrayList<Permission>();
    private Entity target;
    private AclManager aclManager;
    private Action action;

    /**
     * Constructor.
     *
     * @param aclManager instance of manager
     * @param action     action that will be executed when you call
     *                   {@link AclBuilder#on(org.jtalks.jcommune.model.entity.Entity)
     */
    public AclBuilderImpl(AclManager aclManager, Action action) {
        this.aclManager = aclManager;
        this.action = action;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AclBuilder user(String username) {
        sids.add(new PrincipalSid(username));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AclBuilder role(String role) {
        sids.add(new GrantedAuthoritySid(role));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AclBuilder admin() {
        permissions.add(BasePermission.ADMINISTRATION);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AclBuilder read() {
        permissions.add(BasePermission.READ);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AclBuilder write() {
        permissions.add(BasePermission.WRITE);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AclBuilder delete() {
        permissions.add(BasePermission.DELETE);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AclBuilder create() {
        permissions.add(BasePermission.CREATE);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AclBuilder on(Entity object) {
        target = object;
        if (sids.isEmpty() || permissions.isEmpty()) {
            throw new IllegalStateException("You can't grant permissions without sids or permissions");
        }
        executeUpdate();
        return this;
    }

    /**
     * Performs selected action.
     */
    private void executeUpdate() {
        if (action == Action.GRANT) {
            aclManager.grant(sids, permissions, target);
        } else {
            aclManager.delete(sids, permissions, target);
        }
        sids.clear();
        permissions.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsSid(String name) {
        for (Sid sid : sids) {
            if (sid instanceof PrincipalSid) {
                if (((PrincipalSid) sid).getPrincipal().equals(name)) {
                    return true;
                }
            } else if (sid instanceof GrantedAuthoritySid) {
                if (((GrantedAuthoritySid) sid).getGrantedAuthority().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sid> getSids() {
        return new ArrayList<Sid>(sids);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Permission> getPermissions() {
        return new ArrayList<Permission>(permissions);
    }
}

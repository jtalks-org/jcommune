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

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;

import javax.transaction.NotSupportedException;

public class PermissionExpressionRoot extends SecurityExpressionRoot {
    private PermissionEvaluator permissionEvaluator;

    PermissionExpressionRoot(Authentication a) {
        super(a);
    }

    public void setPermissionEvaluator(PermissionEvaluator permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }

    public final boolean hasAnyRole(String roles) {
        String[] rolesList = roles.split(",");
        for (String role : rolesList) {
            AdministrationGroup administrationGroup = AdministrationGroup.getAdministrationGroupByName(role);
            if (administrationGroup == AdministrationGroup.ANONYMOUS) {
                return super.hasAnyRole(rolesList);
            } else if (permissionEvaluator.hasPermission(authentication, administrationGroup.getId(), null)) {
                return true;
            }
        }
        return false;
    }

    public final boolean hasAnyRole(String targetId, String targetType, String permission) {
        return permissionEvaluator.hasPermission(authentication, targetId, targetType, permission);
    }

    @Deprecated
    public final boolean hasAnyRole(String arg, String... args) throws NotSupportedException {
        throw new NotSupportedException("This method is not supported. " +
                "Please use hasAnyRole(String targetId, String targetType, String permission) " +
                "or hasAnyRole(String roles)");
    }
}

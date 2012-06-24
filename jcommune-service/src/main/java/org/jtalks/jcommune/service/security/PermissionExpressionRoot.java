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
import org.springframework.security.web.FilterInvocation;

import javax.transaction.NotSupportedException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PermissionExpressionRoot extends SecurityExpressionRoot {
    private PermissionEvaluator permissionEvaluator;
    private FilterInvocation filterInvocation;

    PermissionExpressionRoot(Authentication a) {
        super(a);
    }

    public void setPermissionEvaluator(PermissionEvaluator permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }

    public final boolean hasPermission(String targetIdName, String targetType, String permission) {
        URL url;
        try {
            url = new URL(filterInvocation.getFullRequestUrl());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Filter URL is invalid.", e);
        }
        String[] params = url.getQuery().split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params) {
            String[] par = param.split("=");
            if (par.length == 2) {
                map.put(par[0], par[1]);
            }
        }
        String targetId = map.get(targetIdName);
        return permissionEvaluator.hasPermission(authentication, targetId, targetType, permission);
    }

    @Deprecated
    public final boolean hasAnyRole(String arg, String... args) throws NotSupportedException {
        throw new NotSupportedException("This method is not supported. " +
                "Please use hasAnyRole(String targetId, String targetType, String permission) " +
                "or hasAnyRole(String roles)");
    }

    public void setFilterInvocation(FilterInvocation filterInvocation) {
        this.filterInvocation = filterInvocation;
    }
}

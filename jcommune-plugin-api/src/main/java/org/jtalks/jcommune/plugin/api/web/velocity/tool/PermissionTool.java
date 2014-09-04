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
package org.jtalks.jcommune.plugin.api.web.velocity.tool;

import org.jtalks.common.service.security.SecurityContextFacade;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

/**
 * Custom tool to check if user have any permission inside velocity template
 *
 * @author Mikhail Stryzhonok
 */
public class PermissionTool {
    private PermissionEvaluator aclEvaluator;
    private SecurityContextFacade securityContextFacade;

    public PermissionTool(ApplicationContext ctx) {
        aclEvaluator = ctx.getBean(PermissionEvaluator.class);
        securityContextFacade = ctx.getBean(SecurityContextFacade.class);
    }

    public boolean hasPermission(Long targetId, String targetType, String permission) {
        Authentication authentication = securityContextFacade.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        } else {
            return aclEvaluator.hasPermission(authentication, targetId, targetType, permission);
        }
    }
}

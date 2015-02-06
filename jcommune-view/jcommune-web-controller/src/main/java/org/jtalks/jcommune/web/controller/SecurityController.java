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
package org.jtalks.jcommune.web.controller;

import org.jtalks.jcommune.service.security.PermissionService;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponse;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for secuirty related REST methods.
 * 
 * @author Vyacheslav Mishcheryakov
 *
 */
@Controller
public class SecurityController {

    private PermissionService permissionService;
    

    /**
     * 
     * @param permissionService to check permissions for current user
     */
    @Autowired
    public SecurityController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * Checks if current user is granted with permission
     * @param targetId the identifier for the object instance
     * @param targetType a String representing the target's type (e.g. 'BRANCH' or 'USER'). Not null.
     * @param permission a representation of the permission object as supplied by the expression system. Not null.
     * @return JSON response with 'status' = 'success' if user is granted and 
     *      'status' = 'fail' otherwise. 'result' object is null in both cases 
     */
    @RequestMapping(value="/security/haspermission", method=RequestMethod.GET)
    @ResponseBody
    public JsonResponse hasPermission(
            @RequestParam("targetId") long targetId,
            @RequestParam("targetType") String targetType,
            @RequestParam("permission") String permission) {
        if (permissionService.hasPermission(targetId, targetType, permission)) {
            return new JsonResponse(JsonResponseStatus.SUCCESS);
        } else {
            return new JsonResponse(JsonResponseStatus.FAIL);
        }
    }
    
}

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
import org.jtalks.jcommune.web.dto.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author Vyacheslav Mishcheryakov
 *
 */
@Controller
public class SecurityController {

    private PermissionService permissionService;
    

    @Autowired
    public SecurityController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @RequestMapping(value="/security/has-permission", method=RequestMethod.GET)
    @ResponseBody
    public JsonResponse hasPermission(
            @RequestParam("targetId") long targetId,
            @RequestParam("targetType") String targetType,
            @RequestParam("permission") String permission) {
        if (permissionService.hasPermission(targetId, targetType, permission)) {
            return new JsonResponse(JsonResponse.RESPONSE_STATUS_SUCCESS);
        } else {
            return new JsonResponse(JsonResponse.RESPONSE_STATUS_FAIL);
        }
    }
    
}

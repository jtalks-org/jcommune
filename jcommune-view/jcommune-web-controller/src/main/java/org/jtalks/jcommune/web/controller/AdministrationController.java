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

import org.jtalks.jcommune.model.entity.ComponentInformation;
import org.jtalks.jcommune.service.ComponentService;
import org.jtalks.jcommune.web.dto.json.JsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Andrei Alikov
 * Controller for processing forum administration related requests
 */
@Controller
public class AdministrationController {
    private static final String ADMIN_ATTRIBUTE_NAME = "adminMode";

    private ComponentService componentService;

    @Autowired
    ServletContext servletContext;

    /**
     * Creates instance of the service
     * @param componentService service to work with the forum component
     */
    @Autowired
    public AdministrationController(ComponentService componentService) {
        this.componentService = componentService;
    }

    /**
     * Change mode to Administrator mode in which
     * @param request Client request
     * @return redirect back to previous page
     */
    @RequestMapping(value = "/admin/enter", method = RequestMethod.GET)
    @PreAuthorize("hasPermission(#componentService.componentOfForum.id, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public String enterAdministrationMode(HttpServletRequest request) {
        request.getSession().setAttribute(ADMIN_ATTRIBUTE_NAME, true);

        return getRedirectToPrevPage(request);
    }

    /**
     * Return back from Administrator mode to Normal mode
     * @param request Client request
     * @return redirect back to previous page
     */
    @RequestMapping(value = "/admin/exit", method = RequestMethod.GET)
    public String exitAdministrationMode(HttpServletRequest request) {
        request.getSession().removeAttribute(ADMIN_ATTRIBUTE_NAME);

        return getRedirectToPrevPage(request);
    }

    @RequestMapping(value = "/admin/edit_ajax", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasPermission(#componentService.componentOfForum.id, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public JsonResponse setForumInformation(@Valid @RequestBody ComponentInformation componentInformation, BindingResult result) {
        if (result.hasErrors()) {
            return new JsonResponse(JsonResponseStatus.FAIL, result.getAllErrors());
        }

        componentService.setComponentInformation(componentInformation);

        return new JsonResponse(JsonResponseStatus.SUCCESS, null);
    }

    /**
     * Returns logo of the forum
     * @param request
     * @param response
     */
    @RequestMapping(value = "/admin/logo", method = RequestMethod.GET)
    public void getForumLogo(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("image/jpeg");
        try {
            // Temporary solution - load image from the resources
            OutputStream stream = response.getOutputStream();
            InputStream logo = servletContext.getResourceAsStream("/resources/images/jcommune.jpeg");
            byte[] bytes = new byte[100 * 1024];
            logo.read(bytes);
            stream.write(bytes);
            logo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * returns redirect string to previous page
     * @param request Client HTTP request
     */
    private String getRedirectToPrevPage(HttpServletRequest request) {
        return "redirect:" + request.getHeader("Referer");
    }
}

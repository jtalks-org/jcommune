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

import org.jtalks.jcommune.service.AvatarService;
import org.jtalks.jcommune.service.util.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Controller for processing avatar related request.
 *
 * @author Alexandre Teterin
 */

@Controller
public class AvatarController {


    private final AvatarService avatarService;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Constructor for controller instantiating, dependencies injected via autowiring.
     *
     * @param avatarService for avatar manipulation
     */
    @Autowired
    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    /**
     * Process avatar file from request and return avatar preview in response.
     * Used for IE, Opera specific request processing
     *
     * @param request  incoming request
     * @param response outcoming response
     * @return response content
     * @throws javax.servlet.ServletException avatar processing problem
     */
    @RequestMapping(value = "/users/ieAvatarpreview", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, String>
    uploadAvatarFromIE(DefaultMultipartHttpServletRequest request,
                       HttpServletResponse response) throws ServletException {

        Map<String, MultipartFile> fileMap = request.getFileMap();
        Collection<MultipartFile> fileCollection = fileMap.values();
        Iterator<MultipartFile> fileIterator = fileCollection.iterator();
        CommonsMultipartFile file = (CommonsMultipartFile) fileIterator.next();

        Map<String, String> responseContent = new HashMap<String, String>();
        try {
            byte[] bytes = file.getBytes();
            prepareNormalResponse(bytes, response, responseContent);
        } catch (IOException e) {
            prepareErrorResponse(response, responseContent, e);
        }

        return responseContent;
    }

    /**
     * Process avatar file from request and return avatar preview in response.
     * Used for FF, Chrome specific request processing
     *
     * @param bytes    input avatar data
     * @param response servlet response
     * @return response content
     * @throws javax.servlet.ServletException avatar processing problem
     */
    @RequestMapping(value = "/users/avatarpreview", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, String>
    uploadAvatar(@RequestBody byte[] bytes,
                 HttpServletResponse response) throws ServletException {

        Map<String, String> responseContent = new HashMap<String, String>();
        try {
            prepareNormalResponse(bytes, response, responseContent);
        } catch (IOException e) {
            prepareErrorResponse(response, responseContent, e);
        }

        return responseContent;
    }

    /**
     * Used for prepare normal response
     *
     * @param bytes           input avatar data
     * @param response        output response
     * @param responseContent response payload
     * @throws IOException avatar processing problem
     */
    private void prepareNormalResponse(byte[] bytes,
                                       HttpServletResponse response,
                                       Map<String, String> responseContent) throws IOException {
        String srcImage = avatarService.convertAvatarToBase64String(bytes);
        response.setStatus(HttpServletResponse.SC_OK);
        responseContent.put("success", "true");
        responseContent.put("srcPrefix", ImageUtils.HTML_SRC_TAG_PREFIX);
        responseContent.put("srcImage", srcImage);
    }

    /**
     * Used for prepare error response
     *
     * @param response        output response
     * @param responseContent response payload
     * @param e               avatar processing problem
     */
    private void prepareErrorResponse(HttpServletResponse response,
                                      Map<String, String> responseContent, IOException e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        responseContent.put("success", "false");
        logger.error(UserController.class.getName() + "has thrown an exception: " + e.getMessage());
    }


}

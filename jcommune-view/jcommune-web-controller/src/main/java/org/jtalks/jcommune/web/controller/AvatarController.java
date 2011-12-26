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

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.AvatarService;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.ImageUtils;
import org.jtalks.jcommune.web.dto.EditUserProfileDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(AvatarController.class);
    private AvatarService avatarService;
    private SecurityService securityService;
    private UserService userService;

    /**
     * Constructor for controller instantiating, dependencies injected via autowiring.
     *
     * @param avatarService   for avatar manipulation
     * @param securityService for current user-related operations
     * @param userService     to manipulate user-related data
     */
    @Autowired
    public AvatarController(AvatarService avatarService, SecurityService securityService, UserService userService) {
        this.avatarService = avatarService;
        this.securityService = securityService;
        this.userService = userService;
    }

    /**
     * Process avatar file from request and return avatar preview in response.
     * Used for IE, Opera specific request processing
     *
     * @param request incoming request
     * @return ResponseEntity
     * @throws javax.servlet.ServletException avatar processing problem
     */
    @RequestMapping(value = "/users/IFrameAvatarpreview", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> uploadAvatarFromIE(DefaultMultipartHttpServletRequest request) throws ServletException {

        Map<String, MultipartFile> fileMap = request.getFileMap();
        Collection<MultipartFile> fileCollection = fileMap.values();
        Iterator<MultipartFile> fileIterator = fileCollection.iterator();
        CommonsMultipartFile file = (CommonsMultipartFile) fileIterator.next();
        HttpHeaders responseHeaders = new HttpHeaders();
        HttpStatus statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        Map<String, String> responseContent = new HashMap<String, String>();
        String body = "";

        try {
            byte[] bytes = file.getBytes();
            prepareNormalResponse(bytes, responseContent);
            body = prepareJSONString(responseContent);
            statusCode = HttpStatus.OK;
        } catch (IOException e) {
            responseContent.clear();
            prepareErrorResponse(responseContent, e);
            try {
                body = prepareJSONString(responseContent);
                statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            } catch (IOException exception) {
                LOGGER.error(new StringBuilder().
                        append(AvatarController.class.getName()).
                        append("has thrown an exception: ").
                        append(exception.getMessage()).toString());
            }
        }

        responseHeaders.setContentType(MediaType.TEXT_HTML);

        return new ResponseEntity<String>(body, responseHeaders, statusCode);
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
    @RequestMapping(value = "/users/XHRavatarpreview", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> uploadAvatar(@RequestBody byte[] bytes,
                                            HttpServletResponse response) throws ServletException {

        Map<String, String> responseContent = new HashMap<String, String>();
        try {
            prepareNormalResponse(bytes, responseContent);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException e) {
            responseContent.clear();
            prepareErrorResponse(responseContent, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return responseContent;
    }

    /**
     * Remove avatar from user profile.
     *
     * @return edit user profile page
     */
    @RequestMapping(value = "/users/edit/avatar", method = RequestMethod.POST)
    public ModelAndView removeAvatarFromCurrentUser() {
        User user = securityService.getCurrentUser();
        userService.removeAvatarFromCurrentUser();
        EditUserProfileDto editedUser = new EditUserProfileDto(user);
        return new ModelAndView("editProfile", "editedUser", editedUser);
    }

    /**
     * Write user avatar in response for rendering it on html pages.
     *
     * @param response        servlet response
     * @param encodedUsername {@link User#getEncodedUsername()}
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *                     if user with given encodedUsername not found
     * @throws IOException throws if an output exception occurred
     */
    @RequestMapping(value = "/{encodedUsername}/avatar", method = RequestMethod.GET)
    public void renderAvatar(HttpServletResponse response, @PathVariable("encodedUsername") String encodedUsername)
            throws NotFoundException, IOException {
        User user = userService.getByEncodedUsername(encodedUsername);
        byte[] avatar = user.getAvatar();
        response.setContentType("image/jpeg");
        response.setContentLength(avatar.length);
        response.getOutputStream().write(avatar);
    }

    /**
     * Used for prepare normal response
     *
     * @param bytes           input avatar data
     * @param responseContent response payload
     * @throws IOException avatar processing problem
     */
    private void prepareNormalResponse(byte[] bytes,
                                       Map<String, String> responseContent) throws IOException {
        String srcImage = avatarService.convertAvatarToBase64String(bytes);
        responseContent.put("success", "true");
        responseContent.put("srcPrefix", ImageUtils.HTML_SRC_TAG_PREFIX);
        responseContent.put("srcImage", srcImage);
    }

    /**
     * Used for prepare error response
     *
     * @param responseContent response payload
     * @param e               avatar processing problem
     */
    private void prepareErrorResponse(Map<String, String> responseContent, IOException e) {
        responseContent.put("success", "false");
        LOGGER.error(new StringBuilder().
                append(AvatarController.class.getName()).
                append("has thrown an exception: ").
                append(e.getMessage()).toString());
    }

    /**
     * Used for prepare JSON string from Map<String, String>
     *
     * @param responseContent input Map<String, String>
     * @return JSON string
     * @throws IOException
     */
    private String prepareJSONString(Map<String, String> responseContent) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        StringWriter stringWriter = new StringWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonGenerator jgen = jsonFactory.createJsonGenerator(stringWriter);
        objectMapper.writeValue(jgen, responseContent);

        return stringWriter.toString();
    }


}

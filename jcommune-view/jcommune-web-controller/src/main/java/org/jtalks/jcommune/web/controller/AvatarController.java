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
import org.jtalks.jcommune.service.exceptions.ImageFormatException;
import org.jtalks.jcommune.service.exceptions.ImageSizeException;
import org.jtalks.jcommune.service.exceptions.ImageUploadException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.ImageUtils;
import org.jtalks.jcommune.web.dto.EditUserProfileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Controller for processing avatar related request.
 *
 * @author Alexandre Teterin
 */

@Controller
public class AvatarController {

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
     * Used for IE, Opera specific request processing.
     *
     * @param request incoming request
     * @return ResponseEntity
     * @throws javax.servlet.ServletException avatar processing problem
     * @throws IOException                    defined in the JsonFactory implementation, caller must implement exception processing
     */
    @RequestMapping(value = "/users/IFrameAvatarpreview", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> uploadAvatar(DefaultMultipartHttpServletRequest request) throws ServletException, IOException {

        //get input file
        Map<String, MultipartFile> fileMap = request.getFileMap();
        Collection<MultipartFile> fileCollection = fileMap.values();
        Iterator<MultipartFile> fileIterator = fileCollection.iterator();
        MultipartFile file = fileIterator.next();

        //prepare response parameters
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_HTML);
        HttpStatus statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        Map<String, String> responseContent = new HashMap<String, String>();
        String body = "";

        return prepareResponse(request, responseHeaders, statusCode, responseContent, body, file);
    }

    /**
     * Process avatar file from request and return avatar preview in response.
     * Used for FF, Chrome specific request processing
     *
     * @param bytes    input avatar data
     * @param request  input request
     * @param response servlet response
     * @return response content
     * @throws javax.servlet.ServletException avatar processing problem
     */
    @RequestMapping(value = "/users/XHRavatarpreview", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> uploadAvatar(@RequestBody byte[] bytes,
                                            ServletRequest request,
                                            HttpServletResponse response) throws ServletException {

        Map<String, String> responseContent = new HashMap<String, String>();
        prepareResponse(bytes, request, response, responseContent);
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
    public void renderAvatar(HttpServletResponse response,
                             @PathVariable("encodedUsername") String encodedUsername) throws NotFoundException,
            IOException {
        User user = userService.getByEncodedUsername(encodedUsername);
        byte[] avatar = user.getAvatar();
        response.setContentType("image/jpeg");
        response.setContentLength(avatar.length);
        response.getOutputStream().write(avatar);
    }

    /**
     * Prepare valid or error response after avatar processing
     *
     * @param request         request with avatar payload
     * @param responseHeaders response HTTP headers
     * @param statusCode      response HTTP status code
     * @param responseContent response content
     * @param body            resulting JSON string response payload
     * @param file            avatar file
     * @return ResponseEntity with avatar processing results
     * @throws IOException defined in the JsonFactory implementation, caller must implement exception processing
     */
    private ResponseEntity<String> prepareResponse(DefaultMultipartHttpServletRequest request,
                                                   HttpHeaders responseHeaders, HttpStatus statusCode,
                                                   Map<String, String> responseContent,
                                                   String body,
                                                   MultipartFile file) throws IOException {
        try {
            avatarService.validateAvatarFormat(file);
            byte[] bytes;
            try {
                bytes = file.getBytes();
            } catch (IOException e) {
                throw new ImageUploadException();
            }
            avatarService.validateAvatarSize(bytes);
            prepareNormalResponse(bytes, responseContent);
            statusCode = HttpStatus.OK;
        } catch (ImageFormatException e) {
            prepareFormatErrorResponse(request, responseContent);
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        } catch (ImageSizeException e) {
            prepareSizeErrorResponse(request, responseContent);
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        } catch (ImageUploadException e) {
            prepareCommonErrorResponse(request, responseContent);
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        body = prepareJSONString(responseContent);

        return new ResponseEntity<String>(body, responseHeaders, statusCode);
    }

    /**
     * Prepare valid or error response after avatar processing
     *
     * @param bytes           input avatar data
     * @param request         used for getting application context
     * @param response        resulting response
     * @param responseContent with avatar processing results
     */
    private void prepareResponse(byte[] bytes, ServletRequest request,
                                 HttpServletResponse response,
                                 Map<String, String> responseContent) {
        try {
            avatarService.validateAvatarSize(bytes);
            prepareNormalResponse(bytes, responseContent);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ImageSizeException e) {
            prepareSizeErrorResponse(request, responseContent);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (ImageUploadException e) {
            prepareCommonErrorResponse(request, responseContent);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Prepare common avatar processing error response content
     *
     * @param request         used for getting application context
     * @param responseContent with avatar processing common error message
     */
    private void prepareCommonErrorResponse(ServletRequest request, Map<String, String> responseContent) {
        responseContent.clear();
        responseContent.put("success", "false");
        responseContent.put("message", getMessage(request, "avatar.500.common.error"));
    }

    /**
     * Prepare invalid size avatar processing error response content
     *
     * @param request         used for getting application context
     * @param responseContent with avatar processing invalid size error message
     */
    private void prepareSizeErrorResponse(ServletRequest request, Map<String, String> responseContent) {
        responseContent.clear();
        responseContent.put("success", "false");
        responseContent.put("message", getMessage(request, "image.wrong.size") + " " + AvatarService.MAX_SIZE);
    }

    /**
     * Prepare invalid format avatar processing error response content
     *
     * @param request         used for getting application context
     * @param responseContent with avatar processing invalid format error message
     */
    private void prepareFormatErrorResponse(ServletRequest request, Map<String, String> responseContent) {
        responseContent.clear();
        responseContent.put("success", "false");
        responseContent.put("message", getMessage(request, "image.wrong.format"));
    }

    /**
     * Used for prepare normal response
     *
     * @param bytes           input avatar data
     * @param responseContent response payload
     * @throws ImageUploadException due to common avatar processing error
     */
    private void prepareNormalResponse(byte[] bytes,
                                       Map<String, String> responseContent) throws ImageUploadException {
        String srcImage;
        srcImage = avatarService.convertAvatarToBase64String(bytes);
        responseContent.put("success", "true");
        responseContent.put("srcPrefix", ImageUtils.HTML_SRC_TAG_PREFIX);
        responseContent.put("srcImage", srcImage);
    }

    /**
     * Used for prepare JSON string from Map<String, String>
     *
     * @param responseContent input Map<String, String>
     * @return JSON string
     * @throws IOException defined in the JsonFactory implementation, caller must implement exception processing
     */
    private String prepareJSONString(Map<String, String> responseContent) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        StringWriter stringWriter = new StringWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonGenerator jgen = jsonFactory.createJsonGenerator(stringWriter);
        objectMapper.writeValue(jgen, responseContent);

        return stringWriter.toString();
    }

    /**
     * Return validation error message
     *
     * @param request used for getting locale
     * @param code    of the validation error message
     * @return validation error message
     */
    private String getMessage(ServletRequest request, String code) {
        WebApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
        Locale locale = RequestContextUtils.getLocale((HttpServletRequest) request);
        return context.getMessage(code, null, locale);
    }


}

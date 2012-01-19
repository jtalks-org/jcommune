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
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.ImageFormatException;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.exceptions.ImageSizeException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.AvatarService;
import org.jtalks.jcommune.service.nontransactional.ImageUtils;
import org.jtalks.jcommune.service.nontransactional.SecurityService;
import org.jtalks.jcommune.web.dto.EditUserProfileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
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
    private ImageUtils imageUtils;
    private MessageSource messageSource;
    public static final String RESULT = "success";

    /**
     * Constructor for controller instantiating, dependencies injected via autowiring.
     *
     * @param avatarService   for avatar manipulation
     * @param securityService for current user-related operations
     * @param userService     to manipulate user-related data
     * @param messageSource   to resolve locale-dependent messages
     * @param imageUtils      to convert image data
     */
    @Autowired
    public AvatarController(AvatarService avatarService, SecurityService securityService, UserService userService,
                            MessageSource messageSource, ImageUtils imageUtils) {
        this.avatarService = avatarService;
        this.securityService = securityService;
        this.userService = userService;
        this.messageSource = messageSource;
        this.imageUtils = imageUtils;
    }

    /**
     * Process avatar file from request and return avatar preview in response.
     * Used for IE, Opera specific request processing.
     *
     * @param request incoming request
     * @param locale  current user locale settings to resolve messages
     * @return ResponseEntity
     * @throws javax.servlet.ServletException avatar processing problem
     * @throws IOException                    defined in the JsonFactory implementation,
     *                                        caller must implement exception processing
     */
    @RequestMapping(value = "/users/IFrameAvatarpreview", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> uploadAvatar(DefaultMultipartHttpServletRequest request, Locale locale)
            throws ServletException, IOException {

        //prepare response parameters
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_HTML);
        Map<String, String> responseContent = new HashMap<String, String>();

        return prepareResponse(request, responseHeaders, responseContent, locale);
    }

    /**
     * Process avatar file from request and return avatar preview in response.
     * Used for FF, Chrome specific request processing
     *
     * @param bytes    input avatar data
     * @param response servlet response
     * @param locale   current user locale settings to resolve messages
     * @return response content
     * @throws ServletException avatar processing problem
     */
    @RequestMapping(value = "/users/XHRavatarpreview", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> uploadAvatar(@RequestBody byte[] bytes,
                                            HttpServletResponse response,
                                            Locale locale) throws ServletException {

        Map<String, String> responseContent = new HashMap<String, String>();
        prepareResponse(bytes, response, responseContent, locale);
        return responseContent;
    }

    /**
     * Remove avatar from user profile.
     *
     * @return edit user profile page
     */
    @RequestMapping(value = "/users/edit/avatar", method = RequestMethod.POST)
    public ModelAndView removeAvatarFromCurrentUser() {
        JCUser user = securityService.getCurrentUser();
        byte[] defaultAvatar = avatarService.getDefaultAvatar();
        user.setAvatar(defaultAvatar);
        EditUserProfileDto editedUser = new EditUserProfileDto(user);
        editedUser.setAvatar(imageUtils.prepareHtmlImgSrc(defaultAvatar));
        return new ModelAndView("editProfile", "editedUser", editedUser);
    }

    /**
     * Write user avatar in response for rendering it on html pages.
     *
     * @param response        servlet response
     * @param encodedUsername {@link JCUser#getEncodedUsername()}
     * @throws NotFoundException if user with given encodedUsername not found
     * @throws IOException       throws if an output exception occurred
     */
    @RequestMapping(value = "/{encodedUsername}/avatar", method = RequestMethod.GET)
    public void renderAvatar(HttpServletResponse response,
                             @PathVariable("encodedUsername") String encodedUsername) throws NotFoundException,
            IOException {
        JCUser user = userService.getByUsername(encodedUsername);
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
     * @param responseContent response content
     * @param locale          current user locale settings to resolve messages
     * @return ResponseEntity with avatar processing results
     * @throws IOException defined in the JsonFactory implementation, caller must implement exception processing
     */
    private ResponseEntity<String> prepareResponse(DefaultMultipartHttpServletRequest request,
                                                   HttpHeaders responseHeaders,
                                                   Map<String, String> responseContent,
                                                   Locale locale) throws IOException {

        HttpStatus statusCode;

        //get input file
        Map<String, MultipartFile> fileMap = request.getFileMap();
        Collection<MultipartFile> fileCollection = fileMap.values();
        Iterator<MultipartFile> fileIterator = fileCollection.iterator();
        MultipartFile file = fileIterator.next();

        try {
            avatarService.validateAvatarFormat(file);
            byte[] bytes = file.getBytes();

            avatarService.validateAvatarSize(bytes);
            prepareNormalResponse(bytes, responseContent);
            statusCode = HttpStatus.OK;
        } catch (ImageFormatException e) {
            prepareFormatErrorResponse(responseContent, locale);
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        } catch (ImageSizeException e) {
            prepareSizeErrorResponse(responseContent, locale);
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        } catch (ImageProcessException e) {
            prepareCommonErrorResponse(responseContent, locale);
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        String body = prepareJSONString(responseContent);

        return new ResponseEntity<String>(body, responseHeaders, statusCode);
    }

    /**
     * Prepare valid or error response after avatar processing
     *
     * @param bytes           input avatar data
     * @param response        resulting response
     * @param responseContent with avatar processing results
     * @param locale          current user locale settings to resolve messages
     */
    private void prepareResponse(byte[] bytes,
                                 HttpServletResponse response,
                                 Map<String, String> responseContent,
                                 Locale locale) {
        try {
            avatarService.validateAvatarSize(bytes);
            prepareNormalResponse(bytes, responseContent);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ImageSizeException e) {
            prepareSizeErrorResponse(responseContent, locale);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (ImageProcessException e) {
            prepareCommonErrorResponse(responseContent, locale);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Prepare common avatar processing error response content
     *
     * @param responseContent with avatar processing common error message
     * @param locale          current user locale settings to resolve messages
     */
    private void prepareCommonErrorResponse(Map<String, String> responseContent, Locale locale) {
        responseContent.clear();
        responseContent.put(RESULT, "false");
        responseContent.put("message", messageSource.getMessage("avatar.500.common.error", null, locale));
    }

    /**
     * Prepare invalid size avatar processing error response content
     *
     * @param responseContent with avatar processing invalid size error message
     * @param locale          current user locale settings to resolve messages
     */
    private void prepareSizeErrorResponse(Map<String, String> responseContent, Locale locale) {
        responseContent.clear();
        responseContent.put(RESULT, "false");
        responseContent.put("message", messageSource.getMessage("image.wrong.size" + " "
                + AvatarService.MAX_SIZE, null, locale));
    }

    /**
     * Prepare invalid format avatar processing error response content
     *
     * @param responseContent with avatar processing invalid format error message
     * @param locale          current user locale settings to resolve messages
     */
    private void prepareFormatErrorResponse(Map<String, String> responseContent, Locale locale) {
        responseContent.clear();
        responseContent.put(RESULT, "false");
        responseContent.put("message", messageSource.getMessage("image.wrong.format", null, locale));
    }

    /**
     * Used for prepare normal response (operation success)
     *
     * @param bytes           input avatar data
     * @param responseContent response payload
     * @throws ImageProcessException due to common avatar processing error
     */
    private void prepareNormalResponse(byte[] bytes,
                                       Map<String, String> responseContent) throws ImageProcessException {
        String srcImage;
        srcImage = avatarService.convertBytesToBase64String(bytes);
        responseContent.put(RESULT, "true");
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


}

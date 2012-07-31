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

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.ImageFormatException;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.exceptions.ImageSizeException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.AvatarService;
import org.jtalks.jcommune.service.nontransactional.ImageUtils;
import org.jtalks.jcommune.web.dto.ErrorDto;
import org.jtalks.jcommune.web.util.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for processing avatar related request.
 * todo: this class is too complex, we need to move some logic either to service or to a helper bean
 *
 * @author Alexandre Teterin
 * @author Anuar Nurmakanov
 */

@Controller
public class AvatarController {
    public static final String RESULT = "success";
    static final String WRONG_FORMAT_RESOURCE_MESSAGE = "image.wrong.format";
    static final String WRONG_SIZE_RESOURCE_MESSAGE = "image.wrong.size";
    static final String COMMON_ERROR_RESOURCE_MESSAGE = "avatar.500.common.error";
    
    private AvatarService avatarService;
    private UserService userService;
    private MessageSource messageSource;
    private JSONUtils jsonUtils;
   

    /**
     * Constructor for controller instantiating, dependencies injected via autowiring.
     *
     * @param avatarService   for avatar manipulation
     * @param userService     to manipulate user-related data
     * @param messageSource   to resolve locale-dependent messages
     */
    @Autowired
    public AvatarController(
            AvatarService avatarService,
            UserService userService,
            MessageSource messageSource,
            JSONUtils jsonUtils) {
        this.avatarService = avatarService;
        this.userService = userService;
        this.messageSource = messageSource;
        this.jsonUtils = jsonUtils;
    }

    /**
     * Process avatar file from request and return avatar preview in response.
     * Used for IE, Opera specific request processing.
     *
     * @param request incoming request
     * @param locale  current user locale settings to resolve messages
     * @return ResponseEntity
     * @throws javax.servlet.ServletException avatar processing problem
     * @throws IOException defined in the JsonFactory implementation,
     * caller must implement exception processing
     * @throws ImageProcessException 
     */
    @RequestMapping(value = "/users/IFrameAvatarpreview", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> uploadAvatar(@RequestParam(value = "qqfile") MultipartFile file,
                                               Locale locale) throws ServletException, IOException, ImageProcessException {
        //prepare response parameters
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_HTML);
        Map<String, String> responseContent = new HashMap<String, String>();

        return prepareResponse(file, responseHeaders, responseContent, locale);
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
     * @throws ImageProcessException 
     */
    @RequestMapping(value = "/users/XHRavatarpreview", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> uploadAvatar(@RequestBody byte[] bytes,
                                            HttpServletResponse response,
                                            Locale locale) throws ServletException, ImageProcessException {

        Map<String, String> responseContent = new HashMap<String, String>();
        prepareResponse(bytes, response, responseContent, locale);
        return responseContent;
    }

    /**
     * Write user avatar in response for rendering it on html pages.
     *
     * @param response servlet response
     * @param id user database identifier
     * @throws NotFoundException if user with given encodedUsername not found
     * @throws IOException throws if an output exception occurred
     */
    @RequestMapping(value = "/users/{id}/avatar", method = RequestMethod.GET)
    public void renderAvatar(HttpServletResponse response, @PathVariable Long id)
        throws NotFoundException, IOException {
        JCUser user = userService.get(id);
        byte[] avatar = user.getAvatar();
        response.setContentType("image/jpeg");
        response.setContentLength(avatar.length);
        response.getOutputStream().write(avatar);
    }

    /**
     * Prepare response with default user avatar
     *
     * @return JSON string with default user avatar
     * @throws ImageProcessException due to common avatar processing error
     * @throws IOException defined in the JsonFactory implementation, caller must implement exception processing
     */
    @RequestMapping(value = "/defaultAvatar", method = RequestMethod.GET)
    @ResponseBody
    public String getDefaultAvatar() throws ImageProcessException, IOException {
        Map<String, String> responseContent = new HashMap<String, String>();
        prepareNormalResponse(avatarService.getDefaultAvatar(), responseContent);
        return jsonUtils.prepareJSONString(responseContent);
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
     * @throws ImageProcessException 
     */
    private ResponseEntity<String> prepareResponse(MultipartFile file,
                                                   HttpHeaders responseHeaders,
                                                   Map<String, String> responseContent,
                                                   Locale locale) throws IOException, ImageProcessException {
        avatarService.validateAvatarFormat(file);
        byte[] bytes = file.getBytes();
        avatarService.validateAvatarSize(bytes);
        prepareNormalResponse(bytes, responseContent);
        String body = jsonUtils.prepareJSONString(responseContent);
        return new ResponseEntity<String>(body, responseHeaders, HttpStatus.OK);
    }

    /**
     * Prepare valid or error response after avatar processing
     *
     * @param bytes           input avatar data
     * @param response        resulting response
     * @param responseContent with avatar processing results
     * @param locale          current user locale settings to resolve messages
     * @throws ImageProcessException 
     */
    private void prepareResponse(byte[] bytes,
                                 HttpServletResponse response,
                                 Map<String, String> responseContent,
                                 Locale locale) throws ImageProcessException {
        avatarService.validateAvatarFormat(bytes);
        avatarService.validateAvatarSize(bytes);
        prepareNormalResponse(bytes, responseContent);
        response.setStatus(HttpServletResponse.SC_OK);
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
     * Handles an exception that is thrown when the avatar has incorrect size.
     * 
     * @param e exception
     * @param locale locale, it's needed for error message localization
     * @return DTO, that contains information about error, it will be converted to JSON
     */
    @ExceptionHandler(value = ImageSizeException.class)
    @ResponseBody
    public ErrorDto handleImageSizeException(ImageSizeException e, Locale locale) {
        return new ErrorDto(false, messageSource.getMessage(WRONG_SIZE_RESOURCE_MESSAGE, null, locale) + " "
                + AvatarService.MAX_SIZE);
    }
    
    /**
     * Handles an exception that is thrown when the avatar has incorrect format.
     * 
     * @param e exception
     * @param locale locale, it's needed for error message localization
     * @return DTO, that contains information about error, it will be converted to JSON
     */
    @ExceptionHandler(value = ImageFormatException.class)
    @ResponseBody
    public ErrorDto handleImageFormatException(ImageFormatException e, Locale locale) {
        return new ErrorDto(false, messageSource.getMessage(WRONG_FORMAT_RESOURCE_MESSAGE, null, locale));
    }
    
    /**
     * Handles common exception that can occur when loading an avatar.
     * 
     * @param e exception
     * @param locale locale, it's needed for error message localization
     * @return DTO, that contains information about error, it will be converted to JSON
     */
    @ExceptionHandler(value = ImageProcessException.class)
    @ResponseBody
    public ErrorDto handleImageProcessException(ImageProcessException e, Locale locale) {
        return new ErrorDto(false, messageSource.getMessage(COMMON_ERROR_RESOURCE_MESSAGE, null, locale));
    }
}

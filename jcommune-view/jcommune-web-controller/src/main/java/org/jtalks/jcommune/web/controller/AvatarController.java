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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateFormatUtils;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.ImageFormatException;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.exceptions.ImageSizeException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.AvatarService;
import org.jtalks.jcommune.service.nontransactional.ImageUtils;
import org.jtalks.jcommune.web.dto.OperationResultDto;
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
    private static final String IF_MODIFIED_SINCE_HEADER = "If-Modified-Since";

    private AvatarService avatarService;
    private UserService userService;
    private MessageSource messageSource;
    private JSONUtils jsonUtils;


    /**
     * Constructor for controller instantiating, dependencies injected via autowiring.
     *
     * @param avatarService for avatar manipulation
     * @param userService   to manipulate user-related data
     * @param messageSource to resolve locale-dependent messages
     * @param jsonUtils     to convert data to JSON format
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
     * @param file file, that contains uploaded image
     * @return ResponseEntity
     * @throws IOException           defined in the JsonFactory implementation,
     *                               caller must implement exception processing
     * @throws ImageProcessException if error occurred while image processing
     */
    @RequestMapping(value = "/users/IFrameAvatarpreview", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> uploadAvatar(
            @RequestParam(value = "qqfile") MultipartFile file) throws IOException, ImageProcessException {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_HTML);
        Map<String, String> responseContent = new HashMap<String, String>();
        return prepareResponse(file, responseHeaders, responseContent);
    }

    /**
     * Process avatar file from request and return avatar preview in response.
     * Used for FF, Chrome specific request processing
     *
     * @param bytes    input avatar data
     * @param response servlet response
     * @return response content
     * @throws ImageProcessException if error occurred while image processing
     */
    @RequestMapping(value = "/users/XHRavatarpreview", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> uploadAvatar(@RequestBody byte[] bytes,
                                            HttpServletResponse response) throws ImageProcessException {
        Map<String, String> responseContent = new HashMap<String, String>();
        prepareResponse(bytes, response, responseContent);
        return responseContent;
    }

    /**
     * Write user avatar in response for rendering it on html pages.
     *
     * @param request servlet request
     * @param response servlet response
     * @param id       user database identifier
     * @throws NotFoundException if user with given encodedUsername not found
     * @throws IOException       throws if an output exception occurred
     */
    @RequestMapping(value = "/users/{id}/avatar", method = RequestMethod.GET)
    public void renderAvatar(
            HttpServletRequest request, 
            HttpServletResponse response, 
            @PathVariable Long id)
            throws NotFoundException, IOException {
        JCUser user = userService.get(id);
        
        Date ifModifiedDate = avatarService.getIfModifiedSineDate(request.getHeader(IF_MODIFIED_SINCE_HEADER));
        if (!user.getAvatarLastModificationTime().isAfter(ifModifiedDate.getTime())) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        } else {
            byte[] avatar = user.getAvatar();
            response.setContentType("image/jpeg");
            response.setContentLength(avatar.length);
            response.getOutputStream().write(avatar);
        }
        
        Date avatarLastModificationDate = new Date(
                user.getAvatarLastModificationTime().getMillis());
        setupAvatarHeaders(response, avatarLastModificationDate);
    }
    
    
    /**
     * Sets up avatar cache related headers. 
     * @param response - HTTP response object where set headers
     * @param avatarLastModificationTime - last modification time of avatar
     */
    private void setupAvatarHeaders(HttpServletResponse response, 
            Date avatarLastModificationTime) {
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control", "public");
        response.addHeader("Cache-Control", "must-revalidate");
        response.addHeader("Cache-Control","max-age=0");
        String formattedDateExpires = DateFormatUtils.format(
                new Date(System.currentTimeMillis()), 
                AvatarService.HTTP_HEADER_DATETIME_PATTERN, Locale.US);
        response.setHeader("Expires", formattedDateExpires);
    
        String formattedDateLastModified = DateFormatUtils.format(
                avatarLastModificationTime, 
                AvatarService.HTTP_HEADER_DATETIME_PATTERN, Locale.US);
        response.setHeader("Last-Modified", formattedDateLastModified);
    }

    /**
     * Prepare response with default user avatar
     *
     * @return JSON string with default user avatar
     * @throws ImageProcessException due to common avatar processing error
     * @throws IOException           defined in the JsonFactory implementation, caller must implement exception processing
     */
    @RequestMapping(value = "/defaultAvatar", method = RequestMethod.GET)
    @ResponseBody
    public String getDefaultAvatar() throws ImageProcessException, IOException {
        Map<String, String> responseContent = new HashMap<String, String>();
        prepareNormalResponse(avatarService.getDefaultAvatar(), responseContent);
        return jsonUtils.prepareJSONString(responseContent);
    }

    /**
     * Prepare valid response after avatar processing
     *
     * @param file            file, that contains uploaded image
     * @param responseHeaders response HTTP headers
     * @param responseContent response content
     * @return ResponseEntity with avatar processing results
     * @throws IOException           defined in the JsonFactory implementation, caller must implement exception processing
     * @throws ImageProcessException if error occurred while image processing
     */
    private ResponseEntity<String> prepareResponse(MultipartFile file,
                                                   HttpHeaders responseHeaders,
                                                   Map<String, String> responseContent)
            throws IOException, ImageProcessException {
        avatarService.validateAvatarFormat(file);
        byte[] bytes = file.getBytes();
        avatarService.validateAvatarSize(bytes);
        prepareNormalResponse(bytes, responseContent);
        String body = jsonUtils.prepareJSONString(responseContent);
        return new ResponseEntity<String>(body, responseHeaders, HttpStatus.OK);
    }

    /**
     * Prepare valid response after avatar processing
     *
     * @param bytes           input avatar data
     * @param response        resulting response
     * @param responseContent with avatar processing results
     * @throws ImageProcessException if it's impossible to form correct image response
     */
    private void prepareResponse(byte[] bytes,
                                 HttpServletResponse response,
                                 Map<String, String> responseContent) throws ImageProcessException {
        avatarService.validateAvatarFormat(bytes);
        avatarService.validateAvatarSize(bytes);
        prepareNormalResponse(bytes, responseContent);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Used for prepare normal response.
     *
     * @param bytes           input avatar data
     * @param responseContent response payload
     * @throws ImageProcessException due to common avatar processing error
     */
    private void prepareNormalResponse(byte[] bytes,
                                       Map<String, String> responseContent) throws ImageProcessException {
        String srcImage = avatarService.convertBytesToBase64String(bytes);
        responseContent.put(RESULT, "true");
        responseContent.put("srcPrefix", ImageUtils.HTML_SRC_TAG_PREFIX);
        responseContent.put("srcImage", srcImage);
    }

    /**
     * Handles an exception that is thrown when the avatar has incorrect size.
     *
     * @param e      exception
     * @param locale locale, it's needed for error message localization
     * @return DTO, that contains information about error, it will be converted to JSON
     */
    @ExceptionHandler(value = ImageSizeException.class)
    @ResponseBody
    public OperationResultDto handleImageSizeException(ImageSizeException e, Locale locale) {
        Object[] parameters = new Object[]{e.getMaxSize()};
        String errorMessage = messageSource.getMessage(WRONG_SIZE_RESOURCE_MESSAGE, parameters, locale);
        return new OperationResultDto(errorMessage);
    }

    /**
     * Handles an exception that is thrown when the avatar has incorrect format.
     *
     * @param e      exception
     * @param locale locale, it's needed for error message localization
     * @return DTO, that contains information about error, it will be converted to JSON
     */
    @ExceptionHandler(value = ImageFormatException.class)
    @ResponseBody
    public OperationResultDto handleImageFormatException(ImageFormatException e, Locale locale) {
        Object[] parameters = new Object[]{e.getValidImageTypes()};
        String errorMessage = messageSource.getMessage(WRONG_FORMAT_RESOURCE_MESSAGE, parameters, locale);
        return new OperationResultDto(errorMessage);
    }

    /**
     * Handles common exception that can occur when loading an avatar.
     *
     * @param e      exception
     * @param locale locale, it's needed for error message localization
     * @return DTO, that contains information about error, it will be converted to JSON
     */
    @ExceptionHandler(value = ImageProcessException.class)
    @ResponseBody
    public OperationResultDto handleImageProcessException(ImageProcessException e, Locale locale) {
        String errorMessage = messageSource.getMessage(COMMON_ERROR_RESOURCE_MESSAGE, null, locale);
        return new OperationResultDto(errorMessage);
    }
}

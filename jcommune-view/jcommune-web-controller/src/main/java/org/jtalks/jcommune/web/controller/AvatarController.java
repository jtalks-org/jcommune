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

import org.apache.commons.lang.time.DateFormatUtils;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.AvatarService;
import org.jtalks.jcommune.web.util.ImageControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Controller for processing avatar related request.
 * todo: this class is too complex, we need to move some logic either to service or to a helper bean
 *
 * @author Alexandre Teterin
 * @author Anuar Nurmakanov
 */

@Controller
public class AvatarController extends ImageUploadController {

    private static final String IF_MODIFIED_SINCE_HEADER = "If-Modified-Since";

    private AvatarService avatarService;
    private UserService userService;
    private ImageControllerUtils imageControllerUtils;

    /**
     * Constructor for controller instantiating, dependencies injected via autowiring.
     *
     * @param avatarService for avatar manipulation
     * @param userService   to manipulate user-related data
     * @param imageControllerUtils utility object for image-related functions
     */
    @Autowired
    public AvatarController(
            AvatarService avatarService,
            UserService userService,
            ImageControllerUtils imageControllerUtils,
            MessageSource messageSource) {
        super(messageSource);

        this.avatarService = avatarService;
        this.userService = userService;
        this.imageControllerUtils = imageControllerUtils;
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
        return imageControllerUtils.prepareResponse(file, responseHeaders, responseContent);
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
        imageControllerUtils.prepareResponse(bytes, response, responseContent);
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
        imageControllerUtils.prepareNormalResponse(avatarService.getDefaultAvatar(), responseContent);
        return imageControllerUtils.getResponceJSONString(responseContent);
    }
}

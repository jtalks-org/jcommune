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

import org.joda.time.DateTime;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.web.util.ImageControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

/**
 * Controller for processing avatar related request.
 *
 * @author Alexandre Teterin
 * @author Anuar Nurmakanov
 * @author Andrei Alikov
 */
@Controller
public class AvatarController extends ImageUploadController {
    private UserService userService;
    private ImageControllerUtils avatarControllerUtils;

    /**
     * Constructor for controller instantiating, dependencies injected via autowiring.
     *
     * @param userService           to manipulate user-related data
     * @param avatarControllerUtils utility object for image-related functions
     * @param messageSource         to resolve locale-dependent messages
     */
    @Autowired
    public AvatarController(
            UserService userService,
            @Qualifier("avatarControllerUtils")
            ImageControllerUtils avatarControllerUtils,
            MessageSource messageSource) {
        super(messageSource);

        this.userService = userService;
        this.avatarControllerUtils = avatarControllerUtils;
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
        return createPreviewOfImage(file, avatarControllerUtils);
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
        return createPreviewOfImage(bytes, response, avatarControllerUtils);
    }

    /**
     * Write user avatar in response for rendering it on html pages.
     *
     * @param request  servlet request
     * @param response servlet response
     * @param id       user database identifier
     * @throws NotFoundException if user with given encodedUsername not found
     * @throws IOException       throws if an output exception occurred
     */
    @RequestMapping(value = "/users/{id}/avatar", method = RequestMethod.GET)
    public void renderAvatar(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable Long id) throws NotFoundException, IOException {
        JCUser user = userService.get(id);

        Date ifModifiedDate = super.getIfModifiedSinceDate(request.getHeader(IF_MODIFIED_SINCE_HEADER));
        // using 0 unix time if avatar has never changed (the date is null). It's easier to work with something
        // non-null than to check for null all the time.
        DateTime avatarLastModificationTime = defaultIfNull(user.getAvatarLastModificationTime(), new DateTime(0));
        // if-modified-since header doesn't include milliseconds, so if it floors the value (millis = 0), then
        // actual modification date will always be after the if-modified-since and we'll always be returning avatar
        avatarLastModificationTime = avatarLastModificationTime.withMillisOfSecond(0);
        if (avatarLastModificationTime.isAfter(ifModifiedDate.getTime())) {
            byte[] avatar = user.getAvatar();
            response.setContentType("image/jpeg");
            response.setContentLength(avatar.length);
            response.getOutputStream().write(avatar);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
        setupAvatarHeaders(response, new Date(avatarLastModificationTime.getMillis()));
    }

    /**
     * Prepare response with default user avatar
     *
     * @return JSON string with default user avatar
     * @throws ImageProcessException due to common avatar processing error
     * @throws IOException           defined in the JsonFactory implementation, caller must implement exception
     *                               processing
     */
    @RequestMapping(value = "/defaultAvatar", method = RequestMethod.GET)
    @ResponseBody
    public String getDefaultAvatar() throws ImageProcessException, IOException {
        Map<String, String> responseContent = new HashMap<>();
        avatarControllerUtils.prepareNormalResponse(avatarControllerUtils.getDefaultImage(), responseContent);
        return avatarControllerUtils.getResponceJSONString(responseContent);
    }
}

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
import org.jtalks.common.model.entity.Component;
import org.jtalks.jcommune.service.ComponentService;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.nontransactional.Base64Wrapper;
import org.jtalks.jcommune.service.transactional.TransactionalComponentService;
import org.jtalks.jcommune.web.util.ImageControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * @author Andrei Alikov
 */
@Controller
public class AdministrationImagesController extends ImageUploadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdministrationImagesController.class);

    private final ImageControllerUtils logoControllerUtils;
    private final ImageControllerUtils favIconPngControllerUtils;
    private final ImageControllerUtils favIconIcoControllerUtils;
    private final ComponentService componentService;

    /**
     * We need this start time because there might be case when time of the last modification
     * of the forum information (logo, icon) was not set (may be even there is no Component for the forum),
     * but we need to use some constant time to decide if we should return "Not Modified" in response to
     * logo or fav icon request.
     */
    private final Date startTime;

    /**
     * Creates instance of the service
     *
     * @param componentService          service to work with the forum component
     * @param logoControllerUtils       utility object for logo converting functions
     * @param favIconPngControllerUtils utility object for fav icon converting (to PNG format) functions
     * @param favIconIcoControllerUtils utility object for fav icon converting (to ICO format) functions
     * @param messageSource             to resolve locale-dependent messages
     */
    @Autowired
    public AdministrationImagesController(ComponentService componentService,
                                          @Qualifier("forumLogoControllerUtils")
                                          ImageControllerUtils logoControllerUtils,
                                          @Qualifier("favIconPngControllerUtils")
                                          ImageControllerUtils favIconPngControllerUtils,
                                          @Qualifier("favIconIcoControllerUtils")
                                          ImageControllerUtils favIconIcoControllerUtils,
                                          MessageSource messageSource) {
        super(messageSource);
        this.componentService = componentService;
        this.logoControllerUtils = logoControllerUtils;
        this.favIconIcoControllerUtils = favIconIcoControllerUtils;
        this.favIconPngControllerUtils = favIconPngControllerUtils;

        DateTime now = new DateTime();
        startTime = now.withMillisOfSecond(0).toDate();
    }

    /**
     * Gets current forum logo
     *
     * @param request  http request
     * @param response http response
     */
    @RequestMapping(value = "/admin/logo", method = RequestMethod.GET)
    public void getForumLogo(HttpServletRequest request, HttpServletResponse response) {
        processImageRequest(request, response, TransactionalComponentService.LOGO_PROPERTY,
                logoControllerUtils, "image/jpeg");
    }

    /**
     * Gets default logo in JSON containing image data in String64 format
     *
     * @return JSON string containing default logo in String64 format
     * @throws java.io.IOException
     * @throws org.jtalks.jcommune.service.exceptions.ImageProcessException
     *
     */
    @RequestMapping(value = "/admin/defaultLogo", method = RequestMethod.GET)
    @ResponseBody
    public String getDefaultLogoInJson() throws IOException, ImageProcessException {
        return getDefaultImageInJSON(logoControllerUtils);
    }

    private String getDefaultImageInJSON(ImageControllerUtils imageUtils) throws ImageProcessException, IOException {
        Map<String, String> responseContent = new HashMap<String, String>();
        imageUtils.prepareNormalResponse(imageUtils.getDefaultImage(), responseContent);
        return imageUtils.getResponceJSONString(responseContent);
    }

    /**
     * Process Logo file from request and return logo preview in response.
     * Used for IE, Opera specific request processing.
     *
     * @param file file, that contains uploaded image
     * @return ResponseEntity
     * @throws IOException defined in the JsonFactory implementation,
     *                     caller must implement exception processing
     * @throws org.jtalks.jcommune.service.exceptions.ImageProcessException
     *                     if error occurred while image processing
     */
    @RequestMapping(value = "/admin/logo/IFrameLogoPreview", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> uploadLogo(
            @RequestParam(value = "qqfile") MultipartFile file) throws IOException, ImageProcessException {
        return createPreviewOfImage(file, logoControllerUtils);
    }

    /**
     * Process logo file from request and return logo preview in response.
     * Used for FF, Chrome specific request processing
     *
     * @param bytes    input logo data
     * @param response servlet response
     * @return response content
     * @throws ImageProcessException if error occurred while image processing
     */
    @RequestMapping(value = "/admin/logo/XHRlogoPreview", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> uploadLogo(@RequestBody byte[] bytes,
                                          HttpServletResponse response) throws ImageProcessException {
        return createPreviewOfImage(bytes, response, logoControllerUtils);
    }

    /**
     * Gets current forum fav icon in the IE format
     *
     * @param request  http request
     * @param response http response
     */
    @RequestMapping(value = "/admin/icon/ico", method = RequestMethod.GET)
    public void getFavIconICO(HttpServletRequest request, HttpServletResponse response) {
        processImageRequest(request, response, TransactionalComponentService.COMPONENT_FAVICON_ICO_PARAM,
                favIconIcoControllerUtils, "image/x-icon");
    }

    /**
     * Gets current forum fav icon in the PNG format (for all browsers except IE)
     *
     * @param request  http request
     * @param response http response
     */
    @RequestMapping(value = "/admin/icon/png", method = RequestMethod.GET)
    public void getFavIconPNG(HttpServletRequest request, HttpServletResponse response) {
        processImageRequest(request, response, TransactionalComponentService.COMPONENT_FAVICON_PNG_PARAM,
                favIconPngControllerUtils, "image/png");
    }

    private void processImageRequest(HttpServletRequest request, HttpServletResponse response,
                                     String propertyName, ImageControllerUtils imageControllerUtils,
                                     String contentType) {
        Date forumModificationDate = componentService.getComponentModificationTime();

        if (forumModificationDate == null) {
            forumModificationDate = startTime;
        }

        Date ifModifiedDate = getIfModifiedSinceDate(request.getHeader(IF_MODIFIED_SINCE_HEADER));

        if (!forumModificationDate.after(ifModifiedDate)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        } else {
            byte[] image = loadImageFromProperties(propertyName, imageControllerUtils);
            response.setContentType(contentType);
            response.setContentLength(image.length);
            try {
                response.getOutputStream().write(image);
            } catch (IOException e) {
                LOGGER.error("Can't write image to the output stream. ", e);
            }
        }

        setupAvatarHeaders(response, forumModificationDate);
    }

    private byte[] loadImageFromProperties(String propertyName, ImageControllerUtils imageControllerUtils) {
        Component forumComponent = componentService.getComponentOfForum();
        String imageProperty = null;
        if (forumComponent != null) {
            imageProperty = forumComponent.getProperty(propertyName);
        }
        byte[] imageBytes = null;

        if (imageProperty == null || imageProperty.isEmpty()) {
            imageBytes = imageControllerUtils.getDefaultImage();
        } else {
            Base64Wrapper wrapper = new Base64Wrapper();
            imageBytes = wrapper.decodeB64Bytes(imageProperty);
        }

        return imageBytes;
    }

    /**
     * Gets default fav icon in JSON containing image data in String64 format
     *
     * @return JSON string containing default fav icon in String64 format
     * @throws IOException
     * @throws ImageProcessException
     */
    @RequestMapping(value = "/admin/defaultIcon", method = RequestMethod.GET)
    @ResponseBody
    public String getDefaultIconInJson() throws IOException, ImageProcessException {
        return getDefaultImageInJSON(favIconPngControllerUtils);
    }

    /**
     * Process Fav Icon file from request and return fav icon preview in response.
     * Used for IE, Opera specific request processing.
     *
     * @param file file, that contains uploaded image
     * @return ResponseEntity
     * @throws IOException defined in the JsonFactory implementation,
     *                     caller must implement exception processing
     * @throws org.jtalks.jcommune.service.exceptions.ImageProcessException
     *                     if error occurred while image processing
     */
    @RequestMapping(value = "/admin/icon/IFrameFavIconPreview", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> uploadFavIcon(
            @RequestParam(value = "qqfile") MultipartFile file) throws IOException, ImageProcessException {
        return createPreviewOfImage(file, favIconPngControllerUtils);
    }

    /**
     * Process Fav Icon file from request and return icon preview in response.
     * Used for FF, Chrome specific request processing
     *
     * @param bytes    input icon data
     * @param response servlet response
     * @return response content
     * @throws ImageProcessException if error occurred while image processing
     */
    @RequestMapping(value = "/admin/icon/XHRFavIconPreview", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> uploadFavIcon(@RequestBody byte[] bytes,
                                             HttpServletResponse response) throws ImageProcessException {
        return createPreviewOfImage(bytes, response, favIconPngControllerUtils);
    }
}

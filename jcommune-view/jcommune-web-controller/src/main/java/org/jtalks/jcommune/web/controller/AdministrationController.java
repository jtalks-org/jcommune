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

import org.jtalks.common.model.entity.Component;
import org.jtalks.jcommune.model.entity.ComponentInformation;
import org.jtalks.jcommune.service.ComponentService;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.nontransactional.Base64Wrapper;
import org.jtalks.jcommune.service.nontransactional.ForumLogoService;
import org.jtalks.jcommune.web.dto.json.JsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.jtalks.jcommune.web.util.ImageControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Andrei Alikov
 * Controller for processing forum administration related requests
 */
@Controller
public class AdministrationController extends ImageUploadController {
    /**
     * Parameter name for forum logo
     */
    public static final String JCOMMUNE_LOGO_PARAM = "jcommune.logo";

    /**
     * Parameter name for forum fav icon in ico format
     */
    public static final String JCOMMUNE_FAVICON_ICO_PARAM = "jcommune.favicon.ico";

    /**
     * Parameter name for forum fav icon in png format
     */
    public static final String JCOMMUNE_FAVICON_PNG_PARAM = "jcommune.favicon.png";

    /**
     * Session's marker attribute name for Administration mode
     */
    public static final String ADMIN_ATTRIBUTE_NAME = "adminMode";
    private static final String ACCESS_DENIED_MESSAGE = "access.denied";

    private static final Logger LOGGER = LoggerFactory.getLogger(AdministrationController.class);
    public static final String PNG_FORMAT = "png";

    private final ComponentService componentService;
    private final ImageControllerUtils imageControllerUtils;
    private final ForumLogoService forumLogoService;

    @Autowired
    ServletContext servletContext;

    /**
     * Creates instance of the service
     * @param componentService service to work with the forum component
     * @param imageControllerUtils utility object for image-related functions
     * @param messageSource to resolve locale-dependent messages
     * @param forumLogoService service for forum logo related operations
     */
    @Autowired
    public AdministrationController(ComponentService componentService,
                                    @Qualifier("forumLogoControllerUtils")
                                    ImageControllerUtils imageControllerUtils,
                                    MessageSource messageSource,
                                    ForumLogoService forumLogoService) {
        super(messageSource);
        this.componentService = componentService;
        this.imageControllerUtils = imageControllerUtils;
        this.forumLogoService = forumLogoService;
    }

    /**
     * Change mode to Administrator mode in which user can edit
     * forum parameters - external links, banners, logo, title, etc.
     * @param request Client request
     * @return redirect back to previous page
     */
    @RequestMapping(value = "/admin/enter", method = RequestMethod.GET)
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

    /**
     * Handler for request of updating Administration information
     * @param componentInformation new forum information
     * @param result form validation result
     */
    @RequestMapping(value = "/admin/edit", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse setForumInformation(@Valid @RequestBody ComponentInformation componentInformation,
                                            BindingResult result, Locale locale) {
        if (result.hasErrors()) {
            return new JsonResponse(JsonResponseStatus.FAIL, result.getAllErrors());
        }

        componentInformation.setId(componentService.getComponentOfForum().getId());

        if (componentInformation.getIcon() != null && !componentInformation.getIcon().isEmpty()) {
            Base64Wrapper wrapper = new Base64Wrapper();
            byte[] favIcon = wrapper.decodeB64Bytes(componentInformation.getIcon());
            try {
                componentInformation.setIconICO(imageControllerUtils.convertImageToIcoInString64(favIcon));
            } catch (ImageProcessException e) {
                LOGGER.error("Can't convert fav icon to *.ico format", e);
            }
        }

        try {
            componentService.setComponentInformation(componentInformation);
        } catch (AccessDeniedException e) {
            String errorMessage = getMessageSource().getMessage(ACCESS_DENIED_MESSAGE, null, locale);
            return new JsonResponse(JsonResponseStatus.FAIL, errorMessage);
        }

        return new JsonResponse(JsonResponseStatus.SUCCESS, null);
    }

    @RequestMapping(value = "/admin/logo", method = RequestMethod.GET)
    public void getForumLogo(HttpServletRequest request, HttpServletResponse response) {

        Date forumModificationDate = componentService.getComponentModificationTime();

        Date ifModifiedDate = getIfModifiedSineDate(request.getHeader(IF_MODIFIED_SINCE_HEADER));
        if (!forumModificationDate.after(ifModifiedDate)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        } else {
            byte[] logo = getLogoBytes();
            response.setContentType("image/jpeg");
            response.setContentLength(logo.length);
            try {
                response.getOutputStream().write(logo);
            } catch (IOException e) {
                LOGGER.error("Can't write to the output stream. ", e);
            }
        }

        setupAvatarHeaders(response, forumModificationDate);
    }

    /**
     * Returns logo image data in String64
     * @return current forum logo image data in String64 format appropriate for "src" attribute of <img> tag
     */
    private byte[] getLogoBytes() {
        Component forumComponent = componentService.getComponentOfForum();
        String logoProperty = null;
        if (forumComponent != null) {
            logoProperty = forumComponent.getProperty(JCOMMUNE_LOGO_PARAM);
        }
        byte[] logoBytes = null;

        if (logoProperty == null || logoProperty.isEmpty()) {
            logoBytes = forumLogoService.getDefaultLogo();
        } else {
            Base64Wrapper wrapper = new Base64Wrapper();
            logoBytes = wrapper.decodeB64Bytes(logoProperty);
        }

        return logoBytes;
    }

    /**
     * Gets default logo in JSON containing image data in String64 format
     * @return JSON string containing default logo in String64 format
     * @throws IOException
     * @throws ImageProcessException
     */
    @RequestMapping(value = "/admin/defaultLogo", method = RequestMethod.GET)
    @ResponseBody
    public String getDefaultLogoInJson() throws IOException, ImageProcessException {
        Map<String, String> responseContent = new HashMap<String, String>();
        imageControllerUtils.prepareNormalResponse(forumLogoService.getDefaultLogo(), responseContent);
        return imageControllerUtils.getResponceJSONString(responseContent);
    }

    /**
     * returns redirect string to previous page
     * @param request Client HTTP request
     */
    private String getRedirectToPrevPage(HttpServletRequest request) {
        return "redirect:" + request.getHeader("Referer");
    }

    /**
     * Process Logo file from request and return logo preview in response.
     * Used for IE, Opera specific request processing.
     *
     * @param file file, that contains uploaded image
     * @return ResponseEntity
     * @throws IOException           defined in the JsonFactory implementation,
     *                               caller must implement exception processing
     * @throws org.jtalks.jcommune.service.exceptions.ImageProcessException if error occurred while image processing
     */
    @RequestMapping(value = "/admin/logo/IFrameLogoPreview", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> uploadLogo(
            @RequestParam(value = "qqfile") MultipartFile file) throws IOException, ImageProcessException {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_HTML);
        Map<String, String> responseContent = new HashMap<String, String>();
        return imageControllerUtils.prepareResponse(file, responseHeaders, responseContent);
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
        Map<String, String> responseContent = new HashMap<String, String>();
        imageControllerUtils.prepareResponse(bytes, response, responseContent);
        return responseContent;
    }


    @RequestMapping(value = "/admin/icon/ico", method = RequestMethod.GET)
    public void getFavIconICO(HttpServletRequest request, HttpServletResponse response) {

        Date forumModificationDate = componentService.getComponentModificationTime();

        Date ifModifiedDate = getIfModifiedSineDate(request.getHeader(IF_MODIFIED_SINCE_HEADER));
        if (!forumModificationDate.after(ifModifiedDate)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        } else {
            byte[] icon = getIconIcoBytes();
            response.setContentType("image/x-icon");
            response.setContentLength(icon.length);
            try {
                response.getOutputStream().write(icon);
            } catch (IOException e) {
                LOGGER.error("Can't write to the output stream. ", e);
            }
        }

        setupAvatarHeaders(response, forumModificationDate);
    }

    private byte[] getIconIcoBytes() {
        Component forumComponent = componentService.getComponentOfForum();
        String iconProperty = null;
        if (forumComponent != null) {
            iconProperty = forumComponent.getProperty(JCOMMUNE_FAVICON_ICO_PARAM);
        }
        byte[] iconBytes = null;

        if (iconProperty == null || iconProperty.isEmpty()) {
            iconBytes = forumLogoService.getDefaultIconICO();
        } else {
            Base64Wrapper wrapper = new Base64Wrapper();
            iconBytes = wrapper.decodeB64Bytes(iconProperty);
        }

        return iconBytes;
    }

    @RequestMapping(value = "/admin/icon/png", method = RequestMethod.GET)
    public void getFavIconPNG(HttpServletRequest request, HttpServletResponse response) {

        Date forumModificationDate = componentService.getComponentModificationTime();

        Date ifModifiedDate = getIfModifiedSineDate(request.getHeader(IF_MODIFIED_SINCE_HEADER));
        if (!forumModificationDate.after(ifModifiedDate)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        } else {
            byte[] icon = getIconPngBytes();
            response.setContentType("image/png");
            response.setContentLength(icon.length);
            try {
                response.getOutputStream().write(icon);
            } catch (IOException e) {
                LOGGER.error("Can't write to the output stream. ", e);
            }
        }

        setupAvatarHeaders(response, forumModificationDate);
    }

    private byte[] getIconPngBytes() {
        Component forumComponent = componentService.getComponentOfForum();
        String iconProperty = null;
        if (forumComponent != null) {
            iconProperty = forumComponent.getProperty(JCOMMUNE_FAVICON_PNG_PARAM);
        }
        byte[] iconBytes = null;

        if (iconProperty == null || iconProperty.isEmpty()) {
            iconBytes = forumLogoService.getDefaultIconPNG();
        } else {
            Base64Wrapper wrapper = new Base64Wrapper();
            iconBytes = wrapper.decodeB64Bytes(iconProperty);
        }

        return iconBytes;
    }

    /**
     * Gets default fav icon in JSON containing image data in String64 format
     * @return JSON string containing default fav icon in String64 format
     * @throws IOException
     * @throws ImageProcessException
     */
    @RequestMapping(value = "/admin/defaultIcon", method = RequestMethod.GET)
    @ResponseBody
    public String getDefaultIconInJson() throws IOException, ImageProcessException {
        Map<String, String> responseContent = new HashMap<String, String>();
        imageControllerUtils.prepareNormalResponse(forumLogoService.getDefaultIconPNG(), responseContent, PNG_FORMAT);
        return imageControllerUtils.getResponceJSONString(responseContent);
    }

    /**
     * Process Fav Icon file from request and return fav icon preview in response.
     * Used for IE, Opera specific request processing.
     *
     * @param file file, that contains uploaded image
     * @return ResponseEntity
     * @throws IOException           defined in the JsonFactory implementation,
     *                               caller must implement exception processing
     * @throws org.jtalks.jcommune.service.exceptions.ImageProcessException if error occurred while image processing
     */
    @RequestMapping(value = "/admin/icon/IFrameFavIconPreview", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> uploadFavIcon(
            @RequestParam(value = "qqfile") MultipartFile file) throws IOException, ImageProcessException {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_HTML);
        Map<String, String> responseContent = new HashMap<String, String>();
        return imageControllerUtils.prepareResponse(file, responseHeaders, responseContent, PNG_FORMAT);
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
        Map<String, String> responseContent = new HashMap<String, String>();
        imageControllerUtils.prepareResponse(bytes, response, responseContent, PNG_FORMAT);
        return responseContent;
    }
}

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

package org.jtalks.jcommune.web.util;

import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.nontransactional.BaseImageService;
import org.jtalks.jcommune.service.nontransactional.ImageUtils;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author Alexandre Teterin
 * @author Anuar Nurmakanov
 */
public class ImageControllerUtils {

    public static final String STATUS = "status";
    public static final String SRC_PREFIX = "srcPrefix";
    public static final String SRC_IMAGE = "srcImage";

    private static final String DEFAULT_IMAGE_FORMAT = "jpeg";

    private BaseImageService baseImageService;
    private JSONUtils jsonUtils;

    /**
     *
     */
    public ImageControllerUtils(BaseImageService baseImageService,
                                    JSONUtils jsonUtils) {
        this.baseImageService = baseImageService;
        this.jsonUtils = jsonUtils;
    }

    /**
     * Prepare valid response after image processing in default image format
     *
     * @param file            file, that contains uploaded image
     * @param responseHeaders response HTTP headers
     * @param responseContent response content
     * @return ResponseEntity with image processing results
     * @throws java.io.IOException           defined in the JsonFactory implementation, caller must implement exception processing
     * @throws org.jtalks.jcommune.service.exceptions.ImageProcessException if error occurred while image processing
     */
    public ResponseEntity<String> prepareResponse(
            MultipartFile file,
            HttpHeaders responseHeaders,
            Map<String, String> responseContent) throws IOException, ImageProcessException {
        return prepareResponse(file, responseHeaders, responseContent, DEFAULT_IMAGE_FORMAT);
    }

    /**
     * Prepare valid response after image processing
     *
     * @param file            file, that contains uploaded image
     * @param responseHeaders response HTTP headers
     * @param responseContent response content
     * @param format target image format e.g. "jpeg" or "png"
     * @return ResponseEntity with image processing results
     * @throws java.io.IOException           defined in the JsonFactory implementation, caller must implement exception processing
     * @throws org.jtalks.jcommune.service.exceptions.ImageProcessException if error occurred while image processing
     */
    public ResponseEntity<String> prepareResponse(
            MultipartFile file,
            HttpHeaders responseHeaders,
            Map<String, String> responseContent, String format) throws IOException, ImageProcessException {
        baseImageService.validateImageFormat(file);
        byte[] bytes = file.getBytes();
        baseImageService.validateImageSize(bytes);
        prepareNormalResponse(bytes, responseContent, format);
        String body = getResponceJSONString(responseContent);
        return new ResponseEntity<String>(body, responseHeaders, HttpStatus.OK);
    }

    public String getResponceJSONString(Map<String, String> responseContent) throws IOException {
        return jsonUtils.prepareJSONString(responseContent);
    }

    /**
     * Prepare valid response after image processing in the default image format
     *
     * @param bytes           input image data
     * @param response        resulting response
     * @param responseContent with emage processing results
     * @throws ImageProcessException if it's impossible to form correct image response
     */
    public void prepareResponse(byte[] bytes,
                                HttpServletResponse response,
                                Map<String, String> responseContent) throws ImageProcessException {
        prepareResponse(bytes, response, responseContent, DEFAULT_IMAGE_FORMAT);
    }

    /**
     * Prepare valid response after image processing
     *
     * @param bytes           input image data
     * @param response        resulting response
     * @param responseContent with emage processing results
     * @param format target image format e.g. "jpeg" or "png"
     * @throws ImageProcessException if it's impossible to form correct image response
     */
    public void prepareResponse(byte[] bytes,
                                 HttpServletResponse response,
                                 Map<String, String> responseContent, String format) throws ImageProcessException {
        baseImageService.validateImageFormat(bytes);
        baseImageService.validateImageSize(bytes);
        prepareNormalResponse(bytes, responseContent, format);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Used for prepare normal response in the default image format
     *
     * @param bytes           input image data
     * @param responseContent response payload
     * @throws ImageProcessException due to common image processing error
     */
    public void prepareNormalResponse(byte[] bytes,
                                      Map<String, String> responseContent) throws ImageProcessException {
        prepareNormalResponse(bytes, responseContent, DEFAULT_IMAGE_FORMAT);
    }

    /**
     * Used for prepare normal response.
     *
     * @param bytes           input image data
     * @param responseContent response payload
     * @param format target image format e.g. "jpeg" or "png"
     * @throws ImageProcessException due to common image processing error
     */
    public void prepareNormalResponse(byte[] bytes,
                                       Map<String, String> responseContent,
                                       String format) throws ImageProcessException {
        String srcImage = baseImageService.convertBytesToBase64String(bytes, format);
        responseContent.put(STATUS, String.valueOf(JsonResponseStatus.SUCCESS));
        responseContent.put(SRC_PREFIX, ImageUtils.getHtmlSrcImagePrefix(format));
        responseContent.put(SRC_IMAGE, srcImage);
    }

    public String convertImageToIcoInString64(byte[] imageBytes) throws ImageProcessException {
        return baseImageService.convertBytesToBase64String(imageBytes, "ico");
    }
}

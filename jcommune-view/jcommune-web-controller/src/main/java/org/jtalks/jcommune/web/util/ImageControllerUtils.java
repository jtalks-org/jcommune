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
import org.jtalks.jcommune.service.nontransactional.AvatarService;
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

    private AvatarService avatarService;
    private JSONUtils jsonUtils;

    /**
     *
     */
    public ImageControllerUtils(AvatarService avatarService,
                                    JSONUtils jsonUtils) {
        this.avatarService = avatarService;
        this.jsonUtils = jsonUtils;
    }

    /**
     * Prepare valid response after image processing
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
        avatarService.validateAvatarFormat(file);
        byte[] bytes = file.getBytes();
        avatarService.validateAvatarSize(bytes);
        prepareNormalResponse(bytes, responseContent);
        String body = getResponceJSONString(responseContent);
        return new ResponseEntity<String>(body, responseHeaders, HttpStatus.OK);
    }

    public String getResponceJSONString(Map<String, String> responseContent) throws IOException {
        return jsonUtils.prepareJSONString(responseContent);
    }

    /**
     * Prepare valid response after image processing
     *
     * @param bytes           input image data
     * @param response        resulting response
     * @param responseContent with emage processing results
     * @throws ImageProcessException if it's impossible to form correct image response
     */
    public void prepareResponse(byte[] bytes,
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
     * @param bytes           input image data
     * @param responseContent response payload
     * @throws ImageProcessException due to common image processing error
     */
    public void prepareNormalResponse(byte[] bytes,
                                       Map<String, String> responseContent) throws ImageProcessException {
        String srcImage = avatarService.convertBytesToBase64String(bytes);
        responseContent.put(STATUS, String.valueOf(JsonResponseStatus.SUCCESS));
        responseContent.put(SRC_PREFIX, ImageUtils.HTML_SRC_TAG_PREFIX);
        responseContent.put(SRC_IMAGE, srcImage);
    }
}

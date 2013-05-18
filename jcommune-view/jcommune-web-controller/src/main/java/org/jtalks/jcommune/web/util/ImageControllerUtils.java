package org.jtalks.jcommune.web.util;

import org.jtalks.jcommune.service.exceptions.ImageFormatException;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.exceptions.ImageSizeException;
import org.jtalks.jcommune.service.nontransactional.AvatarService;
import org.jtalks.jcommune.service.nontransactional.ImageUtils;
import org.jtalks.jcommune.web.dto.json.FailJsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseReason;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

/**
 * @author Alexandre Teterin
 * @author Anuar Nurmakanov
 */
public class ImageControllerUtils {

    public static final String STATUS = "status";
    public static final String SRC_PREFIX = "srcPrefix";
    public static final String SRC_IMAGE = "srcImage";

    static final String WRONG_FORMAT_RESOURCE_MESSAGE = "image.wrong.format";
    static final String WRONG_SIZE_RESOURCE_MESSAGE = "image.wrong.size";
    static final String COMMON_ERROR_RESOURCE_MESSAGE = "avatar.500.common.error";

    private AvatarService avatarService;
    private MessageSource messageSource;
    private JSONUtils jsonUtils;

    /**
     *
     */
    @Autowired
    public ImageControllerUtils(AvatarService avatarService,
                                    MessageSource messageSource,
                                    JSONUtils jsonUtils) {
        this.avatarService = avatarService;
        this.messageSource = messageSource;
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

    /**
     * Handles an exception that is thrown when the image has incorrect size.
     *
     * @param e      exception
     * @param locale locale, it's needed for error message localization
     * @return DTO, that contains information about error, it will be converted to JSON
     */
    @ExceptionHandler(value = ImageSizeException.class)
    @ResponseBody
    public FailJsonResponse handleImageSizeException(ImageSizeException e, Locale locale) {
        Object[] parameters = new Object[]{e.getMaxSize()};
        String errorMessage = messageSource.getMessage(WRONG_SIZE_RESOURCE_MESSAGE, parameters, locale);
        return new FailJsonResponse(JsonResponseReason.VALIDATION, errorMessage);
    }

    /**
     * Handles an exception that is thrown when the image has incorrect format.
     *
     * @param e      exception
     * @param locale locale, it's needed for error message localization
     * @return DTO, that contains information about error, it will be converted to JSON
     */
    @ExceptionHandler(value = ImageFormatException.class)
    @ResponseBody
    public FailJsonResponse handleImageFormatException(ImageFormatException e, Locale locale) {
        Object[] validImageTypes = new Object[]{e.getValidImageTypes()};
        String errorMessage = messageSource.getMessage(WRONG_FORMAT_RESOURCE_MESSAGE, validImageTypes, locale);
        return new FailJsonResponse(JsonResponseReason.VALIDATION, errorMessage);
    }

    /**
     * Handles common exception that can occur when loading an image.
     *
     * @param e      exception
     * @param locale locale, it's needed for error message localization
     * @return DTO, that contains information about error, it will be converted to JSON
     */
    @ExceptionHandler(value = ImageProcessException.class)
    @ResponseBody
    public FailJsonResponse handleImageProcessException(ImageProcessException e, Locale locale) {
        String errorMessage = messageSource.getMessage(COMMON_ERROR_RESOURCE_MESSAGE, null, locale);
        return new FailJsonResponse(JsonResponseReason.INTERNAL_SERVER_ERROR, errorMessage);
    }
}

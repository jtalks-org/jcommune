package org.jtalks.jcommune.web.util;

import org.jtalks.jcommune.service.exceptions.ImageFormatException;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.exceptions.ImageSizeException;
import org.jtalks.jcommune.service.nontransactional.AvatarService;
import org.jtalks.jcommune.service.nontransactional.ImageUtils;
import org.jtalks.jcommune.web.dto.json.FailJsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseReason;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

/**
 *
 */
public class ImageControllerUtilsTest {
    ImageControllerUtils imageControllerUtils;

    @Mock
    private AvatarService avatarService;
    @Mock
    private MessageSource messageSource;
    @Mock
    private JSONUtils jsonUtils;

    private static final String IMAGE_BYTE_ARRAY_IN_BASE_64_STRING = "it's dummy string";

    private byte[] validAvatar = new byte[] {-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0,
            0, 0, 4, 0, 0, 0, 4, 1, 0, 0, 0, 0, -127, -118, -93, -45, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 1,
            -118, 0, 0, 1, -118, 1, 51, -105, 48, 88, 0, 0, 0, 32, 99, 72, 82, 77, 0, 0, 122, 37, 0, 0,
            -128, -125, 0, 0, -7, -1, 0, 0, -128, -23, 0, 0, 117, 48, 0, 0, -22, 96, 0, 0, 58, -104, 0, 0,
            23, 111, -110, 95, -59, 70, 0, 0, 0, 22, 73, 68, 65, 84, 120, -38, 98, -40, -49, -60, -64, -92,
            -64, -60, 0, 0, 0, 0, -1, -1, 3, 0, 5, -71, 0, -26, -35, -7, 32, 96, 0, 0, 0, 0, 73, 69, 78, 68,
            -82, 66, 96, -126
    };


    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        imageControllerUtils = new ImageControllerUtils(avatarService, messageSource, jsonUtils);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void uploadAvatarForOperaAndIEShouldReturnPreviewInResponce()
            throws IOException, ImageProcessException {
        MultipartFile file = new MockMultipartFile("qqfile", validAvatar);
        String expectedBody = "{\"srcPrefix\":\"data:image/jpeg;base64,\",\"srcImage\":\"srcImage\",\"success\":\"true\"}";
        when(avatarService.convertBytesToBase64String(validAvatar)).thenReturn(IMAGE_BYTE_ARRAY_IN_BASE_64_STRING);
        when(imageControllerUtils.getResponceJSONString(Matchers.anyMap())).thenReturn(expectedBody);
        Map<String, String> responseContent = new HashMap<String, String>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_HTML);

        ResponseEntity<String> actualResponseEntity = imageControllerUtils.prepareResponse(file, responseHeaders, responseContent);

        verify(avatarService).validateAvatarFormat(file);
        verify(avatarService).validateAvatarSize(file.getBytes());
        assertEquals(actualResponseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(actualResponseEntity.getBody(), expectedBody);
        HttpHeaders headers = actualResponseEntity.getHeaders();
        assertEquals(headers.getContentType(), MediaType.TEXT_HTML);
    }

    @Test
    public void uploadAvatarForChromeAndFFShouldReturnPreviewInResponce() throws ImageProcessException {
        when(avatarService.convertBytesToBase64String(validAvatar)).thenReturn(IMAGE_BYTE_ARRAY_IN_BASE_64_STRING);
        MockHttpServletResponse response = new MockHttpServletResponse();
        Map<String, String> responseContent = new HashMap<String, String>();

        imageControllerUtils.prepareResponse(validAvatar, response, responseContent);

        verify(avatarService).validateAvatarFormat(validAvatar);
        verify(avatarService).validateAvatarSize(validAvatar);
        assertEquals(responseContent.get(imageControllerUtils.STATUS), "SUCCESS");
        assertEquals(responseContent.get(imageControllerUtils.SRC_PREFIX), ImageUtils.HTML_SRC_TAG_PREFIX);
        assertEquals(responseContent.get(imageControllerUtils.SRC_IMAGE), IMAGE_BYTE_ARRAY_IN_BASE_64_STRING);
        assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
    }

    @Test
    public void imageFormatExceptionShouldProduceNotSuccessOperationResultWithMessageAboutValidImageTypes() {
        Locale locale = Locale.ENGLISH;//it's not matter
        String expectedMessage = "a message";
        String validTypes = "*.png";
        //
        when(messageSource.getMessage(
                imageControllerUtils.WRONG_FORMAT_RESOURCE_MESSAGE,
                new Object[]{validTypes},
                locale)
        ).thenReturn(expectedMessage);

        FailJsonResponse result = imageControllerUtils.handleImageFormatException(new ImageFormatException(validTypes), locale);

        assertEquals(result.getStatus(), JsonResponseStatus.FAIL, "We have an exception, so we should get false value.");
        assertEquals(result.getReason(), JsonResponseReason.VALIDATION, "Failture reason should be validation");
        assertEquals(result.getResult(), expectedMessage, "Result contains incorrect message.");
    }

    @Test
    public void handleImageSizeExceptionShouldReturnValidationErrorAndErrorMessage() {
        int maxSize = 1000;
        ImageSizeException exception = new ImageSizeException(maxSize);
        Locale locale = Locale.ENGLISH;//it's not matter
        String expectedMessage = "a message " + maxSize;
        //
        when(messageSource.getMessage(
                Matchers.anyString(),
                Matchers.any(Object[].class),
                Matchers.any(Locale.class))
        ).thenReturn(expectedMessage);

        FailJsonResponse result = imageControllerUtils.handleImageSizeException(exception, locale);

        assertEquals(result.getStatus(), JsonResponseStatus.FAIL, "We have an exception, so we should get false value.");
        assertEquals(result.getReason(), JsonResponseReason.VALIDATION, "Failture reason should be validation");
        assertEquals(result.getResult(), expectedMessage, "Result contains incorrect message.");
    }

    @Test
    public void handleImageProcessExceptionShouldReturnInternalServerErrorAndErrorMessage() {
        Locale locale = Locale.ENGLISH;//it's not matter
        String expectedMessage = "a message";
        //
        when(messageSource.getMessage(
                ImageControllerUtils.COMMON_ERROR_RESOURCE_MESSAGE,
                null,
                locale)
        ).thenReturn(expectedMessage);

        FailJsonResponse result = imageControllerUtils.handleImageProcessException(null, locale);

        assertEquals(result.getStatus(), JsonResponseStatus.FAIL, "We have an exception, so we should get false value.");
        assertEquals(result.getReason(), JsonResponseReason.INTERNAL_SERVER_ERROR, "Failture reason should be validation");
        assertEquals(result.getResult(), expectedMessage, "Result contains incorrect message.");
    }
}

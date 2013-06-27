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

package org.jtalks.jcommune.service.nontransactional;

import org.apache.commons.io.IOUtils;
import org.jtalks.common.model.entity.Property;
import org.jtalks.jcommune.model.dao.PropertyDao;
import org.jtalks.jcommune.model.entity.JCommuneProperty;
import org.jtalks.jcommune.service.exceptions.ImageFormatException;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.exceptions.ImageSizeException;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ImageServiceTest {

    private static final String PROPERTY_NAME = "property";
    private static final int IMAGE_MAX_SIZE = 1000;
    private JCommuneProperty imageSizeProperty = JCommuneProperty.AVATAR_MAX_SIZE;
    @Mock
    private ImageConverter imageConverter;
    @Mock
    private Base64Wrapper base64Wrapper;
    @Mock
    private PropertyDao propertyDao;
    //
    private ImageService imageService;


    @BeforeMethod
    public void setUp() {
        initMocks(this);
        imageSizeProperty.setName(PROPERTY_NAME);
        imageSizeProperty.setPropertyDao(propertyDao);
        when(propertyDao.getByName(PROPERTY_NAME))
                .thenReturn(new Property(PROPERTY_NAME, String.valueOf(IMAGE_MAX_SIZE)));
        imageService = new ImageService(
                imageConverter,
                base64Wrapper,
                "org/jtalks/jcommune/service/avatar.gif",
                imageSizeProperty);
    }

    @Test
    public void getDefaultImageShouldReturnNotEmptyImage() {
        byte[] avatar = imageService.getDefaultImage();
        assertTrue(avatar.length > 0);
    }

    @Test(dataProvider = "validImageBytesValues")
    public void convertBytesToBase64StringShouldNormalConvertAvatar(
            byte[] originalImageBytes,
            BufferedImage inputImage,
            byte[] processedImageBytes,
            String expectedBase64String) throws ImageProcessException {
        when(imageConverter.convertByteArrayToImage(originalImageBytes)).thenReturn(inputImage);
        when(imageConverter.preprocessImage(inputImage)).thenReturn(processedImageBytes);
        when(base64Wrapper.encodeB64Bytes(processedImageBytes)).thenReturn(expectedBase64String);

        String resultBase64String = imageService.preProcessAndEncodeInString64(originalImageBytes);

        verify(imageConverter).convertByteArrayToImage(originalImageBytes);
        verify(imageConverter).preprocessImage(inputImage);
        verify(base64Wrapper).encodeB64Bytes(processedImageBytes);
        assertEquals(resultBase64String, expectedBase64String);
    }

    @DataProvider
    public Object[][] validImageBytesValues() throws IOException, ImageProcessException {
        byte[] originalImageBytes = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0,
                0, 5, 0, 0, 0, 5, 8, 2, 0, 0, 0, 2, 13, -79, -78, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 1, -118, 0,
                0, 1, -118, 1, 51, -105, 48, 88, 0, 0, 0, 32, 99, 72, 82, 77, 0, 0, 122, 37, 0, 0, -128, -125,
                0, 0, -7, -1, 0, 0, -128, -23, 0, 0, 117, 48, 0, 0, -22, 96, 0, 0, 58, -104, 0, 0, 23, 111,
                -110, 95, -59, 70, 0, 0, 0, 54, 73, 68, 65, 84, 120, -38, 76, -55, -79, 21, -128, 32, 0, -60,
                -48, 28, 14, 32, -52, -30, -2, -93, 121, -79, -112, -126, 116, -1, 37, 42, 71, 3, -72, -41, 4,
                -110, -88, -88, 42, 79, -37, 110, 3, 109, -81, 12, -33, -26, -1, 73, -88, 36, -33, 0, -62, -31,
                36, 71, 49, 115, -89, 85, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126
        };

        ImageConverter imageConverter = new ImageConverter("jpeg", BufferedImage.TYPE_INT_RGB, 100, 100);
        BufferedImage inputImage = imageConverter.convertByteArrayToImage(originalImageBytes);
        byte[] processedImageBytes = imageConverter.preprocessImage(inputImage);
        String expectedBase64String = new Base64Wrapper().encodeB64Bytes(processedImageBytes);

        return new Object[][]{
                {originalImageBytes, inputImage, processedImageBytes, expectedBase64String}
        };
    }

    @Test(expectedExceptions = ImageProcessException.class)
    public void convertBytesToBase64StringShouldNotContinueWhenPassedImageByteArrayIsIncorrect()
            throws ImageProcessException {
        byte[] imageBytes = {8, 2};
        when(imageConverter.convertByteArrayToImage(imageBytes)).thenReturn(null);

        imageService.preProcessAndEncodeInString64(imageBytes);

        verify(imageConverter).convertByteArrayToImage(imageBytes);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void convertBytesToBase64StringShouldNotWorkWithPassedNull() throws ImageProcessException {
        imageService.preProcessAndEncodeInString64(null);
    }

    @Test(expectedExceptions = ImageFormatException.class, dataProvider = "invalidFormatValues")
    public void validateImageFormatShouldNotConsiderIncorrectFormatsAsValid(MultipartFile file)
            throws ImageFormatException {
        imageService.validateImageFormat(file);
    }

    @DataProvider
    public Object[][] invalidFormatValues() {
        return new Object[][]{
                {new MockMultipartFile("test_image", "test_image", "image/bmp", new byte[10])},
                {new MockMultipartFile("test_image", "test_image", "image/tiff", new byte[10])},
                {new MockMultipartFile("test_image", "test_image", "text/plain", new byte[10])},
                {new MockMultipartFile("test_image", "test_image", "audio/mpeg", new byte[10])},
                {new MockMultipartFile("test_image", "test_image", "audio/x-wav", new byte[10])},
                {new MockMultipartFile("test_image", "test_image", "text/plain", new byte[10])},
                {new MockMultipartFile("test_image", "test_image", "text/html", new byte[10])},
                {new MockMultipartFile("test_image", "test_image", "video/mpeg", new byte[10])}
        };
    }

    @Test(dataProvider = "validFormatValues")
    public void validateImageFormatShouldConsiderCorrectFormatsFromOperaAndIEAsValid(MultipartFile file)
            throws ImageFormatException {
        imageService.validateImageFormat(file);
    }

    @DataProvider
    public Object[][] validFormatValues() {
        Set<String> validFormats = new HashSet<String>();
        validFormats.add("image/jpeg");
        validFormats.add("image/png");
        validFormats.add("image/gif");
        List<MultipartFile> files = new ArrayList<MultipartFile>(validFormats.size());
        for (String contentType : validFormats) {
            files.add(new MockMultipartFile("test_image", "test_image", contentType, new byte[10]));
        }
        Object[][] result = new Object[files.size()][];
        for (int i = 0; i < result.length; i++) {
            result[i] = new Object[]{files.get(i)};
        }

        return result;
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void validateImageFormatInFileShouldNotWorkWithPassedNull() throws ImageFormatException {
        MultipartFile nullMultipartFile = null;
        imageService.validateImageFormat(nullMultipartFile);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void validateImageFormatInByteArrayShouldNotWorkWithPassedNull() throws ImageFormatException {
        byte[] nullByteArray = null;
        imageService.validateImageFormat(nullByteArray);
    }

    @Test(dataProvider = "validFormatValuesForChromeFF")
    public void validateImageFormatShouldProcessValidValuesForChromeFF(byte[] bytes) throws ImageFormatException {
        imageService.validateImageFormat(bytes);
    }

    @DataProvider
    public Object[][] validFormatValuesForChromeFF() throws IOException {
        String root = "/org/jtalks/jcommune/service/testdata/avatar/valid/format/";
        Object[][] result = new Object[3][];
        result[0] = new Object[]{IOUtils.toByteArray(this.getClass().getResourceAsStream(root + "avatar.gif"))};
        result[1] = new Object[]{IOUtils.toByteArray(this.getClass().getResourceAsStream(root + "avatar.jpg"))};
        result[2] = new Object[]{IOUtils.toByteArray(this.getClass().getResourceAsStream(root + "avatar.png"))};
        return result;
    }

    @Test(dataProvider = "invalidFormatValuesForChromeFF", expectedExceptions = ImageFormatException.class)
    public void validateImageFormatShouldNotProcessInvalidValuesForChromeFF(byte[] bytes) throws ImageFormatException {
        imageService.validateImageFormat(bytes);
    }

    @DataProvider
    public Object[][] invalidFormatValuesForChromeFF() throws Exception {
        String root = "/org/jtalks/jcommune/service/testdata/avatar/invalid/format/";
        Object[][] result = new Object[4][];
        result[0] = new Object[]{IOUtils.toByteArray(this.getClass().getResourceAsStream(root + "avatar.bmp"))};
        result[1] = new Object[]{IOUtils.toByteArray(this.getClass().getResourceAsStream(root + "avatar.pcx"))};
        result[2] = new Object[]{IOUtils.toByteArray(this.getClass().getResourceAsStream(root + "avatar.pdf"))};
        result[3] = new Object[]{IOUtils.toByteArray(this.getClass().getResourceAsStream(root + "avatar.tif"))};
        return result;
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void validateImageSizeSholdNotContinueWithPassedNullValue() throws ImageSizeException {
        imageService.validateImageSize(null);
    }

    @Test(expectedExceptions = ImageSizeException.class)
    public void validateImageSizeShouldNotConsiderAvatarWithIncorrectSizeAsValid() throws Exception {
        byte[] bytes = new byte[IMAGE_MAX_SIZE * 2];

        imageService.validateImageSize(bytes);
    }

    @Test
    public void validateImageSizeShouldConsiderAvatarWithCorrectSizeAsValid() throws ImageSizeException {
        byte[] bytes = new byte[IMAGE_MAX_SIZE];

        imageService.validateImageSize(bytes);
    }

    @Test
    public void serviceShouldReturnPrefixOfImageConverter() {
        when(imageConverter.getHtmlSrcImagePrefix()).thenReturn("tiff");

        String prefix = imageService.getHtmlSrcImagePrefix();

        assertEquals(prefix, "tiff");
    }
}

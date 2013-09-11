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

import net.sf.image4j.codec.ico.ICODecoder;
import net.sf.image4j.codec.ico.ICOEncoder;
import org.apache.commons.codec.binary.Base64;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.springframework.mock.web.MockMultipartFile;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

/**
 * @author Eugeny Batov
 * @author Alexandre Teterin
 */
public class ImageConverterTest {
    private static final int DEFAULT_MAX_WIDTH = 100;
    private static final int DEFAULT_MAX_HEIGHT = 100;

    private static final int ICON_MAX_WIDTH = 32;
    private static final int ICON_MAX_HEIGHT = 32;

    private ImageConverter imageConverter;
    private byte[] byteArray = new byte[]{1, 2, 3};


    @BeforeClass
    public void init() throws IOException {
        initMocks(this);
        imageConverter = new ImageConverter("jpeg", BufferedImage.TYPE_INT_RGB, DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void convertImageToByteArrayShouldNotWorkWithPassedNull() throws ImageProcessException {
        imageConverter.convertImageToByteArray(null);
    }

    @Test(dataProvider = "parameterResizeImage")
    public void testResizeImage(int maxWidth, int maxHeight, int imageType, String format) throws IOException {
        int expectedWidth = 4;
        int expectedHeight = 4;
        imageConverter = new ImageConverter(format, imageType, maxWidth, maxHeight);
        BufferedImage originalImage = ImageIO.read(new MockMultipartFile("test_image", "test_image", "image/png",
                originalImageByteArray).getInputStream());
        Image modifiedImage = imageConverter.resizeImage(originalImage, imageType);
        assertEquals(modifiedImage.getWidth(null), expectedWidth);
        assertEquals(modifiedImage.getHeight(null), expectedHeight);
    }

    @DataProvider(name = "parameterResizeImage")
    public Object[][] parameterResizeImage() {
        int widthWithAspectRatioOne = 4;
        int heightWithAspectRatioOne = 4;
        int widthWithAspectRationMoreThatOne = 5;
        int heightWithAspectRatioMoreThatOne = 4;
        return new Object[][]{
                {widthWithAspectRatioOne, heightWithAspectRatioOne, BufferedImage.TYPE_INT_RGB, "jpeg"},
                {widthWithAspectRationMoreThatOne, heightWithAspectRatioMoreThatOne, BufferedImage.TYPE_INT_ARGB, "png"}
        };
    }

    @Test
    public void testPrepareHtmlImgSrc() {
        String source = "QUFBQUJBQ0FB";
        Base64Wrapper base64Wrapper = new Base64Wrapper();
        byte[] sourceBytes = base64Wrapper.decodeB64Bytes(source);

        String actual = imageConverter.prepareHtmlImgSrc(sourceBytes);

        assertEquals(actual, source);
    }

    private byte[] originalImageByteArray = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0,
            0, 5, 0, 0, 0, 5, 8, 2, 0, 0, 0, 2, 13, -79, -78, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 1, -118, 0,
            0, 1, -118, 1, 51, -105, 48, 88, 0, 0, 0, 32, 99, 72, 82, 77, 0, 0, 122, 37, 0, 0, -128, -125,
            0, 0, -7, -1, 0, 0, -128, -23, 0, 0, 117, 48, 0, 0, -22, 96, 0, 0, 58, -104, 0, 0, 23, 111,
            -110, 95, -59, 70, 0, 0, 0, 54, 73, 68, 65, 84, 120, -38, 76, -55, -79, 21, -128, 32, 0, -60,
            -48, 28, 14, 32, -52, -30, -2, -93, 121, -79, -112, -126, 116, -1, 37, 42, 71, 3, -72, -41, 4,
            -110, -88, -88, 42, 79, -37, 110, 3, 109, -81, 12, -33, -26, -1, 73, -88, 36, -33, 0, -62, -31,
            36, 71, 49, 115, -89, 85, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126
    };

      private byte[] originalIcoImageByteArray = new byte[]{0, 0, 1, 0, 1, 0, 16, 16, 0, 0, 1, 0, 24, 0, 7, 2, 0, 0, 22,
              0, 0, 0, -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 16, 0, 0, 0, 16, 8, 6, 0,
              0, 0, 31, -13, -1, 97, 0, 0, 1, -50, 73, 68, 65, 84, 56, -115, -51, -45, -49, 107, -46, 113, 28, -57, 113,
              -1, -111, 32, -94, 83, 116, 28, -44, 45, -126, 46, 117, -88, 91, 37, -76, -37, 26, -12, 99, 95, 38, -83,
              -62, 90, -10, 99, 101, 12, -14, -101, 63, -74, 125, -105, -72, -51, -104, -50, -111, -75, 37, 11, -61,
              -48, 73, 36, -77, -48, -24, -69, 10, 15, -126, 30, 12, -119, 82, -28, -21, -14, -117, 124, -31, -5, -3,
              60, 59, -122, 124, 111, -115, -96, 55, -68, 46, -17, -61, -29, -14, 122, -65, 29, 14, -121, -125, 93,
              -58, -63, -33, -50, -65, 7, 76, -45, -28, -90, -5, 26, -7, 124, 30, 33, 4, -106, 101, 81, -87, 84, -16,
              -36, -102, -92, -33, -17, -37, 1, -45, 52, -23, 118, -69, -24, -70, 14, -128, 97, 24, 68, 67, 87, 9, 122,
              47, 18, 85, -90, 80, 30, 79, -78, 24, 112, 49, -1, 72, 66, -41, -11, 65, 64, -45, 52, -54, -27, 50, -86,
              -86, -30, 116, 58, -119, -59, 98, 0, -92, 87, -68, -4, 42, -113, -16, -11, -75, -60, -113, -83, 75, -4,
              44, -116, -78, -7, -52, 109, 7, 84, 85, -91, -45, -23, 0, -96, -21, 58, -75, 90, -115, -51, 108, -122,
              -128, 119, -116, 86, -15, 50, -71, -43, 113, -78, 9, 23, -75, -84, -60, -13, -89, -45, 8, 33, 6, -127,
              -19, -19, 109, 44, -53, 66, 8, 65, 46, -109, -58, 51, 49, -54, -46, -44, 41, 22, 30, -100, -90, -72, 126,
              -123, -4, -22, 56, 27, 81, -119, 111, 111, 37, 22, -97, -124, -20, 64, -67, 94, -89, 90, -83, 18, 14, -34,
              -26, -107, 124, -100, -110, 50, 68, 73, 25, -30, -115, -1, 40, -55, -64, 48, -33, -117, 46, 118, 74, -25,
              73, 68, -18, -47, 104, 52, -20, 64, -81, -41, -29, -6, -40, 57, -118, -54, 97, 82, -14, 9, -34, -51, 29,
              65, 13, 31, -32, -45, -36, 30, -34, -49, 30, 34, 19, 25, 38, 58, -21, -90, -35, 110, -37, 107, 20, 66, 16,
              14, 121, 89, 8, 120, -16, -36, -104, 96, -1, -66, -67, -92, -110, -53, -84, 45, 7, 89, 9, 74, -28, 66,
              -57, 40, 43, 7, -119, 79, -97, -91, -72, 85, -80, 3, 27, 47, -30, -52, -36, -65, 0, -128, 16, 2, 89, -106,
              -111, 101, 25, 77, -45, 48, 12, -125, 47, -97, 85, 18, 75, 126, 94, -6, -49, -80, -26, 59, -55, -57, 15,
              5, 76, -45, -4, 3, -52, -8, -18, -112, 78, 37, 7, -114, -88, -43, 106, 97, 24, -58, -64, -82, -39, 108,
              -78, 30, -97, 39, -14, 112, -124, -112, -17, -18, -1, -12, 11, -69, -55, 111, -87, -80, -30, 120, 52, 83,
              -116, 64, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126};

    @DataProvider
    private Object[][] rangeStringByteData() throws IOException {
        String inputData = Base64.encodeBase64String(byteArray);
        byte[] outputData = Base64.decodeBase64(inputData);

        return new Object[][]{
                {inputData, outputData},
                {null, null}
        };
    }

    @Test(dataProvider = "validDataForConvertByteArrayToImageTest")
    public void testConvertByteArrayToImageForValidData(byte[] bytes, BufferedImage expected)
            throws ImageProcessException {
        BufferedImage actual = imageConverter.convertByteArrayToImage(bytes);

        byte[] actualResult = imageConverter.convertImageToByteArray(actual);
        byte[] expectedResult = imageConverter.convertImageToByteArray(expected);

        assertEquals(actualResult, expectedResult);
    }

    @DataProvider
    private Object[][] validDataForConvertByteArrayToImageTest() throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(originalImageByteArray));
        BufferedImage result = ImageIO.read(bis);

        BufferedInputStream bisIco = new BufferedInputStream(new ByteArrayInputStream(originalIcoImageByteArray));
        BufferedImage resultIco = ICODecoder.read(bisIco).get(0);
        return new Object[][]{
                {originalImageByteArray, result}, {originalIcoImageByteArray, resultIco}
        };
    }

    @Test(dataProvider = "validDataForImageToByteArrayTest")
    public void testConvertImageToByteArrayForValidData(BufferedImage image, byte[] expected, String format)
            throws ImageProcessException {
        imageConverter = ImageConverter.createConverter(format, DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT);
        byte[] actual = imageConverter.convertImageToByteArray(image);
        assertEquals(actual, expected);
    }

    @DataProvider
    private Object[][] validDataForImageToByteArrayTest() throws IOException {
        BufferedImage image = new BufferedImage(DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] result = null;
        try {
            ImageIO.write(image, "jpeg", baos);
            baos.flush();
            result = baos.toByteArray();
        } finally {
            baos.close();
        }

        BufferedImage imagePng = new BufferedImage(DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        baos = new ByteArrayOutputStream();
        byte[] resultPng = null;
        try {
            ImageIO.write(imagePng, "png", baos);
            baos.flush();
            resultPng = baos.toByteArray();
        } finally {
            baos.close();
        }

        BufferedImage imageIco = new BufferedImage(ICON_MAX_WIDTH, ICON_MAX_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        baos = new ByteArrayOutputStream();
        byte[] resultIco = null;
        try {
            ICOEncoder.write(imageIco, 32, baos);
            baos.flush();
            resultIco = baos.toByteArray();
        } finally {
            baos.close();
        }

        return new Object[][]{
                {image, result, "jpeg"}, {imagePng, resultPng, "png"}, {imageIco, resultIco, "ico"}
        };
    }


    @Test(dataProvider = "imageFormats")
    public void createConverterMethodShouldReturnConverterWithSpecifiedType(String imageFormat, String imgPrefix) {
        ImageConverter converter = ImageConverter.createConverter(imageFormat, DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT);

        assertEquals(converter.getFormat(), imageFormat);
        assertEquals(converter.getHtmlSrcImagePrefix(), imgPrefix);
    }

    @DataProvider(name = "imageFormats")
    private Object[][] imageFormats() {
        return new Object[][]{
                {"jpeg", "data:image/jpeg;base64,"}, {"png", "data:image/png;base64,"}, {"ico", "data:image/ico;base64,"}
        };
    }

    @Test
    public void resizeIconWithWidthLessThenMinimumShouldCreateIconWidthMinimumWidth() {
        ImageConverter converter = ImageConverter.createConverter("ico", DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT);
        BufferedImage image = new BufferedImage(ImageConverter.MINIMUM_ICO_WIDTH / 4, 5, BufferedImage.TYPE_INT_ARGB);
        image = converter.resizeImage(image, BufferedImage.TYPE_INT_ARGB);

        assertEquals(image.getHeight(), 5 * 4);
        assertEquals(image.getWidth(), ImageConverter.MINIMUM_ICO_WIDTH);
    }

    @Test
    public void resizeIconWithWidthLessThenMinimumShouldCreateIconWidthMinimumWidthAndNotGreateThenMinimumHeight() {
        int maxSize = 16;
        ImageConverter converter = ImageConverter.createConverter("ico", maxSize, maxSize);
        BufferedImage image = new BufferedImage(ImageConverter.MINIMUM_ICO_WIDTH / 4, 5, BufferedImage.TYPE_INT_ARGB);
        image = converter.resizeImage(image, BufferedImage.TYPE_INT_ARGB);

        assertEquals(image.getHeight(), maxSize);
        assertEquals(image.getWidth(), ImageConverter.MINIMUM_ICO_WIDTH);
    }
}

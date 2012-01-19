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

import org.apache.commons.codec.binary.Base64;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * @author Eugeny Batov
 * @author Alexandre Teterin
 */
public class ImageUtilsTest {

    private ImageUtils imageUtils;
    private byte[] byteArray = new byte[]{1, 2, 3};


    @BeforeClass
    public void init() throws IOException {
        imageUtils = new ImageUtils();
        MultipartFile multipartFile = new MockMultipartFile("test_avatar.png", "test_avatar.png", "image/png",
                originalImageByteArray);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConvertImageToByteArrayForNullData() throws ImageProcessException {
        //invoke object under test
        imageUtils.convertImageToByteArray(null);
    }

    @Test(dataProvider = "rangeByteStringData")
    public void testBase64Coder(byte[] inputData, String expectedData) {
        //invoke object under test
        String result = imageUtils.encodeB64(inputData);

        //check result
        assertEquals(result, expectedData);

    }


    @Test(dataProvider = "rangeStringByteData")
    public void testBase64Decoder(String inputData, byte[] expectedData) throws IOException {
        //invoke object under test
        byte[] result = imageUtils.decodeB64(inputData);

        //check result
        assertEquals(result, expectedData);

    }

    @Test
    public void testResizeImage() throws IOException {
        int expectedWidth = 4;
        int expectedHeight = 4;
        BufferedImage originalImage = ImageIO.read(new MockMultipartFile("test_image", "test_image", "image/png",
                originalImageByteArray).getInputStream());
        Image modifiedImage = imageUtils.resizeImage(originalImage, ImageUtils.IMAGE_PNG, expectedWidth, expectedHeight);
        assertEquals(modifiedImage.getWidth(null), expectedWidth);
        assertEquals(modifiedImage.getHeight(null), expectedHeight);
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


    @DataProvider
    private Object[][] rangeByteStringData() {
        String outputData = Base64.encodeBase64String(byteArray);

        return new Object[][]{
                {byteArray, outputData}
        };

    }

    @DataProvider
    private Object[][] rangeStringByteData() throws IOException {
        String inputData = Base64.encodeBase64String(byteArray);
        byte[] outputData = Base64.decodeBase64(inputData);

        return new Object[][]{
                {inputData, outputData},
                {null, null}
        };
    }
}

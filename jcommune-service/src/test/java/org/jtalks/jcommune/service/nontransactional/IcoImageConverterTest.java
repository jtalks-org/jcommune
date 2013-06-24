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

import net.sf.image4j.codec.ico.ICOEncoder;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.mockito.Mock;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

/**
 * @author Andrei Alikov
 */
public class IcoImageConverterTest {
    @Mock
    private Base64Wrapper base64;
    private ImageConverter imageConverter;

    @BeforeClass
    public void init() throws IOException {
        initMocks(this);
        imageConverter = new IcoImageConverter(base64, 32, 32);
    }

    @Test(dataProvider = "validDataForImageToByteArrayTest")
    public void testConvertImageToByteArrayForValidData(BufferedImage image, byte[] expected) throws ImageProcessException {
        byte[] actual = imageConverter.convertImageToByteArray(image);
        assertEquals(actual, expected);
    }

    @Test(dataProvider = "validDataForConvertByteArrayToImageTest")
    public void testConvertByteArrayToImageForValidData(byte[] bytes, BufferedImage expected) throws ImageProcessException {
        BufferedImage actual = imageConverter.convertByteArrayToImage(bytes);

        byte[] actualResult = imageConverter.convertImageToByteArray(actual);
        byte[] expectedResult = imageConverter.convertImageToByteArray(expected);

        assertEquals(actualResult, expectedResult);
    }

    @DataProvider
    private Object[][] validDataForConvertByteArrayToImageTest() throws IOException {
        BufferedImage result;
        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(originalImageByteArray));
        result = ImageIO.read(bis);
        return new Object[][]{
                {originalImageByteArray, result}
        };
    }

    @DataProvider
    private Object[][] validDataForImageToByteArrayTest() throws IOException {
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ICOEncoder.write(image, 32, baos);
        baos.flush();
        byte[] result = baos.toByteArray();
        baos.close();

        return new Object[][]{
                {image, result}
        };
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
}

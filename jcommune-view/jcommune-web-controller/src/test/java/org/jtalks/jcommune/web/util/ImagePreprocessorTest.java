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

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Eugeny Batov
 * @author Alexandre Teterin
 */
public class ImagePreprocessorTest {

    private ImagePreprocessor imagePreprocessor;
    private byte[] byteArray = new byte[]{1, 2, 3};


    @BeforeClass
    public void init() throws IOException {
        imagePreprocessor = new ImagePreprocessor();
    }

    @Test
    public void testPreprocessImage() throws IOException {
        //init
        Image image = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR);

        //invoke the object under test
        byte[] result = imagePreprocessor.preprocessImage(image);

        //check result
        assertTrue(result.length != 0);
    }


    @Test(dataProvider = "byte-string-provider")
    public void testBase64Coder(byte[] inputData, String expectedData) {
        //invoke object under test
        String result = imagePreprocessor.base64Coder(inputData);

        //check result
        assertEquals(result, expectedData);

    }


    @Test(dataProvider = "string-byte-provider")
    public void testBase64Decoder(String inputData, byte[] expectedData) throws IOException {
        //invoke object under test
        byte[] result = imagePreprocessor.base64Decoder(inputData);

        //check result
        assertEquals(result, expectedData);

    }

    @DataProvider(name = "byte-string-provider")
    private Object[][] rangeByteStringData() {
        String outputData = new BASE64Encoder().encode(byteArray);

        return new Object[][]{
                {byteArray, outputData}
        };

    }

    @DataProvider(name = "string-byte-provider")
    private Object[][] rangeStringByteData() throws IOException {
        String inputData = new BASE64Encoder().encode(byteArray);
        byte[] outputData = new BASE64Decoder().decodeBuffer(inputData);

        return new Object[][]{
                {inputData, outputData},
                {null, null}
        };
    }


}

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

import static org.testng.Assert.assertTrue;

import java.io.IOException;

import org.jtalks.jcommune.service.exceptions.InvalidImageException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Eugeny Batov
 */
public class ImagePreprocessorTest {

    private MultipartFile image;
    private ImagePreprocessor imagePreprocessor;

    @BeforeClass
    public void mockImage() throws IOException {
        imagePreprocessor = new ImagePreprocessor();
        image = new MockMultipartFile("test_avatar.png", "test_avatar.png", "image/png",
                imageByteArray);
    }

    @Test
    public void testPreprocessImage() throws IOException, InvalidImageException {
        byte[] modifiedImageByteArray = imagePreprocessor.preprocessImage(image, 4, 4);
        assertTrue(modifiedImageByteArray instanceof byte[]);
        assertTrue(modifiedImageByteArray != null);
        assertTrue(modifiedImageByteArray.length != 0);
    }

    @Test
    public void testPreprocessImageEmptyMultipartFile() throws IOException, InvalidImageException {
        MultipartFile emptyMultipartFile = new MockMultipartFile("test_avatar.png", "test_avatar.png", "image/png",
                new byte[0]);
        byte[] modifiedImageByteArray = imagePreprocessor.preprocessImage(emptyMultipartFile, 4, 4);
        assertTrue(modifiedImageByteArray == null);
    }

    @Test(expectedExceptions = InvalidImageException.class)
    public void testPreprocessFakeImage() throws IOException, InvalidImageException {
        MultipartFile fakeImage = new MockMultipartFile("test_avatar.doc", "test_avatar.doc", "image/doc",
                new byte[]{1, 3, 2});
        imagePreprocessor.preprocessImage(fakeImage, 4, 4);

    }

    private byte[] imageByteArray = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0,
            0, 0, 4, 0, 0, 0, 4, 1, 0, 0, 0, 0, -127, -118, -93, -45, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 1,
            -118, 0, 0, 1, -118, 1, 51, -105, 48, 88, 0, 0, 0, 32, 99, 72, 82, 77, 0, 0, 122, 37, 0, 0,
            -128, -125, 0, 0, -7, -1, 0, 0, -128, -23, 0, 0, 117, 48, 0, 0, -22, 96, 0, 0, 58, -104, 0, 0,
            23, 111, -110, 95, -59, 70, 0, 0, 0, 22, 73, 68, 65, 84, 120, -38, 98, -40, -49, -60, -64, -92,
            -64, -60, 0, 0, 0, 0, -1, -1, 3, 0, 5, -71, 0, -26, -35, -7, 32, 96, 0, 0, 0, 0, 73, 69, 78, 68,
            -82, 66, 96, -126
    };
}

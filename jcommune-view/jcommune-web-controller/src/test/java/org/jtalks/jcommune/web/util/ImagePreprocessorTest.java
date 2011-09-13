package org.jtalks.jcommune.web.util;

import org.jtalks.jcommune.service.exceptions.InvalidImageException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertTrue;

/**
 * @author Eugeny Batov
 */
public class ImagePreprocessorTest {

    private MultipartFile image;

    @BeforeClass
    public void mockImage() throws IOException {
        image = new MockMultipartFile("test_avatar.png", "test_avatar.png", "image/png",
                imageByteArray);
    }

    @Test
    public void testPreprocessImage() throws IOException, InvalidImageException {
        ImagePreprocessor imagePreprocessor = new ImagePreprocessor();
        byte[] modifiedImageByteArray = imagePreprocessor.preprocessImage(image, 4, 4);
        assertTrue(modifiedImageByteArray instanceof byte[]);
        assertTrue(modifiedImageByteArray != null);
        assertTrue(modifiedImageByteArray.length != 0);
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

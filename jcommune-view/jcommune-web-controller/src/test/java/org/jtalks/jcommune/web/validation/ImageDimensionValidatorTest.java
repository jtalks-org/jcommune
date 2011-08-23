/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.jcommune.web.validation;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * @author Eugeny Batov
 */
public class ImageDimensionValidatorTest {
    /**
     * Class for testing constraint.
     */
    public class TestObject {

        @ImageDimension(width = 4, height = 4)
        private MultipartFile avatar;

        public TestObject(MockMultipartFile avatar) {
            this.avatar = avatar;
        }
    }

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidatorNormalDimension() {
        Set<ConstraintViolation<TestObject>> constraintViolations =
                validator.validate(new TestObject(new MockMultipartFile("test_avatar", "test_avatar",
                        "image/png", normalAvatarByteArray)));

        Assert.assertEquals(constraintViolations.size(), 0, "Validation errors");

    }

    @Test
    public void testValidatorLittleDimension() {
        Set<ConstraintViolation<TestObject>> constraintViolations =
                validator.validate(new TestObject(new MockMultipartFile("test_avatar", "test_avatar",
                        "image/png", littleAvatarByteArray)));

        Assert.assertEquals(constraintViolations.size(), 1, "Validation without errors");
        Assert.assertNotNull(constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void testValidatorBigDimension() {
        Set<ConstraintViolation<TestObject>> constraintViolations =
                validator.validate(new TestObject(new MockMultipartFile("test_avatar", "test_avatar",
                        "image/png", bigAvatarByteArray)));

        Assert.assertEquals(constraintViolations.size(), 1, "Validation without errors");
        Assert.assertNotNull(constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void testValidatorImageNull() {
        Set<ConstraintViolation<TestObject>> constraintViolations =
                validator.validate(new TestObject(new MockMultipartFile("test_avatar", "",
                        "application/octet-stream",
                        new byte[0])));

        Assert.assertEquals(constraintViolations.size(), 0, "Validation errors");
    }

    @Test
    public void testValidatorNotImage() {
        Set<ConstraintViolation<TestObject>> constraintViolations =
                validator.validate(new TestObject(new MockMultipartFile("test_avatar", "",
                        "text/plain",
                        new byte[1024])));

        Assert.assertEquals(constraintViolations.size(), 1, "Validation without errors");
        Assert.assertNotNull(constraintViolations.iterator().next().getMessage());
    }


    private byte[] normalAvatarByteArray = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0,
            0, 0, 4, 0, 0, 0, 4, 1, 0, 0, 0, 0, -127, -118, -93, -45, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 1,
            -118, 0, 0, 1, -118, 1, 51, -105, 48, 88, 0, 0, 0, 32, 99, 72, 82, 77, 0, 0, 122, 37, 0, 0,
            -128, -125, 0, 0, -7, -1, 0, 0, -128, -23, 0, 0, 117, 48, 0, 0, -22, 96, 0, 0, 58, -104, 0, 0,
            23, 111, -110, 95, -59, 70, 0, 0, 0, 22, 73, 68, 65, 84, 120, -38, 98, -40, -49, -60, -64, -92,
            -64, -60, 0, 0, 0, 0, -1, -1, 3, 0, 5, -71, 0, -26, -35, -7, 32, 96, 0, 0, 0, 0, 73, 69, 78, 68,
            -82, 66, 96, -126
    };

    private byte[] littleAvatarByteArray = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0,
            0, 0, 3, 0, 0, 0, 3, 8, 0, 0, 0, 0, 115, 67, -22, 99, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 1, -118, 0,
            0, 1, -118, 1, 51, -105, 48, 88, 0, 0, 0, 32, 99, 72, 82, 77, 0, 0, 122, 37, 0, 0, -128, -125,
            0, 0, -7, -1, 0, 0, -128, -23, 0, 0, 117, 48, 0, 0, -22, 96, 0, 0, 58, -104, 0, 0, 23, 111,
            -110, 95, -59, 70, 0, 0, 0, 27, 73, 68, 65, 84, 120, -38, 98, 100, -8, -49, -64, -16, -97,
            -31, 63, -61, -1, -1, 12, 0, 0, 0, 0, -1, -1, 3, 0, 26, -3, 4, -3, 23, 76, -83, 113, 0, 0, 0,
            0, 73, 69, 78, 68, -82, 66, 96, -126
    };

    private byte[] bigAvatarByteArray = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0,
            0, 5, 0, 0, 0, 5, 8, 2, 0, 0, 0, 2, 13, -79, -78, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 1, -118, 0,
            0, 1, -118, 1, 51, -105, 48, 88, 0, 0, 0, 32, 99, 72, 82, 77, 0, 0, 122, 37, 0, 0, -128, -125,
            0, 0, -7, -1, 0, 0, -128, -23, 0, 0, 117, 48, 0, 0, -22, 96, 0, 0, 58, -104, 0, 0, 23, 111,
            -110, 95, -59, 70, 0, 0, 0, 54, 73, 68, 65, 84, 120, -38, 76, -55, -79, 21, -128, 32, 0, -60,
            -48, 28, 14, 32, -52, -30, -2, -93, 121, -79, -112, -126, 116, -1, 37, 42, 71, 3, -72, -41, 4,
            -110, -88, -88, 42, 79, -37, 110, 3, 109, -81, 12, -33, -26, -1, 73, -88, 36, -33, 0, -62, -31,
            36, 71, 49, 115, -89, 85, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126
    };

}

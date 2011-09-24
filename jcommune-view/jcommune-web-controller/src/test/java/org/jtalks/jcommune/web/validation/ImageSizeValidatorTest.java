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
public class ImageSizeValidatorTest {
    /**
     * Class for testing constraint.
     */
    public class TestObject {

        @ImageSize(size = 65)
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
    public void testValidatorSuccess() {
        Set<ConstraintViolation<TestObject>> constraintViolations =
                validator.validate(new TestObject(new MockMultipartFile("test_avatar", "test_avatar", "image/jpeg",
                        new byte[1024])));

        Assert.assertEquals(constraintViolations.size(), 0, "Validation errors");
    }

    @Test
    public void testValidatorFail() {
        Set<ConstraintViolation<TestObject>> constraintViolations =
                validator.validate(new TestObject(new MockMultipartFile("test_avatar", "test_avatar", "image/jpeg",
                        new byte[102400])));

        Assert.assertEquals(constraintViolations.size(), 1, "Validation without errors");
        Assert.assertNotNull(constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void testValidatorImageNull() {
        Set<ConstraintViolation<TestObject>> constraintViolations =
                validator.validate(new TestObject(new MockMultipartFile("test_avatar", "", "application/octet-stream",
                        new byte[0])));

        Assert.assertEquals(constraintViolations.size(), 0, "Validation errors");
    }

}

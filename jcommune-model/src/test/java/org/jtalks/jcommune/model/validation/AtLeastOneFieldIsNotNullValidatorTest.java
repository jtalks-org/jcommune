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
package org.jtalks.jcommune.model.validation;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.jtalks.jcommune.model.validation.annotations.AtLeastOneFieldIsNotNull;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.validation.*;
import java.util.Set;

import static org.testng.Assert.assertEquals;

/**
 * @author Dmitry S. Dolzhenko
 */
public class AtLeastOneFieldIsNotNullValidatorTest {
    @AtLeastOneFieldIsNotNull(fields = {
            "field1", "field2"
    })
    public static class TestObject {
        private String field1;
        public Integer field2;

        public TestObject(String field1, Integer field2) {
            this.field1 = field1;
            this.field2 = field2;
        }

        public String getField1() {
            return field1;
        }
    }

    @AtLeastOneFieldIsNotNull(fields = {
            "field1", "field2"
    })
    public static class TestObjectWithPrimitive {
        private int field1;
        private String field2;

        public TestObjectWithPrimitive(int field1, String field2) {
            this.field1 = field1;
            this.field2 = field2;
        }

        public int getField1() {
            return field1;
        }

        public String getField2() {
            return field2;
        }
    }

    private Validator validator;

    @BeforeClass
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void validationOfFullyFilledObjectShouldPassSuccessfully() {
        Set<ConstraintViolation<TestObject>> constraintViolations =
                validator.validate(new TestObject(RandomStringUtils.random(5), RandomUtils.nextInt(100)));

        assertEquals(constraintViolations.size(), 0);
    }

    @Test
    public void validationOfPartiallyFilledObjectShouldPassSuccessfully() {
        Set<ConstraintViolation<TestObject>> constraintViolations =
                validator.validate(new TestObject(null, RandomUtils.nextInt(100)));

        assertEquals(constraintViolations.size(), 0);
    }

    @Test
    public void validationOfInvalidObjectShouldFail() {
        Set<ConstraintViolation<TestObject>> constraintViolations =
                validator.validate(new TestObject(null, null));

        assertEquals(constraintViolations.size(), 1);
    }

    @Test
    public void validationOfTestObjectWithPrimitiveShouldPassSuccessFully() throws Exception {
        Set<ConstraintViolation<TestObjectWithPrimitive>> constraintViolations =
                validator.validate(new TestObjectWithPrimitive(RandomUtils.nextInt(100), RandomStringUtils.random(5)));

        assertEquals(constraintViolations.size(), 0);
    }
}

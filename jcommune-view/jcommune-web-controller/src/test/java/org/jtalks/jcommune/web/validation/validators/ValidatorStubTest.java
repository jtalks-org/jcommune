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
package org.jtalks.jcommune.web.validation.validators;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Mikhail Stryzhonok
 */
public class ValidatorStubTest {

    @Test
    public void supportsShouldReturnTrue() {
        ValidatorStub validator = new ValidatorStub();

        assertTrue(validator.supports(Object.class));
    }

    @Test
    public void validateShouldRejectAllSpecifiedFields() {
        String[] fields = new String[]{"field1", "field2", "field3"};
        ValidatorStub validator = new ValidatorStub(fields);
        TestBean testObject = new TestBean();
        Errors errors = new BeanPropertyBindingResult(testObject, "object");

        validator.validate(testObject, errors);

        assertEquals(3, errors.getErrorCount());
        for (String field : fields) {
            assertTrue("Errors should contain field error for " + field, errors.hasFieldErrors(field));
        }
    }

    @Test
    public void validateShouldNotRejectFieldsIfNoErrorFieldSpecified() {
        ValidatorStub validator = new ValidatorStub();
        TestBean testObject = new TestBean();
        Errors errors = new BeanPropertyBindingResult(testObject, "object");

        validator.validate(testObject, errors);

        assertFalse(errors.hasErrors());
    }

    private static class TestBean {
        private String field1;
        private String field2;
        private String field3;
        private String field4;

        public String getField1() {
            return field1;
        }

        public String getField2() {
            return field2;
        }

        public String getField3() {
            return field3;
        }

        public String getField4() {
            return field4;
        }
    }
}

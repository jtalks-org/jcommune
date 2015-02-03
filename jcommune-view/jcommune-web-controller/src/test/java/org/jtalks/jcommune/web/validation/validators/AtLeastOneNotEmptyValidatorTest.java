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

import org.jtalks.jcommune.web.validation.annotations.AtLeastOneNotEmpty;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Mikhail Stryzhonok
 */
public class AtLeastOneNotEmptyValidatorTest {

    @Mock
    private AtLeastOneNotEmpty annotation;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void validationShouldNotPassIfAllFieldsNull() {
        AtLeastOneNotEmptyValidator validator = new AtLeastOneNotEmptyValidator();

        when(annotation.fieldNames()).thenReturn(new String[]{"field1", "field2"});
        validator.initialize(annotation);

        assertFalse(validator.isValid(new TestBean(null, null), null));
    }

    @Test
    public void validationShouldNotPassIfAllFieldsEmpty() {
        AtLeastOneNotEmptyValidator validator = new AtLeastOneNotEmptyValidator();

        when(annotation.fieldNames()).thenReturn(new String[]{"field1", "field2"});
        validator.initialize(annotation);

        assertFalse(validator.isValid(new TestBean("     ", "     "), null));
    }

    @Test
    public void validationShouldPassIfAtLeastOneFieldNotEmpty() {
        AtLeastOneNotEmptyValidator validator = new AtLeastOneNotEmptyValidator();

        when(annotation.fieldNames()).thenReturn(new String[]{"field1", "field2"});
        validator.initialize(annotation);

        assertTrue(validator.isValid(new TestBean("test", "     "), null));
    }

    @Test
    public void validationShouldPassIfAtLeastOneFieldNotNull() {
        AtLeastOneNotEmptyValidator validator = new AtLeastOneNotEmptyValidator();

        when(annotation.fieldNames()).thenReturn(new String[]{"field1", "field2"});
        validator.initialize(annotation);

        assertTrue(validator.isValid(new TestBean("test", null), null));
    }

    @Test
    public void validationShouldPassIfAllFieldsNotEmpty() {
        AtLeastOneNotEmptyValidator validator = new AtLeastOneNotEmptyValidator();

        when(annotation.fieldNames()).thenReturn(new String[]{"field1", "field2"});
        validator.initialize(annotation);

        assertTrue(validator.isValid(new TestBean("test", "test"), null));
    }

    private static class TestBean {
        private String field1;
        private String field2;

        public TestBean(String field1, String field2) {
            this.field1 = field1;
            this.field2 = field2;
        }
    }
}

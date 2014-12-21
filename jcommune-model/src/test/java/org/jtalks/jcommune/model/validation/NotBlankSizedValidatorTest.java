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

import org.jtalks.jcommune.model.validation.annotations.NotBlankSized;
import org.jtalks.jcommune.model.validation.validators.NotBlankSizedValidator;
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
public class NotBlankSizedValidatorTest {

    @Mock
    private NotBlankSized notBlankSized;

    @BeforeMethod
    public void init() {
        initMocks(this);
    }

    @Test
    public void testValidate() {
        NotBlankSizedValidator validator = new NotBlankSizedValidator();

        when(notBlankSized.min()).thenReturn(1);
        when(notBlankSized.max()).thenReturn(3);

        validator.initialize(notBlankSized);

        assertTrue(validator.isValid("12", null));
    }

    @Test
    public void validationOfNullStringShouldFail() {
        NotBlankSizedValidator validator = new NotBlankSizedValidator();

        assertFalse(validator.isValid(null, null));
    }

    @Test
    public void validationOfStringLongerThanMaxThresholdShouldFail() {
        NotBlankSizedValidator validator = new NotBlankSizedValidator();

        when(notBlankSized.min()).thenReturn(1);
        when(notBlankSized.max()).thenReturn(2);

        validator.initialize(notBlankSized);

        assertFalse(validator.isValid("123", null));
    }

    @Test
    public void validationOfStringShorterThanMinThresholdShouldFail() {
        NotBlankSizedValidator validator = new NotBlankSizedValidator();

        when(notBlankSized.min()).thenReturn(2);
        when(notBlankSized.max()).thenReturn(3);

        validator.initialize(notBlankSized);

        assertFalse(validator.isValid("1", null));
    }

    @Test
    public void testIncludeMinThreshold() {
        NotBlankSizedValidator validator = new NotBlankSizedValidator();

        when(notBlankSized.min()).thenReturn(1);
        when(notBlankSized.max()).thenReturn(3);

        validator.initialize(notBlankSized);

        assertTrue(validator.isValid("1", null));
    }

    @Test
    public void testIncludeMaxThreshold() {
        NotBlankSizedValidator validator = new NotBlankSizedValidator();

        when(notBlankSized.min()).thenReturn(1);
        when(notBlankSized.max()).thenReturn(3);

        validator.initialize(notBlankSized);

        assertTrue(validator.isValid("123", null));
    }
}

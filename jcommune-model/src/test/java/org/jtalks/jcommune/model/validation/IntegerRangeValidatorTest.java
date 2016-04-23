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

import org.jtalks.jcommune.model.validation.annotations.IntegerRange;
import org.jtalks.jcommune.model.validation.validators.IntegerRangeValidator;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.qala.datagen.RandomShortApi.integer;
import static io.qala.datagen.RandomShortApi.positiveInteger;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author skythet
 */
public class IntegerRangeValidatorTest {

    private static final int DEFAULT_MIN = -100;
    private static final int DEFAULT_MAX = 100;

    @Mock
    private IntegerRange integerRange;

    @BeforeMethod
    public void init() {
        initMocks(this);
    }

    @Test
    public void testValidate() {
        IntegerRangeValidator validator = new IntegerRangeValidator();

        int min = integer(DEFAULT_MIN, 0);
        int max = integer(0, DEFAULT_MAX);
        integerRange(min, max);

        validator.initialize(integerRange);

        assertTrue(validator.isValid(integer(min, max) + "", null));
    }

    @Test
    public void testValidateWhenMinGreaterThanMax() {
        IntegerRangeValidator validator = new IntegerRangeValidator();

        int min = integer(DEFAULT_MIN, 0);
        int max = integer(0, DEFAULT_MAX);
        integerRange(max, min);

        validator.initialize(integerRange);

        assertFalse(validator.isValid(integer(min, max) + "", null));
    }

    @Test
    public void testValidateEqualsValues() {
        IntegerRangeValidator validator = new IntegerRangeValidator();

        int randomNumber = integer(DEFAULT_MAX);
        integerRange(randomNumber, randomNumber);

        validator.initialize(integerRange);

        assertTrue(validator.isValid(randomNumber + "", null));
    }

    @Test
    public void validationOfNullStringShouldFail() {
        IntegerRangeValidator validator = new IntegerRangeValidator();

        assertFalse(validator.isValid(null, null));
    }

    @Test
    public void validationOfNotNumberStringShouldFail() {
        IntegerRangeValidator validator = new IntegerRangeValidator();

        assertFalse(validator.isValid("123v", null));
    }

    @Test
    public void validationEmptyStringShouldFail() {
        IntegerRangeValidator validator = new IntegerRangeValidator();

        assertFalse(validator.isValid("", null));
        assertFalse(validator.isValid("    ", null));
    }

    @Test
    public void validationContainsSpacesShouldFail() {
        IntegerRangeValidator validator = new IntegerRangeValidator();

        assertFalse(validator.isValid("  12", null));
        assertFalse(validator.isValid("12  ", null));
    }

    @Test
    public void validationNumberGreaterThanMaxValueShouldFail() {
        IntegerRangeValidator validator = new IntegerRangeValidator();

        int min = integer(DEFAULT_MIN, 0);
        int max = integer(0, DEFAULT_MAX);
        integerRange(min, max);

        validator.initialize(integerRange);

        assertFalse(validator.isValid(max + positiveInteger() + "", null));
    }

    @Test
    public void validationNumberLessThanMinValueShouldFail() {
        IntegerRangeValidator validator = new IntegerRangeValidator();

        int min = integer(DEFAULT_MIN, 0);
        int max = integer(0, DEFAULT_MAX);
        integerRange(min, max);

        validator.initialize(integerRange);

        assertFalse(validator.isValid(DEFAULT_MIN + (positiveInteger() * -1) + "", null));
    }

    private void integerRange(int min, int max) {
        when(integerRange.min()).thenReturn(min);
        when(integerRange.max()).thenReturn(max);
    }
}

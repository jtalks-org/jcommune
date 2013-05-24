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

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.ConstraintValidatorContext;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 *
 * @author Andrey Pogorelov
 */
public class NullableNotBlankValidatorTest {

    @Mock
    private ConstraintValidatorContext validatorContext;

    NullableNotBlankValidator validator;

    @BeforeMethod
    public void init() {
        validator = new NullableNotBlankValidator();
    }

    @Test
    public void passwordConsistingOfOnlySpacesShouldBeNotValid() {

        boolean isValid = validator.isValid("  ", validatorContext);

        assertFalse(isValid,
                "If user enters password consisting of only spaces, then we assume that the password is incorrect.");
    }

    @Test
    public void notEnteredPasswordShouldBeCorrectlyValidated() {

        boolean isValid = validator.isValid(null, validatorContext);

        assertTrue(isValid,
                "If user don't enters password, then we assume that the password not edited.");
    }

    @Test
    public void normalPasswordShouldBeValid() {

        boolean isValid = validator.isValid("q1@3W", validatorContext);

        assertTrue(isValid, "Normal password should be valid.");
    }
}

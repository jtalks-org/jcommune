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

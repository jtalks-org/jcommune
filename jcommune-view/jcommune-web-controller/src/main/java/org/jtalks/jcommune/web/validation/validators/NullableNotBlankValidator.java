package org.jtalks.jcommune.web.validation.validators;

import org.jtalks.jcommune.web.validation.annotations.NullableNotBlank;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Check that a string's trimmed length is not empty.
 */
public class NullableNotBlankValidator implements ConstraintValidator<NullableNotBlank, String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(NullableNotBlank annotation) {
    }

    /**
     * Checks that the trimmed string is not empty.
     *
     * @param s The string to validate.
     * @param constraintValidatorContext context in which the constraint is evaluated.
     *
     * @return Returns <code>true</code> if the string is <code>null</code> or the length of <code>s</code> between the specified
     *         <code>min</code> and <code>max</code> values (inclusive), <code>false</code> otherwise.
     */
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if ( s == null ) {
            return true;
        }

        return s.trim().length() > 0;
    }
}


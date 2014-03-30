package org.jtalks.jcommune.model.validation.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.jtalks.jcommune.model.validation.annotations.NotBlankSize;

/**
 * Check that a string's trimmed length is not empty.
 *
 * @author Alexandra Khekhneva
 */
public class NotBlankSizeValidator implements ConstraintValidator<NotBlankSize, String> {

	public void initialize(NotBlankSize annotation) {
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

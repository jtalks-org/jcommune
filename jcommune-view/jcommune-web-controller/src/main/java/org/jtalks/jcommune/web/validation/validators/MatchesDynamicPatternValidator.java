package org.jtalks.jcommune.web.validation.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;
import org.jtalks.jcommune.web.validation.annotations.MatchesDynamicPattern;

/**
 * Validator for {@link MatchesDynamicPattern}. Checks that one string property 
 * matches regular expression stored in other string property.
 *
 * @author Vyacheslav Mishcheryakov
 * @see MatchesDynamicPattern
 */
public class MatchesDynamicPatternValidator implements ConstraintValidator<MatchesDynamicPattern, Object>{

	private String propertyToValidate;
	private String propertyWithPattern;
	private String fieldValue;
	private String pattern;
	
	/**
     * Initialize validator fields from annotation instance.
     *
     * @param constraintAnnotation {@link MatchesDynamicPattern} annotation from class
     * @see MatchesDynamicPattern
     */
	@Override
	public void initialize(MatchesDynamicPattern constraintAnnotation) {
		this.propertyToValidate = constraintAnnotation.field();
		this.propertyWithPattern = constraintAnnotation.fieldWithPattern();
		
	}

	/**
     * Validate object with {@link MatchesDynamicPattern} annotation.
     *
     * @param value   object with {@link MatchesDynamicPattern} annotation
     * @param context validation context
     * @return {@code true} if validation successful or false if fails
     */
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		fetchDataForValidation(value);
		boolean result;
		if (pattern == null) {
			result = true;
		} else if (fieldValue == null) {
			result = false;
		} else {
			result = fieldValue.matches(pattern);
		}
		return result;
	}
	
	/**
     * Retrieving necessary fields from object.
     * Throws {@code IllegalStateException} if field not found.
     *
     * @param value object from which we take values ​​of fields
     */
	private void fetchDataForValidation(Object value) {
        try {
            fieldValue = BeanUtils.getProperty(value, propertyToValidate);
            pattern = BeanUtils.getProperty(value, propertyWithPattern);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}

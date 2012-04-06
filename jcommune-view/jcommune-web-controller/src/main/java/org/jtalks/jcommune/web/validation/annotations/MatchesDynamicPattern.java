package org.jtalks.jcommune.web.validation.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.jtalks.jcommune.web.validation.validators.MatchesDynamicPatternValidator;
import org.jtalks.jcommune.web.validation.validators.MatchesValidator;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;;

/**
 * Constraint for checking that property matches regular expression stored
 * in other property.
 * This constraint for use with JSR-303 validator.
 * <p/>
 * You must annotate your class with {@link MatchesDynamicPattern} annotation
 * You must fill in the parameters <code>field</code> and <code>fieldWithPattern</code>
 * field names to test. Fields must have getters. Both fields must be of type
 * {@link java.lang.String} 
 * <p/>
 * Example:
 * Validate that <code>field1</code> matches regular expression stored in
 * <code>child.field2</code>.
 * {@code
 * &#064;MatchesDynamicPattern(field = "field1", fieldWithPattern = "child.field2")
 * class Test {
 * private String field1;
 * private ChildTest child;
 * public String getField1() {
 * return field1;
 * }
 * public ChildTest getChild() {
 * return child;
 * }
 * }
 * 
 * class ChildTest {
 * private String field2;
 * public String getField2() {
 * return field2;
 * } 
 * }
 * }
 *
 * @author Vyacheslav Mishcheryakov
 * @see MatchesValidator
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy=MatchesDynamicPatternValidator.class)
@Documented
public @interface MatchesDynamicPattern {

	/**
     * Message for display when validation fails.
     */
    String message() default "{org.jtalks.jcommune.web.validation.annotations.MatchesRegExp.message}";
    
    /**
     * Groups element that specifies the processing groups with which the
     * constraint declaration is associated.
     */
    Class<?>[] groups() default {};
    
    /**
     * Payload element that specifies the payload with which the the
     * constraint declaration is associated.
     */
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Name of field to validate.
     */
    String field();

    /**
     * Path to property containing validation pattern (regular expression).
     */
    String fieldWithPattern();
}

package org.jtalks.jcommune.model.validation.annotations;

import org.jtalks.jcommune.model.validation.validators.AtLeastOneFieldIsNotNullValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Constraint for checking that at least one of specified fields is not null
 *
 * @author Dmitry S. Dolzhenko
 */
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = AtLeastOneFieldIsNotNullValidator.class)
public @interface AtLeastOneFieldIsNotNull {
    /**
     * Resource bundle code for error message.
     */
    String message() default "{org.jtalks.jcommune.model.validation.annotations.AtLeastOneFieldIsNotNull.message}";

    /**
     * Groups settings for this validation constraint
     */
    Class<?>[] groups() default {};

    /**
     * Payload element that specifies the payload with which the the
     * constraint declaration is associated.
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Names of fields used for validation.
     */
    String[] fields();
}

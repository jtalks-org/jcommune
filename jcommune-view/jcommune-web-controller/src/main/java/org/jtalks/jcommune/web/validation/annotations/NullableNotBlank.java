package org.jtalks.jcommune.web.validation.annotations;

import org.jtalks.jcommune.web.validation.validators.NullableNotBlankValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validate that the annotated string is not empty.
 * Trailing whitespaces are getting ignored.
 *
 * @author Andrey Pogorelov
 */
@Constraint(validatedBy = { NullableNotBlankValidator.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@ReportAsSingleViolation
public @interface NullableNotBlank {
    String message() default "{org.hibernate.validator.constraints.NotBlank.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}

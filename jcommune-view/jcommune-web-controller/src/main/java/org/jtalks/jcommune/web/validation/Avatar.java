package org.jtalks.jcommune.web.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Eugeny Batov
 */
@AvatarWeight
@AvatarFormat
@AvatarSize
@Target({FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {})
@Documented
public @interface Avatar {

    /**
     * Message for display when validation fails.
     *
     * @return message when validation fails.
     */
    String message() default "{avatar.wrong}";

    /**
     * Groups element that specifies the processing groups with which the
     * constraint declaration is associated.
     *
     * @return array of groups
     */
    Class<?>[] groups() default {};

    /**
     * Payload element that specifies the payload with which the the
     * constraint declaration is associated.
     *
     * @return payload
     */
    Class<? extends Payload>[] payload() default {};
}

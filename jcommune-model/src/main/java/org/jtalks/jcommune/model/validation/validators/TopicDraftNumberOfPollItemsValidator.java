package org.jtalks.jcommune.model.validation.validators;

import org.jtalks.jcommune.model.validation.annotations.TopicDraftNumberOfPollItems;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for {@link TopicDraftNumberOfPollItems}
 *
 * @author Dmitry S. Dolzhenko
 */
public class TopicDraftNumberOfPollItemsValidator
        implements ConstraintValidator<TopicDraftNumberOfPollItems, String> {

    private int min;
    private int max;

    @Override
    public void initialize(TopicDraftNumberOfPollItems constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }

        String[] items = value.split("\n");
        return items.length >= min && items.length <= max;
    }
}

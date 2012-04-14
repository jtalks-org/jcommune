package org.jtalks.jcommune.web.validation.validators;

import org.apache.commons.beanutils.BeanUtils;
import org.jtalks.jcommune.model.entity.PollOption;
import org.jtalks.jcommune.web.util.PollUtil;
import org.jtalks.jcommune.web.validation.annotations.VotingOptionsNumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexandre Teterin
 *         Date: 14.04.12
 */


public class VotingOptionsNumberValidator implements ConstraintValidator<VotingOptionsNumber, Object> {
    private int min;
    private int max;
    private String pollTitleName;
    private String pollTitleValue;
    private String pollOptionsName;
    private String pollOptionsValue;

    private String message;

    @Override
    public void initialize(VotingOptionsNumber constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.message = constraintAnnotation.message();
        this.pollTitleName = constraintAnnotation.pollTitle();
        this.pollOptionsName = constraintAnnotation.pollOptions();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        boolean result = false;
        List<PollOption> list;

        getValidatedFields(value);

        if (pollTitleValue == null) {
            result = true;
        } else {
            if (pollOptionsValue != null) {
                try {
                    list = PollUtil.parseOptions(pollOptionsValue);
                } catch (IOException e) {
                    list = new ArrayList<PollOption>(0);
                }

                if ((list.size() >= min) || (list.size() <= max)) {
                    result = true;
                }
            }
        }

        if (!result) {
            constraintViolated(context);
        }

        return result;
    }

    private void getValidatedFields(Object value) {
        try {
            pollTitleValue = BeanUtils.getProperty(value, pollTitleName);
            pollOptionsValue = BeanUtils.getProperty(value, pollOptionsName);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }


    private void constraintViolated(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addNode(pollOptionsName)
                .addConstraintViolation();
    }
}

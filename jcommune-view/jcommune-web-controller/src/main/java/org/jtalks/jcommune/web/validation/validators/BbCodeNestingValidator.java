/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.jcommune.web.validation.validators;


import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.web.validation.annotations.BbCodeNesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.kefirsf.bb.BBProcessorFactory;
import org.kefirsf.bb.ConfigurationFactory;
import org.kefirsf.bb.TextProcessor;
import org.kefirsf.bb.TextProcessorNestingException;
import org.kefirsf.bb.conf.Configuration;

/**
 * Checked nesting bb code level (maxNestingValue). This is required because our BB-code processor uses recursion for
 * parsing and if we have a deep nesting, we'll run into StackOverflow error. Thus before posting something, we check
 * whether the nesting of BB-codes is not too deep.
 */
public class BbCodeNestingValidator implements ConstraintValidator<BbCodeNesting, String>, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(BbCodeNestingValidator.class);
    private UserService userService;
    private int maxNestingValue;
    private ApplicationContext context;
    private Configuration kefirBbConfig;
    private TextProcessor processor;

    /**
     * Constructor
     *
     * @param userService user service
     */
    @Autowired
    public BbCodeNestingValidator(UserService userService) {
        this.userService = userService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(BbCodeNesting constraintAnnotation) {
        maxNestingValue = constraintAnnotation.maxNestingValue();
        kefirBbConfig = ConfigurationFactory.getInstance().create();
        kefirBbConfig.setPropagateNestingException(true);
        kefirBbConfig.setNestingLimit(maxNestingValue);
        processor = BBProcessorFactory.getInstance().create(kefirBbConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value)) {
            return true;
        }
        try {
            processor.process(value);
            return true;
        } catch (TextProcessorNestingException e) {
            logNestingLimitExced();
            return false;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    /**
     * Get user service. Use lazy initialization
     * @return
     */
    private UserService getUserService() {
        if (userService == null) {
            this.userService = this.context.getBean(UserService.class);
        }
        return this.userService;
    }

    private void logNestingLimitExced() {
        LOGGER.warn("Possible attack: Too deep bb-code nesting. "
                        + "User UUID: {}", getUserService().getCurrentUser().getUuid());
    }
}

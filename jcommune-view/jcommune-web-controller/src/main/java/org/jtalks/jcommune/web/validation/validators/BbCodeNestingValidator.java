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
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checked nesting bb code level (maxNestingValue)
 */
public class BbCodeNestingValidator implements ConstraintValidator<BbCodeNesting,String>{

    private static final Logger LOGGER = LoggerFactory.getLogger(BbCodeNestingValidator.class);

    private UserService userService;

    private int maxNestingValue;

    /**
     * Constructor
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return checkNestingLevel(value);
    }

    /**
     * Checked nesting bb code level (maxNestingValue)
     * @param text text with bb code
     * @return allowed or not allowed
     */
    private boolean checkNestingLevel(String text){
        Pattern pattern = Pattern.compile("\\[[a-zA-Z_0-9=]*\\]|\\[/[a-zA-Z_0-9=]*\\]");
        Matcher matcher = pattern.matcher(text);
        int count=0;
        String teg = "";
        while (matcher.find()){
            teg = matcher.group();
            if(teg.contains("/")){
                count--;
            }else{
                count++;
            }
            if(maxNestingValue <count || (-maxNestingValue)>count){
                LOGGER.warn("Possible attack: Too deep bb-code nesting. ",
                        "User UUID: ",userService.getCurrentUser().getUuid());
                return false;
            }
        }
        return true;
    }
}

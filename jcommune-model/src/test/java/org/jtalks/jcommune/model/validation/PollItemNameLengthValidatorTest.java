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
package org.jtalks.jcommune.model.validation;

import javax.validation.ConstraintValidatorContext;

import org.jtalks.jcommune.model.validation.annotations.PollItemNameLength;
import org.jtalks.jcommune.model.validation.validators.PollItemNameLengthValidator;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class PollItemNameLengthValidatorTest {
    @Mock
    private ConstraintValidatorContext validatorContext;
    @Mock
    private PollItemNameLength pollItemNameLength;
    private PollItemNameLengthValidator validator;
    
    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        this.validator = new PollItemNameLengthValidator();
    }
    
    @Test
    public void testPollItemLengthBetweenMinAndMaxIsValid() {
        String pollItemName = "1234567";
        int min = 5;
        int max = 10;
        Mockito.when(pollItemNameLength.min()).thenReturn(min);
        Mockito.when(pollItemNameLength.max()).thenReturn(max);
        
        validator.initialize(pollItemNameLength);
        boolean isValid = validator.isValid(pollItemName, validatorContext);
        
        Assert.assertTrue(isValid, "PollItem has correct lenght, so it must be valid");
    }
    
    @Test
    public void testPollItemLenghtLessThanMinIsInvalid() {
        String pollItemName = "123";
        int min = 5;
        int max = 10;
        
        Mockito.when(pollItemNameLength.min()).thenReturn(min);
        Mockito.when(pollItemNameLength.max()).thenReturn(max);
        
        validator.initialize(pollItemNameLength);
        boolean isValid = validator.isValid(pollItemName, validatorContext);
        
        Assert.assertFalse(isValid, 
                "PollItems has length, that less than min possible, so it must be invalid");
    }
    
    @Test
    public void testPollItemLengthMoreThanMaxIsInvalid() {
        String pollItemName = "1234567890123456789";
        int min = 5;
        int max = 10;
        Mockito.when(pollItemNameLength.min()).thenReturn(min);
        Mockito.when(pollItemNameLength.max()).thenReturn(max);
        
        validator.initialize(pollItemNameLength);
        boolean isValid = validator.isValid(pollItemName, validatorContext);
        
        Assert.assertFalse(isValid, 
                "Poll has size of the items, that more than min possible size, so it must be invalid");
    }
}

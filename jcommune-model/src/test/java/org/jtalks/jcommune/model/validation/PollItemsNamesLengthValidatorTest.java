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

import org.jtalks.jcommune.model.entity.PollItem;
import org.jtalks.jcommune.model.validation.annotations.PollItemsNamesLength;
import org.jtalks.jcommune.model.validation.validators.PollItemsNamesLengthValidator;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class PollItemsNamesLengthValidatorTest {
    @Mock
    private ConstraintValidatorContext validatorContext;
    @Mock
    private PollItemsNamesLength pollItemsNamesLength;
    private PollItemsNamesLengthValidator validator;
    
    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        this.validator = new PollItemsNamesLengthValidator();
    }

    @Test
    public void emptyPollShouldBeTreatedAsValid() {
        int min = 5;
        int max = 10;
        List<PollItem> pollItems = Collections.emptyList();
        when(pollItemsNamesLength.min()).thenReturn(min);
        when(pollItemsNamesLength.max()).thenReturn(max);

        validator.initialize(pollItemsNamesLength);
        boolean isValid = validator.isValid(pollItems, validatorContext);
        
        Assert.assertTrue(isValid, "PollItem list is empty, so it must be valid");
    }
    
    @Test
    public void testPollItemNameLengthBetweenMinAndMaxIsValid() {
        String pollItemName = "1234567";
        int min = 5;
        int max = 10;
        List<PollItem> pollItems = Arrays.asList(new PollItem(pollItemName));
        when(pollItemsNamesLength.min()).thenReturn(min);
        when(pollItemsNamesLength.max()).thenReturn(max);

        validator.initialize(pollItemsNamesLength);
        boolean isValid = validator.isValid(pollItems, validatorContext);

        Assert.assertTrue(isValid, "PollItems have correct names length, so they must be valid");
    }
    
    @Test
    public void testPollItemNameLengthLessThanMinIsInvalid() {
        String pollItemName = "123";
        int min = 5;
        int max = 10;
        List<PollItem> pollItems = Arrays.asList(new PollItem(pollItemName));
        when(pollItemsNamesLength.min()).thenReturn(min);
        when(pollItemsNamesLength.max()).thenReturn(max);

        validator.initialize(pollItemsNamesLength);
        boolean isValid = validator.isValid(pollItems, validatorContext);
        
        Assert.assertFalse(isValid, 
                "PollItems has names length, that less than min possible, so they must be invalid");
    }
    
    @Test
    public void testPollItemNameLengthMoreThanMaxIsInvalid() {
        String pollItemName = "1234567890123456789";
        int min = 5;
        int max = 10;
        List<PollItem> pollItems = Arrays.asList(new PollItem(pollItemName));
        when(pollItemsNamesLength.min()).thenReturn(min);
        when(pollItemsNamesLength.max()).thenReturn(max);

        validator.initialize(pollItemsNamesLength);
        boolean isValid = validator.isValid(pollItems, validatorContext);
        
        Assert.assertFalse(isValid,
                "PollItems has names length, that more than min possible size, so they must be invalid");
    }
}

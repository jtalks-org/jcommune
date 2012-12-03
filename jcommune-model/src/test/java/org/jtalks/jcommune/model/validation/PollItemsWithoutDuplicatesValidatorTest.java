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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.jtalks.jcommune.model.entity.PollItem;
import org.jtalks.jcommune.model.validation.validators.PollItemsWithoutDuplicatesValidator;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class PollItemsWithoutDuplicatesValidatorTest {
    @Mock
    private ConstraintValidatorContext validatorContext;
    private PollItemsWithoutDuplicatesValidator validator;
    
    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        this.validator = new PollItemsWithoutDuplicatesValidator();
    }
    
    @Test
    public void testItemsWithoutDuplicates() {
        List<PollItem> pollItems = new ArrayList<PollItem>();
        pollItems.add(new PollItem("first"));
        pollItems.add(new PollItem("second"));
        pollItems.add(new PollItem("third"));
        
        boolean isValid = validator.isValid(pollItems, validatorContext);
        
        Assert.assertTrue(isValid, "The poll items without duplicates must be valid");
    }
    
    @Test
    public void testItemsWithDuplicates() {
        List<PollItem> pollItems = new ArrayList<PollItem>();
        pollItems.add(new PollItem("not duplicate"));
        pollItems.add(new PollItem("duplicate"));
        pollItems.add(new PollItem("duplicate"));
        
        boolean isValid = validator.isValid(pollItems, validatorContext);
        
        Assert.assertFalse(isValid, "The poll items with duplicates must be invalid");
    }
    
    @Test
    public void testEmptyItems() {
        List<PollItem> pollItems = Collections.emptyList();
        
        boolean isValid = validator.isValid(pollItems, validatorContext);
        
        Assert.assertTrue(isValid, "Empty list of poll items must be valid");
    }
}

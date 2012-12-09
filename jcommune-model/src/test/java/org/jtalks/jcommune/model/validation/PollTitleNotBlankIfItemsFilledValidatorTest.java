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

import org.apache.commons.lang.StringUtils;
import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.model.entity.PollItem;
import org.jtalks.jcommune.model.validation.validators.PollTitleNotBlankIfItemsFilledValidator;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class PollTitleNotBlankIfItemsFilledValidatorTest {
    @Mock
    private ConstraintValidatorContext validatorContext;
    @Mock
    private ConstraintViolationBuilder constraintViolationBuilder;
    @Mock
    private NodeBuilderDefinedContext nodeBuilderDefinedContext;
    private PollTitleNotBlankIfItemsFilledValidator validator;
    
    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        this.validator = new PollTitleNotBlankIfItemsFilledValidator();
    }
    
    @Test
    public void testEmptyTitleAndEmptyItemsValid() {
        String title = StringUtils.EMPTY;
        List<PollItem> items = Collections.emptyList();
        Poll poll = new Poll(title);
        poll.setPollItems(items);
        
        boolean isValid = validator.isValid(poll, validatorContext);
        
        Assert.assertTrue(isValid, "Poll with empty title and empty items must be valid");
    }
    
    @Test
    public void testFilledTitleAndFilledItemsValid() {
        String title = "larks! Stands and deliver.";
        List<PollItem> items = Arrays.asList(new PollItem("larks"));
        Poll poll = new Poll(title);
        poll.setPollItems(items);
        
        boolean isValid = validator.isValid(poll, validatorContext);
        
        Assert.assertTrue(isValid, "Poll with filled title and filled items must be valid");
    }
    
    @Test
    public void testBlankTitleAndFilledItemsInvalid() {
        String title = " ";
        List<PollItem> items = Arrays.asList(new PollItem("name"));
        Poll poll = new Poll(title);
        poll.setPollItems(items);
        String defaultErrorMessage = "message";
        Mockito.when(validatorContext.getDefaultConstraintMessageTemplate())
            .thenReturn(defaultErrorMessage);
        Mockito.when(validatorContext.buildConstraintViolationWithTemplate(defaultErrorMessage))
            .thenReturn(constraintViolationBuilder);
        Mockito.when(constraintViolationBuilder.addNode(Mockito.anyString()))
            .thenReturn(nodeBuilderDefinedContext);
        
        boolean isValid = validator.isValid(poll, validatorContext);
        
        Assert.assertFalse(isValid, 
                "Poll has blank title, but the list of items is fileld, so it must be invalid");
        Mockito.verify(validatorContext).getDefaultConstraintMessageTemplate();
        Mockito.verify(validatorContext).buildConstraintViolationWithTemplate(defaultErrorMessage);
        Mockito.verify(constraintViolationBuilder).addNode(Mockito.anyString());
        Mockito.verify(nodeBuilderDefinedContext).addConstraintViolation();
    }
}

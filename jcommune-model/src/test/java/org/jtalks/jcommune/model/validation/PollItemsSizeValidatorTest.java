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
import org.jtalks.jcommune.model.validation.annotations.PollItemsSize;
import org.jtalks.jcommune.model.validation.validators.PollItemsSizeValidator;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Anuar_Nurmakanov
 * 
 */
public class PollItemsSizeValidatorTest {
    @Mock
    private ConstraintValidatorContext validatorContext;
    @Mock
    private PollItemsSize pollItemsSize;

    private PollItemsSizeValidator validator;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        this.validator = new PollItemsSizeValidator();
    }

    @Test
    public void testEmptyPollItemsIsValid() {
        List<PollItem> pollItems = Collections.emptyList();

        boolean isValid = validator.isValid(pollItems, validatorContext);

        Assert.assertTrue(isValid, "Poll with empty poll items must be valid");
    }

    @Test
    public void testPollItemsSizeBetweenMinAndMaxIsValid() {
        int min = 5;
        int size = 7;
        int max = 10;
        List<PollItem> pollItems = createPollItems(size);
        Mockito.when(pollItemsSize.min()).thenReturn(min);
        Mockito.when(pollItemsSize.max()).thenReturn(max);

        validator.initialize(pollItemsSize);
        boolean isValid = validator.isValid(pollItems, validatorContext);

        Assert.assertTrue(isValid,
                "Poll has correct size of the items, so it must be valid");
    }

    @Test
    public void testPollItemsSizeLessThanMinIsInvalid() {
        int min = 5;
        int size = 2;
        int max = 10;
        List<PollItem> pollItems = createPollItems(size);
        Mockito.when(pollItemsSize.min()).thenReturn(min);
        Mockito.when(pollItemsSize.max()).thenReturn(max);

        validator.initialize(pollItemsSize);
        boolean isValid = validator.isValid(pollItems, validatorContext);

        Assert.assertFalse(
                isValid,
                "Poll has size of the items, that less than min possible size, so it must be invalid");
    }

    @Test
    public void testPollItemsSizeMoreThanMaxIsInvalid() {
        int min = 5;
        int size = 15;
        int max = 10;
        List<PollItem> pollItems = createPollItems(size);
        Mockito.when(pollItemsSize.min()).thenReturn(min);
        Mockito.when(pollItemsSize.max()).thenReturn(max);

        validator.initialize(pollItemsSize);
        boolean isValid = validator.isValid(pollItems, validatorContext);

        Assert.assertFalse(
                isValid,
                "Poll has size of the items, that more than min possible size, so it must be invalid");
    }

    private List<PollItem> createPollItems(int size) {
        List<PollItem> pollItems = new ArrayList<PollItem>();
        for (int i = 0; i < size; i++) {
            pollItems.add(new PollItem(String.valueOf(i)));
        }
        return pollItems;
    }
}
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

import org.apache.commons.lang.RandomStringUtils;
import org.jtalks.jcommune.model.validation.annotations.TopicDraftNumberOfPollItems;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.testng.Assert.*;

/**
 * @author Dmitry S. Dolzhenko
 */
public class TopicDraftNumberOfPollItemsValidatorTest {

    private static final int MIN_ITEMS_SIZE = 2;
    private static final int MAX_ITEMS_SIZE = 10;

    private static class TestObject {
        @TopicDraftNumberOfPollItems(min = MIN_ITEMS_SIZE, max = MAX_ITEMS_SIZE)
        private String pollItemsValue;

        public TestObject(String pollItemsValue) {
            this.pollItemsValue = pollItemsValue;
        }

        public String getPollItemsValue() {
            return pollItemsValue;
        }
    }

    private Validator validator;

    @BeforeClass
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void validationShouldFailWhenNumberOfPollItemsLessThanMin() {
        TestObject testObject = new TestObject(RandomStringUtils.random(15));

        Set<ConstraintViolation<TestObject>> constraintViolations
                = validator.validate(testObject);

        assertEquals(constraintViolations.size(), 1);
    }

    @Test
    public void validationShouldFailWhenNumberOfPollItemsGreaterThanMax() {
        String pollItemsValue = "";
        for (int i = 0; i < MAX_ITEMS_SIZE + 1; i++) {
            pollItemsValue += RandomStringUtils.random(15) + "\n";
        }
        TestObject testObject = new TestObject(pollItemsValue);

        Set<ConstraintViolation<TestObject>> constraintViolations
                = validator.validate(testObject);

        assertEquals(constraintViolations.size(), 1);
    }

    @Test
    public void validationShouldPassSuccessfullyOnValidNumberOfPollItems() {
        String pollItemsValue = "";
        for (int i = 0; i < MIN_ITEMS_SIZE; i++) {
            pollItemsValue += RandomStringUtils.random(15) + "\n";
        }
        TestObject testObject = new TestObject(pollItemsValue);

        Set<ConstraintViolation<TestObject>> constraintViolations
                = validator.validate(testObject);

        assertEquals(constraintViolations.size(), 0);
    }
}

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

import org.hibernate.validator.engine.ConstraintViolationImpl;
import org.joda.time.DateTime;
import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.web.dto.TopicDto;
import org.jtalks.jcommune.web.validation.annotations.ValidPoll;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Alexandre Teterin
 *         Date: 30.04.12
 */


public class PollValidatorTest {
    private static Validator validator;

    @BeforeClass
    public void setUp() throws Exception {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

    }

    @Test
    public void testValidPoll() {

        Set<ConstraintViolation<TestObject>> constraintViolations = validator.validate(new TestObject("title",
                "item1" + TopicDto.LINE_SEPARATOR + "item2", true));
        Assert.assertEquals(constraintViolations.size(), 0);


    }

    @Test
    public void testNotFutureDateCase() {
        Set<ConstraintViolation<TestObject>> constraintViolations = validator.validate(new TestObject("title",
                "item1" + TopicDto.LINE_SEPARATOR + "item2", false));
        Assert.assertEquals(constraintViolations.size(), 1);
        Assert.assertEquals(constraintViolations.iterator().next().getMessageTemplate(),
                "{javax.validation.constraints.Future.message}");

    }

    @Test
    public void testItemLengthNotValid() {
        Set<ConstraintViolation<TestObject>> constraintViolations = validator.validate(new TestObject("title",
                "item1" + TopicDto.LINE_SEPARATOR +
                        "012345678901234567890123456789012345678901234567890123456789", true));
        Assert.assertEquals(constraintViolations.size(), 1);
        Assert.assertEquals(constraintViolations.iterator().next().getMessageTemplate(), "{VotingItemLength.message}");

    }

    @Test
    public void testTitleBlankItemsDateNotBlank() {
        Set<ConstraintViolation<TestObject>> constraintViolations = validator.validate(new TestObject(null,
                "item1" + TopicDto.LINE_SEPARATOR +
                        "item2", true));
        Assert.assertEquals(constraintViolations.size(), 1);
        Assert.assertEquals(constraintViolations.iterator().next().getMessageTemplate(),
                "{PollTitleNotBlankIfPollItemsNotBlank.message}");

    }

    @Test
    public void testItemsBlankTitleDateNotBlank() {
        Set<ConstraintViolation<TestObject>> constraintViolations = validator.validate(new TestObject("title",
                null, true));
        Assert.assertEquals(constraintViolations.size(), 2);
        Iterator iterator = constraintViolations.iterator();
        String firstMessageTemplate = ((ConstraintViolationImpl) iterator.next()).getMessageTemplate();
        String secondMessageTemplate = ((ConstraintViolationImpl) iterator.next()).getMessageTemplate();
        //if items blank but title not blank we will see
        //VotingOptionsNumber and  PollItemsNotBlankIfPollTitleNotBlank violations
        Assert.assertEquals(firstMessageTemplate, "{VotingOptionsNumber.message}");
        Assert.assertEquals(secondMessageTemplate, "{PollItemsNotBlankIfPollTitleNotBlank.message}");

    }

    @Test
    public void testItemsAndTitleNotBlankDateBlank() {
        TestObject testObject = new TestObject("title", "item1" + TopicDto.LINE_SEPARATOR + "item2", true);
        testObject.setEndingDate(null);
        Set<ConstraintViolation<TestObject>> constraintViolations = validator.validate(testObject);
        Assert.assertEquals(constraintViolations.size(), 1);
        Assert.assertEquals(constraintViolations.iterator().next().getMessageTemplate(),
                "{DateNotBlankIfPollTitleOrItemsNotBlank.message}");
    }

    @ValidPoll(pollTitle = "pollTitle", pollItems = "pollItems", endingDate = "endingDate")
    public class TestObject {
        private String pollTitle;
        private String pollItems;
        private String endingDate;

        public TestObject(String pollTitle, String pollOptions, boolean isFuture) {
            DateTime now = new DateTime();
            DateTime future = new DateTime().plusDays(1);

            if (isFuture) {
                endingDate = future.toLocalDate().toString(Poll.DATE_FORMAT);
            } else {
                endingDate = now.toLocalDate().toString(Poll.DATE_FORMAT);
            }

            this.pollTitle = pollTitle;
            this.pollItems = pollOptions;
        }

        public String getPollTitle() {
            return pollTitle;
        }

        public void setPollTitle(String pollTitle) {
            this.pollTitle = pollTitle;
        }

        public String getPollItems() {
            return pollItems;
        }

        public void setPollItems(String pollItems) {
            this.pollItems = pollItems;
        }

        public String getEndingDate() {
            return endingDate;
        }

        public void setEndingDate(String endingDate) {
            this.endingDate = endingDate;
        }
    }

}

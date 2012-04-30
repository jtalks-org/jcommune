package org.jtalks.jcommune.web.validation.validators;

import org.jtalks.jcommune.web.validation.annotations.ValidPoll;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
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
                "item1\nitem2", null));

    }


    @ValidPoll(pollTitle = "pollTitle", pollOptions = "pollOptions", endingDate = "endingDate")
    public class TestObject {
        private String pollTitle;
        private String pollOptions;
        private String endingDate;

        public TestObject(String pollTitle, String pollOptions, String endingDate) {
            this.pollTitle = pollTitle;
            this.pollOptions = pollOptions;
            this.endingDate = endingDate;
        }

        public String getPollTitle() {
            return pollTitle;
        }

        public void setPollTitle(String pollTitle) {
            this.pollTitle = pollTitle;
        }

        public String getPollOptions() {
            return pollOptions;
        }

        public void setPollOptions(String pollOptions) {
            this.pollOptions = pollOptions;
        }

        public String getEndingDate() {
            return endingDate;
        }

        public void setEndingDate(String endingDate) {
            this.endingDate = endingDate;
        }
    }

}

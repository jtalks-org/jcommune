package org.jtalks.jcommune.web.validation;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * @author Kirill Afonin
 */
public class MatchesValidatorTest {

    @Matches(field = "value", verifyField = "value2", message = "Values not matches")
    public class TestObject {
        String value;
        String value2;

        public TestObject(String value, String value2) {
            this.value = value;
            this.value2 = value2;
        }

        public String getValue() {

            return value;
        }

        public String getValue2() {
            return value2;
        }
    }

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidatorSuccess() {
        Set<ConstraintViolation<TestObject>> constraintViolations =
                validator.validate(new TestObject("value", "value"));

        Assert.assertEquals(constraintViolations.size(), 0);
    }

    @Test
    public void testValidatorFail() {
        Set<ConstraintViolation<TestObject>> constraintViolations =
                validator.validate(new TestObject("value", "not"));

        Assert.assertEquals(constraintViolations.size(), 1);
        Assert.assertEquals(constraintViolations.iterator().next().getMessage(), "Values not matches");
    }

}

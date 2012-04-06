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
package org.jtalks.jcommune.web.validation;

import org.jtalks.jcommune.web.validation.annotations.MatchesDynamicPattern;
import org.jtalks.jcommune.web.validation.validators.MatchesDynamicPatternValidator;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * Test for {@link MatchesDynamicPattern} annotation constraint and 
 * {@link MatchesDynamicPatternValidator} implementation.
 *
 * @author Vyachelsav Mishcheryakov
 */
public class MatchesDynamicPatternValidatorTest {
	
	/** Valid value for tests */
	private final static String VALID_VALUE = "value";
	
	/** Pattern for valid value */
	private final static String VALID_PATTERN = "^value$";
	
	/** Wrong pattern for valid value */
	private final static String INVALID_PATTERN = "^valu$";
	
    /**
     * Class for testing constraint.
     */
    @MatchesDynamicPattern(field = "value", fieldWithPattern = "pattern", message = "Values don't match")
    public class SimpleTestObject {
        String value;
        String pattern;

        public SimpleTestObject(String value, String pattern) {
            this.value = value;
            this.pattern = pattern;
        }

        public String getValue() {

            return value;
        }

        public String getPattern() {
            return pattern;
        }
    }
    
    @MatchesDynamicPattern(field = "value", fieldWithPattern = "child.pattern", message = "Values don't match")
    public class ComplexTestObject {
        String value;
        Child child;

        public ComplexTestObject(String value, Child child) {
            this.value = value;
            this.child = child;
        }

        public String getValue() {

            return value;
        }

        public Child getChild() {
            return child;
        }
    }
    
    public class Child {
    	String pattern;

		public Child(String pattern) {
			this.pattern = pattern;
		}

		public String getPattern() {
			return pattern;
		}
    }

    /**
     * Class for testing constraint with non existing properties.
     */
    @MatchesDynamicPattern(field = "aaa", fieldWithPattern = "child.bbb")
    public class TestObjectBadProperties {
    	String value;
        Child child;

        public TestObjectBadProperties(String value, Child child) {
            this.value = value;
            this.child = child;
        }

        public String getValue() {

            return value;
        }

        public Child getChild() {
            return child;
        }
    }
    

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidatorSimpleSuccess() {
        Set<ConstraintViolation<SimpleTestObject>> constraintViolations =
                validator.validate(new SimpleTestObject(VALID_VALUE, VALID_PATTERN));

        Assert.assertEquals(constraintViolations.size(), 0, "Validation errors");
    }
    
    @Test
    public void testValidatorComplexSuccess() {
        Set<ConstraintViolation<ComplexTestObject>> constraintViolations =
                validator.validate(new ComplexTestObject(VALID_VALUE, new Child(VALID_PATTERN)));

        Assert.assertEquals(constraintViolations.size(), 0, "Validation errors");
    }

    @Test
    public void testValidatorSimpleFail() {
    	Set<ConstraintViolation<SimpleTestObject>> constraintViolations =
            validator.validate(new SimpleTestObject(VALID_VALUE, INVALID_PATTERN));

        Assert.assertEquals(constraintViolations.size(), 1, "Validation without errors");
        Assert.assertEquals(constraintViolations.iterator().next().getMessage(), "Values don't match");
    }
    
    @Test
    public void testValidatorComplexFail() {
        Set<ConstraintViolation<ComplexTestObject>> constraintViolations =
                validator.validate(new ComplexTestObject(VALID_VALUE, new Child(INVALID_PATTERN)));

        Assert.assertEquals(constraintViolations.size(), 1, "Validation without errors");
        Assert.assertEquals(constraintViolations.iterator().next().getMessage(), "Values don't match");
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testPropertiesNotExist() {
        validator.validate(new TestObjectBadProperties("1", new Child("2")));
    }
    
    @Test(expectedExceptions = ValidationException.class)
    public void testPatternObjectNotExist() {
        validator.validate(new TestObjectBadProperties("1", null));
    }
    
    @Test
    public void testValueIsNull() {
        Set<ConstraintViolation<SimpleTestObject>> constraintViolations =
                validator.validate(new SimpleTestObject(null, VALID_PATTERN));

        Assert.assertEquals(constraintViolations.size(), 1, "Validation without errors");
        Assert.assertEquals(constraintViolations.iterator().next().getMessage(), "Values don't match");
    }
    
    @Test
    public void testPatternIsNull() {
        Set<ConstraintViolation<SimpleTestObject>> constraintViolations =
                validator.validate(new SimpleTestObject(VALID_VALUE, null));

        Assert.assertEquals(constraintViolations.size(), 0, "Validation errors");
        
        constraintViolations =
            validator.validate(new SimpleTestObject(null, null));

        Assert.assertEquals(constraintViolations.size(), 0, "Validation errors");
    }

}

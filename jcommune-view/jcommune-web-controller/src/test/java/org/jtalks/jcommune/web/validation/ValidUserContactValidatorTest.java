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

import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.when;

import org.jtalks.jcommune.model.entity.UserContactType;
import org.jtalks.jcommune.service.UserContactsService;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.web.validation.annotations.ValidUserContact;
import org.jtalks.jcommune.web.validation.validators.ValidUserContactValidator;
import org.mockito.Mock;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.ConstraintValidatorContext;

/**
 * Test for {@link ValidUserContact} annotation constraint and 
 * {@link ValidUserContactValidator} implementation.
 *
 * @author Vyachelsav Mishcheryakov
 */
public class ValidUserContactValidatorTest {
	
	/** Valid value for tests */
	private final static String VALID_VALUE = "value";
	
	private final static String WRONG_VALUE = "valu";
	
	/** Pattern for valid value */
	private final static String VALID_PATTERN = "^value$";
	
	/** Id of valid contact type */
	private final static long TYPE_ID_VALID = 1;
	
	/** Id of contact type with null pattern */
	private final static long TYPE_ID_NULL_PATTERN = 2;
	
	/** Id of non-existent contact type */
	private final static long TYPE_ID_NON_EXISTENT = -1;
	
	@Mock
	private UserContactsService contactsService;
	
	@Mock
	private ConstraintValidatorContext validatorContext;
	
	private ValidUserContactValidator validator;
	
    /**
     * Class for testing constraint.
     */
    @ValidUserContact(field = "value", storedTypeId = "typeId", message = "Values don't match")
    public class SimpleTestObject {
        String value;
        long typeId;

        public SimpleTestObject(String value, long typeIdValid) {
            this.value = value;
            this.typeId = typeIdValid;
        }

        public String getValue() {

            return value;
        }

        public long getTypeId() {
            return typeId;
        }
    }
    
    /**
     * Class for testing constraint with non existing properties.
     */
    @ValidUserContact(field = "aaa", storedTypeId = "bbb")
    public class TestObjectBadProperties {
    	String value;
        long typeId;

        public TestObjectBadProperties(String value, long typeIdValid) {
            this.value = value;
            this.typeId = typeIdValid;
        }

        public String getValue() {

            return value;
        }

        public long getTypeId() {
            return typeId;
        }
    }
    
    /**
     * Initialize validator with annotation got from <code>clazz</code>
     * @param validator validator
     * @param clazz target class for validation
     * @return
     */
    private void initializeValidator(ValidUserContactValidator validator, Class<?> clazz) {
    	ValidUserContact annotation = (ValidUserContact)
			clazz.getDeclaredAnnotations()[0];
    	validator.initialize(annotation);
    }
    
    @BeforeMethod
    public void setUp() throws NotFoundException {
        initMocks(this);
        validator = new ValidUserContactValidator();
        
        UserContactType validContactType = new UserContactType();
        validContactType.setId(TYPE_ID_VALID);
        validContactType.setValidationPattern(VALID_PATTERN);
        UserContactType nullPatternContactType = new UserContactType();
        nullPatternContactType.setId(TYPE_ID_NULL_PATTERN);
        nullPatternContactType.setValidationPattern(null);
		when(contactsService.get(TYPE_ID_VALID)).thenReturn(validContactType);
		when(contactsService.get(TYPE_ID_NULL_PATTERN)).thenReturn(nullPatternContactType);
		when(contactsService.get(TYPE_ID_NON_EXISTENT)).thenThrow(new NotFoundException());
		validator = new ValidUserContactValidator();
		validator.setContactsService(contactsService);
    }

    @Test
    public void testValidatorSuccess() {
    	initializeValidator(validator, SimpleTestObject.class);
        boolean result = validator.isValid(
        		new SimpleTestObject(VALID_VALUE, TYPE_ID_VALID),
        		validatorContext);

        Assert.assertEquals(result, true, "Validation errors");
    }
    
    @Test
    public void testValidatorFail() {
    	initializeValidator(validator, SimpleTestObject.class);
    	boolean result = validator.isValid(
    			new SimpleTestObject(WRONG_VALUE, TYPE_ID_VALID),
    			validatorContext);

        Assert.assertEquals(result, false, "Validation without errors");
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testWrongField() {
    	initializeValidator(validator, TestObjectBadProperties.class);
        validator.isValid(new TestObjectBadProperties("aaa", TYPE_ID_VALID), validatorContext);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testPatternObjectNotExist() {
    	initializeValidator(validator, TestObjectBadProperties.class);
        validator.isValid(new TestObjectBadProperties("", TYPE_ID_NON_EXISTENT), validatorContext);
    }
    
    @Test
    public void testValueIsNull() {
    	initializeValidator(validator, SimpleTestObject.class);
    	
    	boolean result = validator.isValid(
    			new SimpleTestObject(null, TYPE_ID_VALID),
    			validatorContext);

        Assert.assertEquals(result, false, "Validation without errors");
    }
    
    @Test
    public void testPatternIsNull() {
    	initializeValidator(validator, SimpleTestObject.class);
    	boolean result = validator.isValid(
    			new SimpleTestObject(VALID_VALUE, TYPE_ID_NULL_PATTERN),
    			validatorContext);

        Assert.assertEquals(result, true, "Validation errors");
    }

}

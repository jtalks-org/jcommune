package org.jtalks.jcommune.model.validation;

import static org.mockito.Mockito.when;

import javax.validation.ConstraintValidatorContext;
import org.jtalks.jcommune.model.validation.annotations.SizeAndNotBlank;
import org.jtalks.jcommune.model.validation.validators.SizeAndNotBlankValidator;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;



/**
 *
 * @author Alexandra Khekhneva
 *
 */
public class SizeAndNotBlankValidatorTest {

	@Mock
    private ConstraintValidatorContext validatorContext;
    @Mock
    private SizeAndNotBlank sizeAndNotBlank;

    private SizeAndNotBlankValidator validator;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        this.validator = new SizeAndNotBlankValidator();
    }

    @Test
    public void testEmptyValueIsValid() {
        String value = "";
        boolean isValid = validator.isValid(value, validatorContext);
        Assert.assertTrue(isValid, "Value has size is null, so it must be invalid");
    }

    @Test
    public void testValueSizeBetweenMinAndMaxIsValid() {
        String value = "user";
        int min = 3;
        int max = 10;
        when(sizeAndNotBlank.min()).thenReturn(min);
        when(sizeAndNotBlank.max()).thenReturn(max);

        validator.initialize(sizeAndNotBlank);
        boolean isValid = validator.isValid(value, validatorContext);

        Assert.assertTrue(isValid, "Value has correct size, so it must be valid");
    }

    @Test
    public void testValueSizeLessThanMinIsValid() {
        String  value= "aaa";
        int min = 5;
        int max = 10;
        when(sizeAndNotBlank.min()).thenReturn(min);
        when(sizeAndNotBlank.max()).thenReturn(max);

        validator.initialize(sizeAndNotBlank);
        boolean isValid = validator.isValid(value, validatorContext);

        Assert.assertFalse(isValid, "Value has size, that less than min possible, so it must be invalid");
    }

    @Test
    public void testValueSizeMoreThanMaxIsInvalid() {
        String value = "user123456789";
        int min = 1;
        int max = 10;

        when(sizeAndNotBlank.min()).thenReturn(min);
        when(sizeAndNotBlank.max()).thenReturn(max);

        validator.initialize(sizeAndNotBlank);
        boolean isValid = validator.isValid(value, validatorContext);

        Assert.assertFalse(isValid, "Value has size, that more than max possible size, so it must be invalid");
    }



}

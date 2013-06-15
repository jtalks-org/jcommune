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

import javax.validation.ConstraintValidatorContext;

import org.jtalks.jcommune.model.entity.JCUser;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class PageSizeValidatorTest {
    @Mock
    private ConstraintValidatorContext validatorContext;
    private PageSizeValidator pageSizeValidator;
    
    @BeforeTest
    public void init() {
        MockitoAnnotations.initMocks(this);
        this.pageSizeValidator = new PageSizeValidator();
    }
    
    @Test(dataProvider = "validPageSizes")
    public void testIsValidWithAvailablePageSize(int availablePageSize) {
        boolean isValid = pageSizeValidator.isValid(availablePageSize, validatorContext);
        
        Assert.assertTrue(isValid, "Value from the list of available sizes must be valid.");
    }
    
    @DataProvider(name = "validPageSizes")
    public Object[][] getValidPageSizes() {
        int rowSize = 1;
        Object[][] validPageSizes = new Object[JCUser.PAGE_SIZES_AVAILABLE.length][rowSize];
        for (int i = 0; i < JCUser.PAGE_SIZES_AVAILABLE.length; i++) {
            validPageSizes[i][rowSize - 1] = JCUser.PAGE_SIZES_AVAILABLE[i];
        }
        return validPageSizes;
    }
    
    @Test
    public void testIsValidWithPageSizeAsNull() {
        boolean isValid = pageSizeValidator.isValid(null, validatorContext);
        
        Assert.assertFalse(isValid, "Null must be invalid value.");
    }
    
    @Test(dataProvider = "boundaryValues")
    public void testIsValidWithBoundaryValue(int boundaryValue) {
        boolean isValid = pageSizeValidator.isValid(null, validatorContext);
        
        Assert.assertFalse(isValid, 
                "Max possible integer and min possible integer must be invalid values.");
    }
    
    @DataProvider(name = "boundaryValues")
    public Object[][] getBoundaryValues() {
        return new Object[][] {
                {Integer.MAX_VALUE},
                {Integer.MIN_VALUE}
        };
    }
    
    @Test
    public void testIsValidWithNotAvailablePageSize() {
        int notAvailablePageSize = 379;
        
        boolean isValid = pageSizeValidator.isValid(notAvailablePageSize, validatorContext);
        
        Assert.assertFalse(isValid, 
                "This value isn't available, so it must be invalid. Passed value - " + notAvailablePageSize);
    }
    
}

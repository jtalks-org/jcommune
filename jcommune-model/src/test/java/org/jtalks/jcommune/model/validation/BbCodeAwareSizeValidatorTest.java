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

import org.jtalks.jcommune.model.validation.annotations.BbCodeAwareSize;
import org.jtalks.jcommune.model.validation.validators.BbCodeAwareSizeValidator;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Evgeniy Naumenko
 */
public class BbCodeAwareSizeValidatorTest {

    @BbCodeAwareSize(min = 5, max = 10)
    public String value;

    @Mock
    private BbCodeAwareSizeValidator validator;

    @BeforeMethod
    public void init() throws NoSuchFieldException {
        BbCodeAwareSize annotation = (BbCodeAwareSize)
                BbCodeAwareSizeValidatorTest.class.getField("value").getDeclaredAnnotations()[0];
        initMocks(this);
        validator = new BbCodeAwareSizeValidator();
        validator.initialize(annotation);
    }

    @Test
    public void testRemoveBBCodes() {
        String incoming = "[bb] lol [code] [/omg]";
        String expected = " lol  ";

        //assertEquals(validator.removeBBCodes(incoming), expected);
    }

    @Test
    public void testValidationPassed() {
        String source = "1234567";
        //when(validator.removeBBCodes(source)).thenReturn(source);

        assertTrue(validator.isValid(source, null));

        //verify(validator).removeBBCodes(source);
    }

    @Test
    public void testNullValue() {
        assertFalse(validator.isValid(null, null));
    }

    @Test
    public void testValueTooLong() {
        String source = "123456789010";
        //when(validator.removeBBCodes(source)).thenReturn(source);

        assertFalse(validator.isValid(source, null));

        //verify(validator).removeBBCodes(source);
    }

    @Test
    public void testValueTooShort() {
        String source = "123";
        //when(validator.removeBBCodes(source)).thenReturn(source);

        assertFalse(validator.isValid(source, null));

        //verify(validator).removeBBCodes(source);
    }

    @Test
    public void testSpaces() {
        String source = "             ";
        //when(validator.removeBBCodes(source)).thenReturn(source);

        assertFalse(validator.isValid(source, null));

        //verify(validator).removeBBCodes(source);
    }
    
    @Test
    public void testMaxLengthWithBbCodes() {
    	String source = "[b][b]123[/b][/b]";
    	
    	assertFalse(validator.isValid(source, null));
    }
}

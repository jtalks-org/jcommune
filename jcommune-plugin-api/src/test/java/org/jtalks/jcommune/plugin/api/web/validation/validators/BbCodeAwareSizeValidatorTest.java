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
package org.jtalks.jcommune.plugin.api.web.validation.validators;

import org.jtalks.jcommune.plugin.api.service.PluginBbCodeService;
import org.jtalks.jcommune.plugin.api.web.validation.annotations.BbCodeAwareSize;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import org.mockito.Spy;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Evgeniy Naumenko
 */
public class BbCodeAwareSizeValidatorTest {

    @BbCodeAwareSize(min = 3, max = 25)
    public String value;

    private BbCodeAwareSizeValidator validator;
    
    @Mock
    private PluginBbCodeService bbCodeService;

    @BeforeMethod
    public void init() throws NoSuchFieldException {
        BbCodeAwareSize annotation = (BbCodeAwareSize)
                BbCodeAwareSizeValidatorTest.class.getField("value").getDeclaredAnnotations()[0];
        initMocks(this);

        validator = new BbCodeAwareSizeValidator(bbCodeService);
        validator.initialize(annotation);
    }

    @Test
    public void testValidationPassed() {
        String source = "1234567";
        when(bbCodeService.stripBBCodes(source)).thenReturn(source);

        assertTrue(validator.isValid(source, null));
    }

    @Test
    public void testNullValue() {
        assertFalse(validator.isValid(null, null));
    }

    @Test
    public void testValueTooLong() {
        String source = "12345678901234567890123456";
        when(bbCodeService.stripBBCodes(source)).thenReturn(source);

        assertFalse(validator.isValid(source, null));
    }

    @Test
    public void testValueTooShort() {
        String source = "12";
        when(bbCodeService.stripBBCodes(source)).thenReturn(source);

        assertFalse(validator.isValid(source, null));
    }

    @Test
    public void testSpaces() {
        String source = "             ";
        when(bbCodeService.stripBBCodes(source)).thenReturn(source);

        assertFalse(validator.isValid(source, null));
    }

    @Test
    public void testMaxLengthWithBbCodes() {
    	String source = "[b]123456789012345678[/b]";
        when(bbCodeService.stripBBCodes(source)).thenReturn("123456789012345678");

        assertTrue(validator.isValid(source, null));
    }

    @Test
    public void testTooLongWithBbCodes() {
        String source = "[b][b]1234567890123[/b][/b]";
        when(bbCodeService.stripBBCodes(source)).thenReturn("1234567890123");

    	assertFalse(validator.isValid(source, null));
    }

    @Test
    public void testTooShortWithBbCodes() {
        String source = "[b][b]12[/b][/b]";
        when(bbCodeService.stripBBCodes(source)).thenReturn("12");

    	assertFalse(validator.isValid(source, null));
    }

    @Test
    public void testBBCodesOnly() {
        String source = "[b][b][/b][/b]";
        when(bbCodeService.stripBBCodes(source)).thenReturn("");

    	assertFalse(validator.isValid(source, null));
    }

    @Test
    public void testBBCodesListOnly() {
        String source = "[list]\n" +
                "[*]\n" +
                "[*]\n" +
                "[/list]";
        when(bbCodeService.stripBBCodes("")).thenReturn("");
        assertFalse(validator.isValid(source, null));
    }
}

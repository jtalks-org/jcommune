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

import org.jtalks.jcommune.web.validation.annotations.BbCodeNesting;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class BbCodeNestingValidatorTest {
    private BbCodeNestingValidator instance;
        
    @BeforeMethod
    public void initMocks() throws NoSuchFieldException {
        BbCodeNesting annotation = mock(BbCodeNesting.class);
        // limit nesting to 2
        when(annotation.maxNestingValue()).thenReturn(2);
        instance = new BbCodeNestingValidator();
        instance.initialize(annotation);
    }

    @Test
    public void nullAndEmptyStringShouldBeTreatedAsValid() {
        assertTrue(instance.isValid(null, null));
        assertTrue(instance.isValid("", null));
    }
    
    @Test(dataProvider = "validMessages")
    public void testValidMessages(String message, String assertinMessage) {
        assertTrue(instance.isValid(message, null), message + " - " + assertinMessage);
    }
    
    @Test(dataProvider = "invalidMessages")
    public void testInvalidMessages(String message, String assertionMessage) {
        assertFalse(instance.isValid(message, null), message + " - " + assertionMessage);
    }    
    
    @DataProvider
    public String[][] invalidMessages() {
        return new String[][] {
            {"[b][b][b]text", "Open tags without close tags are still counted when validating the nesteness."},
            {"[b][b][b]text[/b][/b][/b]", "Message with depth 3 "
                                        + "when depth limit is 2 should be considered as invalid."}
        };
    } 
    
    @DataProvider
    public String[][] validMessages() {
        return new String[][] {
            {"[b][b]text[/b][/b]", "Testing boundary: message with depth 2 "
                                    + "when depth limit is 2 should be considered as valid."},
            {"[*][*][*][*][*]", "List items are not taking into account as nesting. "
                                + "Message should be considered as valid"},
            {"[/b][/b][/b][/b][/b]", "Close tags without respective open tags should be ignored in depth validation."
                                        + "Message should be considered as valid"},
            {"[b]a[/b]b[b]c[/b]d[b]e[/b]a[b]b[/b]c[b]d[/b]e", "Test long sequence of tags with nesting depth 1. "
                                                            + "The message shoud be treated as valid."},
            {"[z][z][z][z][z][z][z][z][z]", "In the depth validation only valid tags should be taken into account. "
                                            + "Invalid tags should be ignored and the message should be treated as valid."}
        };
    } 
}

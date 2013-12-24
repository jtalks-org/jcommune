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

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.web.validation.annotations.BbCodeNesting;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class BbCodeNestingValidatorTest {
    private BbCodeNestingValidator bbCodeNestingValidator;
    @Mock
    private UserService userService;
    @Mock
    private BbCodeNesting annotation;
        
    @BeforeMethod
    public void initMocks() throws NoSuchFieldException {
        MockitoAnnotations.initMocks(this);
        // limit nesting to 2
        when(annotation.maxNestingValue()).thenReturn(2);
        when(userService.getCurrentUser()).thenReturn(new JCUser("", "", ""));
        
        bbCodeNestingValidator = new BbCodeNestingValidator(userService);
        bbCodeNestingValidator.initialize(annotation);
    }

    @Test
    public void nullAndEmptyStringShouldBeTreatedAsValid() {
        assertTrue(bbCodeNestingValidator.isValid(null, null));
        assertTrue(bbCodeNestingValidator.isValid("", null));
    }
    
    @Test(dataProvider = "validMessages")
    public void testValidMessages(String message) {
        assertTrue(bbCodeNestingValidator.isValid(message, null), message);
    }
    
    @Test(dataProvider = "invalidMessages")
    public void testInvalidMessages(String message) {
        assertFalse(bbCodeNestingValidator.isValid(message, null), message);
    }    
    
    @DataProvider
    public String[][] invalidMessages() {
        return new String[][] {
            {"[b][b][b]text"},
            {"[quote][b][i]text[/i][/b][/quote]"},
            {"[quote=\"name\"][b][i]text[/i][/b][/quote]"},
            {"[b][b][b]text[/b][/b][/b]"}
        };
    } 
    
    @DataProvider
    public String[][] validMessages() {
        return new String[][] {
            {"[b][b]text"},
            {"[qoute][b]text[/b][/qoute]"},
            {"[qoute name=\"name\"][b]text[/b][/qoute]"},
            {"[*][*][*][*][*]"},
            {"[/b][/b][/b][/b][/b]"},
            {"[b]a[/b]b[b]c[/b]d[b]e[/b]a[b]b[/b]c[b]d[/b]e"},
            {"[z][z][z][z][z][z][z][z][z]"}
        };
    } 
}

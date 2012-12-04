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

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.web.validation.annotations.BbCodeNesting;
import org.jtalks.jcommune.web.validation.validators.BbCodeNestingValidator;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class BbCodeNestingValidatorTest {

    private BbCodeNestingValidator bbCodeNestingValidator;

    private UserService userService;

    @BbCodeNesting
    private String text;

    public BbCodeNestingValidatorTest() throws NoSuchFieldException {
        userService = mock(UserService.class);
        bbCodeNestingValidator = new BbCodeNestingValidator(userService);
        BbCodeNesting bbCodeNesting = (BbCodeNesting) BbCodeNestingValidatorTest.class
                .getDeclaredField("text").getDeclaredAnnotations()[0];
        bbCodeNestingValidator.initialize(bbCodeNesting);
    }

    @Test(dataProvider="invalidMessages")
    public void testValidationFail(String message){
        when(userService.getCurrentUser()).thenReturn(new JCUser("","",""));
        assertFalse(bbCodeNestingValidator.isValid(message, null));
    }

    @Test(dataProvider="validMessages")
    public void testValidationSuccess(String message){
        assertTrue(bbCodeNestingValidator.isValid(message,null));
    }
    
    @Test
    public void testTextIsNull() {
        assertTrue(bbCodeNestingValidator.isValid(null,null));
    }
    
    @DataProvider
    public String[][] validMessages() {
        return new String[][] { // {"message"}
                {"[b][/b][b][/b][u][/u][u][/u][u][u][u][/u][/u][/u]"},
                {"[b][/b][b][/b][u][/u][u]text[/u][u][u][u][/u][/u][/u]text"},
                {"text"},
                {"[*][*][*][*][*][*][*][*][*][*][*][*][*][*][*]"}
        };
    }
    
    @DataProvider
    public String[][] invalidMessages() {
        return new String[][] { // {"message"}
                {"[b][b][b][b][b][color][b][b][b][b][u]"},
                {"[b][b][b][b][b][b][b][b][b][b][b][b][color][b][/b][/b][/b][u]"}
        };
    }
}

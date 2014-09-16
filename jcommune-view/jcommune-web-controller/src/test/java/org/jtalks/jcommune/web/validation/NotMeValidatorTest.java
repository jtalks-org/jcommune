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

import org.jtalks.jcommune.model.entity.AnonymousUser;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.web.validation.validators.NotMeValidator;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Evgeniy Naumenko
 */
public class NotMeValidatorTest {

    @Mock
    private UserService service;

    private NotMeValidator validator;

    private String username = "username";
    private JCUser user = new JCUser(username, null, null);

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        validator = new NotMeValidator(service);
    }

    @Test
    public void testIsValid() throws Exception {
        when(service.getCurrentUser()).thenReturn(user);

        assertTrue(validator.isValid("other name", null));
    }

    @Test
    public void testIsValidForAnonymous() throws Exception {
        when(service.getCurrentUser()).thenReturn(new AnonymousUser());

        assertTrue(validator.isValid(username, null));
    }

    @Test
    public void testIsValidForTheSameUser() throws Exception {
        when(service.getCurrentUser()).thenReturn(user);

        assertFalse(validator.isValid(username, null));
    }


    @Test
    public void testIsValidForTheSameUserInAnotherCase() throws Exception {
        when(service.getCurrentUser()).thenReturn(user);

        assertFalse(validator.isValid(username.toUpperCase(), null));
    }
}


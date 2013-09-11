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

import org.jtalks.common.model.entity.Entity;
import org.jtalks.jcommune.model.dao.ValidatorDao;
import org.jtalks.jcommune.model.validation.validators.UniqueValidator;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Evgeniy Naumenko
 */
public class UniqueValidatorTest {

    private UniqueValidator validator;
    @Mock
    private ValidatorDao<String> dao;

    @BeforeMethod
    public void init() {
        initMocks(this);
        validator = new UniqueValidator();
        ApplicationContext context = mock(ApplicationContext.class);
        when(context.getBean(ValidatorDao.class)).thenReturn(dao);
        validator.setApplicationContext(context);
    }

    @Test
    public void testValueExists() {
        when(dao.isResultSetEmpty(Matchers.<Class<Entity>>any(), anyString(), anyString(), anyBoolean())).thenReturn(true);

        assertTrue(validator.isValid("value", null));
    }

    @Test
    public void testValueDoesntExist() {
        when(dao.isResultSetEmpty(Matchers.<Class<Entity>>any(), anyString(), anyString(), anyBoolean())).thenReturn(false);

        assertFalse(validator.isValid("value", null));
    }

    @Test
    public void testNullValue() {
        assertTrue(validator.isValid(null, null));
    }
}

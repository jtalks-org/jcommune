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
package org.jtalks.jcommune.service.nontransactional;

import org.jtalks.common.model.entity.Property;
import org.jtalks.jcommune.model.dao.PropertyDao;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar Nurmakanov
 *
 */
public class JcommuneDatabasePropertyTest {
    @Mock
    private PropertyDao propertyDao;
    private JcommuneDatabaseProperty jcommuneDatabaseProperty = 
            JcommuneDatabaseProperty.SENDING_NOTIFICATIONS_ENABLED;
    
    @BeforeTest
    public void init() {
       MockitoAnnotations.initMocks(this); 
    }
    
    @Test
    public void testGetValue() {
        String expected = "value";
        Property property = new Property("name", expected);
        Mockito.when(propertyDao.getByName(Mockito.anyString())).thenReturn(property);
        jcommuneDatabaseProperty.setPropertyDao(propertyDao);
        
        String actual = jcommuneDatabaseProperty.getValue();
        
        Assert.assertEquals(actual, expected, "Returned an invalid property value.");
    }
    
    @Test
    public void testGetValueWithNotFoundedProperty() {
        Property property = null;
        Mockito.when(propertyDao.getByName(Mockito.anyString())).thenReturn(property);
        jcommuneDatabaseProperty.setPropertyDao(propertyDao);
        
        String actual = jcommuneDatabaseProperty.getValue();
        
        Assert.assertEquals(actual, jcommuneDatabaseProperty.getDefaultValue(),
                "Returned an invalid property value.");
    }
    
    @Test
    public void testGetValueWithNullPropertyDao() {
        jcommuneDatabaseProperty.setPropertyDao(null);
        
        String actual = jcommuneDatabaseProperty.getValue();
        
        Assert.assertEquals(actual, jcommuneDatabaseProperty.getDefaultValue(),
                "Returned an invalid property value.");
    }
}

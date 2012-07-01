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
package org.jtalks.jcommune.model.entity;

import org.jtalks.common.model.entity.Property;
import org.jtalks.jcommune.model.dao.PropertyDao;
import org.jtalks.jcommune.model.entity.JCommuneProperty;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar Nurmakanov
 *
 */
public class JCommunePropertyTest {
    @Mock
    private PropertyDao propertyDao;
    private JCommuneProperty jcommuneProperty = JCommuneProperty.SENDING_NOTIFICATIONS_ENABLED;
    
    @BeforeTest
    public void init() {
       MockitoAnnotations.initMocks(this); 
    }
    
    @Test
    public void testGetValue() {
        String expected = "value";
        Property property = new Property("name", expected);
        Mockito.when(propertyDao.getByName(Mockito.anyString())).thenReturn(property);
        jcommuneProperty.setPropertyDao(propertyDao);
        
        String actual = jcommuneProperty.getValue();
        
        Assert.assertEquals(actual, expected, "Returned an invalid property value.");
    }
    
    @Test(dataProvider = "checkBooleanValueParameter")
    public void testBooleanValue(String value, Boolean expectedBoolean) {
        Property property = null;
        Mockito.when(propertyDao.getByName(Mockito.anyString())).thenReturn(property);
        jcommuneProperty.setPropertyDao(propertyDao);
        jcommuneProperty.setDefaultValue(value);
        
        Boolean actualBoolean = jcommuneProperty.booleanValue();
        Assert.assertEquals(actualBoolean, expectedBoolean, "Returned an invalid property value.");
    }
    
    @DataProvider(name = "checkBooleanValueParameter")
    public Object[][] checkBooleanValueParameter() {
        return new Object[][] {
                {Boolean.FALSE.toString(), Boolean.FALSE},
                {Boolean.TRUE.toString(), Boolean.TRUE},
                {"abrakadabra", Boolean.FALSE},
                {String.valueOf(5), Boolean.FALSE}
        };
    }
    
    @Test
    public void testGetValueWithNotFoundedProperty() {
        Property property = null;
        Mockito.when(propertyDao.getByName(Mockito.anyString())).thenReturn(property);
        jcommuneProperty.setPropertyDao(propertyDao);
        String expectedValue = Boolean.TRUE.toString();
        jcommuneProperty.setDefaultValue(expectedValue);
        
        String actual = jcommuneProperty.getValue();
        
        Assert.assertEquals(actual, expectedValue, "Returned an invalid property value.");
    }
}

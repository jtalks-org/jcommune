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

import org.jtalks.common.model.entity.Component;
import org.jtalks.common.model.entity.ComponentType;
import org.jtalks.common.model.entity.Property;
import org.jtalks.jcommune.model.dao.ComponentDao;
import org.jtalks.jcommune.model.dao.PropertyDao;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

/**
 * @author Anuar Nurmakanov
 */
public class JCommunePropertyTest {
    private static final String PROPERTY_VALUE = "property.value";
    private static final String PROPERTY_NAME = "property.name";
    @Mock
    private PropertyDao propertyDao;
    @Mock
    private ComponentDao componentDao;
    @Mock
    Component cmp;
    private JCommuneProperty jcommuneProperty = JCommuneProperty.SENDING_NOTIFICATIONS_ENABLED;
    private JCommuneProperty jcommuneComponentProperty = JCommuneProperty.CMP_NAME;

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
        return new Object[][]{
                {Boolean.FALSE.toString(), Boolean.FALSE},
                {Boolean.TRUE.toString(), Boolean.TRUE},
                {"abrakadabra", Boolean.FALSE},
                {String.valueOf(5), Boolean.FALSE}
        };
    }

    @Test
    public void testIntValue() {
        Property property = null;
        Mockito.when(propertyDao.getByName(Mockito.anyString())).thenReturn(property);
        jcommuneProperty.setPropertyDao(propertyDao);
        jcommuneProperty.setDefaultValue("1");

        int actualValue = jcommuneProperty.intValue();
        Assert.assertEquals(actualValue, 1, "Returned an invalid property value.");
    }

    @Test(expectedExceptions = NumberFormatException.class)
    public void testIntValueWrong() {
        Property property = null;
        Mockito.when(propertyDao.getByName(Mockito.anyString())).thenReturn(property);
        jcommuneProperty.setPropertyDao(propertyDao);
        jcommuneProperty.setDefaultValue("");

        jcommuneProperty.intValue();
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

    @Test
    public void testGetDefaultValue() {
        String expected = "";
        jcommuneProperty.setPropertyDao(null);
        jcommuneProperty.setDefaultValue(expected);

        String actual = jcommuneProperty.getValue();

        Assert.assertEquals(actual, expected, "Returned an invalid property value.");
    }

    @Test
    public void testGetNameOfComponent() {
        String expected = "value";
        Component cmp = new Component(expected, "description", ComponentType.FORUM);
        Mockito.when(componentDao.getComponent()).thenReturn(cmp);
        jcommuneComponentProperty.setComponentDao(componentDao);
        jcommuneComponentProperty.setName("cmp.name");
        String actual = jcommuneComponentProperty.getValueOfComponent();

        Assert.assertEquals(actual, expected, "Returned an invalid property value.");
    }

    @Test
    public void testGetDescriptionOfComponent() {
        String expected = "description";
        Component cmp = new Component("name", expected, ComponentType.FORUM);
        Mockito.when(componentDao.getComponent()).thenReturn(cmp);
        jcommuneComponentProperty.setComponentDao(componentDao);
        jcommuneComponentProperty.setName("anyString");
        String actual = jcommuneComponentProperty.getValueOfComponent();

        Assert.assertEquals(actual, expected, "Returned an invalid property value.");
    }

    @Test
    public void testGetDefaultValueOfComponent() {
        String expected = "";
        Mockito.when(componentDao.getComponent()).thenReturn(null);
        jcommuneComponentProperty.setName("anyString");
        jcommuneComponentProperty.setDefaultValue(expected);
        String actual = jcommuneComponentProperty.getValueOfComponent();

        Assert.assertEquals(actual, expected, "Returned an invalid property value.");
    }

    @Test
    public void testGetDefaultValueOfComponentWithNotFoundedProperty() {
        String expected = "";
        Mockito.when(cmp.getName()).thenReturn(null);
        jcommuneComponentProperty.setName("cmp.name");
        jcommuneComponentProperty.setComponentDao(componentDao);
        jcommuneComponentProperty.setDefaultValue(expected);
        String actual = jcommuneComponentProperty.getValueOfComponent();
        Assert.assertEquals(actual, expected, "Returned an invalid property value.");
    }
    
    @Test
    public void testSetValueComponentDaoIsNull() {
        jcommuneProperty.setComponentDao(null);
        jcommuneProperty.setValue(null);
    }
    
    @Test
    public void testSetValue() {
        when(componentDao.getComponent()).thenReturn(cmp);        
        jcommuneProperty.setComponentDao(componentDao);
        jcommuneProperty.setName(PROPERTY_NAME);
        
        jcommuneProperty.setValue(PROPERTY_VALUE);
        
        verify(cmp).setProperty(PROPERTY_NAME, PROPERTY_VALUE);
    }

}

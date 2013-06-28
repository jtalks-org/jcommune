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
package org.jtalks.jcommune.service.transactional;

import org.jtalks.common.model.entity.Component;
import org.jtalks.jcommune.model.dao.ComponentDao;
import org.jtalks.jcommune.model.entity.ComponentInformation;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.nontransactional.Base64Wrapper;
import org.jtalks.jcommune.service.nontransactional.ImageService;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author Anuar_Nurmakanov
 * @author Andrei Alikov
 */
public class TransactionalComponentServiceTest {
    private static final String FORUM_NAME = "my forum";
    private static final String FORUM_DESCRIPTION = "my forum";
    private static final String FORUM_LOGO_TOOLTIP = "my forum";
    private static final long COMPONENT_ID = 42;
    private static final String LOGO = "logo image";
    private static final String ICON = "icon image";
    private static final String ICON_IN_ICO_FORMAT = "icon ico image";

    @Mock
    private ComponentDao componentDao;
    @Mock
    private ImageService imageService;
    @Mock
    private Component component;

    private TransactionalComponentService componentService;

    @BeforeMethod
    public void init() {
        initMocks(this);
        when(component.getId()).thenReturn(COMPONENT_ID);
        componentService = new TransactionalComponentService(imageService, componentDao);
    }

    @Test
    public void getComponentShouldBeDelegatedToDao() {
        componentService.getComponentOfForum();

        verify(componentDao).getComponent();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void setComponentInformationShouldThrowExceptionWhenComponentIdIsNotSame() throws ImageProcessException {
        when(componentDao.getComponent()).thenReturn(component);

        ComponentInformation information = new ComponentInformation();
        information.setId(COMPONENT_ID + 1);

        componentService.setComponentInformation(information);
    }

    @Test
    public void setComponentInformationShouldSetAllProperties() throws ImageProcessException {
        when(componentDao.getComponent()).thenReturn(component);
        Base64Wrapper wrapper = new Base64Wrapper();
        byte[] iconBytes = wrapper.decodeB64Bytes(ICON);
        when(imageService.preProcessAndEncodeInString64(iconBytes)).thenReturn(ICON_IN_ICO_FORMAT);

        ComponentInformation information = new ComponentInformation();
        information.setId(COMPONENT_ID);
        information.setName(FORUM_NAME);
        information.setDescription(FORUM_DESCRIPTION);
        information.setLogoTooltip(FORUM_LOGO_TOOLTIP);
        information.setLogo(LOGO);
        information.setIcon(ICON);

        componentService.setComponentInformation(information);

        verify(component).setName(FORUM_NAME);
        verify(component).setDescription(FORUM_DESCRIPTION);
        verify(component).setProperty(TransactionalComponentService.LOGO_TOOLTIP_PROPERTY, FORUM_LOGO_TOOLTIP);
        verify(component).setProperty(TransactionalComponentService.COMPONENT_FAVICON_ICO_PARAM, ICON_IN_ICO_FORMAT);
        verify(component).setProperty(TransactionalComponentService.COMPONENT_FAVICON_PNG_PARAM, ICON);
        verify(component).setProperty(TransactionalComponentService.LOGO_PROPERTY, LOGO);

        verify(component).setProperty(eq(TransactionalComponentService.COMPONENT_INFO_CHANGE_DATE_PROPERTY), anyString());
    }

    @Test(dataProvider = "emptyValues")
    public void setComponentInformationShouldShouldNotSetLogoIfItIsEmpty(String logo) throws ImageProcessException {
        when(componentDao.getComponent()).thenReturn(component);
        Base64Wrapper wrapper = new Base64Wrapper();
        byte[] iconBytes = wrapper.decodeB64Bytes(ICON);
        when(imageService.preProcessAndEncodeInString64(iconBytes)).thenReturn(ICON_IN_ICO_FORMAT);

        ComponentInformation information = new ComponentInformation();
        information.setId(COMPONENT_ID);
        information.setName(FORUM_NAME);
        information.setDescription(FORUM_DESCRIPTION);
        information.setLogoTooltip(FORUM_LOGO_TOOLTIP);
        information.setLogo(logo);
        information.setIcon(ICON);

        componentService.setComponentInformation(information);

        verify(component).setName(FORUM_NAME);
        verify(component).setDescription(FORUM_DESCRIPTION);
        verify(component).setProperty(TransactionalComponentService.LOGO_TOOLTIP_PROPERTY, FORUM_LOGO_TOOLTIP);
        verify(component).setProperty(TransactionalComponentService.COMPONENT_FAVICON_ICO_PARAM, ICON_IN_ICO_FORMAT);
        verify(component).setProperty(TransactionalComponentService.COMPONENT_FAVICON_PNG_PARAM, ICON);
        verify(component, never()).setProperty(eq(TransactionalComponentService.LOGO_PROPERTY), anyString());

        verify(component).setProperty(eq(TransactionalComponentService.COMPONENT_INFO_CHANGE_DATE_PROPERTY),
                anyString());
    }

    @Test(dataProvider = "emptyValues")
    public void setComponentInformationShouldNotSetIconIfItIsEmpty(String icon) throws ImageProcessException {
        when(componentDao.getComponent()).thenReturn(component);

        ComponentInformation information = new ComponentInformation();
        information.setId(COMPONENT_ID);
        information.setName(FORUM_NAME);
        information.setDescription(FORUM_DESCRIPTION);
        information.setLogoTooltip(FORUM_LOGO_TOOLTIP);
        information.setLogo(LOGO);
        information.setIcon(icon);

        componentService.setComponentInformation(information);

        verify(component).setName(FORUM_NAME);
        verify(component).setDescription(FORUM_DESCRIPTION);
        verify(component).setProperty(TransactionalComponentService.LOGO_TOOLTIP_PROPERTY, FORUM_LOGO_TOOLTIP);
        verify(component, never()).setProperty(TransactionalComponentService.COMPONENT_FAVICON_ICO_PARAM,
                ICON_IN_ICO_FORMAT);
        verify(component, never()).setProperty(TransactionalComponentService.COMPONENT_FAVICON_PNG_PARAM, ICON);
        verify(component).setProperty(TransactionalComponentService.LOGO_PROPERTY, LOGO);

        verify(component).setProperty(eq(TransactionalComponentService.COMPONENT_INFO_CHANGE_DATE_PROPERTY),
                anyString());
    }

    @DataProvider(name = "emptyValues")
    public Object[][] parameterResizeImage() {
        return new Object[][]{{""}, {null}};
    }

    @Test
    public void getComponentModificationTimeShouldReturnPropertyIfItExists() {
        long lastModificationTime = 42;

        when(componentDao.getComponent()).thenReturn(component);
        when(component.getProperty(TransactionalComponentService.COMPONENT_INFO_CHANGE_DATE_PROPERTY))
                .thenReturn(String.valueOf(lastModificationTime));

        Date modificationTime = componentService.getComponentModificationTime();

        assertEquals(modificationTime.getTime(), lastModificationTime);
    }

    @Test
    public void getComponentModificationTimeShouldReturnNullIfNotExists() {

        when(componentDao.getComponent()).thenReturn(component);
        when(component.getProperty(TransactionalComponentService.COMPONENT_INFO_CHANGE_DATE_PROPERTY))
                .thenReturn(null);

        Date modificationTime = componentService.getComponentModificationTime();

        assertNull(modificationTime);
    }
}
    

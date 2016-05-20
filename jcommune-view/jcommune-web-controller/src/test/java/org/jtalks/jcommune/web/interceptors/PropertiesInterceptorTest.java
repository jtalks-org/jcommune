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
package org.jtalks.jcommune.web.interceptors;

import org.joda.time.LocalDateTime;
import org.jtalks.jcommune.model.dao.ComponentDao;
import org.jtalks.jcommune.model.entity.JCommuneProperty;
import org.mockito.Mock;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.ModelAndViewAssert.assertAndReturnModelAttributeOfType;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author Vyacheslav Mishcheryakov
 *
 */
public class PropertiesInterceptorTest {
    
    private static final String PARAM_CMP_NAME = "cmpName";
    private static final String PARAM_CMP_DESCRIPTION = "cmpDescription";
    private static final String PARAM_SAPE_SHOW_DUMMY_LINKS = "sapeShowDummyLinks";
    private static final String PARAM_LOGO_TOOLTIP = "logoTooltip";
    private static final String PARAM_LAST_INFO_CHAGE = "infoChangeDate";
    private static final String PARAM_SESSION_TIMEOUT = "sessionTimeout";
    private static final String PARAM_AVATAR_MAX_SIZE = "avatarMaxSize";
    private static final String PARAM_EMAIL_NOTIFICATION = "emailNotification";
    
    private static final String CMP_NAME = PARAM_CMP_NAME;
    private static final String CMP_DESCRIPTION = PARAM_CMP_DESCRIPTION;
    private static final boolean SAPE_SHOW_DUMMY_LINKS = false;
    private static final String LOGO_TOOLTIP = PARAM_LOGO_TOOLTIP;
    private static final String LAST_CHANGE_DATE = "2013.01.10 00:00:00";
    private static final String SESSION_TIMEOUT = "24";
    private static final String AVATAR_MAX_SIZE = "1234";
    private static final boolean EMAIL_NOTIFICATION = true;

    private JCommuneProperty cmpName = JCommuneProperty.CMP_NAME;
    private JCommuneProperty cmpDescription = JCommuneProperty.CMP_DESCRIPTION;
    private JCommuneProperty sapeShowDummyLinks = JCommuneProperty.CMP_SAPE_SHOW_DUMMY_LINKS;
    private JCommuneProperty logoToolTip = JCommuneProperty.LOGO_TOOLTIP;
    private JCommuneProperty lastChangeDate = JCommuneProperty.ADMIN_INFO_LAST_UPDATE_TIME;
    private JCommuneProperty titlePrefix = JCommuneProperty.ALL_PAGES_TITLE_PREFIX;
    private JCommuneProperty copyright = JCommuneProperty.COPYRIGHT;
    private JCommuneProperty sessionTimeout = JCommuneProperty.SESSION_TIMEOUT;
    private JCommuneProperty avatarMaxSize = JCommuneProperty.AVATAR_MAX_SIZE;
    private JCommuneProperty sendingNotificationsEnabled = JCommuneProperty.SENDING_NOTIFICATIONS_ENABLED;

    @Mock
    private ComponentDao componentDao;
    
    private PropertiesInterceptor propertiesInterceptor;
    
    @BeforeMethod
    public void init() {
        initMocks(this);
        
        propertiesInterceptor = new PropertiesInterceptor(
                cmpName, cmpDescription, sapeShowDummyLinks, logoToolTip, lastChangeDate, titlePrefix, copyright,
                sessionTimeout, avatarMaxSize, sendingNotificationsEnabled
        );
        
        cmpName.setName("cmp.name");
        cmpDescription.setName("cmp.description");
        sapeShowDummyLinks.setName("sape.show.dummy.links");
        logoToolTip.setName("sape.show.dummy.links");
        lastChangeDate.setName("last.change.date");
        titlePrefix.setName("prefix_title");
        sessionTimeout.setName("jcommune.session_timeout");
        avatarMaxSize.setName("jcommune.avatar_max_size");
        sendingNotificationsEnabled.setName("jcommune.sending_notifications_enabled");
        
        cmpName.setDefaultValue(CMP_NAME);
        cmpDescription.setDefaultValue(CMP_DESCRIPTION);
        sapeShowDummyLinks.setDefaultValue(String.valueOf(SAPE_SHOW_DUMMY_LINKS));
        logoToolTip.setDefaultValue(LOGO_TOOLTIP);
        lastChangeDate.setDefaultValue(LAST_CHANGE_DATE);
        titlePrefix.setDefaultValue("prefix of the title");
        copyright.setDefaultValue("My Copyright {current_year}");
        sessionTimeout.setDefaultValue(SESSION_TIMEOUT);
        avatarMaxSize.setDefaultValue(AVATAR_MAX_SIZE);
        sendingNotificationsEnabled.setDefaultValue(String.valueOf(EMAIL_NOTIFICATION));
        
        cmpName.setComponentDao(componentDao);
        cmpDescription.setComponentDao(componentDao);
        sapeShowDummyLinks.setComponentDao(componentDao);
        logoToolTip.setComponentDao(componentDao);
        lastChangeDate.setComponentDao(componentDao);
        titlePrefix.setComponentDao(componentDao);
        sessionTimeout.setComponentDao(componentDao);
        avatarMaxSize.setComponentDao(componentDao);
        sendingNotificationsEnabled.setComponentDao(componentDao);
    }
    
    
    @Test
    public void testPostHandleNormal() {
        ModelAndView mav = new ModelAndView("view");
        propertiesInterceptor.postHandle(null, null, null, mav);
        
        String cmpName = assertAndReturnModelAttributeOfType(mav, PARAM_CMP_NAME, String.class);
        String cmpDescription = assertAndReturnModelAttributeOfType(mav, PARAM_CMP_DESCRIPTION, String.class);
        boolean showDummyLinks = assertAndReturnModelAttributeOfType(mav, PARAM_SAPE_SHOW_DUMMY_LINKS, Boolean.class);
        String logoTooltip = assertAndReturnModelAttributeOfType(mav, PARAM_LOGO_TOOLTIP, String.class);
        String lastChangeDate = assertAndReturnModelAttributeOfType(mav, PARAM_LAST_INFO_CHAGE, String.class);
        String sessionTimeout = assertAndReturnModelAttributeOfType(mav, PARAM_SESSION_TIMEOUT, String.class);
        String avatarMaxSize = assertAndReturnModelAttributeOfType(mav, PARAM_AVATAR_MAX_SIZE, String.class);
        boolean emailNotification = assertAndReturnModelAttributeOfType(mav, PARAM_EMAIL_NOTIFICATION, Boolean.class);
        String titlePrefixProperty = assertAndReturnModelAttributeOfType(mav, "cmpTitlePrefix", String.class);
        String copyrightValue = assertAndReturnModelAttributeOfType(mav, "userDefinedCopyright", String.class);
        
        assertEquals(cmpName, CMP_NAME);
        assertEquals(cmpDescription, CMP_DESCRIPTION);
        assertEquals(showDummyLinks, SAPE_SHOW_DUMMY_LINKS);
        assertEquals(logoTooltip, LOGO_TOOLTIP);
        assertEquals(lastChangeDate, LAST_CHANGE_DATE);
        assertEquals(sessionTimeout, SESSION_TIMEOUT);
        assertEquals(avatarMaxSize, AVATAR_MAX_SIZE);
        assertEquals(emailNotification, EMAIL_NOTIFICATION);
        assertEquals(titlePrefixProperty, "prefix of the title");
        assertThat(copyrightValue, equalTo("My Copyright " + getCurrentYear()));
    }
    
    @Test
    public void testPostHandleMavIsNull() {
        propertiesInterceptor.postHandle(null, null, null, null);        
    }
    
    @Test
    public void testPostHandleRedirectRequest() {
        ModelAndView mav = new ModelAndView("redirect:/somewhere");
        propertiesInterceptor.postHandle(null, null, null, mav);
        
        assertNull(mav.getModel().get(PARAM_CMP_NAME));
        assertNull(mav.getModel().get(PARAM_CMP_DESCRIPTION));
        assertNull(mav.getModel().get(PARAM_SAPE_SHOW_DUMMY_LINKS));
        assertNull(mav.getModel().get(PARAM_LOGO_TOOLTIP));
        assertNull(mav.getModel().get(PARAM_LAST_INFO_CHAGE));
        assertNull(mav.getModel().get(PARAM_SESSION_TIMEOUT));
        assertNull(mav.getModel().get(PARAM_AVATAR_MAX_SIZE));
        assertNull(mav.getModel().get(PARAM_EMAIL_NOTIFICATION));
        assertNull(mav.getModel().get("cmpTitlePrefix"));
        assertNull(mav.getModel().get("userDefinedCopyright"));
    }

    private int getCurrentYear() {
        return new LocalDateTime().getYear();
    }
    
    

}

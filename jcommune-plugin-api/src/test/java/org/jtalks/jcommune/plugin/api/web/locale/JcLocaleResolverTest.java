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
package org.jtalks.jcommune.plugin.api.web.locale;

import org.jtalks.jcommune.model.entity.AnonymousUser;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Language;
import org.jtalks.jcommune.plugin.api.service.UserReader;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Locale;

import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;


/**
 * @author Mikhail Stryzhonok
 */
public class JcLocaleResolverTest {
    @Mock
    private UserReader userReader;
    @Mock
    private HttpServletRequest request;

    @BeforeMethod
    public void init() {
        initMocks(this);
        JcLocaleResolver resolver = (JcLocaleResolver) JcLocaleResolver.getInstance();
        resolver.setUserReader(userReader);
    }

    @Test
    public void resolveLocaleShouldReturnUserLocaleIfUserLoggedIn() {
        JCUser currentUser = new JCUser("username", "email@mail.ru", "password");
        currentUser.setLanguage(Language.ENGLISH);
        when(userReader.getCurrentUser()).thenReturn(currentUser);

        Locale result = JcLocaleResolver.getInstance().resolveLocale(request);

        assertEquals(result, currentUser.getLanguage().getLocale());
        verify(request).setAttribute(CookieLocaleResolver.LOCALE_REQUEST_ATTRIBUTE_NAME, Locale.ENGLISH);
    }

    @Test
    public void resolveLocaleShouldReturnRequestLocaleIfUserAnonymous() {
        AnonymousUser user = new AnonymousUser();
        user.setLanguage(Language.RUSSIAN);
        Locale defaultLocale = Language.ENGLISH.getLocale();
        when(userReader.getCurrentUser()).thenReturn(user);
        when(request.getLocale()).thenReturn(defaultLocale);

        Locale result = JcLocaleResolver.getInstance().resolveLocale(request);

        assertEquals(result, defaultLocale);
    }

    /**
     * It is important that we don't access DB if we have enough data from the Request, we had performance issues
     * because of that when user locale was fetched from DB for every label on the page.
     */
    @Test
    public void resolveLocaleShouldNotRetrieveCurrentUserIfRequestLocaleAttributeNotNull() {
        Locale locale = Locale.ENGLISH;
        when(request.getAttribute(CookieLocaleResolver.LOCALE_REQUEST_ATTRIBUTE_NAME)).thenReturn(locale);

        Locale result = JcLocaleResolver.getInstance().resolveLocale(request);

        assertEquals(result, locale);
        verify(userReader, never()).getCurrentUser();
    }
}

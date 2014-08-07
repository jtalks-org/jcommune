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
package org.jtalks.jcommune.web.locale;


import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Custom implementation of {@link org.springframework.web.servlet.LocaleResolver}
 *
 * @author Mikhail Stryzhonok
 */
public class JcLocaleResolver extends CookieLocaleResolver {

    private UserService userService;

    public JcLocaleResolver(UserService userService) {
        this.userService = userService;
    }

    /**
     * Resolves user locale. If user is logged in locale will be read from database. If user is anonymous locale will be
     * resolved form request and cookies using standard {@link CookieLocaleResolver} mechanism
     *
     * @param request HttpServletRequest
     * @return user locale
     */
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale locale = (Locale)request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
        if (locale != null) {
            return locale;
        }

        JCUser currentUser = userService.getCurrentUser();
        if (currentUser.isAnonymous()) {
            locale = super.resolveLocale(request);
        } else {
            locale = currentUser.getLanguage().getLocale();
        }
        request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, locale);
        return locale;
    }
}

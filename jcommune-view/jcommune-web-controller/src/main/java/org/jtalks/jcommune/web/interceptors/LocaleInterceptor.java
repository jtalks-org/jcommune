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

import org.jtalks.jcommune.model.entity.Language;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LocaleInterceptor extends HandlerInterceptorAdapter {

    private LocaleChangeInterceptor springInterceptor;

    public LocaleInterceptor(LocaleChangeInterceptor springInterceptor) {
        this.springInterceptor = springInterceptor;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
        String settedLocale = request.getParameter(this.springInterceptor.getParamName());
        if (settedLocale != null) {
            Boolean isValid = false;
            for (Language lang : Language.values()) {
                if (lang.getLanguageCode().equals(settedLocale)) {
                    isValid = true;
                    break;
                }
            }
            if (!isValid) return true;
        }
        return this.springInterceptor.preHandle(request, response, handler);
    }

}

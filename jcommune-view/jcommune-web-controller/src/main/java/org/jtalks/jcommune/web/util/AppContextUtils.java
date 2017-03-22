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

package org.jtalks.jcommune.web.util;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/**
 * Provides possibility to get bean from ApplicationContext
 *
 * @author Oleg Tkachenko
 */
public class AppContextUtils {
    /**
     * Returns bean from application context.
     *
     * @param servletContext to find the web application context for
     * @param tClass         interface or superclass of the actual class.
     * @param beanName       the name of the bean to retrieve
     * @return an instance of the bean
     * @throws IllegalStateException if the root WebApplicationContext could not be found
     * @throws ClassCastException    if the bean found in context is not assignable to the type of tClass
     */
    public static <T> T getBeanFormApplicationContext(ServletContext servletContext, Class<T> tClass, String beanName) {
        WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        if (beanName == null) return context.getBean(tClass);
        return tClass.cast(context.getBean(beanName));
    }

    public static <T> T getBeanFormApplicationContext(ServletContext servletContext, Class<T> tClass) {
        return getBeanFormApplicationContext(servletContext , tClass, null);
    }
}

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
import org.jtalks.jcommune.model.entity.JCommuneProperty;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Injects different JCommune properties into pages so that they can be 
 * accessed from JSP code.
 * These properties are in a database.
 *
 * @author masyan
 * @author Evgeniy Naumenko
 */
public class PropertiesInterceptor extends HandlerInterceptorAdapter {
    private static final String PARAM_CMP_NAME = "cmpName";
    private static final String PARAM_CMP_DESCRIPTION = "cmpDescription";
    private static final String PARAM_CMP_PREFIX = "cmpTitlePrefix";
    private static final String PARAM_SHOW_DUMMY_LINKS = "sapeShowDummyLinks";
    private static final String PARAM_LOGO_TOOLTIP = "logoTooltip";
    private static final String PARAM_ADMIN_INFO_CHANGE_DATE = "infoChangeDate";
    private static final String PARAM_COPYRIGHT_TEMPLATE = "copyrightTemplate";
    private static final String PARAM_USER_DEFINED_COPYRIGHT = "userDefinedCopyright";
    
    private static final String CURRENT_YEAR_PLACEHOLDER = "{current_year}";
    
    private JCommuneProperty componentNameProperty;
    private JCommuneProperty componentDescriptionProperty;
    private JCommuneProperty sapeShowDummyLinksProperty;
    private JCommuneProperty logoTooltipProperty;
    private JCommuneProperty adminInfoChangeDateProperty;
    private JCommuneProperty allPagesTitlePrefixProperty;
    private JCommuneProperty copyrightProperty;
    
    private final String CURRENT_YEAR = String.valueOf(new LocalDateTime().getYear());

    /**
     * @param componentDescriptionProperty component description property
     * @param componentNameProperty        component name property
     * @param sapeShowDummyLinksProperty   show dummy links for SAPE on not
     * @param logoTooltipProperty          tooltip for forum logo
     * @param allPagesTitlePrefixProperty  property of the prefix that should be added to the beginning of the title
     *                                     of every page
     */
    public PropertiesInterceptor(JCommuneProperty componentNameProperty,
                                 JCommuneProperty componentDescriptionProperty,
                                 JCommuneProperty sapeShowDummyLinksProperty,
                                 JCommuneProperty logoTooltipProperty,
                                 JCommuneProperty adminInfoChangeDateProperty,
                                 JCommuneProperty allPagesTitlePrefixProperty,
                                 JCommuneProperty copyrightProperty) {
        this.componentDescriptionProperty = componentDescriptionProperty;
        this.componentNameProperty = componentNameProperty;
        this.sapeShowDummyLinksProperty = sapeShowDummyLinksProperty;
        this.logoTooltipProperty =  logoTooltipProperty;
        this.adminInfoChangeDateProperty = adminInfoChangeDateProperty;
        this.allPagesTitlePrefixProperty = allPagesTitlePrefixProperty;
        this.copyrightProperty = copyrightProperty;
    }

    /**
     * Set properties of component to request parameters.
     *
     * @param request      current HTTP request
     * @param response     current HTTP response
     * @param handler      chosen handler to execute, for type and/or instance examination
     * @param modelAndView the {@code ModelAndView} that the handler returned
     *                     (can also be {@code null})
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        //do not apply to the redirected requests: it's unnecessary and may cause error pages to work incorrectly
        if (modelAndView != null
                && (modelAndView.getViewName() == null || !modelAndView.getViewName().contains("redirect:"))) {
            modelAndView.addObject(PARAM_CMP_NAME, componentNameProperty.getValueOfComponent());
            modelAndView.addObject(PARAM_CMP_DESCRIPTION, componentDescriptionProperty.getValueOfComponent());
            modelAndView.addObject(PARAM_SHOW_DUMMY_LINKS, sapeShowDummyLinksProperty.booleanValue());
            modelAndView.addObject(PARAM_LOGO_TOOLTIP, logoTooltipProperty.getValue());
            modelAndView.addObject(PARAM_CMP_PREFIX, allPagesTitlePrefixProperty.getValue());
            modelAndView.addObject(PARAM_ADMIN_INFO_CHANGE_DATE, adminInfoChangeDateProperty.getValue());
            modelAndView.addObject(PARAM_COPYRIGHT_TEMPLATE, copyrightProperty.getValue());
            modelAndView.addObject(PARAM_USER_DEFINED_COPYRIGHT, getCopyrightWithYear());
        }
    }

    private String getCopyrightWithYear() {
        return copyrightProperty.getValue().replace(CURRENT_YEAR_PLACEHOLDER, CURRENT_YEAR);
    }
}

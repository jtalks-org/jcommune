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

package org.jtalks.jcommune.model.dao.hibernate;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.jtalks.common.model.dao.hibernate.GenericDao;
import org.jtalks.common.model.entity.Component;
import org.jtalks.jcommune.model.dao.ComponentDao;
import org.jtalks.jcommune.model.entity.ComponentInformation;

import java.util.Date;

/**
 * The implementation of {@link org.jtalks.jcommune.model.dao.ComponentDao} based on Hibernate.
 * The class is responsible for loading {@link org.jtalks.common.model.entity.Component} objects from database,
 * but another CRUD operations aren't available, because only and only administrative panel may perform
 * creating, updating, deleting {@link org.jtalks.common.model.entity.Component} in database.
 *
 * @author masyan
 */
public class ComponentHibernateDao extends GenericDao<Component> implements ComponentDao {

    public static final String LOGO_TOOLTIP_PROPERTY = "jcommune.logo_tooltip";
    public static final String LOGO_PROPERTY = "jcommune.logo";
    public static final String COMPONENT_FAVICON_ICO_PARAM = "jcommune.favicon.ico";
    public static final String COMPONENT_FAVICON_PNG_PARAM = "jcommune.favicon.png";

    private static final String COMPONENT_INFO_CHANGE_DATE_PROPERTY = "jcommune.info_change_date";


    /**
     * @param sessionFactory The SessionFactory.
     */
    public ComponentHibernateDao(SessionFactory sessionFactory) {
        super(sessionFactory, Component.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getComponent() {
        return (Component) session().getNamedQuery("getForumComponent").uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setComponentInformation(ComponentInformation componentInformation) {
        Component forumComponent = getComponent();
        forumComponent.setName(componentInformation.getName());
        forumComponent.setDescription(componentInformation.getDescription());
        forumComponent.setProperty(LOGO_TOOLTIP_PROPERTY, componentInformation.getLogoTooltip());
        if (!StringUtils.isEmpty(componentInformation.getLogo())) {
            forumComponent.setProperty(LOGO_PROPERTY, componentInformation.getLogo());
        }

        if (!StringUtils.isEmpty(componentInformation.getIcon())) {
            forumComponent.setProperty(COMPONENT_FAVICON_PNG_PARAM, componentInformation.getIcon());
        }

        if (!StringUtils.isEmpty(componentInformation.getIconInIcoFormat())) {
            forumComponent.setProperty(COMPONENT_FAVICON_ICO_PARAM, componentInformation.getIconInIcoFormat());
        }

        DateTime now = new DateTime();
        now = now.withMillisOfSecond(0);
        forumComponent.setProperty(COMPONENT_INFO_CHANGE_DATE_PROPERTY, String.valueOf(now.getMillis()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getComponentModificationTime() {
        Date modificationDate = new Date();

        if (getComponent() != null) {
            String dateString = getComponent().getProperty(COMPONENT_INFO_CHANGE_DATE_PROPERTY);

            if (dateString != null) {
                modificationDate.setTime(Long.parseLong(dateString));
            }
        }


        return modificationDate;
    }
}

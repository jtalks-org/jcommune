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

import org.apache.commons.lang.time.DateFormatUtils;
import org.hibernate.SessionFactory;
import org.jtalks.common.model.dao.hibernate.GenericDao;
import org.jtalks.common.model.entity.Component;
import org.jtalks.jcommune.model.dao.ComponentDao;
import org.jtalks.jcommune.model.entity.ComponentInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * The implementation of {@link org.jtalks.jcommune.model.dao.ComponentDao} based on Hibernate.
 * The class is responsible for loading {@link org.jtalks.common.model.entity.Component} objects from database,
 * but another CRUD operations aren't available, because only and only administrative panel may perform
 * creating, updating, deleting {@link org.jtalks.common.model.entity.Component} in database.
 *
 * @author masyan
 */
public class ComponentHibernateDao extends GenericDao<Component> implements ComponentDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentHibernateDao.class);
    private static final String PROPERTY_DATETIME_PATTERN = "E, dd MMM yyyy HH:mm:ss z";

    /**
     * @param sessionFactory The SessionFactory.
     */
    public ComponentHibernateDao(SessionFactory sessionFactory) {
        super(sessionFactory, Component.class);
    }

    /**
     * Parameter name for forum logo tooltip
     */
    public static final String LOGO_TOOLTIP_PROPERTY = "jcommune.logo_tooltip";

    /**
     * Parameter name for forum logo
     */
    public static final String LOGO_PROPERTY = "jcommune.logo";

    private static final String COMPONENT_INFO_CHANGE_DATE_PROPERTY = "jcommune.logo_change_date";

    /**
     * Parameter name for forum fav icon in ico format
     */
    public static final String COMPONENT_FAVICON_ICO_PARAM = "jcommune.favicon.ico";

    /**
     * Parameter name for forum fav icon in png format
     */
    public static final String COMPONENT_FAVICON_PNG_PARAM = "jcommune.favicon.png";

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
        if (componentInformation.getLogo() != null && !componentInformation.getLogo().isEmpty()) {
            forumComponent.setProperty(LOGO_PROPERTY, componentInformation.getLogo());
        }

        if (componentInformation.getIcon() != null && !componentInformation.getIcon().isEmpty()) {
            forumComponent.setProperty(COMPONENT_FAVICON_PNG_PARAM, componentInformation.getIcon());
        }

        if (componentInformation.getIconICO() != null && !componentInformation.getIconICO().isEmpty()) {
            forumComponent.setProperty(COMPONENT_FAVICON_ICO_PARAM, componentInformation.getIconICO());
        }

        String formattedDateLastModified = DateFormatUtils.format(
                Calendar.getInstance(),
                PROPERTY_DATETIME_PATTERN, Locale.US);
        forumComponent.setProperty(COMPONENT_INFO_CHANGE_DATE_PROPERTY, formattedDateLastModified);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getComponentModificationTime() {
        if (getComponent() == null) {
            return new Date();
        }

        String dateString = getComponent().getProperty(COMPONENT_INFO_CHANGE_DATE_PROPERTY);
        Date modificationDate = new Date();

        DateFormat dateFormat = new SimpleDateFormat(
                PROPERTY_DATETIME_PATTERN,
                Locale.US);

        if (dateString != null) {
            try {
                modificationDate = dateFormat.parse(dateString);
            } catch (ParseException e) {
                LOGGER.error("Can't parse last forum information modification date from the property", e);
            }
        }

        return modificationDate;
    }
}

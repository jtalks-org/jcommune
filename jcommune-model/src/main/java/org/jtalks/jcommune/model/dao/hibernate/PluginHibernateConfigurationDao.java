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

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.jtalks.common.model.dao.hibernate.GenericDao;
import org.jtalks.common.service.exceptions.NotFoundException;
import org.jtalks.jcommune.model.dao.PluginConfigurationDao;
import org.jtalks.jcommune.model.entity.PluginConfiguration;
import org.jtalks.jcommune.model.entity.PluginConfigurationProperty;

import java.util.List;

/**
 *
 * @author Anuar_Nurmakanov
 */
public class PluginHibernateConfigurationDao extends GenericDao<PluginConfiguration> implements PluginConfigurationDao {
    /**
     * @param sessionFactory The SessionFactory.
     */
    public PluginHibernateConfigurationDao(SessionFactory sessionFactory) {
        super(sessionFactory, PluginConfiguration.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PluginConfiguration get(String name) throws NotFoundException {
        PluginConfiguration configuration =
                (PluginConfiguration) session().createCriteria(PluginConfiguration.class)
                .add(Restrictions.eq("name", name) )
                .uniqueResult();
        if (configuration == null){
            throw new NotFoundException(name + " plugin not found in a database");
        } else {
            return configuration;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveOrUpdate(PluginConfiguration entity) {
        for (PluginConfigurationProperty property: entity.getProperties()) {
            property.setPluginConfiguration(entity);
        }
        super.saveOrUpdate(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateProperties(List<PluginConfigurationProperty> properties) {
        for (PluginConfigurationProperty property: properties) {
            session().saveOrUpdate(property);
        }
        session().flush();
    }
}

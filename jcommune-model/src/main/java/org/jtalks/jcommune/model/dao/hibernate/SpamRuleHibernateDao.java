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
import org.jtalks.common.model.dao.hibernate.GenericDao;
import org.jtalks.jcommune.model.dao.SpamRuleDao;
import org.jtalks.jcommune.model.entity.SpamRule;

import java.util.List;

/**
 * @author Oleg Tkachenko
 */
@SuppressWarnings("unchecked")
public class SpamRuleHibernateDao extends GenericDao<SpamRule> implements SpamRuleDao {
    public SpamRuleHibernateDao(SessionFactory sessionFactory) {
        super(sessionFactory, SpamRule.class);
    }

    @Override
    public List<SpamRule> getAllRules() {
        return session().createCriteria(SpamRule.class).setCacheable(true).list();
    }

    @Override
    public List<SpamRule> getEnabledRules() {
        return session().getNamedQuery("getEnabledSpamRules").list();
    }
}

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
import org.jtalks.jcommune.model.entity.Poll;

/**
 * The implementation of the {@link PollDao} based on Hibernate. The class is responsible for loading
 * {@link Poll} objects from database and update them. This implementation doesn't contain any
 * additional methods, because methods of {@link GenericDao} cover all needed functionality.
 *
 * @author Anuar Nurmakanov
 * @see GenericDao
 * @see Poll
 */
public class PollHibernateDao extends GenericDao<Poll> {
    /**
     * @param sessionFactory The SessionFactory.
     * @param type           An entity type.
     */
    public PollHibernateDao(SessionFactory sessionFactory, Class<Poll> type) {
        super(sessionFactory, type);
    }
}

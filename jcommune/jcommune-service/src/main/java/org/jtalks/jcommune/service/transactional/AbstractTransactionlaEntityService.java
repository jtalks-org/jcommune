/* 
 * JTalks for uniting people
 * Copyright (C) 2011  JavaTalks Team
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * Also add information on how to contact you by electronic and paper mail.
 * 
 * This file creation date: Apr 12, 2011 / 8:05:19 PM
 * The JTalks Project
 * http://www.jtalks.org
 */
package org.jtalks.jcommune.service.transactional;

import java.util.List;

import org.jtalks.jcommune.model.dao.hibernate.TopicHibernateDao;
import org.jtalks.jcommune.model.dao.hibernate.UserHibernateDao;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.EntityService;
import org.jtalks.jcommune.model.dao.Dao;
import org.jtalks.jcommune.model.entity.Persistent;

/**
 * Generic implementation of all entity based services.
 * Most of the implementations of the methods are basing on straightforward calls of the same named method from DAO interface.
 * @author Osadchuck Eugeny
 *
 */
public abstract class  AbstractTransactionlaEntityService<T extends Persistent> implements EntityService<T> {
	/**
	 * Dao object implementation.
	 */

    protected abstract Dao<T> getDao();

	@Override
	public void saveOrUpdate(T persistent) {
		getDao().saveOrUpdate(persistent);
	}

	@Override
	public void delete(Long id) {
		getDao().delete(id);
	}

	@Override
	public void delete(T persistent) {
		getDao().delete(persistent);
	}

	@Override
	public T get(Long id) {
		return getDao().get(id);
	}

	@Override
	public List<T> getAll() {
		return getDao().getAll();
	}

}

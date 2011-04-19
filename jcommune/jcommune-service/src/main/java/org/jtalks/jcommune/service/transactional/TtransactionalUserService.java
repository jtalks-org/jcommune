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

import org.jtalks.jcommune.model.dao.Dao;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.UserService;

/**
 * @author Snail
 *
 */
public class TtransactionalUserService implements UserService {
	private Dao<User> userDao;

	public TtransactionalUserService(Dao<User> userDao) {
		this.userDao = userDao;
	}

	/* (non-Javadoc)
	 * @see main.java.org.jtalks.jcommune.service.EntityService#saveOrUpdate(org.jtalks.jcommune.model.entity.Persistent)
	 */
	@Override
	public void saveOrUpdate(User persistent) {
		userDao.saveOrUpdate(persistent);
	}

	/* (non-Javadoc)
	 * @see main.java.org.jtalks.jcommune.service.EntityService#delete(java.lang.Long)
	 */
	@Override
	public void delete(Long id) {
		userDao.delete(id);
	}

	/* (non-Javadoc)
	 * @see main.java.org.jtalks.jcommune.service.EntityService#delete(org.jtalks.jcommune.model.entity.Persistent)
	 */
	@Override
	public void delete(User persistent) {
		userDao.delete(persistent);
	}

	/* (non-Javadoc)
	 * @see main.java.org.jtalks.jcommune.service.EntityService#get(java.lang.Long)
	 */
	@Override
	public User get(Long id) {
		return userDao.get(id);
	}

	/* (non-Javadoc)
	 * @see main.java.org.jtalks.jcommune.service.EntityService#getAll()
	 */
	@Override
	public List<User> getAll() {
		return userDao.getAll();
	}

}

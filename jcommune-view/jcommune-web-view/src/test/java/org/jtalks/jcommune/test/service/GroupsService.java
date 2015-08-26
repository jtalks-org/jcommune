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
package org.jtalks.jcommune.test.service;

import org.jtalks.common.model.entity.Group;
import org.jtalks.jcommune.model.dao.GroupDao;
import org.jtalks.jcommune.service.security.AdministrationGroup;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Mikhail Stryzhonok
 */
public class GroupsService {

    @Autowired
    private GroupDao groupDao;

    public void create() {
        groupDao.saveOrUpdate(new Group(AdministrationGroup.ADMIN.getName()));
        groupDao.saveOrUpdate(new Group(AdministrationGroup.BANNED_USER.getName()));
        groupDao.saveOrUpdate(new Group(AdministrationGroup.USER.getName()));
    }
    
}

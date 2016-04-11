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

import org.jtalks.jcommune.model.dao.GroupDao;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;

import java.util.List;

/**
 * @author skythet
 */
public class GroupsManager {
    private JCUser user;
    private GroupDao groupDao;
    private UserDao userDao;

    public GroupsManager(JCUser user, GroupDao groupDao, UserDao userDao) {
        this.user = user;
        this.groupDao = groupDao;
        this.userDao = userDao;
    }

    public GroupsManager withGroups(List<String> groups) {
        for (String groupName : groups) {
            user.addGroup(groupDao.getGroupByName(groupName));
            userDao.saveOrUpdate(user);
            userDao.flush();
        }
        return this;
    }
}

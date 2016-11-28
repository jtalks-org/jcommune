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
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;

import static org.jtalks.jcommune.service.security.AdministrationGroup.*;

/**
 * @author Mikhail Stryzhonok
 */
public class GroupsService {

    @Autowired
    private GroupDao groupDao;

    public void create() {
        groupDao.saveOrUpdate(new Group(ADMIN.getName()));
        groupDao.saveOrUpdate(new Group(BANNED_USER.getName()));
        groupDao.saveOrUpdate(new Group(USER.getName()));
    }

    public List<Long> getIdsByName(List<String> groups) {
        List<Long> result = new ArrayList<>();
        for (String groupName : groups) {
            result.add(getIdByName(groupName));
        }
        return result;
    }

    public Long getIdByName(String groupName) {
        return groupDao.getGroupByName(groupName).getId();
    }

    public Long save(Group group){
        groupDao.saveOrUpdate(group);
        return getIdByName(group.getName());
    }
}

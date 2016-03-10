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
package org.jtalks.jcommune.model.logic;

import org.jtalks.common.model.entity.Group;
import org.jtalks.jcommune.model.dao.GroupDao;
import org.jtalks.jcommune.model.dao.UserDao;

import java.util.List;

/**
 * Class for working with users banning
 *
 * @author stanislav bashkirtsev
 * @author maxim reshetov
 */
public class UserBanner {
    public static final String BANNED_USERS_GROUP_NAME = "Banned Users";
    private final GroupDao groupDao;
    private final UserDao userDao;

    /** Constructor for initialization variables */
    public UserBanner(GroupDao groupDao, UserDao userDao) {
        this.groupDao = groupDao;
        this.userDao = userDao;
    }

    /**
     * Revokes ban from users, deleting them from banned users group.
     *
     * @param usersToRevoke {@link UserList} with users to revoke ban.
     */
    public void revokeBan(UserList usersToRevoke) {
        Group bannedUserGroup = getBannedUsersGroups().get(0);
        bannedUserGroup.getUsers().removeAll(usersToRevoke.getUsers());
        groupDao.saveOrUpdate(bannedUserGroup);
    }


    /**
     * Create group to ban
     *
     * @return {@link Group} of ban
     */
    private Group createBannedUserGroup() {
        Group bannedUsersGroup = new Group(BANNED_USERS_GROUP_NAME, "Banned Users");
        groupDao.saveOrUpdate(bannedUsersGroup);
        return bannedUsersGroup;
    }

    /**
     * Search and return list of banned groups.  If groups wasn't found in database, then creates new one.Note, that
     * creating of this group is a temporal solution until we implement Permission Schemas.
     *
     * @return List of banned groups
     */
    public List<Group> getBannedUsersGroups() {
        List<Group> bannedUserGroups = groupDao.getByName(BANNED_USERS_GROUP_NAME);
        if (bannedUserGroups.isEmpty()) {
            bannedUserGroups.add(createBannedUserGroup());
        }

        return bannedUserGroups;
    }
}

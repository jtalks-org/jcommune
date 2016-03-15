package org.jtalks.jcommune.test.service;

import groovy.transform.CompileStatic;
import org.jtalks.jcommune.model.dao.GroupDao;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;

import java.util.List;

/**
 * @author skythet
 */
@CompileStatic
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

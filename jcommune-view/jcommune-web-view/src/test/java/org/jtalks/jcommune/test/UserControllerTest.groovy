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
package org.jtalks.jcommune.test

import org.jtalks.jcommune.service.security.AdministrationGroup
import org.jtalks.jcommune.test.model.User
import org.jtalks.jcommune.test.service.GroupsService
import org.jtalks.jcommune.test.utils.Users
import org.jtalks.jcommune.test.utils.exceptions.WrongResponseException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

/**
 * @author skythet
 */
@WebAppConfiguration
@ContextConfiguration(locations = 'classpath:/org/jtalks/jcommune/web/view/test-configuration.xml')
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
class UserControllerTest extends Specification {
    @Autowired Users users
    @Autowired GroupsService groupsService

    def setup() {
        groupsService.create()
    }

    def 'test must return all the groups of a user'() {
        given: 'Admin login'
            def session = users.signInAsAdmin()
            def expectedGroups = [AdministrationGroup.BANNED_USER.getName(), AdministrationGroup.USER.getName()]
        and: 'User created with groups BANNED_USER and USER'
            def user = new User()
            users.createdWithoutAccess(user).withGroups(expectedGroups);
        when: 'User fetch groups'
            List<Long> receivedGroups = users.fetchGroups(session, user);
        then: 'Fetched all user groups'
            assertThat(receivedGroups, is(groupsService.getIdsByName(expectedGroups)))
    }

    def 'test without access must return fail result'() {
        given: 'User created without access and login'
            def user = new User()
            users.createdWithoutAccess(user);
            def session = users.signIn(user)
        when: 'User fetch groups without access'
            users.fetchGroups(session, user);
        then:
            thrown(WrongResponseException)
    }

    def 'test without authorization should be redirection'() {
        given: 'User created without access and not login'
            def user = new User()
            users.createdWithoutAccess(user);
        when: 'User fetch groups without login'
            users.fetchGroups(session, user);
        then:
            thrown(MissingPropertyException)
    }

    def 'test add group to user'() {
        given: 'Admin login'
            def session = users.signInAsAdmin()
            def userGroups = [AdministrationGroup.BANNED_USER.getName(), AdministrationGroup.USER.getName()]
            def groupID = groupsService.getIdByName(AdministrationGroup.ADMIN.getName())
        and: 'User created with groups BANNED_USER and USER'
            def user = new User()
            users.createdWithoutAccess(user).withGroups(userGroups);
        when: 'User add to group'
            users.addUserToGroup(session, user, groupID);
        then: 'User added to group'
            users.assertUserInGroup(user, groupID)
    }

    def 'test add group to user without access'() {
        given: 'User created without access and login'
            def user = new User()
            users.createdWithoutAccess(user);
            def session = users.signIn(user)
            def groupID = groupsService.getIdByName(AdministrationGroup.ADMIN.getName())
        when: 'Add group to user'
            users.addUserToGroup(session, user, groupID);
        then:
            thrown(WrongResponseException)
    }

    def 'test add group to user without authorization'() {
        given: 'User created without access and not login'
            def user = new User()
            users.createdWithoutAccess(user);
            def groupID = groupsService.getIdByName(AdministrationGroup.ADMIN.getName())
        when: 'Add group to user'
            users.addUserToGroup(session, user, groupID);
        then:
            thrown(MissingPropertyException)
    }

    def 'test delete user from group'() {
        given: 'Admin login'
            def session = users.signInAsAdmin()
            def userGroups = [AdministrationGroup.BANNED_USER.getName(), AdministrationGroup.USER.getName()]
            def groupID = groupsService.getIdByName(AdministrationGroup.BANNED_USER.getName())
        and: 'User created with groups BANNED_USER and USER'
            def user = new User()
            users.createdWithoutAccess(user).withGroups(userGroups);
        when: 'Delete group from user'
            users.deleteUserFromGroup(session, user, groupID);
        then:
            users.assertUserNotMemerOfGroup(user, groupID)
    }

    def 'test delete group from user without access'() {
        given: 'User created without access and login'
            def user = new User()
            users.createdWithoutAccess(user);
            def session = users.signIn(user)
            def groupID = groupsService.getIdByName(AdministrationGroup.ADMIN.getName())
        when: 'Delete group from user'
            users.deleteUserFromGroup(session, user, groupID);
        then:
            thrown(WrongResponseException)
    }

    def 'test delete group from user without authorization'() {
        given: 'User created without access and not login'
            def user = new User()
            users.createdWithoutAccess(user);
            def groupID = groupsService.getIdByName(AdministrationGroup.ADMIN.getName())
        when: 'Delete group from user'
            users.deleteUserFromGroup(session, user, groupID);
        then:
            thrown(MissingPropertyException)
    }
}

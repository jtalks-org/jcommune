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

import org.jtalks.common.model.entity.Component
import org.jtalks.common.model.entity.Group
import org.jtalks.common.model.permissions.GeneralPermission
import org.jtalks.jcommune.test.model.User
import org.jtalks.jcommune.test.service.ComponentService
import org.jtalks.jcommune.test.service.GroupsService
import org.jtalks.jcommune.test.utils.Groups
import org.jtalks.jcommune.test.utils.Users
import org.jtalks.jcommune.test.utils.exceptions.ValidationException
import org.jtalks.jcommune.test.utils.exceptions.WrongResponseException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification
import static io.qala.datagen.RandomShortApi.alphanumeric;

/**
 * @author Oleg Tkachenko
 */
@WebAppConfiguration
@ContextConfiguration(locations = 'classpath:/org/jtalks/jcommune/web/view/test-configuration.xml')
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
class AdministrationControllerTest extends Specification {
    @Autowired Users users
    @Autowired Groups groups
    @Autowired GroupsService groupsService
    @Autowired ComponentService componentService
    private Component forum

    def setup() {
        groupsService.create()
        forum = componentService.createForumComponent()
    }

    def 'must not be able to see group administration page if you are not admin'() {
        given: 'User created without access and login'
            def user = new User()
            users.created(user);
            def session = users.signIn(user);
        when: 'get all groups with count of users'
            groups.getGroupsWithCountOfUsers(session);
        then:
            thrown(WrongResponseException)
    }

    def 'must not be able to see group administration page for not authenticated users'() {
        given: 'User created and not login'
            def user = new User()
            users.created(user);
        when: 'get all groups with count of users'
            groups.getGroupsWithCountOfUsers(session);
        then:
            thrown(MissingPropertyException)
    }

    def 'must not be able to create group if you are not admin'() {
        given: 'User created without access and login'
            def user = new User()
            users.created(user);
            def session = users.signIn(user);
        when: 'User creates group'
            Group group = Groups.random()
            groups.create(group,session)
        then:
            thrown(WrongResponseException)
    }

    def 'test create group success'() {
        given: 'User created and have admin permission on forum'
            def user = new User()
            users.created(user).withPermissionOn(forum, GeneralPermission.ADMIN)
            def session = users.signIn(user)
        when: 'User creates group'
            Group group = Groups.random()
            groups.create(group,session)
        then: 'Group is created'
            groups.isExist(group.name)
    }
    def 'create group with invalid name or description should fail'() {
        given: 'User created and have admin permission on forum'
            def user = new User()
            users.created(user).withPermissionOn(forum, GeneralPermission.ADMIN)
            def session = users.signIn(user)
        when: 'User creates group'
            Group group = new Group()
            group.name = groupName
            group.description = groupDescription
            groups.create(group, session)
        then: 'Validation error occurs'
            def e = thrown(ValidationException)
            [errorMessage] == e.defaultErrorMessages
        and: 'Group is not created'
            groups.assertDoesNotExist(group)
        where:
            groupName           | groupDescription    |errorMessage                                                    | caseName
            ''                  | alphanumeric(0,255) |"Group name length must be between 1 and 100 characters"        | 'Group name is empty'
            '     '             | alphanumeric(0,255) |"Group name length must be between 1 and 100 characters"        | 'Group name contains only spaces'
            alphanumeric(101)   | alphanumeric(0,255) |"Group name length must be between 1 and 100 characters"        | 'Group name too long'
            alphanumeric(1,100) | alphanumeric(256)   |"Group description length must be between 0 and 255 characters" | 'Group description too long'
    }
}

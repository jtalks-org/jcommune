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

import org.jtalks.jcommune.test.model.User
import org.jtalks.jcommune.test.service.GroupsService
import org.jtalks.jcommune.test.utils.Groups
import org.jtalks.jcommune.test.utils.Users
import org.jtalks.jcommune.test.utils.exceptions.WrongResponseException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

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

    def setup() {
        groupsService.create()
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
}

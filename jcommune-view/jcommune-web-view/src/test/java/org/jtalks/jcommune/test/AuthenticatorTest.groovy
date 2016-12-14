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

import org.jtalks.common.model.entity.Group
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException
import org.jtalks.jcommune.service.Authenticator
import org.jtalks.jcommune.service.exceptions.UserTriesActivatingAccountAgainException
import org.jtalks.jcommune.service.security.AdministrationGroup
import org.jtalks.jcommune.test.service.GroupsService
import org.jtalks.jcommune.test.utils.Users
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import static io.qala.datagen.RandomShortApi.alphanumeric

/**
 * @author Oleg Tkachenko
 */
@WebAppConfiguration
@ContextConfiguration(locations = 'classpath:/org/jtalks/jcommune/web/view/test-configuration.xml')
@TransactionConfiguration(transactionManager = 'transactionManager', defaultRollback = true)
@Transactional
class AuthenticatorTest extends Specification {
    @Autowired Users users
    @Autowired GroupsService groupsService
    @Autowired Authenticator authenticator

    def setup() {
        groupsService.save(new Group(AdministrationGroup.USER.name))
    }

    def 'test success account activation'(){
        given: 'user created but not activated'
            def user = users.randomNotActivatedUser()
        when: 'user tries to activate account'
            authenticator.activateAccount(user.uuid)
        then: 'account successfully activated'
            users.isActivated(user.username)
    }

    def 'user should be a member of "Registered Users" group after activation'(){
        given: 'user created but not activated'
            def user = users.randomNotActivatedUser()
        when: 'user tries to activate account'
            authenticator.activateAccount(user.uuid)
        then: 'user is a member of "Registered Users" group'
            def groupId = groupsService.getIdByName(AdministrationGroup.USER.name)
            users.assertUserInGroup(user, groupId)
    }

    def 'account activation should fail if account does not exists'  (){
        when: 'user tries to activate not existing account'
            authenticator.activateAccount(alphanumeric(16))
        then: 'account successfully activated'
            thrown(NotFoundException)
    }

    def 'account activation should fail if account already activated'(){
        given: 'user created and activated'
            def user = users.randomNotActivatedUser()
            authenticator.activateAccount(user.uuid)
        when: 'user tries to activate account again'
            authenticator.activateAccount(user.uuid)
        then: 'account successfully activated'
            thrown(UserTriesActivatingAccountAgainException)
    }
}

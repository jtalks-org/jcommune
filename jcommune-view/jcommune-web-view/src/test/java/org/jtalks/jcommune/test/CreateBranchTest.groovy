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
import org.jtalks.common.model.permissions.GeneralPermission
import org.jtalks.jcommune.model.entity.Branch
import org.jtalks.jcommune.test.model.User
import org.jtalks.jcommune.test.service.ComponentService
import org.jtalks.jcommune.test.service.GroupsService
import org.jtalks.jcommune.test.utils.Branches
import org.jtalks.jcommune.test.utils.Users
import org.jtalks.jcommune.test.utils.exceptions.ProcessingException
import org.jtalks.jcommune.test.utils.exceptions.ValidationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic

/**
 * @author Mikhail Stryzhonak
 */
@WebAppConfiguration
@ContextConfiguration(locations = 'classpath:/org/jtalks/jcommune/web/view/test-configuration.xml')
@TransactionConfiguration(transactionManager = 'transactionManager', defaultRollback = true)
@Transactional
class CreateBranchTest extends Specification {
    @Autowired Branches branches
    @Autowired ComponentService componentService
    @Autowired Users users
    @Autowired GroupsService groupsService

    private Component forum

    def setup() {
        groupsService.create()
        forum = componentService.createForumComponent()
    }

    def 'test create branch success'() {
        given: 'User created and have admin permission on forum'
          def user = new User()
          users.created(user).withPermissionOn(forum, GeneralPermission.ADMIN)
          def session = users.signIn(user)
        when: 'User creates branch'
          def branch = Branches.random()
          branches.create(branch, session)
        then: 'Branch is created'
          branches.assertExists(branch)
    }

    def 'create branch with invalid name should fail'() {
        given: 'User created and have admin permission on forum'
          def user = new User()
          users.created(user).withPermissionOn(forum, GeneralPermission.ADMIN)
          def session = users.signIn(user)
        when: 'User creates branch'
          Branch branch = Branches.random()
          branch.name = branchName
          branches.create(branch, session)
        then: 'Validation error occurs'
          def e = thrown(ValidationException)
          [errorMessage] == e.defaultErrorMessages
        and: 'Branch is not created'
          branches.assertDoesNotExist(branch)
        where:
          branchName | errorMessage                     | caseName
          ''         | "Branch name shouldn't be empty" | 'Branch name is empty'
    }

    def 'create branch with invalid description must fail'() {
        given: 'User created and have admin permission on forum'
          def user = new User()
          users.created(user).withPermissionOn(forum, GeneralPermission.ADMIN)
          def session = users.signIn(user)
        when: 'User creates branch'
          def branch = Branches.random()
          branch.description = description
          branches.create(branch, session)
        then: 'Validation error occurs'
          def e = thrown(ValidationException)
          [errorMessage] == e.defaultErrorMessages
        and: 'Branch is not created'
          branches.assertDoesNotExist(branch)
        where:
          description           | errorMessage                                             | caseName
          randomAlphabetic(256) | 'Branch description length should be 255 characters max' | 'Branch description length greater than 255 characters'
    }

    def 'create branch should fail if user has no permissions'() {
        given: 'User created, logged in and has no admin permission on forum'
          def session = users.signUpAndSignIn(new User())
        when: 'User creates branch'
          def branch = Branches.random()
          branches.create(branch, session)
        then: 'Processing exception occurs'
          def e = thrown(ProcessingException)
          e.defaultMessage == 'Access denied!'
        and: 'Branch is not created'
          branches.assertDoesNotExist(branch)
    }
}

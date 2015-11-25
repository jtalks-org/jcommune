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
import org.jtalks.jcommune.test.model.Branch
import org.jtalks.jcommune.test.model.User
import org.jtalks.jcommune.test.service.BranchService
import org.jtalks.jcommune.test.service.ComponentService
import org.jtalks.jcommune.test.service.GroupsService
import org.jtalks.jcommune.test.service.UserService
import org.jtalks.jcommune.test.utils.Branches
import org.jtalks.jcommune.test.utils.Users
import org.jtalks.jcommune.test.utils.exceptions.ProcessingException
import org.jtalks.jcommune.test.utils.exceptions.ValidationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification


import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic

/**
 * @author Mikhail Stryzhonak
 */
@WebAppConfiguration
@ContextConfiguration(locations = [
        'classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml',
        'classpath:/org/jtalks/jcommune/model/entity/applicationContext-properties.xml',
        'classpath:/org/jtalks/jcommune/service/applicationContext-service.xml',
        'classpath:/org/jtalks/jcommune/service/security-service-context.xml',
        'classpath:/org/jtalks/jcommune/service/email-context.xml',
        'classpath:/org/jtalks/jcommune/web/applicationContext-controller.xml',
        'classpath:security-context.xml',
        'classpath:spring-dispatcher-servlet.xml',
        'classpath:/org/jtalks/jcommune/web/view/test-configuration.xml'
])
@TransactionConfiguration(transactionManager = 'transactionManager', defaultRollback = true)
@Transactional
class CreateBranchTest extends Specification {
    @Autowired Branches branches;
    @Autowired ComponentService componentService
    @Autowired UserService userService
    @Autowired Users users
    @Autowired BranchService branchService
    @Autowired GroupsService groupsService
    @Autowired MockMvc mockMvc

    private Component forum

    def setup() {
        groupsService.create()
        forum = componentService.createForumComponent()
    }

    def 'test create branch success'() {
        given: 'User created and have admin permission on forum'
          def user = new User()
          userService.create(user).withPermissionOn(forum, GeneralPermission.ADMIN)
        and: 'User signed in'
          def session = users.signIn(user)
        when: 'User creates branch'
          def branch = new Branch(name: branchName, description: branchDescription)
          branches.create(branch, session)
        then: 'Branch is created'
          branchService.isExist(branch.name)
        where:
          branchName           | branchDescription     | caseName
          randomAlphabetic(25) | randomAlphabetic(100) | 'Branch name between 1 and 80 characters, branch description between 1 and 255 characters'
    }

    def 'create branch with invalid name should fail'() {
        given: 'User created and have admin permission on forum'
          def user = new User()
          userService.create(user).withPermissionOn(forum, GeneralPermission.ADMIN)
        and: 'User signed in'
          def session = users.signIn(user)
        when: 'User creates branch'
          def branch = new Branch(name: branchName)
          branches.create(branch, session)
        then: 'Validtion error occurs'
          def e = thrown(ValidationException)
          [errorMessage] == e.defaultErrorMessages
        and: 'Branch is not created'
          !branchService.isExist(branch.name)
        where:
          branchName | errorMessage                      | caseName
          ''         | 'Branch name shouldn\'t be empty' | 'Branch name is empty'
    }

    def 'create branch with invalid description should fail'() {
        given: 'User created and have admin permission on forum'
          def user = new User()
          userService.create(user).withPermissionOn(forum, GeneralPermission.ADMIN)
        and: 'User signed in'
          def session = users.signIn(user)
        when: 'User creates branch'
          def branch = new Branch(description: description)
          branches.create(branch, session)
        then: 'Validation error occurs'
          def e = thrown(ValidationException)
          [errorMessage] == e.defaultErrorMessages
        and: 'Branch is not created'
          !branchService.isExist(branch.name)
        where:
          description           | errorMessage                                             | caseName
          randomAlphabetic(256) | 'Branch description length should be 255 characters max' | 'Branch description length greather than 255 characters'
    }

    def 'create branch should fail if user have no permissions'() {
        given: 'User created, logged in and has no admin permission on forum'
          def session = users.signUpAndSignIn(new User())
        when: 'User creates branch'
          def branch = new Branch()
          branches.create(branch, session)
        then: 'Processing exception occurs'
          def e = thrown(ProcessingException)
          e.defaultMessage == 'Access denied!'
        and: 'Branch is not created'
          !branchService.isExist(branch.name)
    }
}

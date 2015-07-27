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

import org.jtalks.jcommune.model.utils.Groups
import org.jtalks.jcommune.test.utils.Users
import org.jtalks.jcommune.test.utils.exceptions.WrongResponseException
import org.jtalks.jcommune.test.utils.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import javax.annotation.Resource
import javax.servlet.Filter

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic

/**
 * @author Mikhail Stryzhonok
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
abstract class SignInTest extends Specification {

    @Autowired
    private WebApplicationContext ctx

    protected Users users
    @Autowired
    private Groups groups

    @Resource(name = 'testFilters')
    List<Filter> filters

    def setup() {
        users.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(filters.toArray(new Filter[filters.size()])).build()
        groups.create()
    }

    def 'Sign in without activation registration should fail'() {
        given: 'User is registered and not activated'
            def user = new User()
            users.singUp(user)
        when: 'User tries to log in'
            users.signIn(user)
        then: 'Wrong response exception is thrown'
            thrown(WrongResponseException)
    }

    def 'Sign in success scenarios'() {
        given: 'User with username and password registered and activated'
            def user = new User(username: username, password: password)
            users.create(user)
        when: caseName
            def session = users.signIn(user)
        then: 'User becomes logged in'
            users.isAuthenticated(session, user)
        where:
        username            |password               |caseName
        randomAlphabetic(25)| randomAlphabetic(50)  |'Username and password valid'
    }

    def 'Sign in fail scenarios'() {
        given: 'User with username and password registered and activated'
            def user = new User(username: usernameForCreation, password: passwordForCreation)
            users.create(user)
        when: caseName
            user.username = usernameForSignIn
            user.password = passwordForSignIn
            users.signIn(user)
        then: 'Wrong response exception is thrown'
            thrown(WrongResponseException)
        where:
        usernameForCreation    |passwordForCreation |usernameForSignIn  |passwordForSignIn    |caseName
        randomAlphabetic(25)   |"sample_password"   |""                 |"sample_password"    |'User tries log in with empty username'
    }


    void setUsers(Users users) {
        this.users = users
    }
}

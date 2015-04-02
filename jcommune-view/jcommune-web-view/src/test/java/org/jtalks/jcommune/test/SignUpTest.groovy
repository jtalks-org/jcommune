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
import org.jtalks.jcommune.test.utils.exceptions.ValidationException
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
import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric

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
abstract class SignUpTest extends Specification {

    @Autowired
    private WebApplicationContext ctx;

    protected Users users;
    @Autowired
    private Groups groups;

    @Resource(name = 'testFilters')
    List<Filter> filters;

    def honeypotErrorResponse;

    abstract void initNonDefaultFailParameters();
    abstract boolean isNonDefaultFailParametersEquals(Object expected, WrongResponseException exception);

    def setup() {
        initNonDefaultFailParameters()
        users.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(filters.toArray(new Filter[filters.size()])).build()
        groups.create();
    }

    def 'test sign up success'() {
        when: 'User send registration request'
            def userName = users.singUp(new User())
        then: 'User created in database'
            users.isExist(userName)
    }

    def 'test sign up and activation'() {
        when: 'User send registration request and goes to activation link'
            def username = users.signUpAndActivate(new User())
        then: 'User created in database'
            users.isExist(username)
        and: 'User activated'
            users.isActivated(username)
    }

    def 'registration should fail if honeypot captcha are filled'() {
        when: 'Bot send registration request'
            def user = new User(honeypot: 'any text')
            users.singUp(user)
        then: 'Wrong response came'
            def e = thrown(WrongResponseException)
            isNonDefaultFailParametersEquals(honeypotErrorResponse, e)
        and: 'User not created in database'
            users.isNotExist(user.username)
    }

    def 'registration should fail if all fields are empty'() {
        when: 'User send registration request'
            def username = ''
            users.singUp(new User(username: username, email: '', password: '', confirmation: ''))
        then: 'Validation error occurs'
            def e = thrown(ValidationException)
            e.getDefaultErrorMessages().containsAll(['Username length must be between 1 and 25 characters',
                                                     'Must not be empty',
                                                     'Password length must be between 1 and 50 characters'])
        and: 'User not created in database'
            users.isNotExist(username)
    }

    def 'registration with invalid username should fail'() {
        when: 'User send registration request with invalid username'
            users.singUp(new User(username: username))
        then: 'validation error occurs'
            def e = thrown(ValidationException)
            [errorMessage].equals(e.defaultErrorMessages)
        and: 'User not created in database'
            users.isNotExist(username)
        where:
            username               |errorMessage                                            |caseName
            '   '                  |'Username length must be between 1 and 25 characters'   |'Username as spaces'
            randomAlphabetic(26)   |'Username length must be between 1 and 25 characters'   |'Username too long'
            ''                     |'Username length must be between 1 and 25 characters'   |'Username is empty'
    }

    def 'registration user with valid username should pass'() {
        when: 'User send registration request with valid username'
            def name = users.singUp(new User(username: username))
        then: 'User created in database'
            users.isExist(name)
        where:
            username                                                    |caseName
            randomAlphanumeric(14)                                      |'Length of username between 1 and 25'
            randomAlphanumeric(8) + ' ' + randomAlphanumeric(8)         |'Username contains spaces'
            randomAlphanumeric(25)                                      |'Username has max allowed length'
            '/' + randomAlphanumeric(8)                                 |'Username contains slash'
            '\\' + randomAlphanumeric(8)                                |'Username contains back slash'
            randomAlphanumeric(5) + '      ' + randomAlphanumeric(5)    |'Username contains several spaces in the middle'
    }

    def 'registration with invalid email should fail'() {
        when: 'User send registration request with invalid email'
            def user = new User(email: email)
            users.singUp(user)
        then: 'Validation exception occurs'
            def e = thrown(ValidationException)
            [errorMessage].equals(e.defaultErrorMessages)
        and: 'User not created in database'
            users.isNotExist(user.username)
        where:
            email                                   |errorMessage                                   |caseName
            randomAlphanumeric(8) + '@' + 'jtalks'  |'An email format should be like mail@mail.ru'  |'Invalid email format'
            ''                                      |'Must not be empty'                            |'Email is empty'
    }

    def 'registration with valid password and confirmation should pass'() {
        when: 'User send registration request with valid password and confirmation'
            def username = users.singUp(new User(password: password, confirmation: password))
        then: 'User created in database'
            users.isExist(username)
        where:
            password                |caseName
            randomAlphanumeric(49)  |'Valid password'
            ' '                     |'Space as password'
    }

    def 'registration with invalid password should fail'() {
        when: 'User send registration request with invalid password'
            def user = new User(password: password, confirmation: password)
            users.singUp(user)
        then: 'Validation exception occurs'
            def e = thrown(ValidationException)
            [errorMessage].equals(e.defaultErrorMessages)
        and: 'User not created in database'
            users.isNotExist(user.username)
        where:
            password                |errorMessage                                           |caseName
            ''                      |'Password length must be between 1 and 50 characters'  |'Password is empty'
            randomAlphanumeric(51)  |'Password length must be between 1 and 50 characters'  |'Too long password'

    }

    def 'registration with different password and confirmation should fail'() {
        when: 'User send registration request with different password and confirmation'
            def user = new User(password: password, confirmation: confirmation)
            users.singUp(user)
        then: 'Validation exception occurs'
            def e = thrown(ValidationException)
            [errorMessage].equals(e.defaultErrorMessages)
        and: 'User not created in database'
            users.isNotExist(user.username)
        where:
            password               |confirmation    |errorMessage                                       | caseName
            randomAlphanumeric(10) |''              |'Password and confirmation password do not match'  | 'Confirmation is empty'
            'password'             |'PASSWORD'      |'Password and confirmation password do not match'  | 'Confirmation in wrong letter case'
            'password'             |' password'     |'Password and confirmation password do not match'  | 'Space at the begin of confirmation'
            'password'             |'password '     |'Password and confirmation password do not match'  | 'Space at the end of confirmation'
    }

    def 'registration with not unique usename should fail'() {
        given: 'User registered'
            def username = 'amazzzing'
            users.singUp(new User(username: username))
        when: 'Other user tries to signUp with same username'
            users.singUp(new User(username: username))
        then: 'Username field marked with error'
            def e = thrown(ValidationException)
            ['User with the username already exists.'].equals(e.defaultErrorMessages)
    }

    def 'registration with not unique email should fail'() {
        given: 'User registered'
            def email = 'mail@example.com'
            users.singUp(new User(email: email))
        when: 'Other user tries to signUp with same email and different username'
            users.singUp(new User(email: email))
        then: 'Email field marked with error'
            def e = thrown(ValidationException)
            ['User with the email already exists.'].equals(e.defaultErrorMessages)
    }

    void setUsers(Users users) {
        this.users = users
    }
}

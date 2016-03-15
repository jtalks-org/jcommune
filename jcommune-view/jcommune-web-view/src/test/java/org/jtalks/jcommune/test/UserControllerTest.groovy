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
import org.jtalks.jcommune.service.security.AdministrationGroup
import org.jtalks.jcommune.test.model.User
import org.jtalks.jcommune.test.service.ComponentService
import org.jtalks.jcommune.test.service.GroupsService
import org.jtalks.jcommune.test.utils.Users
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import static org.hamcrest.Matchers.hasItems
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/**
 * @author skythet
 */
@WebAppConfiguration
@ContextConfiguration(locations = 'classpath:/org/jtalks/jcommune/web/view/test-configuration.xml')
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
class UserControllerTest extends Specification {
    @Autowired ComponentService componentService
    @Autowired Users users
    @Autowired GroupsService groupsService

    @Autowired MockMvc mockMvc

    private Component forum
    private User adminUser;

    def setup() {
        groupsService.create()
        forum = componentService.createForumComponent()

        adminUser = new User()
        users.created(adminUser).withPermissionOn(forum, GeneralPermission.ADMIN)
    }

    def 'test show user groups'() {
        given: 'Admin login'
            def session = users.signIn(adminUser)
            def expectedGroups = [AdministrationGroup.BANNED_USER.getName(), AdministrationGroup.USER.getName()]
        and: 'User created with groups BANNED_USER and USER'
            def user = new User()
            users.createdWithoutAccess(user).withGroups(expectedGroups);
        when: 'User fetch groups'
            def result = mockMvc.perform(get('/user/' + users.userIdByUsername(user.username) + '/groups')
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
            )
        then: 'Fetched groups list'
            result.andExpect(status().isOk())
                .andExpect(jsonPath('$.status', is('SUCCESS')))
                .andExpect(jsonPath('$.result', hasItems(groupsService.getIDsByName(expectedGroups) as int[])))
    }

    def 'try fetch user groups without access'() {
        given: 'User created without access and login'
            def user = new User()
            users.createdWithoutAccess(user);
            def session = users.signIn(user)
        when: 'User fetch groups without access'
            def result = mockMvc.perform(get('/user/' + users.userIdByUsername(user.username) + '/groups')
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
            )
        then:
            result.andExpect(status().isOk())
                .andExpect(jsonPath('$.status', is('FAIL')))
    }

    def 'try fetch user groups without authorization'() {
        given: 'User created without access and not login'
            def user = new User()
            users.createdWithoutAccess(user);
        when: 'User fetch groups without login'
            def result = mockMvc.perform(get('/user/' + users.userIdByUsername(user.username) + '/groups'))
        then:
            result.andExpect(status().isMovedTemporarily()).andExpect(redirectedUrl("http://localhost/login"))
    }

    def 'test add group to user'() {
        given: 'Admin login'
            def session = users.signIn(adminUser)
            def userGroups = [AdministrationGroup.BANNED_USER.getName(), AdministrationGroup.USER.getName()]
            def groupIDForAdd = groupsService.getIdByName(AdministrationGroup.ADMIN.getName())
        and: 'User created with groups BANNED_USER and USER'
            def user = new User()
            users.createdWithoutAccess(user).withGroups(userGroups);
        when: 'User add to group'
            def result = mockMvc.perform(post('/user/' + users.userIdByUsername(user.username) + '/groups/' + groupIDForAdd)
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
            )
        then:
            result.andExpect(status().isOk())
                .andExpect(jsonPath('$.status', is('SUCCESS')))
    }

    def 'try add group to user without access'() {
        given: 'User created without access and login'
            def user = new User()
            users.createdWithoutAccess(user);
            def session = users.signIn(user)
            def groupIDForAdd = groupsService.getIdByName(AdministrationGroup.ADMIN.getName())
        when: 'Add group to user'
            def result = mockMvc.perform(post('/user/' + users.userIdByUsername(user.username) + '/groups/' + groupIDForAdd)
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
            )
        then:
            result.andExpect(status().isOk())
                .andExpect(jsonPath('$.status', is('FAIL')))
    }

    def 'try add group to user without authorization'() {
        given: 'User created without access and not login'
            def user = new User()
            users.createdWithoutAccess(user);
            def groupIDForAdd = groupsService.getIdByName(AdministrationGroup.ADMIN.getName())
        when: 'Add group to user'
            def result = mockMvc.perform(post('/user/' + users.userIdByUsername(user.username) + '/groups/' + groupIDForAdd))
        then:
            result.andExpect(status().isMovedTemporarily()).andExpect(redirectedUrl("http://localhost/login"))
    }

    def 'test delete user from group'() {
        given: 'Admin login'
            def session = users.signIn(adminUser)
            def userGroups = [AdministrationGroup.BANNED_USER.getName(), AdministrationGroup.USER.getName()]
            def groupIDForDelete = groupsService.getIdByName(AdministrationGroup.BANNED_USER.getName())
        and: 'User created with groups BANNED_USER and USER'
            def user = new User()
            users.createdWithoutAccess(user).withGroups(userGroups);
        when: 'Delete group from user'
            def result = mockMvc.perform(delete('/user/' + users.userIdByUsername(user.username) + '/groups/' + groupIDForDelete)
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
        )
        then:
            result.andExpect(status().isOk())
                .andExpect(jsonPath('$.status', is('SUCCESS')))
    }

    def 'try delete group from user without access'() {
        given: 'User created without access and login'
            def user = new User()
            users.createdWithoutAccess(user);
            def session = users.signIn(user)
            def groupIDForDelete = groupsService.getIdByName(AdministrationGroup.ADMIN.getName())
        when: 'Delete group from user'
            def result = mockMvc.perform(post('/user/' + users.userIdByUsername(user.username) + '/groups/' + groupIDForDelete)
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
        )
        then:
            result.andExpect(status().isOk())
                .andExpect(jsonPath('$.status', is('FAIL')))
    }

    def 'try delete group from user without authorization'() {
        given: 'User created without access and not login'
            def user = new User()
            users.createdWithoutAccess(user);
            def groupIDForDelete = groupsService.getIdByName(AdministrationGroup.ADMIN.getName())
        when: 'Delete group from user'
            def result = mockMvc.perform(post('/user/' + users.userIdByUsername(user.username) + '/groups/' + groupIDForDelete))
        then:
            result.andExpect(status().isMovedTemporarily()).andExpect(redirectedUrl("http://localhost/login"))
    }
}

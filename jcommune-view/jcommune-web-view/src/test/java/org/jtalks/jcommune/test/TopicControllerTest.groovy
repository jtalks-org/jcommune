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

import groovy.json.JsonOutput
import org.jtalks.common.model.permissions.BranchPermission
import org.jtalks.jcommune.model.entity.TopicTypeName
import org.jtalks.jcommune.test.service.BranchService
import org.jtalks.jcommune.test.service.GroupsService
import org.jtalks.jcommune.test.service.UserService
import org.jtalks.jcommune.test.utils.Users
import org.jtalks.jcommune.test.model.User
import org.springframework.beans.factory.annotation.Autowire
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import javax.annotation.Resource
import javax.servlet.Filter

import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/**
 * @author Mikhail Stryzhonok
 */
@WebAppConfiguration
@ContextConfiguration(locations = [
    "classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml",
    "classpath:/org/jtalks/jcommune/model/entity/applicationContext-properties.xml",
    "classpath:/org/jtalks/jcommune/service/applicationContext-service.xml",
    "classpath:/org/jtalks/jcommune/service/security-service-context.xml",
    "classpath:/org/jtalks/jcommune/service/email-context.xml",
    "classpath:/org/jtalks/jcommune/web/applicationContext-controller.xml",
    "classpath:security-context.xml",
    "classpath:spring-dispatcher-servlet.xml",
    "classpath:/org/jtalks/jcommune/web/view/test-configuration.xml"
])
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
class TopicControllerTest extends Specification {

    @Autowired
    private WebApplicationContext ctx;
    @Autowired
    private BranchService branches;
    @Autowired Users users;
    @Autowired
    private GroupsService groups;
    @Autowired
    private UserService userService

    private MockMvc mockMvc;

    @Resource(name = "testFilters")
    List<Filter> filters;

    def setup() {
        groups.create()
    }

    def 'test create topic'() {
        given: 'User with username and password'
            def user = new User(username: "name", password: "pwd")
        and: 'Branch created'
            def branch = branches.create()
        and: "User registered and has permissions to create topics in current branch"
            userService.create(user).withPermissionOn(branch, BranchPermission.VIEW_TOPICS)
                    .withPermissionOn(branch, BranchPermission.CREATE_POSTS);
        and: "User logged in"
            def session = users.signIn(user)
        when: 'User creates topic'
            def result = mockMvc.perform(post("/topics/new").session(session as MockHttpSession)
                    .param("bodyText", "text")
                    .param("topic.title", "title")
                    .param("branchId", branch.id.toString()))
        then: 'User redirected to newly created topic'
            result.andExpect(status().isMovedTemporarily()).andExpect(redirectedUrl("/topics/1"))
    }

    def 'Creation of draft topic should pass'() {
        given: 'User with username and password'
            def user = new User(username: "name", password: "pwd")
        and: 'Branch created'
            def branch = branches.create()
        and: "User registered and has permissions to create topics in current branch"
            users.create(user).withPermissionOn(branch, BranchPermission.CREATE_POSTS);
        and: "User logged in"
            def session = users.signIn(user)
        when: 'User created draft topic'
            def result = mockMvc.perform(post("/topics/draft")
                    .session(session as MockHttpSession)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonOutput.toJson([
                        content: 'content',
                        branchId: branch.getId(),
                        topicType: TopicTypeName.DISCUSSION.name
                    ])))
        then:
            result.andExpect(status().isOk())
                  .andExpect(jsonPath('$.status', is('SUCCESS')))
    }

    def 'Creation of code review draft topic by user that has permission to create only discussion topics should fail'() {
        given: 'User with username and password'
            def user = new User(username: "name", password: "pwd")
        and: 'Branch created'
            def branch = branches.create()
        and: "User registered and but has no permissions to create topics in current branch"
            users.create(user)
            users.create(user).withPermissionOn(branch, BranchPermission.CREATE_POSTS);
        and: "User logged in"
            def session = users.signIn(user)
        when: 'User created draft topic'
            def result = mockMvc.perform(post("/topics/draft")
                    .session(session as MockHttpSession)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonOutput.toJson([
                        content: 'content',
                        branchId: branch.getId(),
                        topicType: TopicTypeName.CODE_REVIEW.name
                    ])))
        then:
            // TODO: check that AccessDeniedException has been thrown
            result.andExpect(status().isMovedTemporarily())
    }

    def 'Deletion of draft topic should pass'() {
        given: 'User with username and password'
            def user = new User(username: "name", password: "pwd")
        and: "User registered and but has no permissions to create topics in current branch"
            users.create(user)
        and: "User logged in"
            def session = users.signIn(user)
        when: 'User created draft topic'
            def result = mockMvc.perform(delete("/topics/draft").session(session as MockHttpSession))
        then:
            result.andExpect(status().isOk())
                  .andExpect(jsonPath('$.status', is('SUCCESS')))
    }
}

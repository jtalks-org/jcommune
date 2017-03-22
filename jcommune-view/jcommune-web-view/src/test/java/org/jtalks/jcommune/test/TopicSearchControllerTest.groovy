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
import org.jtalks.jcommune.test.service.ComponentService
import org.jtalks.jcommune.test.service.GroupsService
import org.jtalks.jcommune.test.utils.Users
import org.jtalks.jcommune.web.controller.TopicSearchController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import javax.servlet.http.HttpSession

import static org.jtalks.jcommune.test.utils.assertions.Assert.assertView
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

/**
 * The security test for {@link TopicSearchController}
 * @author Evgeniy Cheban
 */
@WebAppConfiguration
@ContextConfiguration(locations = 'classpath:/org/jtalks/jcommune/web/view/test-configuration.xml')
@TransactionConfiguration(transactionManager = 'transactionManager', defaultRollback = true)
@Transactional
class TopicSearchControllerTest extends Specification {
    @Autowired Users users;
    @Autowired MockMvc mockMvc;
    @Autowired ComponentService componentService;
    @Autowired GroupsService groupsService;

    def setup() {
        groupsService.createPredefinedGroups();
    }

    def 'must not be able to send GET request to indexing data from database'() {
        given: 'User created without admin permissions'
            def user = new User();
            users.created(user);
            def session = users.signIn(user);
        when: 'send GET request to indexing data from database'
           MvcResult mvcResult = rebuildIndexes(session);
        then: 'redirect to 403 error page'
           assertView(mvcResult, "/errors/403");
    }

    def rebuildIndexes(HttpSession session) {
        componentService.createForumComponent();
        def result = mockMvc.perform(get('/search/index/rebuild')
                .session(session as MockHttpSession))
                .andReturn();
        return result;
    }
}

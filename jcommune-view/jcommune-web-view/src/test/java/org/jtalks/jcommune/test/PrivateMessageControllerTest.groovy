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

import org.jtalks.common.model.permissions.ProfilePermission
import org.jtalks.jcommune.service.security.AdministrationGroup
import org.jtalks.jcommune.test.model.User
import org.jtalks.jcommune.test.service.GroupsService
import org.jtalks.jcommune.test.utils.PrivateMessages
import org.jtalks.jcommune.test.utils.Users
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import static org.jtalks.jcommune.test.utils.assertions.Assert.assertView
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

/**
 * @author Evgeniy Cheban
 */
@WebAppConfiguration
@ContextConfiguration(locations = 'classpath:/org/jtalks/jcommune/web/view/test-configuration.xml')
@TransactionConfiguration(transactionManager = 'transactionManager', defaultRollback = true)
@Transactional
class PrivateMessageControllerTest extends Specification {
    @Autowired
    Users users
    @Autowired
    GroupsService groupsService
    @Autowired
    MockMvc mockMvc

    def setup() {
        groupsService.createPredefinedGroups()
    }

    def 'should save draft private message when "to" invalid'() {
        given: 'User logged in'
        def user = User.newInstance()
        def group = groupsService.getGroupByName(AdministrationGroup.USER.name)
        users.created(user).withPermissionOn(group, ProfilePermission.SEND_PRIVATE_MESSAGES)

        def session = users.signIn(user)
        when: 'User save draft private message with invalid "to"'
        def pmDto = PrivateMessages.randomDto()
        def mvcResult = mockMvc.perform(post('/pm/save')
                .session(session as MockHttpSession)
                .param('title', pmDto.title)
                .param('body', pmDto.body)
                .param('recipient', pmDto.recipient)
        ).andReturn()
        then:
        assertView(mvcResult, 'redirect:/drafts')
    }
}

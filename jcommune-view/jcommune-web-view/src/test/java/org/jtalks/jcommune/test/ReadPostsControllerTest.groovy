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

import org.jtalks.common.model.permissions.BranchPermission
import org.jtalks.jcommune.test.model.User
import org.jtalks.jcommune.test.service.GroupsService
import org.jtalks.jcommune.test.utils.Branches
import org.jtalks.jcommune.test.utils.Topics
import org.jtalks.jcommune.test.utils.Users
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

/**
 * @author Pavel Vervenko
 */
@WebAppConfiguration
@ContextConfiguration(locations = 'classpath:/org/jtalks/jcommune/web/view/test-configuration.xml')
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
class ReadPostsControllerTest extends Specification {

    @Autowired Users users;
    @Autowired Topics topics;
    @Autowired GroupsService groupsService
    @Autowired Branches branches;

    def setup() {
        groupsService.createPredefinedGroups()
    }

    def 'must mark topic as read'() {
        given: 'Topic created'
            def branch = branches.created();
            def user = new User();
            users.created(user).withPermissionOn(branch, BranchPermission.VIEW_TOPICS);
            def session = users.signIn(user);
            def topic = topics.created(user, branch);
        when: 'User access mark as read link'
            topics.markAsRead(session, topic, 1);
        then: 'Topic marked as read'
            topics.isMarkedAsRead(user, topic);

    }
}

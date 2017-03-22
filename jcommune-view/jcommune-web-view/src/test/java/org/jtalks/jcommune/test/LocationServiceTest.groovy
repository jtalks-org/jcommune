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
import org.jtalks.common.service.security.SecurityContextFacade
import org.jtalks.jcommune.model.entity.AnonymousGroup
import org.jtalks.jcommune.service.nontransactional.LocationService
import org.jtalks.jcommune.service.security.PermissionManager
import org.jtalks.jcommune.test.model.User
import org.jtalks.jcommune.test.service.ComponentService
import org.jtalks.jcommune.test.service.GroupsService
import org.jtalks.jcommune.test.service.PermissionGranter
import org.jtalks.jcommune.test.utils.Branches
import org.jtalks.jcommune.test.utils.Users
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
/**
 * @author Oleg Tkachenko
 */
@WebAppConfiguration
@ContextConfiguration(locations = 'classpath:/org/jtalks/jcommune/web/view/test-configuration.xml')
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
class LocationServiceTest extends Specification {
    @Autowired private Users users
    @Autowired private Branches branches
    @Autowired private ComponentService componentService
    @Autowired private MockMvc mockMvc
    @Autowired private LocationService locationService
    @Autowired private GroupsService groupsService
    @Autowired private PermissionManager permissionManager
    @Autowired private AuthenticationManager authenticationManager
    @Autowired private SecurityContextFacade securityContextFacade
    @Autowired private SessionAuthenticationStrategy authenticationStrategy

    def setup() {
        groupsService.createPredefinedGroups()
    }

    def 'user should be in a viewers list'() {
        given: 'User and branch were created'
            def user = new User()
            def branch = branches.created()
        and: "User registered and has permissions to view topics in current branch"
            users.created(user).withPermissionOn(branch, BranchPermission.VIEW_TOPICS)
        and: "User logged in"
            def session = users.signIn(user)
        when: 'user open the branch page'
            branches.open(branch, session)
        then: 'user is present in viewers list'
            def viewing = locationService.getUsersViewing(branch)
            assertEquals(viewing.find().username, user.username)
    }

    def "anonymous shouldn't be in a viewers list"() {
        given: 'branch created'
            def branch = branches.created()
        and: 'admin sets permissions on branch for anonymous users'
            def adminUser = new User()
            users.created(adminUser)
            new PermissionGranter(permissionManager, AnonymousGroup.ANONYMOUS_GROUP).withPermissionOn(branch, BranchPermission.VIEW_TOPICS)
        when: 'anonymous user open the branch page'
            def session = users.anonymousSession()
            branches.open(branch, session)
        then: "anonymous isn't in viewers list"
            def viewing = locationService.getUsersViewing(branch)
            assertTrue(viewing.isEmpty())
    }
}

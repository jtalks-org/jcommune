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
import org.jtalks.jcommune.test.service.ComponentService
import org.jtalks.jcommune.test.service.GroupsService
import org.jtalks.jcommune.test.utils.SpamRules
import org.jtalks.jcommune.test.utils.Users
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

import static io.qala.datagen.RandomShortApi.alphanumeric
import static org.jtalks.jcommune.test.utils.SpamRules.randomRule
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals
/**
* @author Oleg Tkachenko
*/
@WebAppConfiguration
@ContextConfiguration(locations = 'classpath:/org/jtalks/jcommune/web/view/test-configuration.xml')
class SpamProtectionRestApiTest extends Specification{
    private final @Autowired SpamRules spamRules
    private final @Autowired Users transactionalUsers
    private final @Autowired ComponentService componentService
    private final @Autowired GroupsService groupsService
    private Component forumComponent

    def setup() {
        groupsService.createPredefinedGroups()
        forumComponent = componentService.createForumComponent()
    }

    def '[POST] /spam-rule/ should create new spam rule'(){
        given: 'random spam rule dto created'
            def session = transactionalUsers.signInAsAdmin()
            def dto = randomRule()
        when: 'performed request to save spam rule'
            spamRules.post(session, dto)
        then: 'spam rule saved and exists'
            spamRules.assertExists(dto.regex)
    }

    def 'only admin can create new spam rule'(){
        given: 'random spam rule dto created'
            def session = transactionalUsers.signInAsRegisteredUser(forumComponent)
            def dto = randomRule()
        when: 'performed request to save spam rule'
            spamRules.post(session, dto)
        then: 'exception is thrown'
            thrown(AccessDeniedException)
        and: 'spam rule is not saved '
            spamRules.assertNotExists(dto.regex)
    }

    def '[GET] /spam-rule/{id} should return saved spam rule'(){
        given: 'spam rule created and saved'
            def session = transactionalUsers.signInAsAdmin()
            def created = spamRules.post(session, randomRule())
        when: 'spam rule requested by admin'
            def actual = spamRules.get(session, created.id)
        then: 'saved spam rule is returned'
            assertReflectionEquals(created, actual)
    }
    def 'only admin can get spam rule'(){
        given: 'random spam rule created by admin'
            def adminSession = transactionalUsers.signInAsAdmin()
            def created = spamRules.post(adminSession, randomRule())
        when: 'user requested spam rule'
            def userSession = transactionalUsers.signInAsRegisteredUser(forumComponent)
            spamRules.get(userSession, created.id)
        then: 'AccessDenied exception is thrown'
            thrown(AccessDeniedException)
    }

    def '[PUT] /spam-rule/{id} should update existed spam rule'(){
        given: 'spam rule created and saved'
            def session = transactionalUsers.signInAsAdmin()
            def created = spamRules.post(session, randomRule())
            def oldRegex = created.regex
        when: 'performed request to edit spam rule'
            created.regex = alphanumeric(255)
            spamRules.put(session, created)
        then: 'edited spam rule saved'
            spamRules.assertExists(created.regex)
        and: 'old spam rule isn"t exist'
            spamRules.assertNotExists(oldRegex)
    }

    def 'only admin can update existed spam rule'(){
        given: 'spam rule created and saved'
            def adminSession = transactionalUsers.signInAsAdmin()
            def created = spamRules.post(adminSession, randomRule())
        when: 'user performed request to edit spam rule'
            def userSession = transactionalUsers.signInAsRegisteredUser(forumComponent)
            spamRules.put(userSession, created)
        then: 'AccessDenied exception is thrown'
            thrown(AccessDeniedException)

    }

    def '[DELETE] /spam-rule/{id} should delete spam rule'(){
        given: 'random spam rule created'
            def session = transactionalUsers.signInAsAdmin()
            def created = spamRules.post(session, randomRule())
        when: 'performed request to delete spam rule'
            spamRules.delete(session, created.id)
        then: 'spam rule deleted'
            spamRules.assertNotExists(created.regex)
    }

    def 'only admin can delete spam rule'(){
        given: 'random spam rule created'
            def adminSession = transactionalUsers.signInAsAdmin()
            def created = spamRules.post(adminSession, randomRule())
        when: 'performed request to delete spam rule'
            def userSession = transactionalUsers.signInAsRegisteredUser(forumComponent)
            spamRules.delete(userSession, created.id)
        then: 'AccessDenied exception is thrown'
            thrown(AccessDeniedException)
    }
}

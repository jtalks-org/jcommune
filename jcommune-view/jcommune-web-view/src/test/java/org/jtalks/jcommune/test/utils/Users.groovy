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
package org.jtalks.jcommune.test.utils

import org.jtalks.common.model.entity.Component
import org.jtalks.common.model.permissions.GeneralPermission
import org.jtalks.common.service.security.SecurityContextFacade
import org.jtalks.jcommune.model.dao.GroupDao
import org.jtalks.jcommune.model.dao.UserDao
import org.jtalks.jcommune.model.entity.JCUser
import org.jtalks.jcommune.model.entity.UserInfo
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponse
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponseStatus
import org.jtalks.jcommune.service.nontransactional.EncryptionService
import org.jtalks.jcommune.service.security.AdministrationGroup
import org.jtalks.jcommune.service.security.PermissionManager
import org.jtalks.jcommune.test.model.User
import org.jtalks.jcommune.test.service.ComponentService
import org.jtalks.jcommune.test.service.GroupsManager
import org.jtalks.jcommune.test.service.PermissionGranter
import org.jtalks.jcommune.test.utils.exceptions.WrongResponseException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.mock.web.MockHttpSession
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult

import javax.servlet.http.HttpSession

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.hasItem
import static org.hamcrest.Matchers.not
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
/**
 * @author Mikhail Stryzhonok
 */
abstract class Users {
    @Autowired GroupDao groupDao
    @Autowired PermissionManager permissionManager
    @Autowired AuthenticationManager authenticationManager
    @Autowired SecurityContextFacade securityFacade
    @Autowired SessionAuthenticationStrategy sessionStrategy
    @Autowired EncryptionService encryptionService
    @Autowired UserDao userDao
    @Autowired MockMvc mockMvc
    @Autowired ComponentService componentService

    String signUpAndActivate(User user) throws Exception {
        singUp(user)

        def registered = userDao.getByUsername(user.username)
        mockMvc.perform(get('/user/activate/' + registered.uuid))

        return user.username;
    }

    HttpSession signUpAndSignIn(User user) {
        signUpAndActivate(user)
        signIn(user)
    }
    abstract HttpSession signIn(User user)
    abstract String singUp(User user)
    abstract void assertMvcResult(MvcResult result)

    PermissionGranter created(User user) {
        def group = groupDao.getGroupByName(AdministrationGroup.USER.name)
        def fromDb = userDao.getByUsername(user.username)
        if (!fromDb) {
            fromDb = new JCUser(user.username, user.email, encryptionService.encryptPassword(user.password))
            fromDb.enabled = true
            fromDb.addGroup(group)
            userDao.saveOrUpdate(fromDb)
            userDao.flush()
        }
        //Needed for managing permissions
        setAuthentication(new JCUser(user.username, 'sample@example.com', user.password))
        return new PermissionGranter(permissionManager, group);
    }

    GroupsManager createdWithoutAccess(User user) {
        def fromDb = userDao.getByUsername(user.username)
        if (!fromDb) {
            fromDb = new JCUser(user.username, user.email, encryptionService.encryptPassword(user.password))
            fromDb.enabled = true
            userDao.saveOrUpdate(fromDb)
            userDao.flush()
        }
        return new GroupsManager(fromDb, groupDao, userDao);
    }

    def createdCountInGroupWithoutAccess(int count, String groupName) {
        def group = groupDao.getGroupByName(groupName)
        for(int i=0; i<count; i++) {
            def user = new User();
            def fromDb = new JCUser(user.username, user.email, encryptionService.encryptPassword(user.password))
            fromDb.enabled = true
            fromDb.addGroup(group)
            userDao.saveOrUpdate(fromDb)
            userDao.flush()
        }
    }

    def createdButNotActivated(User user) {
        def toCreate = new JCUser(user.username, 'sample@example.com', encryptionService.encryptPassword(user.password))
        userDao.saveOrUpdate(toCreate)
        userDao.flush()
    }

    User randomNotActivatedUser() {
        User user = new User()
        def toCreate = new JCUser(user.username, user.email, encryptionService.encryptPassword(user.password))
        toCreate.enabled = false //it is not necessary, only to make sense
        userDao.saveOrUpdate(toCreate)
        userDao.flush()
        return dtoFrom(toCreate)
    }

    def setAuthentication(JCUser user) {
        def token = new UsernamePasswordAuthenticationToken(user.username, user.password)
        token.details = new UserInfo(user)
        def auth = authenticationManager.authenticate(token)
        securityFacade.context.authentication = auth
        def request = new MockHttpServletRequest()
        def response =  new MockHttpServletResponse()
        sessionStrategy.onAuthentication(auth, request, response)
    }

    boolean isActivated(String username) {
        def user = userDao.getByUsername(username)
        if (user == null) {
            throw new IllegalArgumentException("User with name [${user.username}] not exist")
        }
        return user.enabled;
    }

    boolean isExist(String username) {
        return userDao.getByUsername(username) != null
    }

    boolean isNotExist(String username) {
        return userDao.getByUsername(username) == null
    }

    long userIdByUsername(String username) {
        return userDao.getByUsername(username).getId();
    }

    static boolean isAuthenticated(HttpSession session, User user) {
        //From Spring Security source code
        def context = session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)

        if (context == null || ! (context instanceof SecurityContext)) {
            return false;
        }

        def auth = (context as SecurityContext).authentication
        return auth != null && auth.authenticated && (auth.principal as UserInfo).username == user.username
    }

    long[] fetchGroups(HttpSession session, User user) {
        def result = mockMvc.perform(get('/user/' + userIdByUsername(user.username) + '/groups')
            .session(session as MockHttpSession)
            .contentType(MediaType.APPLICATION_JSON)
        ).andReturn();

        return readJsonResponce(result).result as long[];
    }

    def addUserToGroup(HttpSession session, User user, long groupID) {
        def result = mockMvc.perform(post('/user/' + userIdByUsername(user.username) + '/groups/' + groupID)
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()
        readJsonResponce(result)
    }

    def deleteUserFromGroup(HttpSession session, User user, long groupID) {
        def result = mockMvc.perform(delete('/user/' + userIdByUsername(user.username) + '/groups/' + groupID)
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()
        readJsonResponce(result)
    }

    private static JsonResponse readJsonResponce(MvcResult result) {
        def jsonResponse = JsonResponseUtils.parse(result.response.getContentAsString())
        if (jsonResponse.status != JsonResponseStatus.SUCCESS) {
            throw new WrongResponseException(JsonResponseStatus.SUCCESS.name(), jsonResponse.status.name())
        }
        jsonResponse
    }

    void assertUserInGroup(User user, long groupID) {
        def userGroups = userDao.getByUsername(user.username).groups
        def group = groupDao.get(groupID)
        assertThat(userGroups, hasItem(group))
    }

    void assertUserNotMemerOfGroup(User user, long groupID) {
        def userGroups = userDao.getByEmail(user.email).groups
        def group = groupDao.get(groupID)
        assertThat(userGroups, not(hasItem(group)))
    }

    HttpSession signInAsAdmin() {
        def adminUser = new User()
        created(adminUser).withPermissionOn(componentService.createForumComponent(), GeneralPermission.ADMIN)
        signIn(adminUser)
    }

    HttpSession signInAsAdmin(Component forum) {
        def adminUser = User.newInstance()
        created(adminUser).withPermissionOn(forum, GeneralPermission.ADMIN)
        signIn(adminUser)
    }

    HttpSession signInAsRegisteredUser(Component forum) {
        def user = User.newInstance()
        created(user).withPermissionOn(forum, GeneralPermission.READ)
        signIn(user)
    }

    User dtoFrom(JCUser common){
        User dto = new User()
        dto.uuid = common.uuid
        dto.username = common.username
        dto.email = common.email
        return dto
    }

    HttpSession anonymousSession(){
        mockMvc.perform(get("/")).andReturn().request.session
    }
}

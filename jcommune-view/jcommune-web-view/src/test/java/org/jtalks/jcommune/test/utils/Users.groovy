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

import org.jtalks.common.service.security.SecurityContextHolderFacade
import org.jtalks.jcommune.model.dao.GroupDao
import org.jtalks.jcommune.model.dao.UserDao
import org.jtalks.jcommune.model.entity.JCUser
import org.jtalks.jcommune.service.nontransactional.EncryptionService
import org.jtalks.jcommune.service.security.AdministrationGroup
import org.jtalks.jcommune.service.security.PermissionManager
import org.jtalks.jcommune.test.model.User
import org.jtalks.jcommune.test.service.GroupsManager
import org.jtalks.jcommune.test.service.PermissionGranter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult

import javax.servlet.http.HttpSession

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

/**
 * @author Mikhail Stryzhonok
 */
abstract class Users {
    @Autowired GroupDao groupDao
    @Autowired PermissionManager permissionManager
    @Autowired AuthenticationManager authenticationManager
    @Autowired SecurityContextHolderFacade securityFacade
    @Autowired SessionAuthenticationStrategy sessionStrategy
    @Autowired EncryptionService encryptionService
    @Autowired UserDao userDao
    @Autowired MockMvc mockMvc

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
        if (fromDb == null) {
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
        if (fromDb == null) {
            fromDb = new JCUser(user.username, user.email, encryptionService.encryptPassword(user.password))
            fromDb.enabled = true
            userDao.saveOrUpdate(fromDb)
            userDao.flush()
        }
        return new GroupsManager(fromDb, groupDao, userDao);
    }

    def createdButNotActivated(User user) {
        def toCreate = new JCUser(user.username, 'sample@example.com', encryptionService.encryptPassword(user.password))
        userDao.saveOrUpdate(toCreate)
        userDao.flush()
    }

    def setAuthentication(JCUser user) {
        def token = new UsernamePasswordAuthenticationToken(user.username, user.password)
        token.details = user
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
        return auth != null && auth.authenticated && ((auth.principal as JCUser).username.equals(user.username))
    }
}

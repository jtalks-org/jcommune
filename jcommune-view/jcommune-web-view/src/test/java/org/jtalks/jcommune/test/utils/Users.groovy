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
package org.jtalks.jcommune.test.utils;

import org.jtalks.common.service.security.SecurityContextHolderFacade;
import org.jtalks.jcommune.model.dao.GroupDao;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.utils.PermissionGranter;
import org.jtalks.jcommune.service.nontransactional.EncryptionService;
import org.jtalks.jcommune.service.security.AdministrationGroup;
import org.jtalks.jcommune.service.security.PermissionManager;
import org.jtalks.jcommune.test.utils.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.HttpSession;

import java.io.Serializable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * @author Mikhail Stryzhonok
 */
abstract class Users {
    // Default values
    public static final def USERNAME = 'user'
    public static final def PASSWORD = 'pwd'

    @Autowired
    UserDao userDao
    @Autowired
    private GroupDao groupDao
    @Autowired
    private PermissionManager permissionManager
    @Autowired
    private AuthenticationManager authenticationManager
    @Autowired
    private SecurityContextHolderFacade securityFacade
    @Autowired
    private SessionAuthenticationStrategy sessionStrategy
    @Autowired
    private EncryptionService encryptionService
    MockMvc mockMvc

    def PermissionGranter create() {
        def group = groupDao.getGroupByName(AdministrationGroup.USER.name)
        def user = userDao.getByUsername(USERNAME)
        if (user == null) {
            user = new JCUser(USERNAME, 'sample@example.com', encryptionService.encryptPassword(PASSWORD))
            user.enabled = true
            user.addGroup(group)
            userDao.saveOrUpdate(user)
            userDao.flush()
        }
        //Needed for managing permissions
        setAuthentication(new JCUser(USERNAME, 'sample@example.com', PASSWORD));
        return new PermissionGranter(permissionManager, group);
    }

    def void setAuthentication(JCUser user) {
        def token = new UsernamePasswordAuthenticationToken(user.username, user.password)
        token.details = user
        def auth = authenticationManager.authenticate(token)
        securityFacade.context.authentication = auth
        def request = new MockHttpServletRequest()
        def response =  new MockHttpServletResponse()
        sessionStrategy.onAuthentication(auth, request, response)
    }

    def String signUpAndActivate(User user) throws Exception {
        singUp(user)

        def registered = userDao.getByUsername(user.username)
        mockMvc.perform(get('/user/activate/' + registered.uuid))

        return user.username;
    }

    def abstract HttpSession performLogin()

    def abstract String singUp(User user)

    def abstract void assertMvcResult(MvcResult result, Serializable entityIdentifier)

    def boolean isActivated(String username) {
        def user = userDao.getByUsername(username)
        if (user == null) {
            throw new IllegalArgumentException("User with name [${user.username}] not exist")
        }
        return user.enabled;
    }

    def boolean isExist(String username) {
        return userDao.getByUsername(username) != null
    }

    def boolean isNotExist(String username) {
        return userDao.getByUsername(username) == null
    }

}

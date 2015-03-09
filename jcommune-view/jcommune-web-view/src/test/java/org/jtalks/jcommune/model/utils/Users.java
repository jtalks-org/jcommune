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
package org.jtalks.jcommune.model.utils;

import org.jtalks.common.model.entity.Group;
import org.jtalks.common.service.security.SecurityContextHolderFacade;
import org.jtalks.jcommune.model.dao.GroupDao;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.nontransactional.EncryptionService;
import org.jtalks.jcommune.service.security.AdministrationGroup;
import org.jtalks.jcommune.service.security.PermissionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * @author Mikhail Stryzhonok
 */
public class Users {

    @Autowired
    private UserDao userDao;
    @Autowired
    private GroupDao groupDao;
    @Autowired
    private PermissionManager permissionManager;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private SecurityContextHolderFacade securityFacade;
    @Autowired
    private SessionAuthenticationStrategy sessionStrategy;
    @Autowired
    private EncryptionService encryptionService;

    public static String USERNAME = "user";
    public static String PASSWORD = "pwd";

    public PermissionGranter create() {
        Group group = groupDao.getGroupByName(AdministrationGroup.USER.getName());
        JCUser user = userDao.getByUsername(USERNAME);
        if (user == null) {
            user = new JCUser(USERNAME, "sample@example.com", encryptionService.encryptPassword(PASSWORD));
            user.setEnabled(true);
            user.addGroup(group);
            userDao.saveOrUpdate(user);
            userDao.flush();
        }
        //Needed for managing permissions
        setAuthentication(new JCUser(USERNAME, "sample@example.com", PASSWORD));
        return new PermissionGranter(permissionManager, group);
    }

    private void setAuthentication(JCUser user) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        token.setDetails(user);
        Authentication auth = authenticationManager.authenticate(token);
        securityFacade.getContext().setAuthentication(auth);
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response =  new MockHttpServletResponse();
        sessionStrategy.onAuthentication(auth, request , response);
    }

    public HttpSession performLogin(MockMvc mockMvc) throws Exception{
        return mockMvc.perform(post("/login_ajax")
                .param("userName", USERNAME).param("password", PASSWORD))
                .andReturn().getRequest().getSession();
    }


}

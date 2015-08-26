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

import org.jtalks.jcommune.model.dao.UserDao
import org.jtalks.jcommune.test.model.User;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.HttpSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

/**
 * @author Mikhail Stryzhonok
 */
abstract class Users {
    @Autowired
    UserDao userDao
    MockMvc mockMvc

    def String signUpAndActivate(User user) throws Exception {
        singUp(user)

        def registered = userDao.getByUsername(user.username)
        mockMvc.perform(get('/user/activate/' + registered.uuid))

        return user.username;
    }

    def abstract HttpSession signIn(User user)

    def abstract String singUp(User user)

    def abstract void assertMvcResult(MvcResult result)

}

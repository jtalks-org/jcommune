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
package org.jtalks.jcommune.test.utils.popup;

import org.jtalks.jcommune.test.utils.Users;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponse;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponseStatus;
import org.jtalks.jcommune.test.utils.exceptions.ValidationException;
import org.jtalks.jcommune.test.utils.exceptions.WrongResponseException;
import org.jtalks.jcommune.test.utils.model.User;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.HttpSession;

import static org.jtalks.jcommune.test.utils.JsonResponseUtils.fetchErrorMessagesFromJsonString;
import static org.jtalks.jcommune.test.utils.JsonResponseUtils.jsonResponseToString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


/**
 * @author Mikhail Stryzhonok
 */
class PopUpUsers extends Users {

    @Override
    def HttpSession performLogin() {
        return mockMvc.perform(post('/login_ajax')
                .param('userName', USERNAME).param('password', PASSWORD))
                .andReturn().request.session
    }

    @Override
    def void assertMvcResult(MvcResult result, Serializable entityIdentifier) {
        def response = result.response.contentAsString
        if(!jsonResponseToString(new JsonResponse(JsonResponseStatus.SUCCESS)).equals(response)
                && response != null) {
            def defaultErrorMessages = fetchErrorMessagesFromJsonString(response)
            if (defaultErrorMessages.empty) {
                throw new WrongResponseException(jsonResponseToString(new JsonResponse(JsonResponseStatus.SUCCESS)),
                        response)
            } else {
                def e = new ValidationException()
                e.addAllDefaultErrorMessages(defaultErrorMessages)
                throw e
            }
        }
    }

    @Override
    def String singUp(User user) {
        assertMvcResult(mockMvc.perform(post('/user/new_ajax')
                .param('userDto.username', user.username)
                .param('userDto.email', user.email)
                .param('userDto.password', user.password)
                .param('passwordConfirm', user.confirmation)
                .param('honeypotCaptcha', user.honeypot)).andReturn(), user.username)
        return user.username
    }

}

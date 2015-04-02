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
package org.jtalks.jcommune.test.utils.page;

import org.jtalks.jcommune.test.utils.Users;
import org.jtalks.jcommune.test.utils.exceptions.ValidationException;
import org.jtalks.jcommune.test.utils.exceptions.WrongResponseException;
import org.jtalks.jcommune.test.utils.model.User;
import org.jtalks.jcommune.web.controller.UserController;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

import java.io.Serializable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * @author Mikhail Stryzhonok
 */
public class PageUsers extends Users {

    public static final String BINDING_RESULT_ATTRIBUTE_NAME = BindingResult.MODEL_KEY_PREFIX + "newUser";

    @Override
    public HttpSession performLogin() throws Exception{
        return getMockMvc().perform(post("/login")
                .param("userName", USERNAME)
                .param("password", PASSWORD)
                .param("referer", "/"))
                .andReturn().getRequest().getSession();
    }

    @Override
    public String singUp(User user) throws Exception {
        ResultActions resultActions = getMockMvc().perform(post("/user/new")
                .param("userDto.username", user.getUsername())
                .param("userDto.email", user.getEmail())
                .param("userDto.password", user.getPassword())
                .param("passwordConfirm", user.getConfirmation())
                .param("honeypotCaptcha", user.getHoneypot()));

        assertMvcResult(resultActions.andReturn(), user.getUsername());
        return user.getUsername();
    }

    @Override
    public void assertMvcResult(MvcResult mvcResult, Serializable entityIdentifier)
            throws WrongResponseException, ValidationException {
        ModelAndView mav = mvcResult.getModelAndView();
        BindingResult result = (BindingResult) mav.getModel().get(BINDING_RESULT_ATTRIBUTE_NAME);
        if (result.hasErrors()) {
            ValidationException ex = new ValidationException();
            for (ObjectError error : result.getAllErrors()) {
                ex.addDefaultErrorMessage(error.getDefaultMessage());
            }
            throw ex;
        }
        if (!UserController.AFTER_REGISTRATION.equals(mav.getViewName())) {
            throw new WrongResponseException(UserController.AFTER_REGISTRATION,
                    mvcResult.getModelAndView().getViewName());
        }
    }
}

/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.jcommune.web.controller;

import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.DuplicateException;
import org.jtalks.jcommune.web.dto.UserDto;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.ModelAndViewAssert.assertAndReturnModelAttributeOfType;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author Kirill Afonin
 */
public class UserControllerTest {
    private UserService userService;
    private UserController controller;

    @BeforeMethod
    public void setUp() {
        userService = mock(UserService.class);
        controller = new UserController(userService);
    }

    @Test
    public void testRegistrationPage() throws Exception {
        ModelAndView mav = controller.registrationPage();

        assertViewName(mav, "registration");
        UserDto dto = assertAndReturnModelAttributeOfType(mav, "newUser", UserDto.class);
        assertNullFields(dto);
    }

    private void assertNullFields(UserDto dto) {
        assertNull(dto.getEmail());
        assertNull(dto.getUsername());
        assertNull(dto.getPassword());
        assertNull(dto.getPasswordConfirm());
        assertNull(dto.getLastName());
        assertNull(dto.getFirstName());
    }

    @Test
    public void testRegisterUser() throws Exception {
        UserDto dto = getUserDto();
        User user = dto.createUser();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "newUser");

        ModelAndView mav = controller.registerUser(dto, bindingResult);

        assertViewName(mav, "redirect:/");
        verify(userService).registerUser(any(User.class));
    }

    @Test
    public void testRegisterDuplicateUser() throws Exception {
        UserDto dto = getUserDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "newUser");
        doThrow(new DuplicateException("")).when(userService).registerUser(any(User.class));

        ModelAndView mav = controller.registerUser(dto, bindingResult);

        assertViewName(mav, "registration");
        assertEquals(bindingResult.getErrorCount(), 1 , "Result without errors");
        verify(userService).registerUser(any(User.class));
    }

    @Test
    public void testRegisterValidationFail() {
        UserDto dto = getUserDto();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        ModelAndView mav = controller.registerUser(dto, bindingResult);

        assertViewName(mav, "registration");
    }

    /**
     * @return UserDto with default field values
     */
    private UserDto getUserDto() {
        UserDto dto = new UserDto();
        dto.setUsername("username");
        dto.setEmail("mail@mail.com");
        dto.setPassword("password");
        dto.setPasswordConfirm("password");
        dto.setFirstName("first name");
        dto.setLastName("last name");
        return dto;
    }
}

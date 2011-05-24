package org.jtalks.jcommune.web.controller;

import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.DuplicateException;
import org.jtalks.jcommune.web.dto.UserDto;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.ModelAndViewAssert.assertAndReturnModelAttributeOfType;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;

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
        Assert.assertNull(dto.getEmail());
        Assert.assertNull(dto.getUsername());
        Assert.assertNull(dto.getPassword());
        Assert.assertNull(dto.getPasswordConfirm());
        Assert.assertNull(dto.getLastName());
        Assert.assertNull(dto.getFirstName());
    }

    @Test
    public void testRegisterUser() throws Exception {
        UserDto dto = getUserDto();
        User user = dto.createUser();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "newUser");

        ModelAndView mav = controller.registerUser(dto, bindingResult);

        assertViewName(mav, "redirect:/");
        verify(userService, times(1)).registerUser(user);
    }

    @Test
    public void testRegisterDuplicateUser() throws Exception {
        UserDto dto = getUserDto();
        User user = dto.createUser();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "newUser");
        doThrow(new DuplicateException()).when(userService).registerUser(user);

        ModelAndView mav = controller.registerUser(dto, bindingResult);

        assertViewName(mav, "registration");
        Assert.assertEquals(1, bindingResult.getErrorCount(), "Result without errors");
        verify(userService, times(1)).registerUser(user);
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

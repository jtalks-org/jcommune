package org.jtalks.jcommune.web.controller;

import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.DuplicateEmailException;
import org.jtalks.jcommune.service.exceptions.DuplicateException;
import org.jtalks.jcommune.web.dto.RegisterUserDto;
import org.jtalks.jcommune.web.dto.UserDto;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.ModelAndViewAssert.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author Kirill Afonin
 */
public class UserControllerTest {
    private UserService userService;
    private SecurityService securityService;
    private UserController controller;

    @BeforeMethod
    public void setUp() {
        userService = mock(UserService.class);
        securityService = mock(SecurityService.class);
        controller = new UserController(userService, securityService);
    }

    @Test
    public void testRegistrationPage() throws Exception {
        ModelAndView mav = controller.registrationPage();

        assertViewName(mav, "registration");
        RegisterUserDto dto = assertAndReturnModelAttributeOfType(mav, "newUser", RegisterUserDto.class);
        assertNullFields(dto);
    }

    private void assertNullFields(RegisterUserDto dto) {
        assertNull(dto.getEmail());
        assertNull(dto.getUsername());
        assertNull(dto.getPassword());
        assertNull(dto.getPasswordConfirm());
        assertNull(dto.getLastName());
        assertNull(dto.getFirstName());
    }

    @Test
    public void testRegisterUser() throws Exception {
        RegisterUserDto dto = getUserDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "newUser");

        ModelAndView mav = controller.registerUser(dto, bindingResult);

        assertViewName(mav, "redirect:/");
        verify(userService).registerUser(any(User.class));
    }

    @Test
    public void testRegisterDuplicateUser() throws Exception {
        RegisterUserDto dto = getUserDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "newUser");
        doThrow(new DuplicateUserException("User username already exists!")).when(userService).registerUser(any(User.class));

        ModelAndView mav = controller.registerUser(dto, bindingResult);

        assertViewName(mav, "registration");
        assertEquals(bindingResult.getErrorCount(), 1, "Result without errors");
        verify(userService).registerUser(any(User.class));
    }

    @Test
    public void testRegisterUserWithDuplicateEmail() throws Exception {
        RegisterUserDto dto = getUserDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "newUser");
        doThrow(new DuplicateEmailException("E-mail mail@mail.com already exists!")).when(userService).registerUser(any(User.class));

        ModelAndView mav = controller.registerUser(dto, bindingResult);

        assertViewName(mav, "registration");
        assertEquals(bindingResult.getErrorCount(), 1, "Result without errors");
        verify(userService).registerUser(any(User.class));
    }

    @Test
    public void testRegisterValidationFail() {
        RegisterUserDto dto = getUserDto();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        ModelAndView mav = controller.registerUser(dto, bindingResult);

        assertViewName(mav, "registration");
    }


    @Test
    public void testShow() throws Exception {
        long userId = 1L;
        when(userService.get(userId)).thenReturn(new User());

        ModelAndView mav = controller.show(userId);

        assertViewName(mav, "userDetails");
        assertModelAttributeAvailable(mav, "user");
        verify(userService).get(userId);
    }

    /**
     * @return UserDto with default field values
     */
    private RegisterUserDto getUserDto() {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setUsername("username");
        dto.setEmail("mail@mail.com");
        dto.setPassword("password");
        dto.setPasswordConfirm("password");
        dto.setFirstName("first name");
        dto.setLastName("last name");
        return dto;
    }
}

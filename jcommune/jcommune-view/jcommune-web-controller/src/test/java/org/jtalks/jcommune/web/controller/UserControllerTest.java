package org.jtalks.jcommune.web.controller;

import java.io.UnsupportedEncodingException;
import java.util.Map;


import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.DuplicateEmailException;
import org.jtalks.jcommune.service.exceptions.DuplicateUserException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.exceptions.WrongPasswordException;
import org.jtalks.jcommune.web.dto.EditUserProfileDto;
import org.jtalks.jcommune.web.dto.RegisterUserDto;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.ModelAndViewAssert.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author Kirill Afonin
 * @author Osadchuck Eugeny
 */
public class UserControllerTest {
    private UserService userService;
    private SecurityService securityService;
    private UserController controller;
    
    private final String USER_NAME = "username";
    private final String ENCODED_USER_NAME = "encodeUsername";
    private final String FIRST_NAME = "first name";
    private final String LAST_NAME = "last name";
    private final String EMAIL = "mail@mail.com";
    private final String PASSWORD = "password";
    private final String NEW_PASSWORD = "newPassword";


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
        RegisterUserDto dto = getRegisterUserDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "newUser");

        ModelAndView mav = controller.registerUser(dto, bindingResult);

        assertViewName(mav, "redirect:/");
        verify(userService).registerUser(any(User.class));
    }

    @Test
    public void testRegisterDuplicateUser() throws Exception {
        RegisterUserDto dto = getRegisterUserDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "newUser");
        doThrow(new DuplicateUserException("User username already exists!")).when(userService).registerUser(any(User.class));

        ModelAndView mav = controller.registerUser(dto, bindingResult);

        assertViewName(mav, "registration");
        assertEquals(bindingResult.getErrorCount(), 1, "Result without errors");
        verify(userService).registerUser(any(User.class));
    }

    @Test
    public void testRegisterUserWithDuplicateEmail() throws Exception {
        RegisterUserDto dto = getRegisterUserDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "newUser");
        doThrow(new DuplicateEmailException("E-mail mail@mail.com already exists!")).when(userService).registerUser(any(User.class));

        ModelAndView mav = controller.registerUser(dto, bindingResult);

        assertViewName(mav, "registration");
        assertEquals(bindingResult.getErrorCount(), 1, "Result without errors");
        verify(userService).registerUser(any(User.class));
    }

    @Test
    public void testRegisterValidationFail() {
        RegisterUserDto dto = getRegisterUserDto();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        ModelAndView mav = controller.registerUser(dto, bindingResult);

        assertViewName(mav, "registration");
    }

    @Test
    public void testShow() throws Exception {        
        String encodedUsername = "encodedUsername";
        when(userService.getByEncodedUsername(encodedUsername)).thenReturn(new User());

        ModelAndView mav = controller.show(encodedUsername);

        assertViewName(mav, "userDetails");
        assertModelAttributeAvailable(mav, "user");
        verify(userService).getByEncodedUsername(encodedUsername);
    }
    
    @Test
    public void testEditProfilePage() throws NotFoundException{
        User user = getUser();
        when(securityService.getCurrentUser()).thenReturn(user);
        
        ModelAndView mav = controller.editProfilePage();
        
        assertViewName(mav, "editProfile");
        assertModelAttributeAvailable(mav, "editedUser");        
        verify(securityService).getCurrentUser();
        
        Map<String, Object> modelMap = mav.getModel();
        EditUserProfileDto dto = (EditUserProfileDto) modelMap.get("editedUser");
        
        assertEquals(dto.getFirstName(), user.getFirstName(),"First name is not equal");
        assertEquals(dto.getLastName(), user.getLastName(),"Last name is not equal");
        assertEquals(dto.getEmail(), user.getEmail(),"Last name is not equal");
        
    }
    
    @Test
    public void testEditProfile() throws NotFoundException{
        User user = getUser();
        when(securityService.getCurrentUser()).thenReturn(user);
        EditUserProfileDto dto = getEditUserProfileDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "editedUser");
        
        ModelAndView mav = controller.editProfile(dto, bindingResult);
        
        String excpectedUrl = new StringBuilder().append("redirect:/user/").append(user.getEncodedUsername()).append(".html").toString();
        assertViewName(mav, excpectedUrl);
        verify(securityService).getCurrentUser();
    }
        
    @Test
    public void testEditProfileDublicatedEmail() throws Exception{
        User user = getUser();
        EditUserProfileDto dto = getEditUserProfileDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "editedUser");
        
        when(securityService.getCurrentUser()).thenReturn(user);
        doThrow(new DuplicateEmailException()).when(userService).editUserProfile(any(User.class), anyString(), anyString(), anyString());

        ModelAndView mav = controller.editProfile(dto, bindingResult);

        assertViewName(mav, "editProfile");
        assertEquals(bindingResult.getErrorCount(), 1, "Result without errors");
        verify(userService).editUserProfile(any(User.class), anyString(), anyString(), anyString());
        
        for(ObjectError error : bindingResult.getAllErrors()){
            if(error != null && error instanceof FieldError){
                assertEquals(((FieldError)error).getField(), "email");
            }
        }
    }
    
    @Test
    public void testEditProfileWrongPassword() throws Exception{
        User user = getUser();
        EditUserProfileDto dto = getEditUserProfileDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "editedUser");
        
        when(securityService.getCurrentUser()).thenReturn(user);
        doThrow(new WrongPasswordException()).when(userService).editUserProfile(any(User.class), anyString(), anyString(), anyString());

        ModelAndView mav = controller.editProfile(dto, bindingResult);

        assertViewName(mav, "editProfile");
        assertEquals(bindingResult.getErrorCount(), 1, "Result without errors");
        verify(userService).editUserProfile(any(User.class), anyString(), anyString(), anyString());
        
        for(ObjectError error : bindingResult.getAllErrors()){
            if(error != null && error instanceof FieldError){
                assertEquals(((FieldError)error).getField(), "currentUserPassword");
            }
        }
    }
    
    @Test
    public void testeditProfilevalidationFail() throws Exception{
        User user = getUser();
        EditUserProfileDto dto = getEditUserProfileDto();
        BindingResult bindingResult = mock(BindingResult.class);
        
        when(bindingResult.hasErrors()).thenReturn(true);
        
        when(securityService.getCurrentUser()).thenReturn(user);
        
        ModelAndView mav = controller.editProfile(dto, bindingResult);
        assertViewName(mav, "editProfile");
        verify(securityService, times(0)).getCurrentUser();
    }

    /**
     * @return RegisterUserDto with default field values
     */
    private RegisterUserDto getRegisterUserDto() {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setUsername(USER_NAME);
        dto.setEmail(EMAIL);
        dto.setPassword(PASSWORD);
        dto.setPasswordConfirm(PASSWORD);
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        return dto;
    }
    
    /**
     * @return {@link EditUserProfileDto} with default values
     */
    private EditUserProfileDto  getEditUserProfileDto(){
        EditUserProfileDto dto = new EditUserProfileDto();
        dto.setEmail(EMAIL);
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setCurrentUserPassword(PASSWORD);
        dto.setNewUserPassword(NEW_PASSWORD);
        dto.setNewUserPasswordConfirm(NEW_PASSWORD);
        
        return dto;
    }
    
    private User getUser() {
        User newUser = new User();
        newUser.setUsername(USER_NAME);
        try {
            newUser.setEncodedUsername(ENCODED_USER_NAME);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        newUser.setFirstName(FIRST_NAME);
        newUser.setLastName(LAST_NAME);
        newUser.setEmail(EMAIL);
        newUser.setPassword(PASSWORD);
        return newUser;
    }
}

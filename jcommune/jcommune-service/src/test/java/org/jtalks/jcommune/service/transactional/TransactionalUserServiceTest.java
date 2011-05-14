package org.jtalks.jcommune.service.transactional;

import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.DuplicateException;
import org.mockito.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Kirill Afonin
 */
public class TransactionalUserServiceTest {
    final String USERNAME = "username";
    final String EMAIL = "username@mail.com";
    final String PASSWORD = "password";
    final String FIRST_NAME = "first name";
    final String LAST_NAME = "last name";


    private UserService userService;
    private UserDao userDao;

    @BeforeMethod
    public void setUp() throws Exception {
        userDao = mock(UserDao.class);
        userService = new TransactionalUserService(userDao);
    }

    @Test
    public void testGetByUsername() throws Exception {

        User user = new User();
        user.setUsername(USERNAME);
        when(userDao.getByUsername(USERNAME)).thenReturn(user);

        User result = userService.getByUsername(USERNAME);

        Assert.assertEquals(USERNAME, result.getUsername(), "Username not equals");
        verify(userDao, times(1)).getByUsername(USERNAME);
    }

    @Test
    public void testRegisterUser() throws Exception {
        when(userDao.isUserWithEmailExist(EMAIL)).thenReturn(false);
        when(userDao.isUserWithUsernameExist(USERNAME)).thenReturn(false);

        userService.registerUser(USERNAME, EMAIL, FIRST_NAME, LAST_NAME,
                PASSWORD);

        verify(userDao, times(1)).isUserWithEmailExist(EMAIL);
        verify(userDao, times(1)).isUserWithUsernameExist(USERNAME);
        verify(userDao, times(1)).saveOrUpdate(Matchers.<User>any());
    }

    @Test(expectedExceptions = {DuplicateException.class})
    public void testRegisterUser_UsernameExist() throws Exception {
        when(userDao.isUserWithEmailExist(EMAIL)).thenReturn(false);
        when(userDao.isUserWithUsernameExist(USERNAME)).thenReturn(true);

        userService.registerUser(USERNAME, EMAIL, FIRST_NAME, LAST_NAME,
                PASSWORD);
    }

    @Test(expectedExceptions = {DuplicateException.class})
    public void testRegisterUser_EmailExist() throws Exception {
        when(userDao.isUserWithEmailExist(EMAIL)).thenReturn(true);
        when(userDao.isUserWithUsernameExist(USERNAME)).thenReturn(false);

        userService.registerUser(USERNAME, EMAIL, FIRST_NAME, LAST_NAME,
                PASSWORD);
    }
}

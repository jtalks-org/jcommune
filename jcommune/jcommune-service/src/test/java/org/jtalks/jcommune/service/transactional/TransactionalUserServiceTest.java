package org.jtalks.jcommune.service.transactional;

import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.DuplicateException;
import org.mockito.Matchers;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * @author Kirill Afonin
 * @author Osadchuck Eugeny
 */
public class TransactionalUserServiceTest {
    private static final String USERNAME = "username";
    private static final String EMAIL = "username@mail.com";
    private static final String PASSWORD = "password";
    private static final String FIRST_NAME = "first name";
    private static final String LAST_NAME = "last name";
    private static final Long USER_ID = 999L;


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

    @Test(expectedExceptions = UsernameNotFoundException.class)
    public void testGetByUsernameNotFound() throws Exception {
        when(userDao.getByUsername(USERNAME)).thenReturn(null);

        userService.getByUsername(USERNAME);
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
    public void testRegisterUserUsernameExist() throws Exception {
        when(userDao.isUserWithEmailExist(EMAIL)).thenReturn(false);
        when(userDao.isUserWithUsernameExist(USERNAME)).thenReturn(true);

        userService.registerUser(USERNAME, EMAIL, FIRST_NAME, LAST_NAME,
                PASSWORD);
    }

    @Test(expectedExceptions = {DuplicateException.class})
    public void testRegisterUserEmailExist() throws Exception {
        when(userDao.isUserWithEmailExist(EMAIL)).thenReturn(true);
        when(userDao.isUserWithUsernameExist(USERNAME)).thenReturn(false);

        userService.registerUser(USERNAME, EMAIL, FIRST_NAME, LAST_NAME,
                PASSWORD);
    }

    @Test(expectedExceptions = {DuplicateException.class})
    public void testRegisterUserBothExist() throws Exception {
        when(userDao.isUserWithEmailExist(EMAIL)).thenReturn(true);
        when(userDao.isUserWithUsernameExist(USERNAME)).thenReturn(true);

        userService.registerUser(USERNAME, EMAIL, FIRST_NAME, LAST_NAME,
                PASSWORD);
    }

    @Test
    public void deleteByIdTest() {
        userService.delete(USER_ID);
        verify(userDao, times(1)).delete(Matchers.anyLong());
    }

    @Test
    public void getByIdTest() {
        when(userDao.get(USER_ID)).thenReturn(getUser());
        User user = userService.get(USER_ID);
        Assert.assertEquals(user, getUser(), "Users aren't equals");
        verify(userDao, times(1)).get(Matchers.anyLong());
    }

    @Test
    public void getAllTest() {
        List<User> expectedUserList = new ArrayList<User>();
        expectedUserList.add(getUser());
        when(userDao.getAll()).thenReturn(expectedUserList);
        List<User> actualUserList = userService.getAll();
        Assert.assertEquals(actualUserList, expectedUserList, "User lists aren't equals");
        verify(userDao, times(1)).getAll();
    }

    private User getUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        user.setEmail(EMAIL);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);

        return user;
    }
}

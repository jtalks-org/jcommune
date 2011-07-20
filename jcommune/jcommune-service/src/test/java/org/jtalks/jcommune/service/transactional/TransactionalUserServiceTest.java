package org.jtalks.jcommune.service.transactional;

import org.joda.time.DateTime;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.DuplicateEmailException;
import org.jtalks.jcommune.service.exceptions.DuplicateException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.exceptions.WrongPasswordException;
import org.jtalks.jcommune.service.security.SecurityConstants;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * @author Kirill Afonin
 * @author Osadchuck Eugeny
 */
public class TransactionalUserServiceTest {
    private static final String USERNAME = "username";
    private static final String ENCODED_USERNAME = "encodedUsername";
    private static final String FIRST_USERNAME = "first name";
    private static final String LAST_USERNAME = "last name";
    private static final String NEW_EMAIL = "new_username@mail.com";
    private static final String EMAIL = "username@mail.com";
    private static final String PASSWORD = "password";
    private static final String WRONG_PASSWORD = "abracodabra";
    private static final String NEW_PASSWORD = "newPassword";
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
        User expectedUser = getUser();
        when(userDao.getByUsername(USERNAME)).thenReturn(expectedUser);

        User result = userService.getByUsername(USERNAME);

        assertEquals(result, expectedUser, "Username not equals");
        verify(userDao).getByUsername(USERNAME);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testGetByUsernameNotFound() throws Exception {
        when(userDao.getByUsername(USERNAME)).thenReturn(null);

        userService.getByUsername(USERNAME);
    }

    @Test
    public void testGetByEncodedUsername() throws Exception {
        User user = getUser();
        when(userDao.getByEncodedUsername(ENCODED_USERNAME)).thenReturn(user);

        User actualUser = userService.getByEncodedUsername(ENCODED_USERNAME);

        assertEquals(actualUser, user, "Users are not equal");
        verify(userDao).getByEncodedUsername(anyString());
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testGetByEncodedUsernamenotFound() throws Exception {
        when(userDao.getByEncodedUsername(ENCODED_USERNAME)).thenReturn(null);

        User actualUser = userService.getByEncodedUsername(ENCODED_USERNAME);

        verify(userDao).getByEncodedUsername(anyString());
    }

    @Test
    public void testRegisterUser() throws Exception {
        User user = getUser();
        when(userDao.isUserWithEmailExist(EMAIL)).thenReturn(false);
        when(userDao.isUserWithUsernameExist(USERNAME)).thenReturn(false);

        User registeredUser = userService.registerUser(user);

        assertEquals(registeredUser.getUsername(), USERNAME);
        assertEquals(registeredUser.getEmail(), EMAIL);
        assertEquals(registeredUser.getPassword(), PASSWORD);
        verify(userDao).isUserWithEmailExist(EMAIL);
        verify(userDao).isUserWithUsernameExist(USERNAME);
        verify(userDao).saveOrUpdate(user);
    }

    @Test(expectedExceptions = {DuplicateException.class})
    public void testRegisterUserUsernameExist() throws Exception {
        User user = getUser();
        when(userDao.isUserWithEmailExist(EMAIL)).thenReturn(false);
        when(userDao.isUserWithUsernameExist(USERNAME)).thenReturn(true);

        userService.registerUser(user);
    }

    @Test(expectedExceptions = {DuplicateException.class})
    public void testRegisterUserEmailExist() throws Exception {
        User user = getUser();
        when(userDao.isUserWithEmailExist(EMAIL)).thenReturn(true);
        when(userDao.isUserWithUsernameExist(USERNAME)).thenReturn(false);

        userService.registerUser(user);
    }

    @Test(expectedExceptions = {DuplicateException.class})
    public void testRegisterUserBothExist() throws Exception {
        User user = getUser();
        when(userDao.isUserWithEmailExist(EMAIL)).thenReturn(true);
        when(userDao.isUserWithUsernameExist(USERNAME)).thenReturn(true);

        userService.registerUser(user);
    }

    @Test(expectedExceptions = {DuplicateException.class})
    public void testRegisterUserAnonymous() throws Exception {
        User user = getUser(SecurityConstants.ANONYMOUS_USERNAME);

        userService.registerUser(user);
    }

    @Test
    public void testEditUserProfile() throws Exception {
        User user = getUser();
        User editedUsed = getEditedUser(true);

        when(userDao.getByUsername(anyString())).thenReturn(user);
        when(userDao.isUserWithEmailExist(anyString())).thenReturn(false);

        userService.editUserProfile(editedUsed, USERNAME, PASSWORD, NEW_PASSWORD);

        verify(userDao).getByUsername(anyString());
        verify(userDao).saveOrUpdate(any(User.class));

        assertEquals(user.getEmail(), editedUsed.getEmail(), "Email was not changed");
        assertEquals(user.getFirstName(), editedUsed.getFirstName(), "first name was not changed");
        assertEquals(user.getLastName(), editedUsed.getLastName(), "last name was not changed");
        assertEquals(user.getPassword(), NEW_PASSWORD, "new password was not accepted");
    }

    @Test(expectedExceptions = WrongPasswordException.class)
    public void testEditUserProfileWrongPassword() throws Exception {
        User user = getUser();
        User editedUsed = getEditedUser(true);

        when(userDao.getByUsername(anyString())).thenReturn(user);

        userService.editUserProfile(editedUsed, USERNAME, WRONG_PASSWORD, NEW_PASSWORD);

        verify(userDao).getByUsername(anyString());
        verify(userDao, times(0)).isUserWithEmailExist(anyString());
        verify(userDao, times(0)).saveOrUpdate(any(User.class));
    }

    @Test(expectedExceptions = WrongPasswordException.class)
    public void testEditUserProfilecurrentPasswordNull() throws Exception {
        User user = getUser();
        User editedUsed = getEditedUser(true);

        when(userDao.getByUsername(anyString())).thenReturn(user);

        userService.editUserProfile(editedUsed, USERNAME, null, NEW_PASSWORD);

        verify(userDao).getByUsername(anyString());
        verify(userDao, times(0)).isUserWithEmailExist(anyString());
        verify(userDao, times(0)).saveOrUpdate(any(User.class));
    }

    @Test(expectedExceptions = DuplicateEmailException.class)
    public void testEditUserProfileDublicateEmail() throws Exception {
        User user = getUser();
        User editedUsed = getEditedUser(true);

        when(userDao.getByUsername(anyString())).thenReturn(user);
        when(userDao.isUserWithEmailExist(anyString())).thenReturn(true);

        userService.editUserProfile(editedUsed, USERNAME, null, null);

        verify(userDao).getByUsername(anyString());
        verify(userDao).isUserWithEmailExist(anyString());
        verify(userDao, times(0)).saveOrUpdate(any(User.class));
    }


    /**
     * @param username username
     * @return create and return {@link User} with default username, encodedUsername,
     *         first name, last name,  email and password
     */
    private User getUser(String username) {
        User user = new User(username, EMAIL, PASSWORD);
        user.setFirstName(FIRST_USERNAME);
        user.setLastName(LAST_USERNAME);
        return user;
    }

    private User getUser() {
        return getUser(USERNAME);
    }


    /**
     * @param isNewEmail - if true NEW_EMAIL will be used, if false EMAIL will be used as email
     * @return create and return {@link User} with edit (new) emw_email,
     *         new first name, new last name and new_password
     */
    private User getEditedUser(boolean isNewEmail) {
        User user = new User(USERNAME, isNewEmail ? NEW_EMAIL : EMAIL, PASSWORD);
        user.setFirstName(FIRST_USERNAME);
        user.setLastName(LAST_USERNAME);
        return user;
    }

    @Test
    public void testGet() throws NotFoundException {
        User expectedUser = new User();
        when(userDao.get(USER_ID)).thenReturn(expectedUser);
        when(userDao.isExist(USER_ID)).thenReturn(true);

        User user = userService.get(USER_ID);

        assertEquals(user, expectedUser, "Users aren't equals");
        verify(userDao).isExist(USER_ID);
        verify(userDao).get(USER_ID);
    }

    @Test
    public void testUpdateLastLoginTime() throws Exception {
        User user = new User();
        DateTime dateTimeBefore = new DateTime();
        Thread.sleep(1000);

        userService.updateLastLoginTime(user);

        DateTime dateTimeAfter = user.getLastLogin();
        assertEquals(dateTimeAfter.compareTo(dateTimeBefore), 1, "last login time lesser than before test");
        verify(userDao).saveOrUpdate(user);
    }
}

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
package org.jtalks.jcommune.service.transactional;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Language;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.dto.UserInfoContainer;
import org.jtalks.jcommune.service.exceptions.DuplicateEmailException;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.exceptions.WrongPasswordException;
import org.jtalks.jcommune.service.nontransactional.AvatarService;
import org.jtalks.jcommune.service.nontransactional.Base64Wrapper;
import org.jtalks.jcommune.service.nontransactional.ImageUtils;
import org.jtalks.jcommune.service.nontransactional.MailService;
import org.jtalks.jcommune.service.nontransactional.SecurityService;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;

/**
 * @author Kirill Afonin
 * @author Osadchuck Eugeny
 * @author Evgeniy Naumenko
 */
public class TransactionalUserServiceTest {
    private static final String USERNAME = "username";
    private static final String ENCODED_USERNAME = "encodedUsername";
    private static final String FIRST_NAME = "first name";
    private static final String LAST_NAME = "last name";
    private static final String NEW_EMAIL = "new_username@mail.com";
    private static final String EMAIL = "username@mail.com";
    private static final String PASSWORD = "password";
    private static final String SIGNATURE = "signature";
    private static final String NEW_PASSWORD = "newPassword";
    private static final Language LANGUAGE = Language.ENGLISH;
    private static final int PAGE_SIZE = 50;
    private static final String LOCATION = "location";
    private byte[] avatar = new byte[10];
    private static final Long USER_ID = 999L;
    private static final long MAX_REGISTRATION_TIMEOUT = 1000L;

    private UserService userService;
    @Mock
    private UserDao userDao;
    @Mock
    private SecurityService securityService;
    @Mock
    private MailService mailService;
    @Mock
    private AvatarService avatarService;
    @Mock
    private Base64Wrapper base64Wrapper;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        userService = new TransactionalUserService(userDao, securityService, mailService, base64Wrapper, avatarService);
    }

    @Test
    public void testGetByUsername() throws Exception {
        JCUser expectedUser = getUser(USERNAME);
        when(userDao.getByUsername(USERNAME)).thenReturn(expectedUser);

        JCUser result = userService.getByUsername(USERNAME);

        assertEquals(result, expectedUser, "Username not equals");
        verify(userDao).getByUsername(USERNAME);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testGetByUsernameNotFound() throws Exception {
        when(userDao.getByUsername(USERNAME)).thenReturn(null);

        userService.getByUsername(USERNAME);
    }


    @Test
    public void testRegisterUser() throws Exception {
        JCUser user = getUser(USERNAME);
        when(userDao.getByEmail(EMAIL)).thenReturn(null);

        JCUser registeredUser = userService.registerUser(user);
        DateTime now = new DateTime();

        assertEquals(registeredUser.getUsername(), USERNAME);
        assertEquals(registeredUser.getEmail(), EMAIL);
        assertEquals(registeredUser.getPassword(), PASSWORD);
        assertTrue(new Interval(registeredUser.getRegistrationDate(), now)
                .toDuration().getMillis() <= MAX_REGISTRATION_TIMEOUT);
        verify(userDao).saveOrUpdate(user);
    }


    @Test
    public void testEditUserProfile() throws Exception {
        JCUser user = getUser(USERNAME);
        when(securityService.getCurrentUser()).thenReturn(user);
        when(userDao.getByEmail(EMAIL)).thenReturn(null);

        String newAvatar = new String(new byte[12]);

        JCUser editedUser = userService.editUserProfile(new UserInfoContainer(FIRST_NAME, LAST_NAME, EMAIL,
                PASSWORD, NEW_PASSWORD, SIGNATURE, newAvatar, LANGUAGE, PAGE_SIZE, LOCATION));

        verify(securityService).getCurrentUser();
        verify(userDao).saveOrUpdate(user);
        assertUserUpdated(editedUser);
        assertEquals(editedUser.getLanguage(), LANGUAGE, "language was not changed");
    }

    @Test
    public void testEditUserProfileSameEmail() throws Exception {
        JCUser user = getUser(USERNAME);
        when(securityService.getCurrentUser()).thenReturn(user);
        when(userDao.getByEmail(EMAIL)).thenReturn(null);

        String newAvatar = new String(new byte[0]);

        JCUser editedUser = userService.editUserProfile(new UserInfoContainer(FIRST_NAME, LAST_NAME, EMAIL,
                PASSWORD, NEW_PASSWORD, SIGNATURE, newAvatar, LANGUAGE, PAGE_SIZE, LOCATION));

        verify(securityService).getCurrentUser();
        verify(userDao).saveOrUpdate(user);
        assertEquals(editedUser.getEmail(), EMAIL, "Email was changed");
    }

    @Test
    public void testEditUserProfileNullAvatar() throws Exception {
        JCUser user = getUser(USERNAME);
        when(securityService.getCurrentUser()).thenReturn(user);
        when(userDao.getByEmail(EMAIL)).thenReturn(null);

        JCUser editedUser = userService.editUserProfile(new UserInfoContainer(FIRST_NAME, LAST_NAME, EMAIL,
                PASSWORD, NEW_PASSWORD, SIGNATURE, null, LANGUAGE, PAGE_SIZE, LOCATION));

        verify(securityService).getCurrentUser();
        verify(userDao).saveOrUpdate(user);
        assertUserUpdated(editedUser);
        assertEquals(editedUser.getAvatar(), avatar, "avatar was changed");
    }

    @Test
    public void testEditUserProfileEmptyAvatar() throws Exception {
        JCUser user = getUser(USERNAME);
        when(securityService.getCurrentUser()).thenReturn(user);
        when(userDao.getByEmail(EMAIL)).thenReturn(null);

        String newAvatar = new String(new byte[0]);

        JCUser editedUser = userService.editUserProfile(new UserInfoContainer(FIRST_NAME, LAST_NAME, EMAIL,
                PASSWORD, NEW_PASSWORD, SIGNATURE, newAvatar, LANGUAGE, PAGE_SIZE, LOCATION));

        verify(securityService).getCurrentUser();
        verify(userDao).saveOrUpdate(user);
        assertUserUpdated(editedUser);
        assertEquals(editedUser.getAvatar(), avatar, "avatar was changed");
    }

    private void assertUserUpdated(JCUser user) {
        assertEquals(user.getEmail(), EMAIL, "Email was not changed");
        assertEquals(user.getSignature(), SIGNATURE, "Signature was not changed");
        assertEquals(user.getFirstName(), FIRST_NAME, "first name was not changed");
        assertEquals(user.getLastName(), LAST_NAME, "last name was not changed");
        assertEquals(user.getPassword(), NEW_PASSWORD, "new password was not accepted");
    }

    @Test(expectedExceptions = WrongPasswordException.class)
    public void testEditUserProfileWrongPassword() throws Exception {
        JCUser user = getUser(USERNAME);
        when(securityService.getCurrentUser()).thenReturn(user);

        userService.editUserProfile(new UserInfoContainer(FIRST_NAME, LAST_NAME, EMAIL,
                "abracodabra", NEW_PASSWORD, SIGNATURE, null, LANGUAGE, PAGE_SIZE, LOCATION));

        verify(securityService).getCurrentUser();
        verify(userDao, never()).getByEmail(anyString());
        verify(userDao, never()).saveOrUpdate(any(JCUser.class));
    }

    @Test(expectedExceptions = WrongPasswordException.class)
    public void testEditUserProfileCurrentPasswordNull() throws Exception {
        JCUser user = getUser(USERNAME);
        when(securityService.getCurrentUser()).thenReturn(user);

        userService.editUserProfile(new UserInfoContainer(FIRST_NAME, LAST_NAME, EMAIL,
                null, NEW_PASSWORD, SIGNATURE, null, LANGUAGE, PAGE_SIZE, LOCATION));

        verify(securityService).getCurrentUser();
        verify(userDao, never()).getByEmail(anyString());
        verify(userDao, never()).saveOrUpdate(any(JCUser.class));
    }

    @Test(expectedExceptions = DuplicateEmailException.class)
    public void testEditUserProfileDuplicateEmail() throws Exception {
        JCUser user = getUser(USERNAME);
        when(securityService.getCurrentUser()).thenReturn(user);
        when(userDao.getByEmail(NEW_EMAIL)).thenReturn(user);

        userService.editUserProfile(new UserInfoContainer(FIRST_NAME, LAST_NAME, NEW_EMAIL,
                null, null, SIGNATURE, null, LANGUAGE, PAGE_SIZE, LOCATION));

        verify(securityService).getCurrentUser();
        verify(userDao).getByEmail(NEW_EMAIL);
        verify(userDao, never()).saveOrUpdate(any(JCUser.class));
    }

    @Test
    public void testGet() throws NotFoundException {
        JCUser expectedUser = new JCUser(USERNAME, EMAIL, PASSWORD);
        when(userDao.get(USER_ID)).thenReturn(expectedUser);
        when(userDao.isExist(USER_ID)).thenReturn(true);

        JCUser user = userService.get(USER_ID);

        assertEquals(user, expectedUser, "Users aren't equals");
        verify(userDao).isExist(USER_ID);
        verify(userDao).get(USER_ID);
    }

    @Test
    public void testUpdateLastLoginTime() throws Exception {
        JCUser user = new JCUser(USERNAME, EMAIL, PASSWORD);
        DateTime dateTimeBefore = new DateTime();
        Thread.sleep(25);

        userService.updateLastLoginTime(user);

        DateTime dateTimeAfter = user.getLastLogin();
        assertEquals(dateTimeAfter.compareTo(dateTimeBefore), 1, "last login time lesser than before test");
        verify(userDao).saveOrUpdate(user);
    }

    @Test
    public void testRestorePassword() throws NotFoundException, MailingFailedException {
        JCUser user = new JCUser(USERNAME, EMAIL, PASSWORD);
        when(userDao.getByEmail(EMAIL)).thenReturn(user);
        when(userDao.getByEmail(EMAIL)).thenReturn(user);

        userService.restorePassword(EMAIL);

        verify(mailService).sendPasswordRecoveryMail(eq(USERNAME), eq(EMAIL), matches("^[a-zA-Z0-9]*$"));
        ArgumentCaptor<JCUser> captor = ArgumentCaptor.forClass(JCUser.class);
        verify(userDao).update(captor.capture());
        assertEquals(captor.getValue().getUsername(), USERNAME);
        assertEquals(captor.getValue().getEmail(), EMAIL);
        assertFalse(PASSWORD.equals(captor.getValue().getPassword()));
    }

    @Test(expectedExceptions = MailingFailedException.class)
    public void testRestorePasswordFail() throws NotFoundException, MailingFailedException {
        JCUser user = new JCUser(USERNAME, EMAIL, PASSWORD);
        Exception fail = new MailingFailedException("", new RuntimeException());
        doThrow(fail).when(mailService).sendPasswordRecoveryMail(anyString(), anyString(), anyString());
        when(userDao.getByEmail(EMAIL)).thenReturn(user);

        try {
            userService.restorePassword(EMAIL);
        } finally {
            // ensure db modification haven't been done if mailing failed
            verify(userDao, never()).update(Matchers.<JCUser>any());
        }
    }

    @Test
    public void activateAccountTest() throws NotFoundException {
        JCUser user = new JCUser(USERNAME, EMAIL, PASSWORD);
        byte[] bytes = org.apache.commons.codec.binary.StringUtils.getBytesUtf8(USERNAME);
        when(base64Wrapper.decodeB64Bytes(USERNAME)).thenReturn(bytes);
        when(userDao.getByUsername(USERNAME)).thenReturn(user);

        userService.activateAccount(USERNAME);

        assertTrue(user.isEnabled());
        verify(userDao).getByUsername(USERNAME);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void activateNotFoundAccountTest() throws NotFoundException {
        JCUser user = new JCUser(USERNAME, EMAIL, PASSWORD);
        byte[] bytes = org.apache.commons.codec.binary.StringUtils.getBytesUtf8(USERNAME);
        when(base64Wrapper.decodeB64Bytes(USERNAME)).thenReturn(bytes);
        when(userDao.getByUsername(USERNAME)).thenReturn(null);

        userService.activateAccount(USERNAME);
    }

    /**
     * @param username username
     * @return create and return {@link org.jtalks.jcommune.model.entity.JCUser} with default username, encodedUsername,
     *         first name, last name,  email and password
     */
    private JCUser getUser(String username) {
        JCUser user = new JCUser(username, EMAIL, PASSWORD);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setAvatar(avatar);
        return user;
    }
}

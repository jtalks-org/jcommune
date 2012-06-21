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

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.jtalks.common.model.dao.GroupDao;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.User;
import org.jtalks.common.model.permissions.ProfilePermission;
import org.jtalks.common.security.SecurityService;
import org.jtalks.common.security.acl.builders.CompoundAclBuilder;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Language;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.dto.UserInfoContainer;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.AvatarService;
import org.jtalks.jcommune.service.nontransactional.Base64Wrapper;
import org.jtalks.jcommune.service.nontransactional.EncryptionService;
import org.jtalks.jcommune.service.nontransactional.MailService;
import org.jtalks.jcommune.service.security.AdministrationGroup;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.jtalks.jcommune.service.TestUtils.mockAclBuilder;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;

/**
 * @author Kirill Afonin
 * @author Osadchuck Eugeny
 * @author Evgeniy Naumenko
 * @author Anuar Nurmakanov
 */
public class TransactionalUserServiceTest {
    private static final String USERNAME = "username";
    private static final String FIRST_NAME = "first name";
    private static final String LAST_NAME = "last name";
    private static final String EMAIL = "username@mail.com";
    private static final String PASSWORD = "password";
    //if you change the PASSWORD, regenerate md5 hash
    private static final String PASSWORD_MD5_HASH = "5f4dcc3b5aa765d61d8327deb882cf99";
    private static final String SIGNATURE = "signature";
    private static final String NEW_PASSWORD = "newPassword";
    //if you change the NEW_PASSWORD, regenerate md5 hash
    private static final String NEW_PASSWORD_MD5_HASH = "14a88b9d2f52c55b5fbcf9c5d9c11875";
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
    private GroupDao groupDao;
    @Mock
    private SecurityService securityService;
    @Mock
    private MailService mailService;
    @Mock
    private AvatarService avatarService;
    @Mock
    private Base64Wrapper base64Wrapper;
    @Mock
    private EncryptionService encryptionService;
    @Mock
    private CompoundAclBuilder<User> aclBuilder;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        when(encryptionService.encryptPassword(PASSWORD))
                .thenReturn(PASSWORD_MD5_HASH);
        aclBuilder = mockAclBuilder();
        when(securityService.<User>createAclBuilder()).thenReturn(aclBuilder);
        userService = new TransactionalUserService(
                userDao,
                groupDao,
                securityService,
                mailService,
                base64Wrapper,
                avatarService,
                encryptionService);
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
        Group group = new Group();
        when(groupDao.get(AdministrationGroup.USER.getId())).thenReturn(group);

        JCUser registeredUser = userService.registerUser(user);
        DateTime now = new DateTime();

        assertEquals(registeredUser.getUsername(), USERNAME);
        assertEquals(registeredUser.getEmail(), EMAIL);
        assertEquals(registeredUser.getPassword(), PASSWORD_MD5_HASH);
        assertTrue(new Interval(registeredUser.getRegistrationDate(), now)
                .toDuration().getMillis() <= MAX_REGISTRATION_TIMEOUT);
        verify(userDao).saveOrUpdate(user);
        verify(groupDao).update(group);
        verify(aclBuilder).grant(ProfilePermission.EDIT_PROFILE);
        verify(aclBuilder).grant(ProfilePermission.SEND_PRIVATE_MESSAGES);
    }


    @Test
    public void testEditUserProfile() throws Exception {
        JCUser user = getUser(USERNAME);
        when(securityService.getCurrentUser()).thenReturn(user);
        when(userDao.getByEmail(EMAIL)).thenReturn(null);
        when(encryptionService.encryptPassword(NEW_PASSWORD))
                .thenReturn(NEW_PASSWORD_MD5_HASH);

        String newAvatar = new String(new byte[12]);

        JCUser editedUser = userService.editUserProfile(new UserInfoContainer(FIRST_NAME, LAST_NAME, EMAIL,
                PASSWORD, NEW_PASSWORD, SIGNATURE, newAvatar, LANGUAGE, PAGE_SIZE, LOCATION));

        verify(securityService).getCurrentUser();
        verify(userDao).saveOrUpdate(user);
        assertUserUpdated(editedUser);
        assertEquals(editedUser.getLanguage(), LANGUAGE, "language was not changed");
    }

    @Test
    public void testEditUserProfileNewPasswordNull() {
        JCUser user = getUser(USERNAME);
        when(securityService.getCurrentUser()).thenReturn(user);
        when(encryptionService.encryptPassword(null)).thenReturn(null);

        String newAvatar = new String(new byte[12]);
        String newPassword = null;
        UserInfoContainer userInfo = new UserInfoContainer(FIRST_NAME, LAST_NAME, EMAIL,
                PASSWORD, newPassword, SIGNATURE, newAvatar, LANGUAGE, PAGE_SIZE, LOCATION);
        JCUser editedUser = userService.editUserProfile(userInfo);
        assertEquals(editedUser.getPassword(), user.getPassword());
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

    private void assertUserUpdated(JCUser user) {
        assertEquals(user.getEmail(), EMAIL, "Email was not changed");
        assertEquals(user.getSignature(), SIGNATURE, "Signature was not changed");
        assertEquals(user.getFirstName(), FIRST_NAME, "first name was not changed");
        assertEquals(user.getLastName(), LAST_NAME, "last name was not changed");
        assertEquals(user.getPassword(), NEW_PASSWORD_MD5_HASH, "new password was not accepted");
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

        verify(mailService).sendPasswordRecoveryMail(eq(user), matches("^[a-zA-Z0-9]*$"));
        ArgumentCaptor<JCUser> captor = ArgumentCaptor.forClass(JCUser.class);
        verify(userDao).update(captor.capture());
        assertEquals(captor.getValue().getUsername(), USERNAME);
        assertEquals(captor.getValue().getEmail(), EMAIL);
        assertFalse(PASSWORD.equals(captor.getValue().getPassword()));
    }

    @Test(expectedExceptions = MailingFailedException.class)
    public void testRestorePasswordFail() throws NotFoundException, MailingFailedException {
        JCUser user = new JCUser(USERNAME, EMAIL, PASSWORD);
        Exception fail = new MailingFailedException(new RuntimeException());
        doThrow(fail).when(mailService).sendPasswordRecoveryMail(eq(user), anyString());
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
        when(userDao.getByUuid(user.getUuid())).thenReturn(user);

        userService.activateAccount(user.getUuid());

        assertTrue(user.isEnabled());
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void activateNotFoundAccountTest() throws NotFoundException {
        when(userDao.getByUsername(USERNAME)).thenReturn(null);

        userService.activateAccount(USERNAME);
    }

    @Test
    public void testNonActivatedAccountExpiration() throws NotFoundException {
        JCUser user1 = new JCUser(USERNAME, EMAIL, PASSWORD);
        user1.setRegistrationDate(new DateTime());
        JCUser user2 = new JCUser(USERNAME, EMAIL, PASSWORD);
        user2.setRegistrationDate(new DateTime().minusHours(25));
        JCUser user3 = new JCUser(USERNAME, EMAIL, PASSWORD);
        user3.setRegistrationDate(new DateTime().minusHours(50));

        List<JCUser> users = new ArrayList<JCUser>();
        Collections.addAll(users, user1, user2, user3);

        when(userDao.getNonActivatedUsers()).thenReturn(users);

        userService.deleteUnactivatedAccountsByTimer();

        verify(userDao).delete(user2);
        verify(userDao).delete(user3);
        verify(userDao, never()).delete(user1);
    }

    /**
     * @param username username
     * @return create and return {@link JCUser} with default username, encodedUsername,
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
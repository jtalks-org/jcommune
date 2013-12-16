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

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.jtalks.common.model.dao.GroupDao;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.User;
import org.jtalks.common.security.SecurityService;
import org.jtalks.common.security.acl.builders.CompoundAclBuilder;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.service.Authenticator;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.dto.UserInfoContainer;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.Base64Wrapper;
import org.jtalks.jcommune.service.nontransactional.EncryptionService;
import org.jtalks.jcommune.service.nontransactional.MailService;
import org.jtalks.jcommune.service.nontransactional.MentionedUsers;
import org.jtalks.jcommune.service.security.AdministrationGroup;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.jtalks.jcommune.service.TestUtils.mockAclBuilder;
import org.mockito.ArgumentMatcher;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.matches;
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
    private static final long USER_ID = 999L;

    private static final String MENTIONING_TEMPLATE = "This post contains not notified [user]%s[/user] mentioning " +
            "and notified [user notified=true]%s[/user] mentioning";
    private static final String MENTIONING_MESSAGE_WHEN_USER_NOT_FOUND = "This post contains not notified %s mentioning " +
            "and notified %s mentioning";


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
    private Base64Wrapper base64Wrapper;
    @Mock
    private EncryptionService encryptionService;
    @Mock
    private PostDao postDao;
    @Mock
    private Authenticator authenticator;


    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        when(encryptionService.encryptPassword(PASSWORD))
                .thenReturn(PASSWORD_MD5_HASH);
        CompoundAclBuilder<User> aclBuilder = mockAclBuilder();
        when(securityService.<User>createAclBuilder()).thenReturn(aclBuilder);
        userService = new TransactionalUserService(
                userDao,
                groupDao,
                securityService,
                mailService,
                base64Wrapper,
                encryptionService,
                postDao,
                authenticator);
    }

    @Test
    public void getByUsernameShouldReturnUserWithPassedNameFromRepository() throws NotFoundException {
        JCUser expectedUser = user(USERNAME);

        when(userDao.getByUsername(USERNAME)).thenReturn(expectedUser);

        JCUser result = userService.getByUsername(USERNAME);

        assertEquals(result, expectedUser, "Found incorrect user, cause usernames aren't equals");
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void getByUsernameSholdThrowErrorWhenUserWasNofFound() throws NotFoundException {
        when(userDao.getByUsername(USERNAME)).thenReturn(null);

        userService.getByUsername(USERNAME);
    }

    @Test
    public void editUserProfileShouldUpdateHimAndSaveInRepository() throws NotFoundException {
        JCUser user = user(USERNAME);
        when(securityService.getCurrentUserUsername()).thenReturn(StringUtils.EMPTY);
        when(userDao.getByUsername(anyString())).thenReturn(user);
        when(userDao.getByEmail(EMAIL)).thenReturn(null);
        when(encryptionService.encryptPassword(NEW_PASSWORD))
                .thenReturn(NEW_PASSWORD_MD5_HASH);
        when(userDao.isExist(USER_ID)).thenReturn(Boolean.TRUE);
        when(userDao.get(USER_ID)).thenReturn(user);
        String newAvatar = new String(new byte[12]);

        JCUser editedUser = userService.saveEditedUserProfile(USER_ID, new UserInfoContainer(FIRST_NAME, LAST_NAME, EMAIL,
                PASSWORD, NEW_PASSWORD, SIGNATURE, newAvatar, 50, true, true,
                "location", true));

        verify(userDao).saveOrUpdate(user);
        assertUserUpdated(editedUser);
        assertEquals(editedUser.getLanguage(), Language.ENGLISH, "language was not changed");
    }

    private void assertUserUpdated(JCUser user) {
        assertEquals(user.getEmail(), EMAIL, "Email was not changed");
        assertEquals(user.getSignature(), SIGNATURE, "Signature was not changed");
        assertEquals(user.getFirstName(), FIRST_NAME, "first name was not changed");
        assertEquals(user.getLastName(), LAST_NAME, "last name was not changed");
        assertEquals(user.getPassword(), NEW_PASSWORD_MD5_HASH, "new password was not accepted");
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void editUserProfileShouldNotUpdateHimAndSaveInRepositoryIfHeIsNotFound() throws NotFoundException {
        String newAvatar = new String(new byte[12]);
        when(userDao.isExist(USER_ID)).thenReturn(Boolean.FALSE);

        userService.saveEditedUserProfile(USER_ID, new UserInfoContainer(FIRST_NAME, LAST_NAME, EMAIL,
                PASSWORD, NEW_PASSWORD, SIGNATURE, newAvatar, 50, true, true,
                "location", true));
    }

    @Test
    public void editUserProfileShouldNotChangePasswordToNull() throws NotFoundException {
        JCUser user = user(USERNAME);
        when(securityService.getCurrentUserUsername()).thenReturn(StringUtils.EMPTY);
        when(userDao.getByUsername(anyString())).thenReturn(user);
        when(encryptionService.encryptPassword(null)).thenReturn(null);
        when(userDao.isExist(USER_ID)).thenReturn(Boolean.TRUE);
        when(userDao.get(USER_ID)).thenReturn(user);
        String newAvatar = new String(new byte[12]);
        String newPassword = null;
        UserInfoContainer userInfo = new UserInfoContainer(FIRST_NAME, LAST_NAME, EMAIL,
                PASSWORD, newPassword, SIGNATURE, newAvatar, 50, true, true,
                "location", true);

        JCUser editedUser = userService.saveEditedUserProfile(USER_ID, userInfo);

        assertEquals(editedUser.getPassword(), user.getPassword());
    }

    @Test
    public void testEditUserProfileSameEmail() throws Exception {
        JCUser user = user(USERNAME);
        when(securityService.getCurrentUserUsername()).thenReturn("");
        when(userDao.getByUsername(anyString())).thenReturn(user);
        when(userDao.getByEmail(EMAIL)).thenReturn(null);
        when(userDao.isExist(USER_ID)).thenReturn(Boolean.TRUE);
        when(userDao.get(USER_ID)).thenReturn(user);
        String newAvatar = new String(new byte[0]);

        JCUser editedUser = userService.saveEditedUserProfile(USER_ID, new UserInfoContainer(FIRST_NAME, LAST_NAME, EMAIL,
                PASSWORD, NEW_PASSWORD, SIGNATURE, newAvatar, 50, true, true,
                "location", true));

        verify(userDao).saveOrUpdate(user);
        assertEquals(editedUser.getEmail(), EMAIL, "Email was changed");
    }

    @Test
    public void getShouldReturnUserFromRepositoryWithoutModifications() throws NotFoundException {
        JCUser expectedUser = new JCUser(USERNAME, EMAIL, PASSWORD);
        when(userDao.get(USER_ID)).thenReturn(expectedUser);
        when(userDao.isExist(USER_ID)).thenReturn(true);

        JCUser user = userService.get(USER_ID);

        assertEquals(user, expectedUser, "Returned user is incorrect.");
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void getCommonUserByUsernameShouldNotFind() throws NotFoundException {
        userService.getCommonUserByUsername("username");
    }

    @Test
    public void getCommonUserByUsernameShouldReturnOne() throws NotFoundException {
        User expectedUser = new User("username", null, null, null);
        doReturn(expectedUser).when(userDao).getCommonUserByUsername("username");

        User actualUser = userService.getCommonUserByUsername("username");
        assertSame(expectedUser, actualUser);
    }

    @Test
    public void updateLastLoginTimeShouldSaveNewLoginTimeForUSer() throws Exception {
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
        verify(userDao).saveOrUpdate(captor.capture());
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
            verify(userDao, never()).saveOrUpdate(Matchers.<JCUser>any());
        }
    }

    @Test
    public void activateAccountShouldEnableUser() throws Exception {
        JCUser user = new JCUser(USERNAME, EMAIL, PASSWORD);
        when(userDao.getByUuid(user.getUuid())).thenReturn(user);
        when(groupDao.getGroupByName(AdministrationGroup.USER.getName())).thenReturn(new Group());

        userService.activateAccount(user.getUuid());
        assertTrue(user.isEnabled());
    }

    @Test
    public void activateAccountShouldAddUserToRegisteredUsersGroup() throws Exception {
        JCUser user = new JCUser(USERNAME, EMAIL, PASSWORD);
        when(userDao.getByUuid(user.getUuid())).thenReturn(user);
        Group registeredUsersGroup = new Group();
        when(groupDao.getGroupByName(AdministrationGroup.USER.getName())).thenReturn(registeredUsersGroup);

        userService.activateAccount(user.getUuid());
        assertTrue(user.getGroups().contains(registeredUsersGroup));
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testActivateNotFoundAccountTest() throws NotFoundException {
        when(userDao.getByUsername(USERNAME)).thenReturn(null);

        userService.activateAccount(USERNAME);
    }

    @Test
    public void testActivateAccountAlreadyEnabled() throws NotFoundException {
        JCUser user = new JCUser(USERNAME, EMAIL, PASSWORD);
        user.setEnabled(true);
        when(userDao.getByUuid(user.getUuid())).thenReturn(user);
        Group group = new Group();
        when(groupDao.getGroupByName(AdministrationGroup.USER.getName())).thenReturn(group);

        userService.activateAccount(user.getUuid());

        assertTrue(user.isEnabled());
        verify(groupDao, never()).saveOrUpdate(any(Group.class));
        assertFalse(group.getUsers().contains(user));
    }

    @Test
    public void testNonActivatedAccountExpiration() throws NotFoundException {
        JCUser user1 = new JCUser(USERNAME, EMAIL, PASSWORD);
        user1.setRegistrationDate(new DateTime());
        JCUser user2 = new JCUser(USERNAME, EMAIL, PASSWORD);
        user2.setRegistrationDate(new DateTime().minusHours(25));
        JCUser user3 = new JCUser(USERNAME, EMAIL, PASSWORD);
        user3.setRegistrationDate(new DateTime().minusHours(50));
        List<JCUser> users = asList(user1, user2, user3);
        when(userDao.getNonActivatedUsers()).thenReturn(users);

        userService.deleteUnactivatedAccountsByTimer();

        verify(userDao).delete(user2);
        verify(userDao).delete(user3);
        verify(userDao, never()).delete(user1);
    }

    @Test
    public void testGetCurrentUser() {
        JCUser expected = user(USERNAME);

        when(securityService.getCurrentUserUsername()).thenReturn(USERNAME);
        when(userDao.getByUsername(USERNAME)).thenReturn(expected);

        JCUser actual = userService.getCurrentUser();

        assertEquals(actual, expected);
    }

    @Test
    public void testGetCurrentUserForAnonymous() {
        when(securityService.getCurrentUserUsername()).thenReturn(null);

        JCUser user = userService.getCurrentUser();
        assertNotNull(user);
        assertTrue(user instanceof AnonymousUser);
    }

    @Test
    public void testLoginUserWithCorrectCredentialsShouldBeSuccessful()
            throws UnexpectedErrorException, NoConnectionException {
        HttpServletRequest httpRequest = new MockHttpServletRequest();
        HttpServletResponse httpResponse = new MockHttpServletResponse();
        when(authenticator.authenticate("username", "password", true, httpRequest, httpResponse))
                .thenReturn(true);

        boolean result = userService.loginUser("username", "password", true, httpRequest, httpResponse);

        assertTrue(result, "Login user with correct credentials should be successful.");
    }

    @Test
    public void testLoginUserWithBadCredentialsShouldFail()
            throws UnexpectedErrorException, NoConnectionException {
        HttpServletRequest httpRequest = new MockHttpServletRequest();
        HttpServletResponse httpResponse = new MockHttpServletResponse();
        when(authenticator.authenticate("", "password", true, httpRequest, httpResponse))
                .thenReturn(false);

        boolean result = userService.loginUser("", "password", true, httpRequest, httpResponse);

        assertFalse(result, "Login user with bad credentials should fail.");
    }

    @Test
    public void userShouldBeNotifiedWhenMentioned() {
        JCUser toBeNotified = user("to-be-notified");
        when(userDao.getByUsernames(asSet("to-be-notified"))).thenReturn(asList(toBeNotified));
        boundMockHttpRequestToThread("web-context-path");

        Post post = post(toBeNotified, "[user]to-be-notified[/user]");
        userService.notifyAndMarkNewlyMentionedUsers(post);

        verify(mailService).sendUserMentionedNotification(toBeNotified, post.getId());
    }

    @Test
    public void processShouldNotAttachProfileLinkToNotExistUsers() throws NotFoundException {
        String firstMentionedUserName = "Shogun";
        String secondMentionedUserName = "jk1";
        when(userDao.getByUsername(firstMentionedUserName)).thenReturn(null);
        when(userDao.getByUsername(secondMentionedUserName)).thenReturn(null);
        String notProcessedSource = format(MENTIONING_TEMPLATE, firstMentionedUserName, secondMentionedUserName);

        MentionedUsers mentionedUsers = mock(MentionedUsers.class);
        when(mentionedUsers.extractAllMentionedUsers(notProcessedSource))
                .thenReturn(asSet(firstMentionedUserName, secondMentionedUserName));

        String actualAfterProcess = userService.processUserBbCodesInPost(notProcessedSource);

        String msgWithNotFoundUsers = format(MENTIONING_MESSAGE_WHEN_USER_NOT_FOUND, firstMentionedUserName,
                secondMentionedUserName);

        assertEquals(actualAfterProcess, msgWithNotFoundUsers);
    }

    @Test
    public void testGetUsernames() {
        String usernamePattern = "Us";
        int usernameCount = 10;
        List<String> usernames = Lists.newArrayList("User1", "User2", "User3");
        when(userDao.getUsernames(usernamePattern, usernameCount)).thenReturn(usernames);

        assertEquals(userService.getUsernames(usernamePattern).size(), 3);
    }

    @Test
    public void testChangeLanguage() {
        final JCUser user = user(USERNAME);
        user.setLanguage(Language.ENGLISH);

        userService.changeLanguage(user, Language.RUSSIAN);
        
        verify(userDao).saveOrUpdate(argThat(new ArgumentMatcher<JCUser>() {
            @Override
            public boolean matches(Object argument) {
                JCUser argUser = (JCUser)argument;
                return argument == user && Language.RUSSIAN.equals(argUser.getLanguage());
            }
        }));
    }
    
    public static <T> Set<T> asSet(T... values) {
        return new HashSet<T>(asList(values));
    }

    private void boundMockHttpRequestToThread(String contextPath) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("testing.com");
        request.setServerPort(1234);
        request.setContextPath(contextPath);
        RequestContextHolder.setRequestAttributes(new ServletWebRequest(request));
    }

    private JCUser user(String username) {
        JCUser user = new JCUser(username, EMAIL, PASSWORD);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setAvatar(new byte[10]);
        user.setMentioningNotificationsEnabled(true);
        return user;
    }

    private Post post(JCUser toBeNotified, String postContent) {
        Post post = new Post(toBeNotified, postContent);
        post.setId(new Random(10000).nextLong());
        post.setTopic(new Topic());
        return post;
    }
}

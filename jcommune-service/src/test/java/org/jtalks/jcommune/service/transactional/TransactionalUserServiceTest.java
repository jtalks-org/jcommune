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
import org.jtalks.common.service.security.SecurityContextFacade;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.dto.LoginUserDto;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.plugin.api.exceptions.NoConnectionException;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.plugin.api.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.service.Authenticator;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.dto.UserInfoContainer;
import org.jtalks.jcommune.service.dto.UserNotificationsContainer;
import org.jtalks.jcommune.service.dto.UserSecurityContainer;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.nontransactional.Base64Wrapper;
import org.jtalks.jcommune.service.nontransactional.EncryptionService;
import org.jtalks.jcommune.service.nontransactional.MailService;
import org.jtalks.jcommune.service.nontransactional.MentionedUsers;
import org.jtalks.jcommune.service.util.AuthenticationStatus;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionContaining.hasItems;
import static org.jtalks.jcommune.service.TestUtils.mockAclBuilder;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.argThat;
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
    @Mock
    private SecurityContextFacade securityContextFacade;


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
                postDao, authenticator,
                securityContextFacade);
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
        when(encryptionService.encryptPassword(NEW_PASSWORD)).thenReturn(NEW_PASSWORD_MD5_HASH);
        when(userDao.isExist(USER_ID)).thenReturn(Boolean.TRUE);
        when(userDao.get(USER_ID)).thenReturn(user);
        String newAvatar = new String(new byte[12]);

        JCUser editedUser = userService.saveEditedUserProfile(USER_ID,
                new UserInfoContainer(FIRST_NAME, LAST_NAME, EMAIL, SIGNATURE, newAvatar, 50, "location"));

        verify(userDao).saveOrUpdate(user);
        assertUserProfileUpdated(editedUser);
    }

    private void assertUserProfileUpdated(JCUser user) {
        assertEquals(user.getEmail(), EMAIL, "Email was not changed");
        assertEquals(user.getSignature(), SIGNATURE, "Signature was not changed");
        assertEquals(user.getFirstName(), FIRST_NAME, "first name was not changed");
        assertEquals(user.getLastName(), LAST_NAME, "last name was not changed");
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void editUserProfileShouldNotUpdateHimAndSaveInRepositoryIfHeIsNotFound() throws NotFoundException {
        String newAvatar = new String(new byte[12]);
        when(userDao.isExist(USER_ID)).thenReturn(Boolean.FALSE);

        userService.saveEditedUserProfile(USER_ID,
                new UserInfoContainer(FIRST_NAME, LAST_NAME, EMAIL, SIGNATURE, newAvatar, 50, "location"));
    }

    @Test
    public void editUserProfileShouldNotUpdateOtherSettings() throws NotFoundException {
        JCUser user = user(USERNAME);
        when(securityService.getCurrentUserUsername()).thenReturn(StringUtils.EMPTY);
        when(userDao.getByUsername(anyString())).thenReturn(user);
        when(userDao.getByEmail(EMAIL)).thenReturn(null);
        when(encryptionService.encryptPassword(NEW_PASSWORD))
                .thenReturn(NEW_PASSWORD_MD5_HASH);
        when(userDao.isExist(USER_ID)).thenReturn(Boolean.TRUE);
        when(userDao.get(USER_ID)).thenReturn(user);
        String newAvatar = new String(new byte[12]);

        JCUser editedUser = userService.saveEditedUserProfile(USER_ID,
                new UserInfoContainer(FIRST_NAME, LAST_NAME, EMAIL, SIGNATURE, newAvatar, 50, "location"));

        verify(userDao).saveOrUpdate(user);

        assertUserSecurityAndNotificationsAreSame(user, editedUser);
        assertEquals(editedUser.getLanguage(), Language.ENGLISH, "language was changed");
    }

    private void assertUserSecurityAndNotificationsAreSame(JCUser user, JCUser editedUser) {
        assertEquals(editedUser.getPassword(), user.getPassword(), "User password was changed");
        assertEquals(editedUser.isMentioningNotificationsEnabled(),
                user.isMentioningNotificationsEnabled(), "User mentioning notifications was changed");
        assertEquals(editedUser.isSendPmNotification(),
                user.isSendPmNotification(), "Send pm notification was changed");
        assertEquals(editedUser.isAutosubscribe(), user.isAutosubscribe(), "Autosubscribe was changed");
    }

    @Test
    public void editUserProfileSecurityShouldUpdatePassword() throws NotFoundException {
        JCUser user = user(USERNAME);
        when(encryptionService.encryptPassword(NEW_PASSWORD)).thenReturn(NEW_PASSWORD_MD5_HASH);
        when(userDao.isExist(USER_ID)).thenReturn(Boolean.TRUE);
        when(userDao.get(USER_ID)).thenReturn(user);

        JCUser editedUser = userService.saveEditedUserSecurity(USER_ID,
                new UserSecurityContainer(PASSWORD, NEW_PASSWORD));

        verify(userDao).saveOrUpdate(user);
        assertEquals(editedUser.getPassword(), NEW_PASSWORD_MD5_HASH, "new password was not accepted");
    }

    @Test
    public void editUserProfileShouldNotChangePasswordToNull() throws NotFoundException {
        JCUser user = user(USERNAME);
        when(securityService.getCurrentUserUsername()).thenReturn(StringUtils.EMPTY);
        when(userDao.getByUsername(anyString())).thenReturn(user);
        when(encryptionService.encryptPassword(null)).thenReturn(null);
        when(userDao.isExist(USER_ID)).thenReturn(Boolean.TRUE);
        when(userDao.get(USER_ID)).thenReturn(user);
        JCUser editedUser = userService.saveEditedUserSecurity(USER_ID,
                new UserSecurityContainer(PASSWORD, null));

        assertEquals(editedUser.getPassword(), user.getPassword());
    }

    @Test
    public void editUserProfileSecurityShouldNotUpdateOtherSettings() throws NotFoundException {
        JCUser user = user(USERNAME);
        when(encryptionService.encryptPassword(NEW_PASSWORD)).thenReturn(NEW_PASSWORD_MD5_HASH);
        when(userDao.isExist(USER_ID)).thenReturn(Boolean.TRUE);
        when(userDao.get(USER_ID)).thenReturn(user);

        JCUser editedUser = userService.saveEditedUserSecurity(USER_ID,
                new UserSecurityContainer(PASSWORD, NEW_PASSWORD));

        verify(userDao).saveOrUpdate(user);

        assertUserProfileAndNotificationsAreSame(user, editedUser);
        assertEquals(editedUser.getLanguage(), Language.ENGLISH, "language was changed");
    }

    private void assertUserProfileAndNotificationsAreSame(JCUser user, JCUser editedUser) {
        assertEquals(editedUser.getEmail(), user.getEmail(), "Email was changed");
        assertEquals(editedUser.getSignature(), user.getSignature(), "Signature was changed");
        assertEquals(editedUser.getFirstName(), user.getFirstName(), "First name was changed");
        assertEquals(editedUser.getLastName(), user.getLastName(), "Last name was changed");
        assertEquals(editedUser.isMentioningNotificationsEnabled(),
                user.isMentioningNotificationsEnabled(), "User mentioning notifications was changed");
        assertEquals(editedUser.isSendPmNotification(),
                user.isSendPmNotification(), "Send pm notification was changed");
        assertEquals(editedUser.isAutosubscribe(), user.isAutosubscribe(), "Autosubscribe was changed");
    }

    @Test
    public void editUserProfileNotificationsShouldUpdateMentioningAndSendPmMessages() throws NotFoundException {
        JCUser user = user(USERNAME);
        when(encryptionService.encryptPassword(NEW_PASSWORD)).thenReturn(NEW_PASSWORD_MD5_HASH);
        when(userDao.isExist(USER_ID)).thenReturn(Boolean.TRUE);
        when(userDao.get(USER_ID)).thenReturn(user);

        JCUser editedUser = userService.saveEditedUserNotifications(USER_ID,
                new UserNotificationsContainer(true, false, false));

        verify(userDao).saveOrUpdate(user);

        assertEquals(editedUser.isAutosubscribe(), true, "Autosubscribe was not changed");
        assertEquals(editedUser.isSendPmNotification(), false, "Send pm notification was not changed");
        assertEquals(editedUser.isMentioningNotificationsEnabled(), false,
                "User mentioning notifications was not changed");
    }

    @Test
    public void editUserProfileNotificationsShouldNotUpdateOtherSettings() throws NotFoundException {
        JCUser user = user(USERNAME);
        when(encryptionService.encryptPassword(NEW_PASSWORD)).thenReturn(NEW_PASSWORD_MD5_HASH);
        when(userDao.isExist(USER_ID)).thenReturn(Boolean.TRUE);
        when(userDao.get(USER_ID)).thenReturn(user);

        JCUser editedUser = userService.saveEditedUserNotifications(USER_ID,
                new UserNotificationsContainer(true, false, false));

        verify(userDao).saveOrUpdate(user);

        assertUserProfileAndSecurityAreSame(user, editedUser);
        assertEquals(editedUser.getLanguage(), Language.ENGLISH, "language was changed");
    }

    private void assertUserProfileAndSecurityAreSame(JCUser user, JCUser editedUser) {
        assertEquals(editedUser.getEmail(), user.getEmail(), "Email was changed");
        assertEquals(editedUser.getSignature(), user.getSignature(), "Signature was changed");
        assertEquals(editedUser.getFirstName(), user.getFirstName(), "First name was changed");
        assertEquals(editedUser.getLastName(), user.getLastName(), "Last name was changed");
        assertEquals(editedUser.getPassword(), user.getPassword(), "User password was changed");
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

        JCUser editedUser = userService.saveEditedUserProfile(USER_ID,
                new UserInfoContainer(FIRST_NAME, LAST_NAME, EMAIL, SIGNATURE, newAvatar, 50, "location"));

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
    public void shouldReturnUserIfAuthenticated() {
        JCUser expected = user(USERNAME);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(expected, null));
        when(securityContextFacade.getContext()).thenReturn(SecurityContextHolder.getContext());
        JCUser actual = userService.getCurrentUser();
        assertEquals(actual, expected);
    }

    @Test
    public void shouldReturnAnonymousUserIfNotAuthenticated() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(null, null));
        when(securityContextFacade.getContext()).thenReturn(SecurityContextHolder.getContext());
        JCUser user = userService.getCurrentUser();
        assertNotNull(user);
        assertTrue(user instanceof AnonymousUser);
    }

    @Test
    public void testLoginUserWithCorrectCredentialsShouldBeSuccessful()
            throws UnexpectedErrorException, NoConnectionException {
        HttpServletRequest httpRequest = new MockHttpServletRequest();
        HttpServletResponse httpResponse = new MockHttpServletResponse();
        LoginUserDto loginUserDto = new LoginUserDto("username", "password", true, "192.168.1.1");
        when(authenticator.authenticate(loginUserDto, httpRequest, httpResponse))
                .thenReturn(AuthenticationStatus.AUTHENTICATED);

        AuthenticationStatus result = userService.loginUser(loginUserDto, httpRequest, httpResponse);

        assertEquals(result, AuthenticationStatus.AUTHENTICATED,
                "Login user with correct credentials should be successful.");
    }

    @Test
    public void testLoginUserWithBadCredentialsShouldFail()
            throws UnexpectedErrorException, NoConnectionException {
        HttpServletRequest httpRequest = new MockHttpServletRequest();
        HttpServletResponse httpResponse = new MockHttpServletResponse();
        LoginUserDto loginUserDto = new LoginUserDto("", "password", true, "192.168.1.1");
        when(authenticator.authenticate(loginUserDto, httpRequest, httpResponse))
                .thenReturn(AuthenticationStatus.AUTHENTICATION_FAIL);

        AuthenticationStatus result = userService.loginUser(loginUserDto, httpRequest, httpResponse);

        assertEquals(result, AuthenticationStatus.AUTHENTICATION_FAIL, "Login user with bad credentials should fail.");
    }

    @Test
    public void testLoginNotActivatedUserShouldFail() throws Exception {
        HttpServletRequest httpRequest = new MockHttpServletRequest();
        HttpServletResponse httpResponse = new MockHttpServletResponse();
        LoginUserDto loginUserDto = new LoginUserDto("username", "password", true, "192.168.1.1");
        when(authenticator.authenticate(loginUserDto, httpRequest, httpResponse))
                .thenReturn(AuthenticationStatus.NOT_ENABLED);

        AuthenticationStatus result = userService.loginUser(loginUserDto, httpRequest, httpResponse);

        assertEquals(result, AuthenticationStatus.NOT_ENABLED, "Login not activated user should fail.");
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
                JCUser argUser = (JCUser) argument;
                return argument == user && Language.RUSSIAN.equals(argUser.getLanguage());
            }
        }));
    }

    @Test
    public void testFindByUsernameOrEmail() {
        String searchKey = "key";
        List<JCUser> users = Lists.newArrayList(user("user1"), user("user2"), user("user3"));

        when(userDao.findByUsernameOrEmail(searchKey, TransactionalUserService.MAX_SEARCH_USER_COUNT)).thenReturn(users);

        List<JCUser> result = userService.findByUsernameOrEmail(1L, searchKey);

        assertEquals(result, users);
    }

    @Test
    public void testGetUserGroupIDs() throws NotFoundException {
        Long[] expectedGroupIDs = {4l, 5l, 6l};

        JCUser jcUser = createUserWithGroups(expectedGroupIDs);
        when(userDao.get(anyLong())).thenReturn(jcUser);

        assertThat(userService.getUserGroupIDs(0l, 1l), hasItems(expectedGroupIDs));
    }

    @Test
    public void testAddUserToGroup() throws NotFoundException {
        JCUser jcUser = createUserWithGroups(4l, 5l, 6l);
        Group group = new Group();

        when(userDao.get(anyLong())).thenReturn(jcUser);
        when(groupDao.get(anyLong())).thenReturn(group);

        userService.addUserToGroup(0l, 1l, 2l);

        assertThat(group.getUsers().contains(jcUser), is(true));
        assertThat(jcUser.getGroups().contains(group), is(true));
    }

    @Test
    public void testDeleteUserFromGroup() throws NotFoundException {
        long groupForDeleteID = 5l;

        JCUser jcUser = createUserWithGroups(4l, groupForDeleteID, 6l);
        Group group = new Group();
        group.setId(groupForDeleteID);

        when(userDao.get(anyLong())).thenReturn(jcUser);
        when(groupDao.get(anyLong())).thenReturn(group);

        userService.deleteUserFromGroup(0l, 1l, groupForDeleteID);

        assertThat(jcUser.getGroups().contains(group), is(false));
    }

    public static <T> Set<T> asSet(T... values) {
        return new HashSet<>(asList(values));
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
        user.setSendPmNotification(true);
        return user;
    }

    private Post post(JCUser toBeNotified, String postContent) {
        Post post = new Post(toBeNotified, postContent);
        post.setId(new Random(10000).nextLong());
        post.setTopic(new Topic());
        return post;
    }

    private JCUser createUserWithGroups(Long... expectedGroupIDs) {
        JCUser jcUser = new JCUser("user", "email@email.com", "pwd");
        for (int i = 0; i < expectedGroupIDs.length; i++) {
            Group group = new Group("group" + i);
            group.setId(expectedGroupIDs[i]);
            jcUser.getGroups().add(group);
        }
        return jcUser;
    }
}

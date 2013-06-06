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
package org.jtalks.jcommune.service.nontransactional;

import org.apache.commons.collections.CollectionUtils;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class MentionedUsersTest {
    private static final String MENTIONING_TEMPLATE = "This post contains not notified [user]%s[/user] mentioning " +
            "and notified [user notified=true]%s[/user] mentioning";
    private static final String MENTIONING_WITH_LINK_TO_PROFILE_TEMPALTE =
            "This post contains not notified [user=%s]%s[/user] mentioning and notified [user=%s]%s[/user] mentioning";

    @Mock
    private MailService mailService;
    @Mock
    private UserDao userDao;
    @Mock 
    private PostDao postDao;
 
    @BeforeMethod
    public void init() {
        initMocks(this);
    }
    
    @Test
    public void extractMentionedUserShouldReturnEmptyListWhenPassedTextDoesNotContainMentioning() {
        String textWithoutUserMentioning = "This text mustn't contain user mentioning. Be carefull.";

        MentionedUsers mentionedUsers = MentionedUsers.parse(textWithoutUserMentioning);

        Set<String> extractedUserNames = mentionedUsers.extractAllMentionedUsers(textWithoutUserMentioning);
        
        assertTrue(CollectionUtils.isEmpty(extractedUserNames), "Passed user should not contain any user mentioning.");
    }

    @Test
    public void extractMentionedUserShouldReturnAllMentionedUserInBBCodes() {
        String textWithUsersMentioning = "In this text we have 3 user mentioning: first [user]Shogun[/user]," +
        		"second [user notified=true]masyan[/user]," +
        		"third [user]jk1[/user]";

        MentionedUsers mentionedUsers = MentionedUsers.parse(textWithUsersMentioning);
        Set<String> extractedUserNames = mentionedUsers.extractAllMentionedUsers(textWithUsersMentioning);
        
        assertTrue(extractedUserNames.size() == 3, "Passed text should contain 3 user mentioning.");
        assertTrue(extractedUserNames.contains("Shogun"), "Shogun is mentioned, so he should be extracted.");
        assertTrue(extractedUserNames.contains("masyan"), "masyan is mentioned, so he should be extracted.");
        assertTrue(extractedUserNames.contains("jk1"), "jk1 is mentioned, so he should be extracted.");
    }

    @Test
    public void extractMentionedUserShouldReturnAllMentionedCyrillicUserInBBCodes() {
        String textWithUsersMentioning = "In this text we have 3 user mentioning: first [user]Иванов[/user]," +
                "second [user notified=true]Петров[/user]," +
                "third [user]Сидоров[/user]";

        MentionedUsers mentionedUsers = MentionedUsers.parse(textWithUsersMentioning);
        Set<String> extractedUserNames = mentionedUsers.extractAllMentionedUsers(textWithUsersMentioning);

        assertTrue(extractedUserNames.size() == 3, "Passed text should contain 3 user mentioning.");
        assertTrue(extractedUserNames.contains("Иванов"), "Иванов is mentioned, so he should be extracted.");
        assertTrue(extractedUserNames.contains("Петров"), "Петров is mentioned, so he should be extracted.");
        assertTrue(extractedUserNames.contains("Сидоров"), "Сидоров is mentioned, so he should be extracted.");
    }

    @Test
    public void extractMentionedUserShouldReturnAllMentionedScpecialCharactersUserInBBCodes() {
        String textWithUsersMentioning = "In this text we have 3 user mentioning: first [user]<yak[/user]," +
                "second [user notified=true]\\yak[/user]," +
                "third [user]]yak[/user]";

        MentionedUsers mentionedUsers = MentionedUsers.parse(textWithUsersMentioning);
        Set<String> extractedUserNames = mentionedUsers.extractAllMentionedUsers(textWithUsersMentioning);

        assertTrue(extractedUserNames.size() == 3, "Passed text should contain 3 user mentioning.");
        assertTrue(extractedUserNames.contains("<yak"), "<yak is mentioned, so he should be extracted.");
        assertTrue(extractedUserNames.contains("\\yak"), "\\yak is mentioned, so he should be extracted.");
        assertTrue(extractedUserNames.contains("]yak"), "]yak is mentioned, so he should be extracted.");
    }

    @Test
     public void extractMentionedUserShouldReturnAllMentionedUserWithSpacesInBBCodes() {
        String textWithUsersMentioning = "In this text we have 3 user mentioning: first [user]и в а н о в[/user]," +
                "second [user notified=true]\\y a k[/user]," +
                "third [user]y a k[/user]";

        MentionedUsers mentionedUsers = MentionedUsers.parse(textWithUsersMentioning);
        Set<String> extractedUserNames = mentionedUsers.extractAllMentionedUsers(textWithUsersMentioning);

        assertTrue(extractedUserNames.size() == 3, "Passed text should contain 3 user mentioning.");
        assertTrue(extractedUserNames.contains("и в а н о в"), "и в а н о в is mentioned, so he should be extracted.");
        assertTrue(extractedUserNames.contains("\\y a k"), "\\y a k is mentioned, so he should be extracted.");
        assertTrue(extractedUserNames.contains("y a k"), "y a k is mentioned, so he should be extracted.");
    }

    @Test
    public void extractMentionedUserShouldReturnAllMentionedEncodedUserInBBCodes() {
        String textWithUsersMentioning = "In this text we have 4 user mentioning: " +
                "first [user]@ywdffgg434y@yak[/user]," +
                "second [user notified=true]gertfgertgf@@@@@#4324234yak[/user]," +
                "third [user]14@123435vggv4fyak[/user]" +
                "forth [user]@w0956756wo@yak[/user]";

        MentionedUsers mentionedUsers = MentionedUsers.parse(textWithUsersMentioning);
        Set<String> extractedUserNames = mentionedUsers.extractAllMentionedUsers(textWithUsersMentioning);

        assertTrue(extractedUserNames.size() == 4, "Passed text should contain 4 user mentioning.");
        assertTrue(extractedUserNames.contains("[yak"), "[yak is mentioned, so he should be extracted.");
        assertTrue(extractedUserNames.contains("<yak"), "<yak is mentioned, so he should be extracted.");
        assertTrue(extractedUserNames.contains("\\yak"), "\\yak is mentioned, so he should be extracted.");
        assertTrue(extractedUserNames.contains("]yak"), "]yak is mentioned, so he should be extracted.");
    }

    @Test
    public void extractMentionedUserShouldReturnAllMentionedEncodedByEncodeURIUserInBBCodes() {
        String textWithUsersMentioning = "In this text we have 2 user mentioning: " +
                "first [user]%D0%B8%D0%B2%D0%B0%D0%BD%D0%BE%D0%B2[/user]" +
                "second [user]%5Cyak[/user]";

        MentionedUsers mentionedUsers = MentionedUsers.parse(textWithUsersMentioning);
        Set<String> extractedUserNames = mentionedUsers.extractAllMentionedUsers(textWithUsersMentioning);

        assertTrue(extractedUserNames.size() == 2, "Passed text should contain 2 user mentioning.");
        assertTrue(extractedUserNames.contains("иванов"), "иванов is mentioned, so he should be extracted.");
        assertTrue(extractedUserNames.contains("\\yak"), "\\yak is mentioned, so he should be extracted.");
    }
    
    @Test
    public void notifyNewlyhouldSendEmailForNewlyMentionedUsers() {
        String textWithUsersMentioning = "In this text we have 3 user mentioning: first [user]Shogun[/user]," +
                "second [user]jk1[/user], third [user]masyan[/user]";
        Post mentioningPost = getPost(25L, textWithUsersMentioning);
        List<JCUser> users = asList(
                getJCUser("Shogun", true), getJCUser("jk1", true), getJCUser("masyan", true));
        when(userDao.getByUsernames(asSet("Shogun", "jk1", "masyan")))
            .thenReturn(users);

        MentionedUsers mentionedUsers = MentionedUsers.parse(mentioningPost);
        List<JCUser> notifiedUsers = mentionedUsers.getNewUsersToNotify(userDao);
        assertEquals(notifiedUsers, users);
    }
    
    @Test
    public void notifyNewlyMentionedUsersShouldMarkBBCodesOfNotfiiedUsers() {
        Post mentioningPost = getPost(25L, "In this text we have user mentioning [user]Shogun[/user]");
        when(userDao.getByUsernames(asSet("Shogun")))
            .thenReturn(asList(getJCUser("Shogun", true)));

        MentionedUsers mentionedUsers = MentionedUsers.parse(mentioningPost);

        mentionedUsers.markUsersAsAlreadyNotified(postDao);
        
        assertEquals(mentioningPost.getPostContent(), "In this text we have user mentioning [user notified=true]Shogun[/user]",
                "After sending email [user][/user] tag shoud be changed to [user notified=true][/user]");
        verify(postDao).saveOrUpdate(mentioningPost);
    }
    
    @Test
    public void notifyNewlyMentionedUsersShouldNotNotifyNotAgreedWithNotificationsUsers() {
        String textWithUsersMentioning = "In this text we have 1 user mentioning - [user]Shogun[/user]";
        Post mentioningPost = getPost(25L, textWithUsersMentioning);
        JCUser mentionedUser = getJCUser("Shogun", false);
        when(userDao.getByUsernames(asSet("Shogun"))).thenReturn(asList(mentionedUser));

        MentionedUsers mentionedUsers = MentionedUsers.parse(mentioningPost);
        List<JCUser> usersToNotify = mentionedUsers.getNewUsersToNotify(userDao);
        
        assertEquals(mentioningPost.getPostContent(), textWithUsersMentioning,
                "After sending email [user][/user] tag shoudn't be changed");
        assertEquals(usersToNotify.size(), 0);
        verify(postDao, never()).saveOrUpdate(mentioningPost);
    }
    
    @Test
    public void notifyNewlyMentionedUsersShouldNotSendWhenUsersWereNotFound() {
        String textWithUsersMentioning = "In this text we have 3 user mentioning: first [user]Shogun[/user]," +
                "second [user]masyan[/user]," +
                "third [user]jk1[/user]";
        Post mentioningPost = getPost(25L, textWithUsersMentioning);
        when(userDao.getByUsernames(asSet("Shogun", "jk1", "masyan")))
            .thenReturn(Collections.<JCUser> emptyList());

        MentionedUsers mentionedUsers = MentionedUsers.parse(mentioningPost);
        List<JCUser> usersToNotify = mentionedUsers.getNewUsersToNotify(userDao);
        
        assertEquals(mentioningPost.getPostContent(), textWithUsersMentioning,
                "After sending email [user][/user] tag shoudn't be changed");
        assertEquals(usersToNotify.size(), 0);
        verify(postDao, never()).saveOrUpdate(mentioningPost);
    }
    
    @Test
    public void notifyNewlyMentionedUsersShouldNotSendIfUserIsSubscriberOfTopic() {
        String textWithUsersMentioning = 
                "In this text we have 1 user mentioning - [user]Shogun[/user]";
        JCUser mentionedUser = getJCUser("Shogun", true);
        //
        Post mentioningPost = getPost(25L, textWithUsersMentioning);
        Set<JCUser> topicSubscribers = new HashSet<JCUser>();
        topicSubscribers.add(mentionedUser);
        mentioningPost.getTopic().setSubscribers(topicSubscribers);
        //
        when(userDao.getByUsernames(asSet("Shogun")))
            .thenReturn(asList(mentionedUser));

        MentionedUsers mentionedUsers = MentionedUsers.parse(mentioningPost);
        List<JCUser> usersToNotify = mentionedUsers.getNewUsersToNotify(userDao);
        
        assertEquals(mentioningPost.getPostContent(), textWithUsersMentioning,
                "After sending email [user][/user] tag shoudn't be changed");
        assertEquals(usersToNotify.size(), 0);
        verify(postDao, never()).saveOrUpdate(mentioningPost);
    }
    
    private JCUser getJCUser(String name, boolean isMentioningEnabled) {
        JCUser user = new JCUser(name, "email@gmail.com", "password");
        user.setMentioningNotificationsEnabled(isMentioningEnabled);
        return user;
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void mentionedUserNotifyNewlyMentionedShouldThrowExceptionWhenNotCreatedBasedOnPost() {
        MentionedUsers mentionedUsers = MentionedUsers.parse("");
        mentionedUsers.getNewUsersToNotify(userDao);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void mentionedUserMarkAsNotifiedShouldThrowExceptionWhenNotCreatedBasedOnPost() {
        MentionedUsers mentionedUsers = MentionedUsers.parse("");
        mentionedUsers.markUsersAsAlreadyNotified(postDao);
    }

    private Post getPost(long id, String content) {
        Post post = new Post(null, content);
        post.setTopic(new Topic());
        post.setId(id); 
        return post;
    }

    private void setupRequestAttributes() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.setContextPath("/forum");
        RequestContextHolder.setRequestAttributes(new ServletWebRequest(request));
    }

    public static <T> Set<T> asSet(T... values) {
        return new HashSet<T>(Arrays.asList(values));
    }

    private JCUser getUser(String username, long userId) {
        JCUser user = new JCUser(username, "sshogunn@gmail.com", "shogun password");
        user.setId(userId);
        return user;
    }


    @Test
    public void processShouldAttachProfileLinkToExistCyrillicUsers() throws NotFoundException {
        setupRequestAttributes();

        String cyrillicCharsUserName = "Иванов";
        String cyrillicCharsUserNameWithSpaces = "П е т р о в";

        JCUser cyrillicCharsUser = getUser(cyrillicCharsUserName, 100L);
        when(userDao.getByUsername(cyrillicCharsUserName)).thenReturn(cyrillicCharsUser);
        JCUser notifiedMentionedUser = getUser(cyrillicCharsUserNameWithSpaces, 101L);
        when(userDao.getByUsername(cyrillicCharsUserNameWithSpaces)).thenReturn(notifiedMentionedUser);
        //
        String cyrillicCharsUserProfile = "/forum/users/" + cyrillicCharsUser.getId();
        String cyrillicCharsUserWithSpaceProfile = "/forum/users/" + notifiedMentionedUser.getId();
        String notProcessedSource = format(MENTIONING_TEMPLATE, cyrillicCharsUserName, cyrillicCharsUserNameWithSpaces);

        MentionedUsers mentionedUsers = MentionedUsers.parse(notProcessedSource);

        String expectedAfterProcess = format(MENTIONING_WITH_LINK_TO_PROFILE_TEMPALTE,
                cyrillicCharsUserProfile, cyrillicCharsUserName,
                cyrillicCharsUserWithSpaceProfile, cyrillicCharsUserNameWithSpaces);

        String actualAfterProcess = mentionedUsers.getTextWithProcessedUserTags(userDao);

        assertEquals(actualAfterProcess, expectedAfterProcess);
    }

    @Test
    public void processShouldAttachProfileLinkToExistUsersWithBracketsInName() throws NotFoundException {
        setupRequestAttributes();

        String userNameWithOpenBracket = "]yak";
        String userNameWithCloseBracket = "yak";

        JCUser userWithOpenBracketInName = getUser(userNameWithOpenBracket, 100L);
        when(userDao.getByUsername(userNameWithOpenBracket)).thenReturn(userWithOpenBracketInName);
        JCUser userWithCloseBracketInName = getUser(userNameWithCloseBracket, 101L);
        when(userDao.getByUsername(userNameWithCloseBracket)).thenReturn(userWithCloseBracketInName);
        //
        String withOpenBracketUserProfile = "/forum/users/" + userWithOpenBracketInName.getId();
        String withCloseBracketUserProfile = "/forum/users/" + userWithCloseBracketInName.getId();
        String notProcessedSource = format(MENTIONING_TEMPLATE, userNameWithOpenBracket, userNameWithCloseBracket);

        MentionedUsers mentionedUsers = MentionedUsers.parse(notProcessedSource);

        String expectedAfterProcess = format(MENTIONING_WITH_LINK_TO_PROFILE_TEMPALTE,
                withOpenBracketUserProfile, userNameWithOpenBracket,
                withCloseBracketUserProfile, userNameWithCloseBracket);

        String actualAfterProcess = mentionedUsers.getTextWithProcessedUserTags(userDao);

        assertEquals(actualAfterProcess, expectedAfterProcess);
    }

    @Test
    public void processShouldAttachProfileLinkToExistUsersWithSlashesInName() throws NotFoundException {
        setupRequestAttributes();

        String userNameWithSlash = "/yak";
        String userNameWithBackSlash = "\\yak";

        JCUser userWithSlashInName = getUser(userNameWithSlash, 100L);
        when(userDao.getByUsername(userNameWithSlash)).thenReturn(userWithSlashInName);
        JCUser userWithBackSlashInName = getUser(userNameWithBackSlash, 101L);
        when(userDao.getByUsername(userNameWithBackSlash)).thenReturn(userWithBackSlashInName);
        //
        String withSlashUserProfile = "/forum/users/" + userWithSlashInName.getId();
        String withBackSlashUserProfile = "/forum/users/" + userWithBackSlashInName.getId();
        String notProcessedSource = format(MENTIONING_TEMPLATE, userNameWithSlash, userNameWithBackSlash);

        MentionedUsers mentionedUsers = MentionedUsers.parse(notProcessedSource);

        String expectedAfterProcess = format(MENTIONING_WITH_LINK_TO_PROFILE_TEMPALTE,
                withSlashUserProfile, userNameWithSlash,
                withBackSlashUserProfile, userNameWithBackSlash);

        String actualAfterProcess = mentionedUsers.getTextWithProcessedUserTags(userDao);

        assertEquals(actualAfterProcess, expectedAfterProcess);
    }

    @Test
    public void processShouldAttachProfileLinkToExistUsersWithLowerGreaterInName() throws NotFoundException {
        setupRequestAttributes();

        String userNameWithLower = "<yak";
        String userNameWithGreater = ">yak";

        JCUser userWithLowerInName = getUser(userNameWithLower, 100L);
        when(userDao.getByUsername(userNameWithLower)).thenReturn(userWithLowerInName);
        JCUser userWithGreaterInName = getUser(userNameWithGreater, 101L);
        when(userDao.getByUsername(userNameWithGreater)).thenReturn(userWithGreaterInName);
        //
        String withLowerUserProfile = "/forum/users/" + userWithLowerInName.getId();
        String withGreaterUserProfile = "/forum/users/" + userWithGreaterInName.getId();
        String notProcessedSource = format(MENTIONING_TEMPLATE, userNameWithLower, userNameWithGreater);

        MentionedUsers mentionedUsers = MentionedUsers.parse(notProcessedSource);

        String expectedAfterProcess = format(MENTIONING_WITH_LINK_TO_PROFILE_TEMPALTE,
                withLowerUserProfile, userNameWithLower,
                withGreaterUserProfile, userNameWithGreater);

        String actualAfterProcess = mentionedUsers.getTextWithProcessedUserTags(userDao);

        assertEquals(actualAfterProcess, expectedAfterProcess);
    }

    @Test
    public void processShouldAttachProfileLinkToExistUsersWithSpecialCharsInName() throws NotFoundException {
        setupRequestAttributes();

        String userNameWithPercents = "%yak";
        String userNameWithPipeSymbol = "|yak";

        JCUser userWithPercentsInName = getUser(userNameWithPercents, 100L);
        when(userDao.getByUsername(userNameWithPercents)).thenReturn(userWithPercentsInName);
        JCUser userWithPipeSymbolInName = getUser(userNameWithPipeSymbol, 101L);
        when(userDao.getByUsername(userNameWithPipeSymbol)).thenReturn(userWithPipeSymbolInName);
        //
        String withPercentsUserProfile = "/forum/users/" + userWithPercentsInName.getId();
        String withPipeSymbolUserProfile = "/forum/users/" + userWithPipeSymbolInName.getId();
        String notProcessedSource = format(MENTIONING_TEMPLATE, userNameWithPercents, userNameWithPipeSymbol);

        MentionedUsers mentionedUsers = MentionedUsers.parse(notProcessedSource);

        String expectedAfterProcess = format(MENTIONING_WITH_LINK_TO_PROFILE_TEMPALTE,
                withPercentsUserProfile, userNameWithPercents,
                withPipeSymbolUserProfile, userNameWithPipeSymbol);

        String actualAfterProcess = mentionedUsers.getTextWithProcessedUserTags(userDao);

        assertEquals(actualAfterProcess, expectedAfterProcess);
    }

    @Test
    public void processShouldAttachProfileLinkToExistEncodedCyrillicUsers() throws NotFoundException {
        setupRequestAttributes();

        String cyrillicCharsUserName = "%D0%98%D0%B2%D0%B0%D0%BD%D0%BE%D0%B2";
        String cyrillicCharsUserNameWithSpaces = "%D0%9F %D0%B5 %D1%82 %D1%80 %D0%BE %D0%B2";

        JCUser cyrillicCharsUser = getUser("Иванов", 100L);
        when(userDao.getByUsername(cyrillicCharsUser.getUsername())).thenReturn(cyrillicCharsUser);
        JCUser cyrillicCharsWithSpacesUser = getUser("П е т р о в", 101L);
        when(userDao.getByUsername(cyrillicCharsWithSpacesUser.getUsername())).thenReturn(cyrillicCharsWithSpacesUser);
        //
        String cyrillicCharsUserProfile = "/forum/users/" + cyrillicCharsUser.getId();
        String cyrillicCharsUserWithSpaceProfile = "/forum/users/" + cyrillicCharsWithSpacesUser.getId();
        String notProcessedSource = format(MENTIONING_TEMPLATE, cyrillicCharsUserName, cyrillicCharsUserNameWithSpaces);

        MentionedUsers mentionedUsers = MentionedUsers.parse(notProcessedSource);

        String expectedAfterProcess = format(MENTIONING_WITH_LINK_TO_PROFILE_TEMPALTE,
                cyrillicCharsUserProfile, cyrillicCharsUserName,
                cyrillicCharsUserWithSpaceProfile, cyrillicCharsUserNameWithSpaces);

        String actualAfterProcess = mentionedUsers.getTextWithProcessedUserTags(userDao);

        assertEquals(actualAfterProcess, expectedAfterProcess);
    }

    @Test
    public void processShouldAttachProfileLinkToExistUsersWithEncodedBracketsInName() throws NotFoundException {
        setupRequestAttributes();

        String userNameWithOpenBracket = "@ywdffgg434y@yak";
        String userNameWithCloseBracket = "@w0956756wo@yak";

        JCUser userWithOpenBracketInName = getUser("[yak", 100L);
        when(userDao.getByUsername("[yak")).thenReturn(userWithOpenBracketInName);
        JCUser userWithCloseBracketInName = getUser("]yak", 101L);
        when(userDao.getByUsername("]yak")).thenReturn(userWithCloseBracketInName);
        //
        String withOpenBracketUserProfile = "/forum/users/" + userWithOpenBracketInName.getId();
        String withCloseBracketUserProfile = "/forum/users/" + userWithCloseBracketInName.getId();
        String notProcessedSource = format(MENTIONING_TEMPLATE, userNameWithOpenBracket, userNameWithCloseBracket);

        MentionedUsers mentionedUsers = MentionedUsers.parse(notProcessedSource);

        String expectedAfterProcess = format(MENTIONING_WITH_LINK_TO_PROFILE_TEMPALTE,
                withOpenBracketUserProfile, userNameWithOpenBracket,
                withCloseBracketUserProfile, userNameWithCloseBracket);

        String actualAfterProcess = mentionedUsers.getTextWithProcessedUserTags(userDao);

        assertEquals(actualAfterProcess, expectedAfterProcess);
    }

    @Test
    public void processShouldAttachProfileLinkToExistUsersWithEncodedLowerGreaterInName() throws NotFoundException {
        setupRequestAttributes();

        String userNameWithLower = "gertfgertgf@@@@@#4324234yak";
        String userNameWithGreater = ">yak";

        JCUser userWithLowerInName = getUser("<yak", 100L);
        when(userDao.getByUsername("<yak")).thenReturn(userWithLowerInName);
        JCUser userWithGreaterInName = getUser(">yak", 101L);
        when(userDao.getByUsername(">yak")).thenReturn(userWithGreaterInName);
        //
        String withLowerUserProfile = "/forum/users/" + userWithLowerInName.getId();
        String withGreaterUserProfile = "/forum/users/" + userWithGreaterInName.getId();
        String notProcessedSource = format(MENTIONING_TEMPLATE, userNameWithLower, userNameWithGreater);

        MentionedUsers mentionedUsers = MentionedUsers.parse(notProcessedSource);

        String expectedAfterProcess = format(MENTIONING_WITH_LINK_TO_PROFILE_TEMPALTE,
                withLowerUserProfile, userNameWithLower,
                withGreaterUserProfile, userNameWithGreater);

        String actualAfterProcess = mentionedUsers.getTextWithProcessedUserTags(userDao);

        assertEquals(actualAfterProcess, expectedAfterProcess);
    }

    @Test
    public void processShouldAttachProfileLinkToExistUsersWithEncodedSlashesInName() throws NotFoundException {
        setupRequestAttributes();

        String userNameWithSlash = "/yak";
        String userNameWithBackSlash = "14@123435vggv4fyak";

        JCUser userWithSlashInName = getUser(userNameWithSlash, 100L);
        when(userDao.getByUsername(userNameWithSlash)).thenReturn(userWithSlashInName);
        JCUser userWithBackSlashInName = getUser("\\yak", 101L);
        when(userDao.getByUsername("\\yak")).thenReturn(userWithBackSlashInName);
        //
        String withSlashUserProfile = "/forum/users/" + userWithSlashInName.getId();
        String withBackSlashUserProfile = "/forum/users/" + userWithBackSlashInName.getId();
        String notProcessedSource = format(MENTIONING_TEMPLATE, userNameWithSlash, userNameWithBackSlash);

        MentionedUsers mentionedUsers = MentionedUsers.parse(notProcessedSource);

        String expectedAfterProcess = format(MENTIONING_WITH_LINK_TO_PROFILE_TEMPALTE,
                withSlashUserProfile, userNameWithSlash,
                withBackSlashUserProfile, userNameWithBackSlash);

        String actualAfterProcess = mentionedUsers.getTextWithProcessedUserTags(userDao);

        assertEquals(actualAfterProcess, expectedAfterProcess);
    }
}

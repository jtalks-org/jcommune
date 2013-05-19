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

import static java.util.Arrays.asList;
import static org.jtalks.jcommune.model.entity.JCommuneProperty.SENDING_NOTIFICATIONS_ENABLED;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.jtalks.common.model.entity.Property;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dao.PropertyDao;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.JCommuneProperty;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class UserMentionServiceTest {
    private static final String PROPERTY_NAME = "property";
    private static final String TRUE_STRING = Boolean.TRUE.toString();
    private static final String FALSE_STRING = Boolean.FALSE.toString();
    @Mock
    private MailService mailService;
    @Mock
    private UserDao userDao;
    @Mock 
    private PostDao postDao;
    @Mock
    private PropertyDao propertyDao;
    private JCommuneProperty notificationsEnabledProperty = SENDING_NOTIFICATIONS_ENABLED;
    
    private UserMentionService userMentionService;
 
    @BeforeMethod
    public void init() {
        initMocks(this);
        notificationsEnabledProperty.setPropertyDao(propertyDao);
        notificationsEnabledProperty.setName(PROPERTY_NAME);
        userMentionService = new UserMentionService(mailService, userDao, postDao, notificationsEnabledProperty);
    }
    
    @Test
    public void extractMentionedUserShouldReturnEmptyListWhenPassedTextDoesNotContainMentioning() {
        String textWithoutUserMentioning = "This text mustn't contain user mentioning. Be carefull.";
        
        List<String> extractedUserNames = userMentionService.extractAllMentionedUsers(textWithoutUserMentioning);
        
        assertTrue(CollectionUtils.isEmpty(extractedUserNames), "Passed user should not contain any user mentioning.");
    }

    @Test
    public void extractMentionedUserShouldReturnAllMentionedUserInBBCodes() {
        String textWithUsersMentioning = "In this text we have 3 user mentioning: first [user]Shogun[/user]," +
        		"second [user notified=true]masyan[/user]," +
        		"third [user]jk1[/user]";
        
        List<String> extractedUserNames = userMentionService.extractAllMentionedUsers(textWithUsersMentioning);
        
        assertTrue(extractedUserNames.size() == 3, "Passed text should contain 3 user mentioning.");
        assertTrue(extractedUserNames.contains("Shogun"), "Shogun is mentioned, so he should be extracted.");
        assertTrue(extractedUserNames.contains("masyan"), "masyan is mentioned, so he should be extracted.");
        assertTrue(extractedUserNames.contains("jk1"), "jk1 is mentioned, so he should be extracted.");
    }
    
    @Test
    public void notifyNotMentionedUsersShouldSendForNotYetNotifiedMentionedUsers() {
        prepareEnabledProperty();
        Post mentioningPost = getPost(25L);
        //
        String firstUsername = "Shogun";
        String secondUsername = "jk1";
        String thirdUsername = "masyan";
        String textWithUsersMentioning = "In this text we have 3 user mentioning: first [user]" + firstUsername + "[/user]," +
                "second [user]"+ secondUsername + "[/user]," +
                "third [user]" + thirdUsername + "[/user]";
        mentioningPost.setPostContent(textWithUsersMentioning);
        List<String> usernames = asList(firstUsername, secondUsername, thirdUsername);
        List<JCUser> users = asList(
                getJCUser(firstUsername, true),
                getJCUser(secondUsername, true),
                getJCUser(thirdUsername, true));
        when(userDao.getByUsernames(usernames)).thenReturn(users);
        
        userMentionService.notifyNotMentionedUsers(mentioningPost);
        
        assertNotSame(mentioningPost.getPostContent(), textWithUsersMentioning,
                "After sending email [user][/user] tag shoud be changed to [user notified=true][/user]");
        verify(mailService, times(users.size()))
            .sendUserMentionedNotification(any(JCUser.class), anyLong());
        verify(postDao, times(users.size())).update(mentioningPost);
    }
    
    @Test
    public void notifyNotMentionedUsersShouldNotNotifyNotAgreedWithNotificationsUsers() {
        prepareEnabledProperty();
        Post mentioningPost = getPost(25L);
        //
        String mentionedUsername = "Shogun";
        String textWithUsersMentioning = "In this text we have 1 user mentioning - [user]" + mentionedUsername + "[/user]";
        mentioningPost.setPostContent(textWithUsersMentioning);
        List<String> usernames = asList(mentionedUsername);
        JCUser mentionedUser = getJCUser(mentionedUsername, false);
        List<JCUser> users = asList(mentionedUser);
        when(userDao.getByUsernames(usernames)).thenReturn(users);
        
        userMentionService.notifyNotMentionedUsers(mentioningPost);
        
        assertEquals(mentioningPost.getPostContent(), textWithUsersMentioning,
                "After sending email [user][/user] tag shoudn't be changed");
        verify(mailService, never())
            .sendUserMentionedNotification(any(JCUser.class), anyLong());
        verify(postDao, never()).update(mentioningPost);
    }
    
    @Test
    public void notifyNotMentionedUsersShouldNotSendWhenUsersWereNotFound() {
        prepareEnabledProperty();
        long mentioningPostId = 1L;
        Post mentioningPost = getPost(mentioningPostId);
        //
        String textWithUsersMentioning = "In this text we have 3 user mentioning: first [user]Shogun[/user]," +
                "second [user]masyan[/user]," +
                "third [user]jk1[/user]";
        mentioningPost.setPostContent(textWithUsersMentioning);
        List<String> usernames = asList("Shogun", "jk1", "masyan");
        when(userDao.getByUsernames(usernames)).thenReturn(Collections.<JCUser> emptyList());
        
        userMentionService.notifyNotMentionedUsers(mentioningPost);
        
        assertEquals(mentioningPost.getPostContent(), textWithUsersMentioning,
                "After sending email [user][/user] tag shoudn't be changed");
        verify(mailService, never())
            .sendUserMentionedNotification(any(JCUser.class), anyLong());
        verify(postDao, never()).update(mentioningPost);
    }
    
    @Test
    public void notifyNotMentionedUsersShouldNotSendIfUserIsSubscriberOfTopic() {
        prepareEnabledProperty();
        String mentionedUsername = "Shogun";
        String textWithUsersMentioning = 
                "In this text we have 1 user mentioning - [user]" + mentionedUsername + "[/user]";
        JCUser mentionedUser = getJCUser(mentionedUsername, true);
        //
        Post mentioningPost = getPost(25L);
        mentioningPost.setPostContent(textWithUsersMentioning);
        Set<JCUser> topicSubscribers = new HashSet<JCUser>();
        topicSubscribers.add(mentionedUser);
        mentioningPost.getTopic().setSubscribers(topicSubscribers);
        //
        when(userDao.getByUsernames(asList(mentionedUsername)))
            .thenReturn(asList(mentionedUser));
        
        userMentionService.notifyNotMentionedUsers(mentioningPost);
        
        assertEquals(mentioningPost.getPostContent(), textWithUsersMentioning,
                "After sending email [user][/user] tag shoudn't be changed");
        verify(mailService, never())
            .sendUserMentionedNotification(any(JCUser.class), anyLong());
        verify(postDao, never()).update(mentioningPost);
    }
    
    private JCUser getJCUser(String name, boolean isMentioningEnabled) {
        JCUser user = new JCUser(name, "email@gmail.com", "password");
        user.setMentioningNotificationsEnabled(isMentioningEnabled);
        return user;
    }
    
    private Post getPost(long id) {
        Post post = new Post(null, "test post");
        post.setTopic(new Topic());
        post.setId(id); 
        return post;
    }
    
    @Test
    public void notifyNotMentionedUsersShouldNotSendIsForumNotificationsAreDisabled() {
        prepareDisabledProperty();
        String mentionedUsername = "Shogun";
        String textWithUsersMentioning = 
                "In this text we have 1 user mentioning - [user]" + mentionedUsername + "[/user]";
        JCUser mentionedUser = getJCUser(mentionedUsername, true);
        //
        Post mentioningPost = getPost(25L);
        mentioningPost.setPostContent(textWithUsersMentioning);
        //
        when(userDao.getByUsernames(asList(mentionedUsername)))
            .thenReturn(asList(mentionedUser));
        
        userMentionService.notifyNotMentionedUsers(mentioningPost);
        
        assertNotSame(mentioningPost.getPostContent(), textWithUsersMentioning,
                "When forum notifications are disabled we should mark that user was notified," +
                " otherwise after enabling notifications user will recieve all accumulated in queue notifications.");
        verify(postDao).update(mentioningPost);
        verify(mailService, never())
            .sendUserMentionedNotification(any(JCUser.class), anyLong());
    }
    
    private void prepareDisabledProperty() {
        Property disabledProperty = new Property(PROPERTY_NAME, FALSE_STRING);
        when(propertyDao.getByName(PROPERTY_NAME)).thenReturn(disabledProperty);
    }
    
    private void prepareEnabledProperty() {
        Property enabledProperty = new Property(PROPERTY_NAME, TRUE_STRING);
        when(propertyDao.getByName(PROPERTY_NAME)).thenReturn(enabledProperty);
    }
}

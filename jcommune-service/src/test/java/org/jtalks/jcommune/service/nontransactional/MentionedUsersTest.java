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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
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
public class MentionedUsersTest {
    @Mock
    private MailService mailService;
    @Mock
    private UserDao userDao;
    @Mock 
    private PostDao postDao;
    
    private MentionedUsers mentionedUsers;
 
    @BeforeMethod
    public void init() {
        initMocks(this);
    }
    
    /*@Test
    public void extractMentionedUserShouldReturnEmptyListWhenPassedTextDoesNotContainMentioning() {
        String textWithoutUserMentioning = "This text mustn't contain user mentioning. Be carefull.";
        
        Set<String> extractedUserNames = mentionedUsers.extractAllMentionedUsers(textWithoutUserMentioning);
        
        assertTrue(CollectionUtils.isEmpty(extractedUserNames), "Passed user should not contain any user mentioning.");
    }

    @Test
    public void extractMentionedUserShouldReturnAllMentionedUserInBBCodes() {
        String textWithUsersMentioning = "In this text we have 3 user mentioning: first [user]Shogun[/user]," +
        		"second [user notified=true]masyan[/user]," +
        		"third [user]jk1[/user]";
        
        Set<String> extractedUserNames = mentionedUsers.extractAllMentionedUsers(textWithUsersMentioning);
        
        assertTrue(extractedUserNames.size() == 3, "Passed text should contain 3 user mentioning.");
        assertTrue(extractedUserNames.contains("Shogun"), "Shogun is mentioned, so he should be extracted.");
        assertTrue(extractedUserNames.contains("masyan"), "masyan is mentioned, so he should be extracted.");
        assertTrue(extractedUserNames.contains("jk1"), "jk1 is mentioned, so he should be extracted.");
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
        
        mentionedUsers.notifyNewlyMentionedUsers(mentioningPost);
        
        verify(mailService, times(users.size()))
            .sendUserMentionedNotification(any(JCUser.class), anyLong());
    }
    
    @Test
    public void notifyNewlyMentionedUsersShouldMarkBBCodesOfNotfiiedUsers() {
        Post mentioningPost = getPost(25L, "In this text we have user mentioning [user]Shogun[/user]");
        when(userDao.getByUsernames(asSet("Shogun")))
            .thenReturn(asList(getJCUser("Shogun", true)));
            
        mentionedUsers.notifyNewlyMentionedUsers(mentioningPost);
        
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
        
        mentionedUsers.notifyNewlyMentionedUsers(mentioningPost);
        
        assertEquals(mentioningPost.getPostContent(), textWithUsersMentioning,
                "After sending email [user][/user] tag shoudn't be changed");
        verify(mailService, never())
            .sendUserMentionedNotification(any(JCUser.class), anyLong());
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
        
        mentionedUsers.notifyNewlyMentionedUsers(mentioningPost);
        
        assertEquals(mentioningPost.getPostContent(), textWithUsersMentioning,
                "After sending email [user][/user] tag shoudn't be changed");
        verify(mailService, never())
            .sendUserMentionedNotification(any(JCUser.class), anyLong());
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
        
        mentionedUsers.notifyNewlyMentionedUsers(mentioningPost);
        
        assertEquals(mentioningPost.getPostContent(), textWithUsersMentioning,
                "After sending email [user][/user] tag shoudn't be changed");
        verify(mailService, never())
            .sendUserMentionedNotification(any(JCUser.class), anyLong());
        verify(postDao, never()).saveOrUpdate(mentioningPost);
    }
    
    private JCUser getJCUser(String name, boolean isMentioningEnabled) {
        JCUser user = new JCUser(name, "email@gmail.com", "password");
        user.setMentioningNotificationsEnabled(isMentioningEnabled);
        return user;
    }
    
    private Post getPost(long id, String content) {
        Post post = new Post(null, content);
        post.setTopic(new Topic());
        post.setId(id); 
        return post;
    }
    
    private static<T> Set<T> asSet(T... items) {
        Set<T> result = new HashSet<T>();
        result.addAll(asList(items));
        return result;
    }*/
}

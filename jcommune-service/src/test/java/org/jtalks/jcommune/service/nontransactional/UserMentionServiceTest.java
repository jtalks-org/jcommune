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

import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.MockitoAnnotations.initMocks;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class UserMentionServiceTest {
    @Mock
    private MailService mailService;
    @Mock
    private UserDao userDao;
    
    private UserMentionService userMentionService;
 
    @BeforeTest
    public void init() {
        initMocks(this);
        userMentionService = new UserMentionService(mailService, userDao);
    }
    
    @Test
    public void extractMentionedUserShouldReturnEmptyListWhenPassedTextDoesNotContainMentioning() {
        String textWithoutUserMentioning = "This text mustn't contain user mentioning. Be carefull.";
        
        List<String> extractedUserNames = userMentionService.extractMentionedUsers(textWithoutUserMentioning);
        
        assertTrue(CollectionUtils.isEmpty(extractedUserNames), "Passed user should not contain any user mentioning.");
    }

    @Test
    public void extractMentionedUserShouldReturnAllMentionedUserInBBCodes() {
        String textWithUsersMentioning = "In this text we have 3 user mentioning: first [user]Shogun[/user]," +
        		"second [user]masyan[/user]," +
        		"third [user]jk1[/user]";
        
        List<String> extractedUserNames = userMentionService.extractMentionedUsers(textWithUsersMentioning);
        
        assertTrue(extractedUserNames.size() == 3, "Passed text should contain 3 user mentioning.");
        assertTrue(extractedUserNames.contains("Shogun"), "Shogun is mentioned, so he should be extracted.");
        assertTrue(extractedUserNames.contains("masyan"), "masyan is mentioned, so he should be extracted.");
        assertTrue(extractedUserNames.contains("jk1"), "masyan is mentioned, so he should be extracted.");
    }
    
    @Test
    public void notifyAllMentionedUsersShouldSendForAllFoundMentionedUsers() {
        long mentioningPostId = 1l;
        String firstUsername = "Shogun";
        String secondUsername = "jk1";
        String thirdUsername = "masyan";
        String textWithUsersMentioning = "In this text we have 3 user mentioning: first [user]" + firstUsername + "[/user]," +
                "second [user]"+ secondUsername + "[/user]," +
                "third [user]" + thirdUsername + "[/user]";
        List<String> usernames = Arrays.asList(firstUsername, secondUsername, thirdUsername);
        List<JCUser> users = Arrays.asList(
                getJCUser(firstUsername, true),
                getJCUser(secondUsername, true),
                getJCUser(thirdUsername, true));
        when(userDao.getByUsernames(usernames)).thenReturn(users);
        
        userMentionService.notifyAllMentionedUsers(textWithUsersMentioning, mentioningPostId);
        
        verify(mailService, times(users.size()))
            .sendUserMentionedNotification(any(JCUser.class), anyLong());
    }
    
    @Test
    public void notifyAllMentionedUsersShouldNotNotifyNotAgreedWithNotificationsUsers() {
        long mentioningPostId = 1l;
        String mentionedUsername = "Shogun";
        String textWithUsersMentioning = "In this text we have 1 user mentioning - [user]" + mentionedUsername + "[/user]";
        List<String> usernames = Arrays.asList(mentionedUsername);
        JCUser mentionedUser = getJCUser(mentionedUsername, false);
        List<JCUser> users = Arrays.asList(mentionedUser);
        when(userDao.getByUsernames(usernames)).thenReturn(users);
        
        userMentionService.notifyAllMentionedUsers(textWithUsersMentioning, mentioningPostId);
        
        verify(mailService, never())
            .sendUserMentionedNotification(any(JCUser.class), anyLong());
    }
    
    private JCUser getJCUser(String name, boolean isMentioningEnabled) {
        JCUser user = new JCUser(name, "email@gmail.com", "password");
        user.setMentioningNotificationsEnabled(isMentioningEnabled);
        return user;
    }
    
    @Test
    public void notifyAllMentionedUsersShouldNotSendWhenUsersWereNotFound() {
        String textWithUsersMentioning = "In this text we have 3 user mentioning: first [user]Shogun[/user]," +
                "second [user]masyan[/user]," +
                "third [user]jk1[/user]";
        long mentioningPostId = 1l;
        List<String> usernames = Arrays.asList("Shogun", "jk1", "masyan");
        when(userDao.getByUsernames(usernames)).thenReturn(Collections.<JCUser> emptyList());
        
        userMentionService.notifyAllMentionedUsers(textWithUsersMentioning, mentioningPostId);
        
        verify(mailService, never())
            .sendUserMentionedNotification(any(JCUser.class), anyLong());
    }
}

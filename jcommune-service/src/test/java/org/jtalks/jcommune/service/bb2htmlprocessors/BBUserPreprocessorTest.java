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
package org.jtalks.jcommune.service.bb2htmlprocessors;

import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.UserMentionService;
import org.mockito.Mock;
import static org.mockito.Mockito.when;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class BBUserPreprocessorTest {
    private static final String MENTIONING_TEMPLATE = "This post contains [user]%s[/user] mentioning";
    private static final String MENTIONING_WITH_LINK_TO_PROFILE_TEMPALTE = 
            "This post contains [user=%s]%s[/user] mentioning";
    @Mock
    private UserService userService;
    @Mock
    private UserMentionService userMentionService;
    private BbUserPreprocessor userPreprocessor;
    
    
    @BeforeMethod
    public void init() {
        initMocks(this);
        userPreprocessor = new BbUserPreprocessor(userService, userMentionService);
        //
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.setContextPath("/forum");
        RequestContextHolder.setRequestAttributes(new ServletWebRequest(request));
    }
    
    @Test
    public void processShouldAttachProfileLinkToExistUsers() throws NotFoundException {
        String mentionedUserName = "Shogun";
        long mentionedUserId = 100L;
        JCUser mentionedUser = getUser(mentionedUserName, mentionedUserId);
        when(userService.getByUsername(mentionedUserName)).thenReturn(mentionedUser);
        //
        String expectedUserProfileLink = "http://localhost:8080/forum/users/" + mentionedUserId;
        String notProcessedSource = String.format(MENTIONING_TEMPLATE, mentionedUserName);
        when(userMentionService.extractMentionedUsers(notProcessedSource))
            .thenReturn(Arrays.asList(mentionedUserName));
        String expectedAfterProcess = String.format(
                MENTIONING_WITH_LINK_TO_PROFILE_TEMPALTE, expectedUserProfileLink, mentionedUserName);
        
        String actualAfterProcess = userPreprocessor.process(notProcessedSource);
        
        assertEquals(actualAfterProcess, expectedAfterProcess);
    }
    
    private JCUser getUser(String username, long userId) {
        JCUser user = new JCUser(username, "sshogunn@gmail.com", "shogun password");
        user.setId(userId);
        return user;
    }
    
    @Test
    public void processShouldNotAttachProfileLinkToNotExistUsers() throws NotFoundException {
        String mentionedUserName = "Shogun";
        when(userService.getByUsername(mentionedUserName)).thenThrow(new NotFoundException());
        String notProcessedSource = String.format(MENTIONING_TEMPLATE, mentionedUserName);
        when(userMentionService.extractMentionedUsers(notProcessedSource))
            .thenReturn(Arrays.asList(mentionedUserName));
        
        String actualAfterProcess = userPreprocessor.process(notProcessedSource);
        
        assertEquals(actualAfterProcess, notProcessedSource);
    }
}

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
    @Mock
    private UserService userService;
    @Mock
    private UserMentionService userMentionService;
    private BBUserPreprocessor userPreprocessor;
    
    
    @BeforeMethod
    public void init() {
        initMocks(this);
        userPreprocessor = new BBUserPreprocessor(userService, userMentionService);
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
        long mentionedUserId = 100l;
        JCUser mentionedUser = new JCUser(mentionedUserName, "sshogunn@gmail.com", "shogun password");
        mentionedUser.setId(mentionedUserId);
        when(userService.getByUsername(mentionedUserName)).thenReturn(mentionedUser);
        String expectedUserProfileLink = "http://localhost:8080/forum/users/" + mentionedUserId;
        String notProcessedSource = "This post contains [user]" + mentionedUserName + "[/user] mentioning";
        when(userMentionService.extractMentionedUsers(notProcessedSource))
            .thenReturn(Arrays.asList(mentionedUserName));
        String expectedAfterProcess = "This post contains [user=" + expectedUserProfileLink + "]" 
                + mentionedUserName + "[/user] mentioning";
        
        String actualAfterProcess = userPreprocessor.process(notProcessedSource);
        
        assertEquals(actualAfterProcess, expectedAfterProcess);
    }
    
    @Test
    public void processShouldNotAttachProfileLinkToNotExistUsers() throws NotFoundException {
        String mentionedUserName = "Shogun";
        when(userService.getByUsername(mentionedUserName)).thenThrow(new NotFoundException());
        String notProcessedSource = "This post contains [user]" + mentionedUserName + "[/user] mentioning";
        when(userMentionService.extractMentionedUsers(notProcessedSource))
            .thenReturn(Arrays.asList(mentionedUserName));
        
        String actualAfterProcess = userPreprocessor.process(notProcessedSource);
        
        assertEquals(actualAfterProcess, notProcessedSource);
    }
}

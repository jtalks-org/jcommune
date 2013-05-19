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

import static java.lang.String.format;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.UserMentionService;
import org.mockito.Mock;
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
public class BbUserPreprocessorTest {
    private static final String MENTIONING_TEMPLATE = "This post contains not notified [user]%s[/user] mentioning " +
    		"and notified [user notified=true]%s[/user] mentioning";
    private static final String MENTIONING_WITH_LINK_TO_PROFILE_TEMPALTE = 
            "This post contains not notified [user=%s]%s[/user] mentioning and notified [user=%s]%s[/user] mentioning";
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
        String notNotifiedMentionedUserName = "Shogun";
        String notifiedMentionedUserName = "jk1";
        long notNotifiedMentionedUserId = 100L;
        long notifiedMentionedUserId = 200L;
        JCUser notNotifiedMentionedUser = getUser(notNotifiedMentionedUserName, notNotifiedMentionedUserId);
        when(userService.getByUsername(notNotifiedMentionedUserName)).thenReturn(notNotifiedMentionedUser);
        JCUser notifiedMentionedUser = getUser(notifiedMentionedUserName, notifiedMentionedUserId);
        when(userService.getByUsername(notifiedMentionedUserName)).thenReturn(notifiedMentionedUser);
        //
        String expectedNotNotifiedUserProfile = "/forum/users/" + notNotifiedMentionedUserId;
        String expectedNotifiedUserProfile = "/forum/users/" + notifiedMentionedUserId;
        String notProcessedSource = format(MENTIONING_TEMPLATE, notNotifiedMentionedUserName, notifiedMentionedUserName);
        when(userMentionService.extractAllMentionedUsers(notProcessedSource))
            .thenReturn(asSet(notNotifiedMentionedUserName, notifiedMentionedUserName));
        String expectedAfterProcess = format(MENTIONING_WITH_LINK_TO_PROFILE_TEMPALTE, 
                expectedNotNotifiedUserProfile, notNotifiedMentionedUserName, 
                expectedNotifiedUserProfile, notifiedMentionedUserName);
        
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
        String firstMentionedUserName = "Shogun";
        String secondMentionedUserName = "jk1";
        when(userService.getByUsername(firstMentionedUserName)).thenThrow(new NotFoundException());
        when(userService.getByUsername(secondMentionedUserName)).thenThrow(new NotFoundException());
        String notProcessedSource = format(MENTIONING_TEMPLATE, firstMentionedUserName, secondMentionedUserName);
        when(userMentionService.extractAllMentionedUsers(notProcessedSource))
            .thenReturn(asSet(firstMentionedUserName, secondMentionedUserName));
        
        String actualAfterProcess = userPreprocessor.process(notProcessedSource);
        
        assertEquals(actualAfterProcess, notProcessedSource);
    }
    
    public static <T> Set<T> asSet(T... values) {
        return new HashSet<T>(Arrays.asList(values));
    }
}

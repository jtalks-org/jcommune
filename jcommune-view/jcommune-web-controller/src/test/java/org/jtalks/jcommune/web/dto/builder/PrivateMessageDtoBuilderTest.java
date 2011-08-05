/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */

package org.jtalks.jcommune.web.dto.builder;

import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.web.dto.PrivateMessageDto;
import org.jtalks.jcommune.web.dto.PrivateMessageDtoBuilder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;

/**
 * @author Alexandre Teterin
 */


public class PrivateMessageDtoBuilderTest {
    private PrivateMessageDtoBuilder pmDtoBuilder;
    @Mock
    private PrivateMessage pm;
    @Mock
    private User user;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        pmDtoBuilder = new PrivateMessageDtoBuilder();
    }

    @Test
    public void testGetFullPmDtoFor() throws Exception {
        //set expectations
        when(pm.getBody()).thenReturn("");
        when(pm.getTitle()).thenReturn("");
        when(pm.getUserTo()).thenReturn(user);
        when(user.getUsername()).thenReturn("");
        when(pm.getId()).thenReturn(2L);

        //invoke the object under test
        PrivateMessageDto actualDto = pmDtoBuilder.getFullPmDtoFor(pm);

        //check expectations
        verify(pm).getBody();
        verify(pm).getTitle();
        verify(pm).getUserTo();
        verify(user).getUsername();
        verify(pm).getId();

        //check result
        assertNotNull(actualDto.getBody());
        assertNotNull(actualDto.getTitle());
        assertNotNull(actualDto.getRecipient());
        assertNotNull(actualDto.getId());

    }

    @Test
    public void testGetReplyDtoFor() throws Exception {
        //set expectations
        when(pm.getUserFrom()).thenReturn(user);
        when(user.getUsername()).thenReturn("");
        when(pm.prepareTitleForReply()).thenReturn("");

        //invoke the object under test
        PrivateMessageDto actualDto = pmDtoBuilder.getReplyDtoFor(pm);

        //check expectations
        verify(pm).getUserFrom();
        verify(user).getUsername();
        verify(pm).prepareTitleForReply();

        //check result
        assertNotNull(actualDto.getRecipient());
        assertNotNull(actualDto.getTitle());

    }

    @Test
    public void testGetQuoteDtoFor() throws Exception {
        //set expectations
        when(pm.getUserFrom()).thenReturn(user);
        when(user.getUsername()).thenReturn("");
        when(pm.prepareTitleForReply()).thenReturn("");
        when(pm.prepareBodyForQuote()).thenReturn("");

        //invoke the object under test
        PrivateMessageDto actualDto = pmDtoBuilder.getQuoteDtoFor(pm);

        //check expectations
        verify(pm).getUserFrom();
        verify(user).getUsername();
        verify(pm).prepareTitleForReply();
        verify(pm).prepareBodyForQuote();

        //check result
        assertNotNull(actualDto.getRecipient());
        assertNotNull(actualDto.getTitle());
        assertNotNull(actualDto.getBody());
    }

}

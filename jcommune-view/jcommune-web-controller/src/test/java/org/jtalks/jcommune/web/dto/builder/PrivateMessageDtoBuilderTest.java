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
package org.jtalks.jcommune.web.dto.builder;

import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.web.dto.PrivateMessageDto;
import org.jtalks.jcommune.web.util.PrivateMessageDtoBuilder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Alexandre Teterin
 * @author Evgeniy Naumenko
 */
public class PrivateMessageDtoBuilderTest {
    
    private PrivateMessageDtoBuilder pmDtoBuilder;
    private User user = new User("username", "email", "password");

    private String BODY = "body";
    private String TITLE = "title";
    private long ID = 1L;
    PrivateMessage pm;
    
    @BeforeMethod
    public void init() {
        pmDtoBuilder = new PrivateMessageDtoBuilder();
        pm = new PrivateMessage(user,user, TITLE, BODY);
        pm.setId(ID);
    }

    @Test
    public void testGetRegularPmDto() throws Exception {
        //invoke the object under test
        PrivateMessageDto dto = pmDtoBuilder.getFullPmDtoFor(pm);

        //check result
        assertEquals(dto.getBody(), BODY);
        assertEquals(dto.getId(), ID);
        assertEquals(dto.getRecipient(), user.getUsername());
        assertEquals(dto.getTitle(), TITLE);

    }

    @Test
    public void testGetReplyDto() throws Exception {
        //invoke the object under test
        PrivateMessageDto dto = pmDtoBuilder.getReplyDtoFor(pm);

        //check result
        assertEquals(dto.getRecipient(), user.getUsername());
        assertEquals(dto.getTitle(), "Re: " + TITLE);
    }

    @Test
    public void testGetQuoteDto() throws Exception {
        //invoke the object under test
        PrivateMessageDto dto = pmDtoBuilder.getQuoteDtoFor(pm);

        //check result
        assertEquals(dto.getBody(), "> " + BODY + "\r\n");
        assertEquals(dto.getRecipient(), user.getUsername());
        assertEquals(dto.getTitle(), "Re: " + TITLE);
    }

}

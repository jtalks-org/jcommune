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
package org.jtalks.jcommune.web.dto;

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.web.dto.PrivateMessageDto;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Alexandre Teterin
 * @author Evgeniy Naumenko
 */
public class PrivateMessageDtoTest {
    
    private JCUser user = new JCUser("username", "email", "password");

    private String BODY = "body";
    private String TITLE = "title";
    private long ID = 1L;
    PrivateMessage pm;
    
    @BeforeMethod
    public void init() {
        pm = new PrivateMessage(user,user, TITLE, BODY);
        pm.setId(ID);
    }

    @Test
    public void testGetRegularPmDto() throws Exception {
        //invoke the object under test
        PrivateMessageDto dto = PrivateMessageDto.getFullPmDtoFor(pm);

        //check result
        assertEquals(dto.getBody(), BODY);
        assertEquals(dto.getId(), ID);
        assertEquals(dto.getRecipient(), user.getUsername());
        assertEquals(dto.getTitle(), TITLE);

    }

        @Test
    public void testGetRegularPmDtoNullUser() throws Exception {
        //invoke the object under test
            pm.setUserTo(null);
        PrivateMessageDto dto = PrivateMessageDto.getFullPmDtoFor(pm);

        //check result
        assertEquals(dto.getBody(), BODY);
        assertEquals(dto.getId(), ID);
        assertEquals(dto.getRecipient(), null);
        assertEquals(dto.getTitle(), TITLE);

    }

    @Test
    public void testGetReplyDto() throws Exception {
        //invoke the object under test
        PrivateMessageDto dto = PrivateMessageDto.getReplyDtoFor(pm);

        //check result
        assertEquals(dto.getRecipient(), user.getUsername());
        assertEquals(dto.getTitle(), "Re: " + TITLE);
    }

}

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
package org.jtalks.jcommune.model.entity;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;


/**
 * @author Alexandre Teterin
 */
public class
        PrivateMessageTest {

    private PrivateMessage pm = new PrivateMessage();

    @Test(dataProvider = "title-provider")
    public void testPrepareTitleForReply(String title, String expectedReTitle) throws Exception {

        pm.setTitle(title);
        String result = pm.prepareTitleForReply();
        assertEquals(result, expectedReTitle);

    }

    @Test(dataProvider = "status-provider")
    public void testIsReplyAllowed(PrivateMessageStatus status, boolean expectedResult) {
        pm.setStatus(status);
        assertEquals(expectedResult, pm.isReplyAllowed());
    }

    @DataProvider(name = "title-provider")
    public Object[][] rangeTitleData() {

        return new Object[][]{
                {"Title", "Re: Title"},
                {"Re: Title", "Re: Title"},
                {"Title Re:", "Re: Title Re:"},
                {"Tit Re: le", "Re: Tit Re: le"},
        };
    }

    // PM statuses eligible for reply & quote operations
    @DataProvider(name = "status-provider")
    public Object[][] statusData() {
        return new Object[][]{
                {PrivateMessageStatus.NEW, false},
                {PrivateMessageStatus.DRAFT, false},
                {PrivateMessageStatus.SENT, true},
                {PrivateMessageStatus.DELETED_FROM_INBOX, false},
                {PrivateMessageStatus.DELETED_FROM_OUTBOX, true}
        };
    }
}
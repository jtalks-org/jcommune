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

package org.jtalks.jcommune.model.entity;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Alexandre Teterin
 */
public class PrivateMessageTest {

    private PrivateMessage pm = new PrivateMessage();

    @Test(dataProvider = "title-provider")
    public void testPrepareTitleForReply(String title, String expectedReTitle) throws Exception {

        pm.setTitle(title);
        String result = pm.prepareTitleForReply();
        assertEquals(result, expectedReTitle);

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


    @Test(dataProvider = "body-provider")
    public void testPrepareBodyForQuote(String body, String expectedBody) throws Exception {
        pm.setBody(body);
        String result = pm.prepareBodyForQuote();
        assertEquals(result, expectedBody);

    }

    @DataProvider(name = "body-provider")
    public Object[][] rangeBodyData() {

        return new Object[][]{
                //data set for unquoted line test
                {"Line1" + PrivateMessage.NEW_LINE + "Line2",
                        PrivateMessage.QUOTE_PREFIX + PrivateMessage.QUOTE_SEPARATOR + "Line1"
                                + PrivateMessage.NEW_LINE
                                + PrivateMessage.QUOTE_PREFIX + PrivateMessage.QUOTE_SEPARATOR
                                + "Line2" + PrivateMessage.NEW_LINE},
                //data set for quoted line test
                {PrivateMessage.QUOTE_PREFIX + PrivateMessage.QUOTE_SEPARATOR + "Line1" + PrivateMessage.NEW_LINE
                        + PrivateMessage.QUOTE_PREFIX + PrivateMessage.QUOTE_SEPARATOR + "Line2",
                        PrivateMessage.QUOTE_PREFIX + PrivateMessage.QUOTE_PREFIX + PrivateMessage.QUOTE_SEPARATOR
                                + "Line1" + PrivateMessage.NEW_LINE
                                + PrivateMessage.QUOTE_PREFIX + PrivateMessage.QUOTE_PREFIX
                                + PrivateMessage.QUOTE_SEPARATOR + "Line2" + PrivateMessage.NEW_LINE}
        };

    }
}
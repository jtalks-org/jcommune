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


package org.jtalks.jcommune.web.util;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Alexandre Teterin
 */
public class ForumUtilsTest {

    private ForumUtils forumUtils = new ForumUtils();

    @Test
    public void testPrepareRequestedPageValidCase() {
        String page = "2";
        int pageSize = 10;
        int postCount = 19;
        int expected = 2;
        int actual = forumUtils.prepareRequestedPage(page, pageSize, postCount);
        assertEquals(actual, expected);
    }

    @Test
    public void testPrepareRequestedNotANumberCase() {
        String page = "qq";
        int pageSize = 10;
        int postCount = 19;
        int expected = 1;
        int actual = forumUtils.prepareRequestedPage(page, pageSize, postCount);
        assertEquals(actual, expected);
    }

    @Test
    public void testPrepareRequestedMoreThanMaxPageNumberCase() {
        String page = "77";
        int pageSize = 10;
        int postCount = 19;
        int expected = 2;
        int actual = forumUtils.prepareRequestedPage(page, pageSize, postCount);
        assertEquals(actual, expected);
    }


}

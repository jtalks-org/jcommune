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

import org.jtalks.jcommune.model.entity.JCUser;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Evgeniy Naumenko
 */
public class BBCodeServiceTest {

    private BBCodeService service;

    private static final String SOURCE = "source";
    private static final JCUser USER = new JCUser("name", "mail", "pass");

    @BeforeMethod
    public void setUp() {
        service = new BBCodeService();
    }

    @Test
    public void testQuote() {
        String result = service.quote(SOURCE, USER);
        assertEquals(result, "[quote=\"name\"]source[/quote]");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testQuoteNullSource() {
        service.quote(null, USER);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testQuoteNullAuthor() {
        service.quote(SOURCE, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testQuoteNullAuthorAndSource() {
        service.quote(null, null);
    }
}

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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;

public class SimplePageTest {

    private static final String NAME = "name";
    private static final String CONTENT = "content";
    private static final String PATH_NAME = "pathName";

    private SimplePage simplePage;

    @Test
    public void testGetName() throws Exception {
        simplePage = new SimplePage(NAME, CONTENT, PATH_NAME);
        assertEquals(simplePage.getName(), "name");
    }

    @Test
    public void testGetContent() throws Exception {
        simplePage = new SimplePage(NAME, CONTENT, PATH_NAME);
        assertEquals(simplePage.getContent(), "content");
    }

    @Test
    public void testGetPathName() throws Exception {
        simplePage = new SimplePage(NAME, CONTENT, PATH_NAME);
        assertEquals(simplePage.getPathName(), "pathName");
    }

    @Test
    public void testNotEmptyName() throws Exception {
        simplePage = new SimplePage(NAME, CONTENT, PATH_NAME);
        assertNotSame(simplePage.getName(), "");
    }

}

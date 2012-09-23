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

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Mikhail Zaitsev
 */
public class ViewTopicsBranchesTest {

    @Test
    public void testFunctions(){
        ViewTopicsBranches viewTopicsBranches = new ViewTopicsBranches();
        viewTopicsBranches.setId(1L);
        viewTopicsBranches.setBranchId(2L);
        viewTopicsBranches.setSid("testSid");
        assertEquals(viewTopicsBranches.getId(),1);
        assertEquals((Object) viewTopicsBranches.getBranchId(),2L);
        assertEquals(viewTopicsBranches.getSid(), "testSid");
    }
}

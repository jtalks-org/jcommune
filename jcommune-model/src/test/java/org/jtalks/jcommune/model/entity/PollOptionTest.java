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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Anuar Nurmakanov
 */
public class PollOptionTest {
    @Test
    public void testIncreaseVoteCount() {
        int initialVotesCount = 10;
        PollItem option = new PollItem("Option");
        option.setVotesCount(initialVotesCount);

        option.increaseVotesCount();

        Assert.assertEquals(option.getVotesCount(), initialVotesCount + 1,
                "Count of votes hasn't been increased.");
    }
}

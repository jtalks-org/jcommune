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

import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Item;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class RssViewerTest {
    private RssViewer rssViewer;
    private Channel channel;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private Map model;
    private Topic topic;

    @BeforeMethod
    protected void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        rssViewer = new RssViewer();
        channel = new Channel();
        model = new HashMap();
        List<Topic> topics = new ArrayList<Topic>();
        User user = new User("", "", "");
        topic = new Topic(user, "");
        topics.add(topic);
        topics.add(topic);
        model.put("topics", topics);
    }

    @Test
    public void testBuildFeed() throws Exception {

        List<Item> items = rssViewer.buildFeedItems(model, request, response);
        assertEquals(items, items);

        rssViewer.buildFeedMetadata(model, channel, request);
        assertFalse(channel.equals(new Channel()));
    }

}

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

package org.jtalks.jcommune.web.view;

import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.feed.rss.Channel;
import org.jtalks.common.model.entity.Component;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.*;

/**
 * @author Andrey Kluev
 */
public class RssViewerTest {
    private RssViewer rssViewer;
    private RssViewer rssViewerMock;
    private Channel channel;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private Map<String, Object> newsComponents;

    @BeforeMethod
    protected void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        rssViewer = new RssViewer();
        rssViewer.setContentType("application/rss+xml;charset=UTF-8");
        rssViewerMock = mock(RssViewer.class);
        channel = new Channel();
        newsComponents = getNewsComponents();
    }

    @Test
    public void testBuildFeedItems() throws Exception {

        List<Item> items = rssViewer.buildFeedItems(newsComponents, request, response);
        assertEquals(items.get(0).getAuthor(), "username");
        assertEquals(response.getContentType(), rssViewer.getContentType());
    }

    @Test
    public void testFeedBodyMustStripInvalidXMLSymbols() throws Exception {
        newsComponents.put("topics", getTopicsWithXMLSpecChars());
        List<Item> items = rssViewer.buildFeedItems(newsComponents, request, response);
        assertTrue(containtsOnlyValidXMLChars(items.get(0).getDescription().getValue()));
    }

    @Test
    public void testRedirect() throws IOException {

        newsComponents.put("topics", null);

        assertEquals(rssViewer.buildFeedItems(newsComponents, request, response), null);
    }

    @Test
    public void rssShouldBeGeneratedWithMetaDataFromComponent() throws Exception {
        Component component = new Component();
        String name = "my component";
        String description = "my description";
        component.setName(name);
        component.setDescription(description);
        newsComponents.put("forumComponent", component);

        rssViewer.buildFeedMetadata(newsComponents, channel, request);
        assertFalse(channel.equals(new Channel()));
        assertEquals(channel.getTitle(), name);
        assertEquals(channel.getDescription(), description);
    }

    @Test
    public void rssShouldBeGeneratedWithEmptyFeedMetaDataWhenThereIsNoComponent() {
        rssViewer.buildFeedMetadata(newsComponents, channel, request);
        assertTrue(channel.getTitle().isEmpty());
        assertTrue(channel.getDescription().isEmpty());
    }

    @Test
    public void testRssFeed() throws Exception {

        rssViewerMock.buildFeedMetadata(newsComponents, channel, request);
        rssViewerMock.buildFeedItems(newsComponents, request, response);

        verify(rssViewerMock).buildFeedMetadata(newsComponents, channel, request);
        verify(rssViewerMock).buildFeedItems(newsComponents, request, response);
    }

    private boolean containtsOnlyValidXMLChars(String stringToValidate) {
        String pattern = "[^"
                + "\u0009\r\n"
                + "\u0020-\uD7FF"
                + "\uE000-\uFFFD"
                + "\ud800\udc00-\udbff\udfff"
                + "]";
        String resultString = stringToValidate.replaceAll(pattern, "");
        return resultString.equals(stringToValidate);
    }

    private List<Topic> getTopicsWithXMLSpecChars(){
        JCUser user = new JCUser("username", "email", "password");
        List<Topic> topicsWithSpecChars = new ArrayList<>();
        Post postWithSpecChars = new Post(user, "����\u000F���");
        Topic topicWithSpecChars = new Topic(user, "");
        topicWithSpecChars.addPost(postWithSpecChars);
        topicWithSpecChars.setId(2L);
        topicsWithSpecChars.add(topicWithSpecChars);

        return topicsWithSpecChars;
    }
    private Map<String, Object> getNewsComponents()  {
        Map<String, Object> newsComponents = new HashMap<>();
        List<Topic> topics = new ArrayList<>();
        JCUser user = new JCUser("username", "email", "password");
        user.setSignature("Signature");
        Post post = new Post(user, "sagjalighjh eghjwhjslhjsdfhdfhljdfh");
        Topic topic = new Topic(user, "");
        topic.addPost(post);
        topic.setId(1L);
        topics.add(topic);
        topics.add(topic);
        newsComponents.put("topics", topics);

        return newsComponents;
    }
}

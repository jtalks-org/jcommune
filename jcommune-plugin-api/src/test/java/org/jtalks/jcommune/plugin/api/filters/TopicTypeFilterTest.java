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
package org.jtalks.jcommune.plugin.api.filters;

import org.jtalks.jcommune.plugin.api.core.Plugin;
import org.jtalks.jcommune.plugin.api.core.TopicPlugin;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Dmitry S. Dolzhenko
 */
public class TopicTypeFilterTest {
    @Test
    public void filterShouldAcceptTopicPluginWithCorrectTopicType() throws Exception {
        TopicPlugin topicPlugin = getPluginWithTopicType("type");

        TopicTypeFilter topicTypeFilter = new TopicTypeFilter("type");
        assertTrue(topicTypeFilter.accept(topicPlugin));
    }

    @Test
    public void filterShouldRejectTopicPluginWithIncorrectTopicType() throws Exception {
        TopicPlugin topicPlugin = getPluginWithTopicType("type");

        TopicTypeFilter topicTypeFilter = new TopicTypeFilter("type1");
        assertFalse(topicTypeFilter.accept(topicPlugin));
    }

    @Test
    public void filterShouldRejectNotTopicPlugin() {
        Plugin plugin = mock(Plugin.class);

        TopicTypeFilter topicTypeFilter = new TopicTypeFilter("type");
        assertFalse(topicTypeFilter.accept(plugin));
    }

    @Test
    public void filterShouldRejectTopicPlugin_IfTopicTypeCaseDoesNotMatch() {
        TopicPlugin topicPlugin = getPluginWithTopicType("type");

        TopicTypeFilter topicTypeFilter = new TopicTypeFilter("Type");
        assertFalse(topicTypeFilter.accept(topicPlugin));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void filterShouldThrowException_IfTopicTypeIsNull() {
        new TopicTypeFilter(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void filterShouldThrowException_IfTopicTypeIsEmpty() {
        new TopicTypeFilter("");
    }

    private TopicPlugin getPluginWithTopicType(String type) {
        TopicPlugin topicPlugin = mock(TopicPlugin.class);
        when(topicPlugin.getTopicType()).thenReturn(type);

        return topicPlugin;
    }
}
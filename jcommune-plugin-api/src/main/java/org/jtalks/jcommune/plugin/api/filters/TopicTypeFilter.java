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

import org.apache.commons.lang.Validate;
import org.jtalks.jcommune.plugin.api.core.Plugin;
import org.jtalks.jcommune.plugin.api.core.TopicPlugin;

import java.util.Objects;

/**
 * @author Dmitry S. Dolzhenko
 */
public class TopicTypeFilter implements PluginFilter {

    private final String topicType;

    public TopicTypeFilter(String topicType) {
        Validate.notEmpty(topicType, "Could not lookup plugin by empty topicType: [" + topicType + "]");

        this.topicType = topicType;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean accept(Plugin plugin) {
        if (!(plugin instanceof TopicPlugin)) {
            return false;
        }

        return Objects.equals(topicType, ((TopicPlugin) plugin).getTopicType());
    }
}

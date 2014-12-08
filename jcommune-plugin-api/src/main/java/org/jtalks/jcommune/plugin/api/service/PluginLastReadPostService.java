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
package org.jtalks.jcommune.plugin.api.service;

import org.jtalks.jcommune.model.entity.Topic;

/**
 * @author Mikhail Stryzhonok
 */
public interface PluginLastReadPostService {


    /**
     * Marks topic page as read for the current user.
     * That means all posts on this page are to marked as read.
     * If paging as disabled all posts in the topic will be marked as read.
     * <p/>
     * For anonymous user call will have no effect.
     *
     * @param topic   topic to mark as read
     * @param pageNum page to mark as read
     */
    void markTopicPageAsRead(Topic topic, int pageNum);
}

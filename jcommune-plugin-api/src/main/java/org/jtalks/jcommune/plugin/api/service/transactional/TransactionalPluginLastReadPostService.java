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
package org.jtalks.jcommune.plugin.api.service.transactional;

import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.plugin.api.service.PluginLastReadPostService;

/**
 * @author Mikhail Stryzhonok
 */
public class TransactionalPluginLastReadPostService implements PluginLastReadPostService {

    private static final TransactionalPluginLastReadPostService INSTANCE = new TransactionalPluginLastReadPostService();
    private PluginLastReadPostService lastReadPostService;

    private TransactionalPluginLastReadPostService(){

    }

    public static TransactionalPluginLastReadPostService getInstance() {
        return INSTANCE;
    }

    public void setLastReadPostService(PluginLastReadPostService lastReadPostService) {
        this.lastReadPostService = lastReadPostService;
    }


    @Override
    public void markTopicPageAsRead(Topic topic, int pageNum) {
        lastReadPostService.markTopicPageAsRead(topic, pageNum);
    }
}

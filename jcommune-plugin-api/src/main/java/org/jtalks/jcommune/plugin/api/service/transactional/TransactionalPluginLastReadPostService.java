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
 * Class for marking topics provided by plugins as read.
 * For core topic types use classes from service module
 *
 * This class is  singleton because we can't use spring dependency injection mechanism in plugins due plugins can be
 * added or removed in runtime.
 *
 * @author Mikhail Stryzhonok
 */
public class TransactionalPluginLastReadPostService implements PluginLastReadPostService {

    private static final TransactionalPluginLastReadPostService INSTANCE = new TransactionalPluginLastReadPostService();
    private PluginLastReadPostService lastReadPostService;

    /** Use {@link #getInstance()}, this class is singleton. */
    private TransactionalPluginLastReadPostService(){

    }

    /**
     * Gets instance of {@link TransactionalPluginLastReadPostService}
     *
     * @return instance of {@link TransactionalPluginLastReadPostService}
     */
    public static PluginLastReadPostService getInstance() {
        return INSTANCE;
    }

    /**
     * Sets last read post service. Should be used once, during initialization
     *
     * @param lastReadPostService last read post service to set
     */
    public void setLastReadPostService(PluginLastReadPostService lastReadPostService) {
        this.lastReadPostService = lastReadPostService;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public void markTopicAsRead(Topic topic) {
        lastReadPostService.markTopicAsRead(topic);
    }
}

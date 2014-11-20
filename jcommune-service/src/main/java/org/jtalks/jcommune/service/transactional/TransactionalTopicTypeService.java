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
package org.jtalks.jcommune.service.transactional;

import org.jtalks.jcommune.model.dao.TopicTypeDao;
import org.jtalks.jcommune.model.entity.TopicType;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.service.TopicTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transactional topic type service implementation. This class contains method needed to manipulate with topic type
 * persistent entity.
 *
 * @author Mikhail Stryzhonok
 */
public class TransactionalTopicTypeService implements TopicTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalTopicTypeService.class);

    private TopicTypeDao topicTypeDao;

    /**
     * @param topicTypeDao dao object to manipulate {@link TopicType} entities
     */
    public TransactionalTopicTypeService(TopicTypeDao topicTypeDao) {
        this.topicTypeDao = topicTypeDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createNewTopicType(String name) {
        TopicType topicType = new TopicType(name);
        topicTypeDao.saveOrUpdate(topicType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TopicType getTopicTypeByName(String name) throws NotFoundException {
        TopicType topicType = topicTypeDao.getByName(name);
        if (topicType == null) {
            String msg = "Can't found topic type for name " + name;
            LOGGER.info(msg);
            throw new NotFoundException(msg);
        }
        return topicType;
    }
}

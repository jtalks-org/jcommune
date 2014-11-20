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
package org.jtalks.jcommune.service;

import org.jtalks.jcommune.model.entity.TopicType;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;

/**
 * Service for manipulating {@link TopicType} entity
 *
 * @author Mikhail Stryzhonok
 */
public interface TopicTypeService {

    /**
     * Creates new topic type with specified name
     *
     * @param name name of new topic type
     */
    public void createNewTopicType(String name);

    /**
     * Gets topic type with specified name
     *
     * @param name name of interested topic type
     *
     * @return topic type with specified name
     *
     * @throws NotFoundException if no topic types found
     */
    public TopicType getTopicTypeByName(String name) throws NotFoundException;
}

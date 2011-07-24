/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.poulpe.service;

import java.util.Collection;
import java.util.List;
import org.jtalks.poulpe.model.entity.TopicType;
import org.jtalks.poulpe.service.exceptions.NotUniqueException;

/**
 * Service for operations with {@link TopicType}
 * 
 * @author Pavel Vervenko
 */
public interface TopicTypeService extends EntityService<TopicType> {

    /**
     * Get all topic types.
     * @return the list of the TopicType
     */
    List<TopicType> getAll();

    /**
     * Delete the specified TopicType.
     * @param topicType topicType to delete
     */
    void deleteTopicType(TopicType topicType);
    
    /**
     * Delete the specified TopicType.
     * @param topicType collection to delete
     */
    void deleteTopicTypes(Collection<TopicType> topicType);

    /**
     * Save new or update TopicType.
     * @param topicType topicType to save
     * @throws NotUniqueException 
     */
    void saveTopicType(TopicType topicType) throws NotUniqueException;
    
    /**
     * Check if type of topic with given name exists.
     * @param topicTypeName
     * @return true if exists
     */
    boolean isTopicTypeNameExists(String topicTypeName);
}

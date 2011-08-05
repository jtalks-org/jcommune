package org.jtalks.poulpe.model.dao;

import java.util.List;

import org.jtalks.poulpe.model.entity.TopicType;

/**
 * DAO for persistent operations with {@link TopicType}
 * 
 * @author Vladimir Bukhtoyarov 
 *
 */
public interface TopicTypeDao extends Dao<TopicType> {
	
	/**
     * Get all topic types.
     * @return the list of the TopicType
     */
    List<TopicType> getAll();

    /**
     * Check if type of topic with given name exists.
     * @param topicTypeName
     * @return true if exists
     */
    boolean isTopicTypeNameExists(String topicTypeName);

}

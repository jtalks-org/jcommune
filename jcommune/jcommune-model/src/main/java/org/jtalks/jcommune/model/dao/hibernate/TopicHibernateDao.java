/* 
 * JTalks for uniting people
 * Copyright (C) 2011  JavaTalks Team
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * Also add information on how to contact you by electronic and paper mail.
 * 
 * This file creation date: Apr 12, 2011 / 8:05:19 PM
 * The JTalks Project
 * http://www.jtalks.org
 */
package org.jtalks.jcommune.model.dao.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.jtalks.jcommune.model.entity.Topic;

/**
 * Data Access Object for {@link Topic} instances.
 * 
 * @author Pavel Vervenko
 */
public class TopicHibernateDao extends AbstractHibernateDao<Topic>{ 
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveOrUpdate(Topic topic) {
        getSession().save(topic);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) {
       Query query = getSession().createQuery("delete Topic where id= :topicId");
        query.setLong("topicId", id);
        query.executeUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Topic get(Long id) {
        return (Topic) getSession().load(Topic.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Topic> getAll() {
        return getSession().createQuery("from Topic").list();
    }

    /**
     * Load the Topic with userCreated field initialized. The method doesn't load related posts.
     * @param id Topic id
     * @return the Topic or null if the appropriate topic wasn't found
     */

    public Topic getTopicWithUser(Long id) {
        Query query = getSession().createQuery("from Topic as topic "
                + "join fetch topic.userCreated "
                + "WHERE topic.id = :topicId");
        query.setLong("topicId", id);
        return (Topic) query.uniqueResult();
    }

    /**
     * Load the Topic with userCreated and related posts.
     * @param id Topic id
     * @return loaded Topic or null if the appropriate topic wasn't found
     */
    
    public Topic getTopicWithPosts(Long id) {
        Query query = getSession().createQuery("from Topic as topic "
                + "join fetch topic.userCreated "
                + "join fetch topic.posts "
                + "WHERE topic.id = :topicId");
        query.setLong("topicId", id);
        return (Topic) query.uniqueResult();
    }
}

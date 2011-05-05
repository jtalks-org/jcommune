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

import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.Topic;
import java.util.List;
import org.hibernate.Query;

/**
 * Hibernate DAO implementation from the {@link Topic}.
 * 
 * @author Pavel Vervenko
 */
public class TopicHibernateDao extends AbstractHibernateDao<Topic> implements TopicDao {

    /**
     * Save or update the Topic to the database. 
     * The operation apples cascadely to the associated Posts.
     * @param topic Topic to save
     */
    @Override
    public void saveOrUpdate(Topic topic) {
        getSession().save(topic);
    }

    /**
     * Delete the Topic from the database. 
     * Associated Posts will be also deleted cascadely.
     * @param id The id of the Topic
     */
    @Override
    public void delete(Long id) {
        Query query = getSession().createQuery("delete Topic where id= :topicId");
        query.setLong("topicId", id);
        query.executeUpdate();
    }

    /**
     * Load the topic form the DB by it's id.
     * The associated User created the topic will be loaded too.
     * @param id The id of the Topic
     */
    @Override
    public Topic get(Long id) {
        return (Topic) getSession().get(Topic.class, id);
    }

    /**
     * Load the full list of Topics. 
     * The topicStarter field will be populated, but related posts will not be loaded.
     * @return the list of Topics
     */
    @Override
    public List<Topic> getAll() {
        return getSession().createQuery("from Topic").list();
    }

    /**
     * {@inheritDoc}
     * @deprecated This method is not needed any more. Use {@link get(id)} instead,
     * it returns Topic with User now.
     */
    @Override
    public Topic getTopicWithUser(Long id) {
        return get(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Topic getTopicWithPosts(Long id) {
        Query query = getSession().getNamedQuery("getTopicWithPosts");
        query.setLong("topicId", id);
        return (Topic) query.uniqueResult();
    }
}

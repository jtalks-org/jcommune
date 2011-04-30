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
package org.jtalks.jcommune.service.transactional;


import org.jtalks.jcommune.model.dao.Dao;
import org.jtalks.jcommune.model.dao.hibernate.TopicHibernateDao;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.TopicService;

import java.util.ArrayList;
import java.util.List;

/**
 * Topic service class. This class contains method needed to manipulate with Topic persistent entity.
 *
 * @author Osadchuck Eugeny
 */
public class TransactionalTopicService extends AbstractTransactionlaEntityService<Topic> implements TopicService {

    private Dao<Topic> topicDao;

     private Topic topicWithUser;
     private Topic topicWithOutUser;


    /**
     * Create an instance of User entity based service
     *
     * @param dao - data access object, which should be able do all CRUD operations with topic entity.
     */
    public TransactionalTopicService(Dao<Topic> dao) {
        topicDao = dao;
    }

    @Override
     protected Dao<Topic> getDao() {
        return topicDao;
    }

    @Override
    public Topic getTopic(long id, boolean isLoadPosts) {
        TopicHibernateDao topicHibernateDao = (TopicHibernateDao) topicDao;
        Topic topic;
        if (isLoadPosts) {
            topic =  topicHibernateDao.getTopicWithPosts(id);
        } else {
            topic = topicHibernateDao.getTopicWithUser(id);
        }
        return topic;
    }

    @Override
    public List<Topic> getAllTopicsWithUsers() {
       TopicHibernateDao topicHibernateDao = (TopicHibernateDao) topicDao;
        
        List<Topic> topicsWithOutUsers = topicHibernateDao.getAll();
        List<Topic> topicsWithUsers = new ArrayList<Topic>(topicsWithOutUsers.size());
        for (int i = 0; i <= topicsWithOutUsers.size(); i++) {
            topicWithOutUser = topicsWithOutUsers.get(i);
            long topicID = topicWithOutUser.getId();
            topicWithUser = topicHibernateDao.getTopicWithUser(topicID);
            topicsWithUsers.add(topicWithUser);
        }
        return topicsWithUsers;
    }
}

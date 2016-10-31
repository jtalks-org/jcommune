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
package org.jtalks.jcommune.test.utils

import org.jtalks.jcommune.model.dao.LastReadPostDao
import org.jtalks.jcommune.model.dao.TopicDao
import org.jtalks.jcommune.model.dao.UserDao
import org.jtalks.jcommune.model.entity.Branch
import org.jtalks.jcommune.model.entity.JCUser
import org.jtalks.jcommune.model.entity.Post
import org.jtalks.jcommune.model.entity.Topic
import org.jtalks.jcommune.test.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.MockMvc

import javax.servlet.http.HttpSession

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

/**
 * @author Pavel Vervenko
 */
class Topics {
    @Autowired TopicDao topicDao;
    @Autowired UserDao userDao
    @Autowired MockMvc mockMvc
    @Autowired LastReadPostDao lastReadPostDao;

    Topic created(User user, Branch branch) {
        JCUser jcUser = userDao.getByEmail(user.email);
        def topic = new Topic(jcUser, "title", "Discussion")
        Post first = new Post(jcUser, "post text");
        topic.setBranch(branch);
        topic.addPost(first);
        topicDao.saveOrUpdate(topic);
        return topic;
    }

    def markAsRead(HttpSession session, Topic topic, int page) {
         mockMvc.perform(get("/topics/${topic.id}/page/$page/markread")
                 .session(session as MockHttpSession)
         );
    }

    boolean isMarkedAsRead(User user, Topic topic) {
        JCUser jcUser = userDao.getByEmail(user.email);
        def post = lastReadPostDao.getLastReadPost(jcUser, topic);
        return post != null;
    }
}

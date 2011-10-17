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

import org.joda.time.DateTime;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.exceptions.InvalidHttpSessionException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * This interface should have methods which give us more abilities in manipulating Topic persistent entity.
 *
 * @author Osadchuck Eugeny
 * @author Vervenko Pavel
 * @author Kirill Afonin
 * @author Vitaliy Kravchenko
 */
public interface TopicService extends EntityService<Topic> {

    /**
     * Add the answer to the topic. Add the specified message to the target topic and save.
     * User should be authorized to answer to the topic. Otherwise {@link IllegalStateException} will be thrown.
     *
     * @param topicId    target topic primary id.
     * @param answerBody the text of the answer
     * @return created {@link Post}
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic not found
     */
    Post addAnswer(long topicId, String answerBody) throws NotFoundException;

    /**
     * Add new topic with given title and body.
     * Author is current user.
     *
     * @param topicName name of topic
     * @param bodyText  body of topic
     * @param branchId  branch containing topic
     * @return created topic
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when branch not found
     */
    Topic createTopic(String topicName, String bodyText, long branchId) throws NotFoundException;

    /**
     * Get posts range from branch.
     *
     * @param branchId branch id from which we obtain topics
     * @param start    start index of topic
     * @param max      number of topics
     * @return list of {@code Topic} objects with size {@code max}
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when branch not found
     */
    List<Topic> getTopicRangeInBranch(long branchId, int start, int max) throws NotFoundException;

    /**
     * Get topic past last 24 hour.
     *
     * @param start start index of topic
     * @param max   number of topics
     * @return list of {@code Topic} objects with size {@code max}
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when branch not found
     */
    List<Topic> getAllTopicsPastLastDay(int start, int max, DateTime lastLogin);

    /**
     * Get number of topics in branch.
     *
     * @param branchId branch id where you have to count topics
     * @return number of topics in branch
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when branch not found
     */
    int getTopicsInBranchCount(long branchId) throws NotFoundException;

    /**
     * Get number of topics past last 24 hour.
     *
     * @return number of topics
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when branch not found
     */
    int getTopicsPastLastDayCount(DateTime lastLogin);

    /**
     * Update current topic with given title and body.
     *
     * @param topicId   topic id
     * @param topicName name of topic
     * @param bodyText  body of topic
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic not found
     */
    void saveTopic(long topicId, String topicName, String bodyText) throws NotFoundException;

    /**
     * Update current topic with given title and body.
     *
     * @param topicId      topic id
     * @param topicName    name of topic
     * @param bodyText     body of topic
     * @param topicWeight  priority for sticked topic
     * @param sticked      flag for sticking a topic
     * @param announcement flag, which set topic as announcement
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic not found
     */
    void saveTopic(long topicId, String topicName, String bodyText, int topicWeight,
                   boolean sticked, boolean announcement) throws NotFoundException;

    /**
     * Delete topic by id.
     *
     * @param topicId topic id
     * @return branch from which topic deleted
     * @throws NotFoundException when topic not found
     */
    Branch deleteTopic(long topicId) throws NotFoundException;

    /**
     * Topic views count increment
     *
     * @param topic topic that is viewed now
     * @param session current session that contains viewed topics Ids
     * @throws NotFoundException when topic not found
     */
    void addTopicView(Topic topic, HttpSession session) throws NotFoundException, InvalidHttpSessionException;
}

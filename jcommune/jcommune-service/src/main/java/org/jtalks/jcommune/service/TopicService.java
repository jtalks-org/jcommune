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
package org.jtalks.jcommune.service;

import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.exceptions.NotFoundException;

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
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic not found
     */
    void addAnswer(long topicId, String answerBody) throws NotFoundException;

    /**
     * Add new topic with given title and body.
     * Author is current user.
     *
     *
     * @param topicName name of topic
     * @param bodyText  body of topic
     * @param branchId  branch containing topic
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when branch not found
     * @return created topic
     */
    Topic createTopic(String topicName, String bodyText, long branchId) throws NotFoundException;

    /**
     * Delete post from topic.
     *
     * @param topicId topic id.
     * @param postId  post id.
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic or post not found
     */
    void deletePost(long topicId, long postId) throws NotFoundException;

    /**
     * Get posts range from branch.
     *
     * @param branchId branch id from which we obtain topics
     * @param start    start index of topic
     * @param max      number of topics
     * @return list of {@code Topic} objects with size {@code max}
     */
    List<Topic> getTopicRangeInBranch(long branchId, int start, int max);

    /**
     * Get number of topics in branch.
     *
     * @param branchId branch id where you have to count topics
     * @return number of topics in branch
     */
    int getTopicsInBranchCount(long branchId);
}

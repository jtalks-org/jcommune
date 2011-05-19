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
import org.jtalks.jcommune.service.exceptions.UserNotLoggedInException;

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
     * Get topic with fetched topics fields(userCreated, posts).
     *
     * @param id - topic primary id.
     * @return - <code>Topic<code> with fetched topic fields or null if no topic found by this primary id.
     */
    Topic getTopicWithPosts(long id);

    /**
     * Add the answer to the topic. Add the specified message to the target topic and save.
     * User should be authorized to answer to the topic. Otherwise {@link UserNotLoggedInException} will be thrown.
     *
     * @param topicId    target topic primary id.
     * @param answerBody the text of the answer
     */
    void addAnswer(long topicId, String answerBody);

    /**
     * Get all topics according to branch Id
     * @param id Topic Branch id
     * @return  List of Topics, if no corresponding Topics it will return empty List
     */
    List<Topic> getAllTopicsAccordingToBranch(Long id);

    /**
     * /**
     * Add new topic with given title and body.
     * Author is current user.
     *
     * @param topicName name of topic
     * @param bodyText  body of topic
     * @param branchId
     */
    void createTopic(String topicName, String bodyText, long branchId);

}

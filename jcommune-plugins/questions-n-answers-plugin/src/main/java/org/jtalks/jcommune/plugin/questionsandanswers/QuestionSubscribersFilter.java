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
package org.jtalks.jcommune.plugin.questionsandanswers;

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.SubscriptionAwareEntity;
import org.jtalks.jcommune.plugin.api.core.SubscribersFilter;

import java.util.Collection;

/**
 * Implementation of {@link SubscribersFilter} of Questions and Answers plugin
 * Provides possibility to send notifications only to author of post when comment is added
 *
 * @author Mikhail Stryzhonok
 */
public class QuestionSubscribersFilter implements SubscribersFilter {

    /**
     * {@inheritDoc}
     */
    @Override
    public void filter(Collection<JCUser> users, SubscriptionAwareEntity entity) {
        if (entity instanceof Post
                && ((Post)entity).getTopic().getType().equals(QuestionsAndAnswersPlugin.TOPIC_TYPE)) {
            JCUser postCreator = ((Post)entity).getUserCreated();
            boolean containsCreator = users.contains(postCreator);
            users.clear();
            if (containsCreator) {
                users.add(((Post) entity).getUserCreated());
            }
        }
    }
}

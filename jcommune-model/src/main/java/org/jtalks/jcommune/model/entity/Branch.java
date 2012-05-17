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
package org.jtalks.jcommune.model.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Forum branch that contains topics related to branch theme.
 *
 * @author Vitaliy Kravchenko
 * @author Kirill Afonin
 * @author Max Malakhov
 */
public class Branch extends org.jtalks.common.model.entity.Branch 
                    implements SubscriptionAwareEntity {

    private List<Topic> topics = new ArrayList<Topic>();
    private Set<JCUser> subscribers = new HashSet<JCUser>();

    /**
     * For Hibernate use only
     */
    protected Branch() {}

    /**
     * Creates the Branch instance with required fields.
     *
     * @param name branch name
     */
    public Branch(String name, String description) {
        super(name, description);
    }

    /**
     * Returns the next topic for the topic given.
     * This method is sorting aware, i. e. all the sorting applied
     * in topic selection query will be taken into account.
     *
     * @param topic a topic to found the next one for
     * @return next topic or null, if the argument is the last topic in the branch
     */
    public Topic getNextTopic(Topic topic) {
        int index = this.getTopicIndexInList(topic);
        if (index == topics.size() - 1) {
            return null;
        } else {
            return topics.get(index + 1);
        }
    }

    /**
     * Returns the previous topic for the topic given.
     * This method is sorting aware, i. e. all the sorting applied
     * in topic selection query will be be taken into account.
     *
     * @param topic a topic to found predecessor for
     * @return previous topic or null, if the argument is the first topic in the branch
     */
    public Topic getPreviousTopic(Topic topic) {
        int index = this.getTopicIndexInList(topic);
        if (index == 0) {
            return null;
        } else {
            return topics.get(index - 1);
        }
    }

    /**
     * Gets topic index in the branch or throws an exception if there is no such
     * a topic in the branch
     *
     * @param topic topic to find index for
     * @return index of the topic branch's collection
     */
    private int getTopicIndexInList(Topic topic) {
        int index = topics.indexOf(topic);
        if (index == -1) {
            throw new IllegalArgumentException("There is no such topic in the branch");
        } else {
            return index;
        }
    }


    /**
     * Get the branch's last updated topic.
     * Updates include post addition/update/removal or changes in the topic itself.
     *
     * @return last topic or null if there are no topics in the branch
     */
    public Topic getLastUpdatedTopic() {
        if (topics.isEmpty()) {
            return null;
        }
        int lastTopicIndex = 0;
        for (int i = 1; i < this.getTopicCount(); i++) {
            if (topics.get(i).getModificationDate().isAfter(topics.get(lastTopicIndex).getModificationDate())) {
                lastTopicIndex = i;
            }
        }
        return topics.get(lastTopicIndex);
    }

    /**
     * @return list of topics
     */
    public List<Topic> getTopics() {
        return topics;
    }

    /**
     * @param topics list of topics
     */
    protected void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    /**
     * Add topic to branch.
     *
     * @param topic topic
     */
    public void addTopic(Topic topic) {
        topic.setBranch(this);
        this.topics.add(topic);
    }

    /**
     * Delete topic from branch.
     *
     * @param topic topic
     */
    public void deleteTopic(Topic topic) {
        this.topics.remove(topic);
    }

    /**
     * @return count topics in branch
     */
    public int getTopicCount() {
        return topics.size();
    }

    /**
     * Returns a sum of all topic's post count for that branch
     *
     * @return sum of post count for all the topics in this branch
     */
    public int getPostCount() {
        int postCount = 0;
        for (Topic topic : topics) {
            postCount += topic.getPostCount();
        }
        return postCount;
    }
    
    /**
     *  {@inheritDoc}
     */
    public Set<JCUser> getSubscribers() {
        return subscribers;
    }

    /**
     * {@inheritDoc}
     */
    public void setSubscribers(Set<JCUser> subscribers) {
        this.subscribers = subscribers;
    }

}

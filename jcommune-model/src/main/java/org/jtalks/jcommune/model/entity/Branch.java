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

import ch.lambdaj.Lambda;
import org.apache.commons.lang.Validate;

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
 * @author masyan
 * @author Anuar_Nurmakanov
 */
public class Branch extends org.jtalks.common.model.entity.Branch
        implements SubscriptionAwareEntity {

    public static final int BRANCH_NAME_MAX_LENGTH = 80;
    public static final int BRANCH_DESCRIPTION_MAX_LENGTH = 255;

    public static final String URL_SUFFIX = "/branches/";
    private List<Topic> topics = new ArrayList<>();
    private Set<JCUser> subscribers = new HashSet<>();

    private Integer topicsCount;
    private Integer postsCount;
    private boolean unreadPosts;
    private Post lastPost;

    /**
     * For Hibernate use only
     */
    protected Branch() {
    }

    /**
     * Creates the Branch instance with required fields.
     *
     * @param name        unique branch name
     * @param description branch description
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
        }
        else {
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
        }
        else {
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
        Validate.isTrue(index != -1, "There is no such topic in the branch");
        return index;
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
        if (topicsCount == null) {
            return topics.size();
        }
        return topicsCount;
    }

    /**
     * Returns a sum of all topic's post count for that branch.
     * <p/>
     * Value is computed only for the first time (if not set explicitly before),
     * so it may not take into account the posts added later
     *
     * @return sum of post count for all the topics in this branch
     */
    public int getPostCount() {
        if (postsCount == null) {
            postsCount = Lambda.sumFrom(topics, Topic.class).getPostCount();
        }
        return postsCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<JCUser> getSubscribers() {
        return subscribers;
    }

    /**
     * {@inheritDoc}
     */
    public void setSubscribers(Set<JCUser> subscribers) {
        this.subscribers = subscribers;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The target URL has the next format http://{forum root}/branches/{id}
     */
    @Override
    public String getUrlSuffix() {
        return URL_SUFFIX + getId();
    }

    /**
     * Set count of topics in this branch.
     *
     * @param topicsCount count of posts in this branch
     */
    public void setTopicsCount(Integer topicsCount) {
        this.topicsCount = topicsCount;
    }

    /**
     * Set count of posts in this branch.
     *
     * @param postsCount count of posts in this branch
     */
    public void setPostsCount(Integer postsCount) {
        this.postsCount = postsCount;
    }


    /**
     * Returns state of unread posts in branch to user
     *
     * @return state of unread posts
     */
    public boolean isUnreadPosts() {
        return this.unreadPosts;
    }

    /**
     * Set state of unread posts in branch to user
     *
     * @param unreadPosts actual state of unread posts
     */
    public void setUnreadPosts(boolean unreadPosts) {
        this.unreadPosts = unreadPosts;
    }

    /**
     * Get last post in this branch.
     * 
     * @return last post in this branch
     */
    public Post getLastPost() {
        return lastPost;
    }

    /**
     * Set last post in this branch.
     *
     */
    public void setLastPost(Post lastPost) {
        this.lastPost = lastPost;
    }
    
    /**
     * Checks whether a sent post is the last in the branch.
     * 
     * @param checkedPost checked post
     * @return {@code true} if sent post is the last in the branch,
     *         otherwise {@code false}
     */
    public boolean isLastPost(Post checkedPost) {
        Validate.notNull(checkedPost);
        return checkedPost.equals(lastPost);
    }
    
    /**
     * Clear the last post in the branch.
     */
    public void clearLastPost() {
        this.lastPost = null;
    }
}

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
package org.jtalks.jcommune.service.transactional;

import org.joda.time.DateTime;
import org.jtalks.jcommune.model.dao.LastReadPostDao;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.LastReadPost;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.LastReadPostService;
import org.jtalks.jcommune.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Performs last read posts management to track topic updates
 * since user's last visit.
 *
 * @author Evgeniy Naumenko
 * @author Anuar_Nurmakanov
 */
public class TransactionalLastReadPostService implements LastReadPostService {

    private UserService userService;
    private LastReadPostDao lastReadPostDao;
    private UserDao userDao;

    /**
     * Constructs an instance with required fields.
     * 
     * @param userService     to figure out the current user logged in
     * @param lastReadPostDao to save/read last read post information from a database
     * @param userDao         to save an information about user of forum
     */
    public TransactionalLastReadPostService(
            UserService userService,
            LastReadPostDao lastReadPostDao,
            UserDao userDao) {
        this.userService = userService;
        this.lastReadPostDao = lastReadPostDao;
        this.userDao = userDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Topic> fillLastReadPostForTopics(List<Topic> topics) {
        JCUser currentUser = userService.getCurrentUser();
        if (!currentUser.isAnonymous()) {
            for (Topic topic : topics) {
                Integer lastReadPostIndex = getLastReadPostIndex(topic, currentUser);
                if (lastReadPostIndex != null) {
                    topic.setLastReadPostIndex(lastReadPostIndex);
                }
            }
        }
        return topics;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getLastReadPostForTopic(Topic topic) {
        JCUser currentUser = userService.getCurrentUser();
        if (currentUser.isAnonymous()) {
            return null;
        } else {
            return getLastReadPostIndex(topic, currentUser);
        }
    }
    
    /**
     * Get last read post index for given topic.
     * 
     * @param topic for this topic we must find last read post index
     * @param currentUser current user for who we find last read post index in topic
     */
    private Integer getLastReadPostIndex(Topic topic, JCUser currentUser) {
        DateTime forumMarkedAsReadDate = currentUser.getAllForumMarkedAsReadTime();
        DateTime topicModificationDate = topic.getModificationDate(); 
        Integer lastReadPostIndex = null;
        if (forumMarkedAsReadDate != null && topicModificationDate.isBefore(forumMarkedAsReadDate)) {
            lastReadPostIndex = topic.getPostCount() - 1;
        } else {
            LastReadPost post = lastReadPostDao.getLastReadPost(currentUser, topic);
            if (post != null) {
                lastReadPostIndex = post.getPostIndex();
            }
        }
        return lastReadPostIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void markTopicPageAsRead(Topic topic, int pageNum, boolean pagingEnabled) {
        JCUser current = userService.getCurrentUser();
        if (!current.isAnonymous()) { // topics are always unread for anonymous users
            int postIndex = this.calculatePostIndex(current, topic, pageNum, pagingEnabled);
            saveLastReadPost(current, topic, postIndex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void markTopicAsRead(Topic topic) {
        JCUser current = userService.getCurrentUser();
        if (!current.isAnonymous()) { // topics are always unread for anonymous users
            saveLastReadPost(current, topic, topic.getPostCount() - 1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#branch.id, 'BRANCH', 'BranchPermission.VIEW_TOPICS')")
    public void markAllTopicsAsRead(Branch branch) {
        JCUser user = userService.getCurrentUser();
        if (!user.isAnonymous()) {
            lastReadPostDao.markAllRead(user, branch);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void markAllForumAsReadForCurrentUser() {
        JCUser currentUser = userService.getCurrentUser();
        
        currentUser.setAllForumMarkedAsReadTime(new DateTime());
        userDao.saveOrUpdate(currentUser);
        
        lastReadPostDao.deleteLastReadPostsFor(currentUser);
    }

    /**
     * Computes new last read post index based on the topic size and
     * current pagination settings.
     *
     * @param user          user to calculate index for
     * @param topic         topic to calculate index for
     * @param pageNum       page number co calculate last post seen by the user
     * @param pagingEnabled if paging is enabled on page. If so, last post index in topic is returned
     * @return new last post index, counting from 0
     */
    @PreAuthorize("hasPermission(#topic.branch.id, 'BRANCH', 'BranchPermission.VIEW_TOPICS')")
    private int calculatePostIndex(JCUser user, Topic topic, int pageNum, boolean pagingEnabled) {
        if (pagingEnabled) {  // last post on the page given
            int maxPostIndex = user.getPageSize() * pageNum - 1;
            return Math.min(topic.getPostCount() - 1, maxPostIndex);
        }
        else {              // last post in the topic
            return topic.getPostCount() - 1;
        }
    }

    /**
     * Stores last read post info in a database for the particular
     * topic and user.
     *
     * @param user      user to save last read post data for
     * @param topic     topic to store info for
     * @param postIndex actual post index, starting from 0
     */
    @PreAuthorize("hasPermission(#topic.branch.id, 'BRANCH', 'BranchPermission.VIEW_TOPICS')")
    private void saveLastReadPost(JCUser user, Topic topic, int postIndex) {
        LastReadPost post = lastReadPostDao.getLastReadPost(user, topic);
        if (post == null) {
            post = new LastReadPost(user, topic, postIndex);
        }
        else {
            post.setPostIndex(Math.max(Math.min(topic.getPostCount() - 1, post.getPostIndex()), postIndex));
        }
        lastReadPostDao.update(post);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#post.topic.branch.id, 'BRANCH', 'BranchPermission.VIEW_TOPICS')")
    public void updateLastReadPostsWhenPostIsDeleted(Post post) {
        List<LastReadPost> lastReadPosts = lastReadPostDao.listLastReadPostsForTopic(post.getTopic());
        for (LastReadPost lastReadPost : lastReadPosts) {
            int index = lastReadPost.getPostIndex();
            if (index >= post.getPostIndexInTopic()) {
                lastReadPost.setPostIndex(index - 1);
                lastReadPostDao.update(lastReadPost);
            }
        }
    }
}

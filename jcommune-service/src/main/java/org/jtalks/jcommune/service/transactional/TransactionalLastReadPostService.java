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

import org.apache.commons.collections.ListUtils;
import org.joda.time.DateTime;
import org.jtalks.jcommune.model.dao.LastReadPostDao;
import org.jtalks.jcommune.model.dao.BranchReadedMarkerDao;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.plugin.api.service.PluginLastReadPostService;
import org.jtalks.jcommune.service.LastReadPostService;
import org.jtalks.jcommune.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.ArrayList;
import java.util.List;

/**
 * Performs last read posts management to track topic updates
 * since user's last visit.
 *
 * @author Evgeniy Naumenko
 * @author Anuar_Nurmakanov
 */
public class TransactionalLastReadPostService implements LastReadPostService, PluginLastReadPostService {

    private final UserService userService;
    private final LastReadPostDao lastReadPostDao;
    private final UserDao userDao;
    private final BranchReadedMarkerDao branchReadedMarkerDao;
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
            UserDao userDao,
            BranchReadedMarkerDao branchReadedMarkerDao) {
        this.userService = userService;
        this.lastReadPostDao = lastReadPostDao;
        this.userDao = userDao;
        this.branchReadedMarkerDao = branchReadedMarkerDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Topic> fillLastReadPostForTopics(List<Topic> topics) {
        JCUser currentUser = userService.getCurrentUser();
        if (!currentUser.isAnonymous()) {
            List<Topic> notModifiedTopics = extractNotModifiedTopicsSinceForumMarkedAsRead(
                    currentUser, topics);
            for (Topic notModifiedTopic : notModifiedTopics) {
                Post lastPost = notModifiedTopic.getLastDisplayedPost();
                notModifiedTopic.setLastReadPostDate(lastPost.getCreationDate());
            }
            //
            @SuppressWarnings("unchecked")
            List<Topic> modifiedTopics = ListUtils.removeAll(topics, notModifiedTopics);
            fillLastReadPostsForModifiedTopics(modifiedTopics, currentUser);
        }
        return topics;
    }

    /**
     * Extract topics that don't have modifications after marking all forum as read
     * or after marking their branch as read.
     *
     * @param currentUser the current user of application
     * @param sourceTopics        the list of topics that must be processed
     * @return topics that don't have modification after marking all forum as read
     */
    private List<Topic> extractNotModifiedTopicsSinceForumMarkedAsRead(
            JCUser currentUser,
            List<Topic> sourceTopics) {
        DateTime forumMarkAsReadDate = currentUser.getAllForumMarkedAsReadTime();
        List<Topic> topics = new ArrayList<>();
        if (!sourceTopics.isEmpty()) {
            Branch branch = sourceTopics.get(0).getBranch();
            BranchReadedMarker markBranch = branchReadedMarkerDao.getMarkerFor(currentUser, branch);
            DateTime markTime = getLastMarkDateTime(markBranch, forumMarkAsReadDate);
            for (Topic topic : sourceTopics) {
                if (!topic.getBranch().equals(branch)) {
                    branch = topic.getBranch();
                    markBranch = branchReadedMarkerDao.getMarkerFor(currentUser, branch);
                    markTime = getLastMarkDateTime(markBranch, forumMarkAsReadDate);
                }
                if(markTime != null && topic.getModificationDate().isBefore(markTime)) {
                    topics.add(topic);
                }
            }
        }
        return topics;
    }

    /**
     * Compares date from marker with specified date and returns greater value
     *
     * @param marker marker object
     * @param date date to compare
     *
     * @return greater value if both not null
     *         null if both null
     *         not null one if another null
     */
    private DateTime getLastMarkDateTime(BranchReadedMarker marker, DateTime date) {
        if (marker == null) {
            return date;
        } else if (date == null) {
            return marker.getMarkTime();
        } else {
            return marker.getMarkTime().isBefore(date) ? date : marker.getMarkTime();
        }
    }

    /**
     * For topics modified since forum was marked as all read we need to calculate
     * last read posts from data that were saved in repository.
     *
     * @param modifiedTopics the list of modified topics
     * @param currentUser    the current user of application
     */
    private void fillLastReadPostsForModifiedTopics(List<Topic> modifiedTopics, JCUser currentUser) {
        List<LastReadPost> lastReadPosts = lastReadPostDao.getLastReadPosts(currentUser, modifiedTopics);
        for (Topic topic : modifiedTopics) {
            LastReadPost lastReadPost = findLastReadPost(lastReadPosts, topic.getId());
            if (lastReadPost != null) {
                topic.setLastReadPostDate(lastReadPost.getPostCreationDate());
            }
        }
    }

    /**
     * Find last read post in the list for given topic.
     *
     * @param lastReadPosts the list of last read posts where we are going to search
     * @param topicId       an identifier of topic for which we find last read post in list
     * @return last read post for given topic
     */
    private LastReadPost findLastReadPost(List<LastReadPost> lastReadPosts, long topicId) {
        for (LastReadPost lastReadPost : lastReadPosts) {
            if (lastReadPost.getTopic().getId() == topicId) {
                return lastReadPost;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#topic.branch.id, 'BRANCH', 'BranchPermission.VIEW_TOPICS')")
    public void markTopicPageAsRead(Topic topic, int pageNum) {
        JCUser current = userService.getCurrentUser();
        // topics are always unread for anonymous users
        if (!current.isAnonymous()) {
            Post lastPostOnPage = this.calculatePostOnPage(current, topic, pageNum);
            saveLastReadPost(current, topic, lastPostOnPage);
        }
    }

    /**
     * Computes new last read post on the page based on the topic size and
     * current pagination settings.
     *
     * @param user          user to calculate post for
     * @param topic         topic to calculate post for
     * @param pageNum       page number co calculate last post seen by the user
     * @return last post on the page
     */
    private Post calculatePostOnPage(JCUser user, Topic topic, int pageNum) {
        int maxPostIndex = user.getPageSize() * pageNum - 1;
        maxPostIndex = Math.min(topic.getPostCount() - 1, maxPostIndex);
        return topic.getPosts().get(maxPostIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#topic.branch.id, 'BRANCH', 'BranchPermission.VIEW_TOPICS')")
    public void markTopicAsRead(Topic topic) {
        JCUser current = userService.getCurrentUser();
        if (!current.isAnonymous()) { // topics are always unread for anonymous users
            saveLastReadPost(current, topic, topic.getLastDisplayedPost());
        }
    }

    /**
     * Stores last read post info into database for the particular topic and user.
     *
     * @param user      user to save last read post data for
     * @param topic     topic to store info for
     * @param lastPost  last post in the topic (or in the last read page of the topic)
     */
    private void saveLastReadPost(JCUser user, Topic topic, Post lastPost) {
        DateTime lastTimeForumWasMarkedRead = user.getAllForumMarkedAsReadTime();
        DateTime topicModifiedDate = topic.getModificationDate();
        if (lastTimeForumWasMarkedRead != null && topicModifiedDate.isBefore(lastTimeForumWasMarkedRead)) {
            return;
        }

        LastReadPost lastReadPost = lastReadPostDao.getLastReadPost(user, topic);
        if (lastReadPost == null) {
            lastReadPost = new LastReadPost(user, topic, lastPost.getCreationDate());
        } else {
            if (lastPost.getCreationDate().isAfter(lastReadPost.getPostCreationDate())) {
                lastReadPost.setPostCreationDate(lastPost.getCreationDate());
            } else {
                return;
            }
        }
        lastReadPostDao.saveOrUpdate(lastReadPost);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#branch.id, 'BRANCH', 'BranchPermission.VIEW_TOPICS')")
    public void markAllTopicsAsRead(Branch branch) {
        JCUser user = userService.getCurrentUser();
        if (!user.isAnonymous()) {
            // would be logical to remove per-topic Last Read Post records from DB,
            // but it's not worth it since most people will anyway press on global Mark All As Read
            // at some point and this will clean the records for user. Ergo, it's not expected
            // that the DB will be overwhelmed with per-topic Last Read Post records.
            branchReadedMarkerDao.markBranchAsRead(user, branch);
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

}

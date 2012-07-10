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
import org.jtalks.common.model.permissions.GeneralPermission;
import org.jtalks.common.security.SecurityService;
import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.dto.JCommunePageRequest;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.SubscriptionService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Topic service class. This class contains method needed to manipulate with Topic persistent entity.
 *
 * @author Osadchuck Eugeny
 * @author Vervenko Pavel
 * @author Kirill Afonin
 * @author Vitaliy Kravchenko
 * @author Max Malakhov
 * @author Eugeny Batov
 */
public class TransactionalTopicService extends AbstractTransactionalEntityService<Topic, TopicDao>
        implements TopicService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private SecurityService securityService;
    private BranchService branchService;
    private BranchDao branchDao;
    private NotificationService notificationService;
    private SubscriptionService subscriptionService;
    private UserService userService;

    /**
     * Create an instance of User entity based service
     *
     * @param dao                 data access object, which should be able do all CRUD operations with topic entity
     * @param securityService     {@link org.jtalks.common.security.SecurityService} for retrieving current user
     * @param branchService       {@link org.jtalks.jcommune.service.BranchService} instance to be injected
     * @param branchDao           used for checking branch existence
     * @param notificationService to send email nofications on topic updates to subscribed users
     * @param subscriptionService for subscribing user on topic if notification enabled
     * @param paginationService auxiliary services for pagination
     * @param userService         to get current logged in user
     */
    public TransactionalTopicService(TopicDao dao, SecurityService securityService,
                                     BranchService branchService, BranchDao branchDao,
                                     NotificationService notificationService,
                                     SubscriptionService subscriptionService,
                                     UserService userService) {
        super(dao);
        this.securityService = securityService;
        this.branchService = branchService;
        this.branchDao = branchDao;
        this.notificationService = notificationService;
        this.subscriptionService = subscriptionService;
        this.userService = userService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#branchId, 'BRANCH', 'BranchPermission.CREATE_POSTS')")
    public Post replyToTopic(long topicId, String answerBody, long branchId) throws NotFoundException {
        JCUser currentUser = userService.getCurrentUser();

        currentUser.setPostCount(currentUser.getPostCount() + 1);
        Topic topic = get(topicId);
        Post answer = new Post(currentUser, answerBody);
        topic.addPost(answer);
        this.getDao().update(topic);

        securityService.createAclBuilder().grant(GeneralPermission.WRITE).to(currentUser).on(answer).flush();
        notificationService.topicChanged(topic);
        logger.debug("New post in topic. Topic id={}, Post id={}, Post author={}",
                new Object[]{topicId, answer.getId(), currentUser.getUsername()});

        return answer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#branchId, 'BRANCH', 'BranchPermission.CREATE_TOPICS')")
    public Topic createTopic(String topicName, String bodyText, long branchId
            , boolean notifyOnAnswers) throws NotFoundException {
        JCUser currentUser = userService.getCurrentUser();

        currentUser.setPostCount(currentUser.getPostCount() + 1);
        Branch branch = branchService.get(branchId);
        Topic topic = new Topic(currentUser, topicName);
        Post first = new Post(currentUser, bodyText);
        topic.addPost(first);
        branch.addTopic(topic);
        branchDao.update(branch);

        JCUser user =userService.getCurrentUser();
        securityService.createAclBuilder().grant(GeneralPermission.WRITE).to(user).on(topic).flush();
        securityService.createAclBuilder().grant(GeneralPermission.WRITE).to(user).on(first).flush();

        notificationService.branchChanged(branch);

        subscribeOnTopicIfNotificationsEnabled(notifyOnAnswers, topic, currentUser);

        logger.debug("Created new topic id={}, branch id={}, author={}",
                new Object[]{topic.getId(), branchId, currentUser.getUsername()});
        logger.info("Created new topic: \"{}\". Author: {}", topicName, currentUser.getUsername());

        return topic;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Topic> getRecentTopics(int page) {
        JCommunePageRequest pageRequest = JCommunePageRequest.
                createWithPagingEnabled(page, userService.getCurrentUser().getPageSize());
        DateTime date24HoursAgo = new DateTime().minusDays(1);
        return this.getDao().getTopicsUpdatedSince(date24HoursAgo, pageRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Topic> getUnansweredTopics(int page) {
        JCommunePageRequest pageRequest = JCommunePageRequest.
                createWithPagingEnabled(page, userService.getCurrentUser().getPageSize());
        return this.getDao().getUnansweredTopics(pageRequest);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#topicId, 'TOPIC', 'GeneralPermission.WRITE') or " +
    		"hasPermission(#branchId, 'BRANCH', 'BranchPermission.EDIT_OTHERS_POSTS")
    public void updateTopic(long topicId, long branchId, String topicName, String bodyText)
            throws NotFoundException {
        updateTopic(topicId, branchId, topicName, bodyText, 0, false, false, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#topicId, 'TOPIC', 'GeneralPermission.WRITE') or " +
    		"hasPermission(#branchId, 'BRANCH', 'BranchPermission.EDIT_OTHERS_POSTS')")
    public void updateTopic(long topicId, long branchId, String topicName, String bodyText,
                            int topicWeight, boolean sticked, boolean announcement, boolean notifyOnAnswers) throws NotFoundException {
        Topic topic = get(topicId);
        topic.setTitle(topicName);
        topic.setTopicWeight(topicWeight);
        topic.setSticked(sticked);
        topic.setAnnouncement(announcement);
        Post post = topic.getFirstPost();
        post.setPostContent(bodyText);
        post.updateModificationDate();
        topic.updateModificationDate();
        this.getDao().update(topic);
        notificationService.topicChanged(topic);
        JCUser currentUser = userService.getCurrentUser();
        subscribeOnTopicIfNotificationsEnabled(notifyOnAnswers, topic, currentUser);

        logger.debug("Topic id={} updated", topic.getId());
    }

    /**
     * Subscribes topic starter on created topic if notifications enabled("Notify me about the answer" checkbox).
     *
     * @param notifyOnAnswers flag that indicates notifications state(enabled or disabled)
     * @param topic           topic to subscription
     * @param currentUser     current user
     */
    private void subscribeOnTopicIfNotificationsEnabled(boolean notifyOnAnswers, Topic topic, JCUser currentUser) {
        boolean subscribed = topic.userSubscribed(currentUser);
        if (notifyOnAnswers ^ subscribed) {
            subscriptionService.toggleTopicSubscription(topic);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#branchId, 'BRANCH', 'BranchPermission.DELETE_TOPICS')")
    public Branch deleteTopic(long topicId, long branchId) throws NotFoundException {
        Topic topic = get(topicId);

        for (Post post : topic.getPosts()) {
            JCUser user = post.getUserCreated();
            user.setPostCount(user.getPostCount() - 1);
        }

        Branch branch = topic.getBranch();
        branch.deleteTopic(topic);
        branchDao.update(branch);

        securityService.deleteFromAcl(Topic.class, topicId);
        notificationService.branchChanged(branch);

        logger.info("Deleted topic \"{}\". Topic id: {}", topic.getTitle(), topicId);
        return branch;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#branchId, 'BRANCH', 'BranchPermission.MOVE_TOPICS')")
    public void moveTopic(Long topicId, Long branchId) throws NotFoundException {
        Topic topic = get(topicId);
        Branch targetBranch = branchService.get(branchId);
        targetBranch.addTopic(topic);
        branchDao.update(targetBranch);

        notificationService.topicMoved(topic, topicId);

        logger.info("Moved topic \"{}\". Topic id: {}", topic.getTitle(), topicId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Topic get(Long id) throws NotFoundException {
        Topic topic = super.get(id);
        topic.setViews(topic.getViews() + 1);
        this.getDao().update(topic);
        return topic;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Topic> getTopics(Branch branch, int page, boolean pagingEnabled) {
        JCommunePageRequest pageRequest = new JCommunePageRequest(
                page, userService.getCurrentUser().getPageSize(), pagingEnabled);
        return getDao().getTopics(branch, pageRequest);
    }
}

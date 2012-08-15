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
import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.PollService;
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
    private BranchDao branchDao;
    private NotificationService notificationService;
    private SubscriptionService subscriptionService;
    private UserService userService;
    private PollService pollService;

    /**
     * Create an instance of User entity based service
     *
     * @param dao                 data access object, which should be able do all CRUD operations with topic entity
     * @param securityService     {@link org.jtalks.common.security.SecurityService} for retrieving current user
     * @param branchDao           used for checking branch existence
     * @param notificationService to send email nofications on topic updates to subscribed users
     * @param subscriptionService for subscribing user on topic if notification enabled
     * @param userService         to get current logged in user
     * @param pollService         to create a poll and vote in a poll
     */
    public TransactionalTopicService(TopicDao dao, SecurityService securityService,
                                     BranchDao branchDao,
                                     NotificationService notificationService,
                                     SubscriptionService subscriptionService,
                                     UserService userService,
                                     PollService pollService) {
        super(dao);
        this.securityService = securityService;
        this.branchDao = branchDao;
        this.notificationService = notificationService;
        this.subscriptionService = subscriptionService;
        this.userService = userService;
        this.pollService = pollService;
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
    @PreAuthorize("hasPermission(#topicDto.branch.id, 'BRANCH', 'BranchPermission.CREATE_TOPICS')")
    public Topic createTopic(Topic topicDto, String bodyText,
                             boolean notifyOnAnswers) throws NotFoundException {
        JCUser currentUser = userService.getCurrentUser();

        currentUser.setPostCount(currentUser.getPostCount() + 1);
        Topic topic = new Topic(currentUser, topicDto.getTitle());
        Post first = new Post(currentUser, bodyText);
        topic.addPost(first);
        Branch branch = topicDto.getBranch();

        branch.addTopic(topic);
        branchDao.update(branch);

        JCUser user = userService.getCurrentUser();
        securityService.createAclBuilder().grant(GeneralPermission.WRITE).to(user).on(topic).flush();
        securityService.createAclBuilder().grant(GeneralPermission.WRITE).to(user).on(first).flush();

        notificationService.branchChanged(branch);

        subscribeOnTopicIfNotificationsEnabled(notifyOnAnswers, topic, currentUser);

        Poll poll = topicDto.getPoll();
        if (poll!=null && poll.isHasPoll()) {
            poll.setTopic(topic);
            pollService.createPoll(poll);
        }

        logger.debug("Created new topic id={}, branch id={}, author={}",
                new Object[]{topic.getId(), branch.getId(), currentUser.getUsername()});
        logger.info("Created new topic: \"{}\". Author: {}", topicDto.getTitle(), currentUser.getUsername());

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
    public void updateTopic(Topic topicDto, String bodyText) throws NotFoundException {
        updateTopic(topicDto, bodyText, false);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#topicDto.id, 'TOPIC', 'GeneralPermission.WRITE') or " +
            "hasPermission(#topicDto.branch.id, 'BRANCH', 'BranchPermission.EDIT_OTHERS_POSTS')")
    public void updateTopic(Topic topicDto, String bodyText, boolean notifyOnAnswers) throws NotFoundException {
        Topic topic = get(topicDto.getId());
        topic.setTitle(topicDto.getTitle());
        topic.setSticked(topicDto.isSticked());
        topic.setAnnouncement(topicDto.isAnnouncement());
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
    public Branch deleteTopic(long topicId) throws NotFoundException {
        Topic topic = get(topicId);
        long branchId = topic.getBranch().getId();
        return deleteTopic(topic, branchId);
    }
    
    /**
     * Performs topic deleting with permission check
     *
     * @param topic    topic to delete
     * @param branchId used for annotation permission check only
     * @return branch without deleted topic
     */
    @PreAuthorize("hasPermission(#branchId, 'BRANCH', 'BranchPermission.DELETE_TOPICS')")
    private Branch deleteTopic(Topic topic, long branchId) throws NotFoundException {
        Branch branch = deleteTopicSilent(topic);
        notificationService.branchChanged(branch);

        logger.info("Deleted topic \"{}\". Topic id: {}", topic.getTitle(), topic.getId());
        return branch;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Branch deleteTopicSilent(long topicId) throws NotFoundException {
        Topic topic = get(topicId);
        long branchId = topic.getBranch().getId();
        return deleteTopicSilent(topic, branchId);
    }
    
    /**
     * Performs silent topic deleting with permission check
     *
     * @param topic    topic to delete
     * @param branchId used for annotation permission check only
     * @return branch without deleted topic
     */
    @PreAuthorize("hasPermission(#branchId, 'BRANCH', 'BranchPermission.DELETE_TOPICS')")
    private Branch deleteTopicSilent(Topic topic, long branchId) throws NotFoundException {
        return deleteTopicSilent(topic);
    }
    
    /**
     * Performs actual topic deleting. Deletes all topic related data and 
     * recalculates user's post count.
     *
     * @param topic    topic to delete
     * @return branch without deleted topic
     */
    private Branch deleteTopicSilent(Topic topic) {
        for (Post post : topic.getPosts()) {
            JCUser user = post.getUserCreated();
            user.setPostCount(user.getPostCount() - 1);
        }

        Branch branch = topic.getBranch();
        branch.deleteTopic(topic);
        branchDao.update(branch);

        securityService.deleteFromAcl(Topic.class, topic.getId());
        return branch;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void moveTopic(Long topicId, Long branchId) throws NotFoundException {
        Topic topic = get(topicId);
        moveTopic(topic, branchId);
    }
    
    /**
     * Performs actual topic moving with permission check
     *
     * @param topic    topic to move
     * @param branchId ID of target branch
     * @throws NotFoundException if target branch was not found by id
     * 
     */
    @PreAuthorize("hasPermission(#topic.branch.id, 'BRANCH', 'BranchPermission.MOVE_TOPICS')")
    private void moveTopic(Topic topic, Long branchId) throws NotFoundException {
        Branch targetBranch = branchDao.get(branchId);
        targetBranch.addTopic(topic);
        branchDao.update(targetBranch);

        notificationService.topicMoved(topic, topic.getId());

        logger.info("Moved topic \"{}\". Topic id: {}", topic.getTitle(), topic.getId());
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

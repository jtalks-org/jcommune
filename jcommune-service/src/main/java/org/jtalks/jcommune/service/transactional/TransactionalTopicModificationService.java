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

import org.jtalks.common.model.permissions.GeneralPermission;
import org.jtalks.common.security.SecurityService;
import org.jtalks.common.service.security.SecurityContextFacade;
import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.service.*;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.List;
import java.util.Set;


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
public class TransactionalTopicModificationService implements TopicModificationService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private TopicDao dao;
    private PostDao postDao;

    private SecurityService securityService;
    private BranchDao branchDao;
    private NotificationService notificationService;
    private SubscriptionService subscriptionService;
    private UserService userService;
    private PollService pollService;
    private PermissionEvaluator permissionEvaluator;
    private SecurityContextFacade securityContextFacade;
    private BranchLastPostService branchLastPostService;
    private LastReadPostService lastReadPostService;

    /**
     * Create an instance of User entity based service.
     *
     * @param dao                   data access object, which should be able do all CRUD operations with topic entity
     * @param securityService       {@link org.jtalks.common.security.SecurityService} for retrieving current user
     * @param branchDao             used for checking branch existence
     * @param notificationService   to send email notifications on topic updates to subscribed users
     * @param subscriptionService   for subscribing user on topic if notification enabled
     * @param userService           to get current logged in user
     * @param pollService           to create a poll and vote in a poll
     * @param securityContextFacade authentication object retrieval
     * @param permissionEvaluator   for authorization purposes
     * @param branchLastPostService to refresh the last post of the branch
     * @param lastReadPostService   to work with last read post
     * @param postDao               to store newly created posts in database
     */
    public TransactionalTopicModificationService(TopicDao dao, SecurityService securityService,
                                                 BranchDao branchDao,
                                                 NotificationService notificationService,
                                                 SubscriptionService subscriptionService,
                                                 UserService userService,
                                                 PollService pollService,
                                                 SecurityContextFacade securityContextFacade,
                                                 PermissionEvaluator permissionEvaluator,
                                                 BranchLastPostService branchLastPostService,
                                                 LastReadPostService lastReadPostService,
                                                 PostDao postDao) {
        this.dao = dao;
        this.securityService = securityService;
        this.branchDao = branchDao;
        this.notificationService = notificationService;
        this.subscriptionService = subscriptionService;
        this.userService = userService;
        this.pollService = pollService;
        this.securityContextFacade = securityContextFacade;
        this.permissionEvaluator = permissionEvaluator;
        this.branchLastPostService = branchLastPostService;
        this.lastReadPostService = lastReadPostService;
        this.postDao = postDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#branchId, 'BRANCH', 'BranchPermission.CREATE_POSTS')")
    public Post replyToTopic(long topicId, String answerBody, long branchId) throws NotFoundException {
        Topic topic = dao.get(topicId);
        this.assertPostingIsAllowed(topic);

        JCUser currentUser = userService.getCurrentUser();
        currentUser.setPostCount(currentUser.getPostCount() + 1);

        Post answer = new Post(currentUser, answerBody);
        topic.addPost(answer);
        if (currentUser.isAutosubscribe()) {
            Set<JCUser> topicSubscribers = topic.getSubscribers();
            topicSubscribers.add(currentUser);
        }
        postDao.saveOrUpdate(answer);

        Branch branch = topic.getBranch();
        branch.setLastPost(answer);
        branchDao.saveOrUpdate(branch);

        securityService.createAclBuilder().grant(GeneralPermission.WRITE).to(currentUser).on(answer).flush();
        notificationService.subscribedEntityChanged(topic);

        userService.notifyAndMarkNewlyMentionedUsers(answer);

        logger.debug("New post in topic. Topic id={}, Post id={}, Post author={}",
                new Object[]{topicId, answer.getId(), currentUser.getUsername()});

        return answer;
    }

    /**
     * Checks if the current topic is closed for posting.
     * Some users, however, can add posts even to the closed branches. These
     * users are granted with BranchPermission.CLOSE_TOPICS permission.
     *
     * @param topic topic to be checked for if posting is allowed
     */
    private void assertPostingIsAllowed(Topic topic) {
        Authentication auth = securityContextFacade.getContext().getAuthentication();
        if (topic.isClosed() && !permissionEvaluator.hasPermission(
                auth, topic.getBranch().getId(), "BRANCH", "BranchPermission.CLOSE_TOPICS")) { // holy shit...
            throw new AccessDeniedException("Posting is forbidden for closed topics");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#topicDto.branch.id, 'BRANCH', 'BranchPermission.CREATE_POSTS')")
    public Topic createTopic(Topic topicDto, String bodyText) throws NotFoundException {
        JCUser currentUser = userService.getCurrentUser();
        Branch branch = topicDto.getBranch();

        currentUser.setPostCount(currentUser.getPostCount() + 1);
        Topic topic = new Topic(currentUser, topicDto.getTitle());
        topic.setAnnouncement(topicDto.isAnnouncement());
        topic.setSticked(topicDto.isSticked());
        topic.setBranch(topicDto.getBranch());
        Post first = new Post(currentUser, bodyText);
        topic.addPost(first);
        topic.setBranch(branch);
        branch.setLastPost(first);

        dao.saveOrUpdate(topic);
        branchDao.saveOrUpdate(branch);

        JCUser user = userService.getCurrentUser();
        securityService.createAclBuilder().grant(GeneralPermission.WRITE).to(user).on(topic).flush();
        securityService.createAclBuilder().grant(GeneralPermission.WRITE).to(user).on(first).flush();

        notificationService.sendNotificationAboutTopicCreated(topic);

        subscribeOnTopicIfNotificationsEnabled(topic, currentUser);
        createPoll(topicDto.getPoll(), topic);

        userService.notifyAndMarkNewlyMentionedUsers(topic.getFirstPost());

        lastReadPostService.markTopicAsRead(topic);

        logger.debug("Created new topic id={}, branch id={}, author={}",
                new Object[]{topic.getId(), topic.getBranch().getId(), currentUser.getUsername()});
        return topic;
    }

    /**
     * Creates a poll for the topic or updates an existing one.
     * On update all poll items with the same name remain unchanged,
     * except probably their position in list. Users, previously voted
     * for the deleted items can NOT vote again.
     *
     * @param poll            poll data from UI form
     * @param persistentTopic topic from a database
     */
    private void createPoll(Poll poll, Topic persistentTopic) {
        if (poll != null && poll.isHasPoll()) {
            if (persistentTopic.getPoll() == null) {
                persistentTopic.setPoll(poll);
                poll.setTopic(persistentTopic);
                pollService.createPoll(poll);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#topicDto.branch.id, 'BRANCH', 'BranchPermission.CREATE_CODE_REVIEW')")
    public Topic createCodeReview(Topic topicDto, String bodyText) throws NotFoundException {
        JCUser currentUser = userService.getCurrentUser();
        Branch branch = topicDto.getBranch();

        currentUser.setPostCount(currentUser.getPostCount() + 1);
        Topic topic = new Topic(currentUser, topicDto.getTitle());
        Post first = new Post(currentUser, wrapWithCodeTag(bodyText));
        topic.addPost(first);
        CodeReview codeReview = new CodeReview();
        codeReview.setTopic(topic);
        topic.setCodeReview(codeReview);
        topic.setBranch(branch);
        branch.setLastPost(first);

        dao.saveOrUpdate(topic);
        branchDao.saveOrUpdate(branch);

        JCUser user = userService.getCurrentUser();
        securityService.createAclBuilder().grant(GeneralPermission.WRITE).to(user).on(topic).flush();
        securityService.createAclBuilder().grant(GeneralPermission.WRITE).to(user).on(first).flush();

        notificationService.subscribedEntityChanged(topic.getBranch());

        subscribeOnTopicIfNotificationsEnabled(topic, currentUser);

        lastReadPostService.markTopicAsRead(topic);

        logger.debug("Created new code review topic id={}, branch id={}, author={}",
                new Object[]{topic.getId(), topic.getBranch().getId(), currentUser.getUsername()});
        return topic;
    }

    /**
     * Wrap given message with [code=java]...[/code] tags if it is not wrapped
     * yet
     *
     * @param message message to wrap
     * @return wrapped message
     */
    private String wrapWithCodeTag(String message) {
        String trimmedMessage = message.trim();
        if (!trimmedMessage.startsWith(CODE_JAVA_BBCODE_START) ||
                !trimmedMessage.endsWith(CODE_JAVA_BBCODE_END)) {
            return CODE_JAVA_BBCODE_START + message + CODE_JAVA_BBCODE_END;
        }
        return message;
    }

    /**
     * {@inheritDoc}
     *
     * @throws AccessDeniedException besides other reasons, always throws this when Code Review is edited because it
     *                               shouldn't be possible to edit it. More details on requirements can be found here
     *                               <a href="http://jtalks.org/display/jcommune/1.1+Larks">here</a>.
     */
    @Override
    @PreAuthorize("(hasPermission(#topic.id, 'TOPIC', 'GeneralPermission.WRITE') and " +
            "hasPermission(#topic.branch.id, 'BRANCH', 'BranchPermission.EDIT_OWN_POSTS')) or " +
            "(not hasPermission(#topic.id, 'TOPIC', 'GeneralPermission.WRITE') and " +
            "hasPermission(#topic.branch.id, 'BRANCH', 'BranchPermission.EDIT_OTHERS_POSTS'))")
    public void updateTopic(Topic topic, Poll poll) {
        if (topic.getCodeReview() != null) {
            throw new AccessDeniedException("It is not allowed to edit Code Review!");
        }
        Post post = topic.getFirstPost();
        post.updateModificationDate();
        if (poll != null && poll.getEndingDate() != null) {
            topic.getPoll().setEndingDate(poll.getEndingDate());
        }
        dao.saveOrUpdate(topic);
        JCUser currentUser = userService.getCurrentUser();
        subscribeOnTopicIfNotificationsEnabled(topic, currentUser);
        logger.debug("Topic id={} updated", topic.getId());
    }

    /**
     * Subscribes topic starter on created topic if notifications enabled("Notify me about the answer" checkbox).
     * Subscribes and unsubscribes do if autoSubscribe enabled/disabled.
     *
     * @param topic       topic to subscription
     * @param currentUser current user
     */
    private void subscribeOnTopicIfNotificationsEnabled(Topic topic, JCUser currentUser) {
        boolean subscribed = topic.userSubscribed(currentUser);
        if (currentUser.isAutosubscribe() ^ subscribed) {
            subscriptionService.toggleTopicSubscription(topic);
        }
    }

    /**
     * {@inheritDoc}
     */
    @PreAuthorize("(hasPermission(#topic.branch.id, 'BRANCH', 'BranchPermission.DELETE_OWN_POSTS') and " +
            "#topic.topicStarter.username == principal.username and " +
            "#topic.postCount == 1) or " +
            "(hasPermission(#topic.branch.id, 'BRANCH', 'BranchPermission.DELETE_OTHERS_POSTS') and " +
            "hasPermission(#topic.branch.id, 'BRANCH', 'BranchPermission.DELETE_OWN_POSTS'))")
    @Override
    public void deleteTopic(Topic topic) throws NotFoundException {

        Collection<JCUser> subscribers = subscriptionService.getAllowedSubscribers(topic);

        Branch branch = deleteTopicSilent(topic);
        notificationService.sendNotificationAboutRemovingTopic(topic, subscribers);
        notificationService.subscribedEntityChanged(branch, subscribers);

        logger.info("Deleted topic \"{}\". Topic id: {}", topic.getTitle(), topic.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteTopicSilent(long topicId) throws NotFoundException {
        Topic topic = dao.get(topicId);
        if (topic == null) {
            throw new NotFoundException("Topic with given id not exist");
        }
        this.deleteTopicSilent(topic);
    }

    /**
     * Performs actual topic deletion. Deletes all topic related data and
     * recalculates user's post count.
     *
     * @param topic topic to delete
     * @return branch without deleted topic
     */
    private Branch deleteTopicSilent(Topic topic) {
        List<Post> topicPosts = topic.getPosts();
        for (Post post : topicPosts) {
            JCUser user = post.getUserCreated();
            user.setPostCount(user.getPostCount() - 1);
        }
        Branch branch = topic.getBranch();
        Post lastPostInBranch = branch.getLastPost();
        boolean branchLastPostFromDeletedTopic = topicPosts.contains(lastPostInBranch);
        if (branchLastPostFromDeletedTopic) {
            branch.clearLastPost();
        }

        branch.deleteTopic(topic);
        branchDao.saveOrUpdate(branch);

        if (branchLastPostFromDeletedTopic) {
            branchLastPostService.refreshLastPostInBranch(branch);
        }

        securityService.deleteFromAcl(Topic.class, topic.getId());
        return branch;
    }

    /**
     * {@inheritDoc}
     */
    @PreAuthorize("hasPermission(#topic.branch.id, 'BRANCH', 'BranchPermission.MOVE_TOPICS')")
    @Override
    public void moveTopic(Topic topic, Long branchId) throws NotFoundException {
        Branch sourceBranch = topic.getBranch();
        Branch targetBranch = branchDao.get(branchId);
        targetBranch.addTopic(topic);
        branchDao.saveOrUpdate(targetBranch);

        List<Post> topicPosts = topic.getPosts();
        if (topicPosts.contains(sourceBranch.getLastPost())) {
            branchLastPostService.refreshLastPostInBranch(sourceBranch);
        }
        branchLastPostService.refreshLastPostInBranch(targetBranch);

        notificationService.sendNotificationAboutTopicMoved(topic);

        logger.info("Moved topic \"{}\". Topic id: {}", topic.getTitle(), topic.getId());
    }

    /**
     * {@inheritDoc}
     */
    @PreAuthorize("hasPermission(#topic.branch.id, 'BRANCH', 'BranchPermission.CLOSE_TOPICS')")
    @Override
    public void closeTopic(Topic topic) {
        if (topic.getCodeReview() != null) {
            throw new AccessDeniedException("Close for code review");
        }
        topic.setClosed(true);
        dao.saveOrUpdate(topic);
    }

    /**
     * {@inheritDoc}
     */
    @PreAuthorize("hasPermission(#topic.branch.id, 'BRANCH', 'BranchPermission.CLOSE_TOPICS')")
    @Override
    public void openTopic(Topic topic) {
        topic.setClosed(false);
        dao.saveOrUpdate(topic);
    }
}

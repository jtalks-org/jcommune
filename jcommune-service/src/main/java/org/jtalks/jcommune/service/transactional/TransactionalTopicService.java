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
import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.LastReadPost;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.NotificationService;
import org.jtalks.jcommune.service.nontransactional.SecurityService;
import org.jtalks.jcommune.service.security.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

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
    private static final String AUTHENTICATED =
            "hasAnyRole('" + SecurityConstants.ROLE_USER + "','" + SecurityConstants.ROLE_ADMIN + "')";

    private SecurityService securityService;
    private BranchService branchService;
    private BranchDao branchDao;
    private PostDao postDao;
    private NotificationService notificationService;

    /**
     * Create an instance of User entity based service
     *
     * @param dao                 data access object, which should be able do all CRUD operations with topic entity
     * @param securityService     {@link SecurityService} for retrieving current user
     * @param branchService       {@link org.jtalks.jcommune.service.BranchService} instance to be injected
     * @param branchDao           used for checking branch existence
     * @param postDao             to mark posts as read
     * @param notificationService to send email nofications on topic updates to subscribed users
     */
    public TransactionalTopicService(TopicDao dao, SecurityService securityService,
                                     BranchService branchService, BranchDao branchDao, PostDao postDao,
                                     NotificationService notificationService) {
        super(dao);
        this.securityService = securityService;
        this.branchService = branchService;
        this.branchDao = branchDao;
        this.postDao = postDao;
        this.notificationService = notificationService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize(AUTHENTICATED)
    public Post replyToTopic(long topicId, String answerBody) throws NotFoundException {
        JCUser currentUser = securityService.getCurrentUser();

        currentUser.setPostCount(currentUser.getPostCount() + 1);
        Topic topic = get(topicId);
        Post answer = new Post(currentUser, answerBody);
        topic.addPost(answer);
        this.getDao().update(topic);

        securityService.grantToCurrentUser().role(SecurityConstants.ROLE_ADMIN).admin().on(answer);
        notificationService.topicChanged(topic);
        logger.debug("New post in topic. Topic id={}, Post id={}, Post author={}",
                new Object[]{topicId, answer.getId(), currentUser.getUsername()});

        return answer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize(AUTHENTICATED)
    public Topic createTopic(String topicName, String bodyText, long branchId) throws NotFoundException {
        JCUser currentUser = securityService.getCurrentUser();

        currentUser.setPostCount(currentUser.getPostCount() + 1);
        Branch branch = branchService.get(branchId);
        Topic topic = new Topic(currentUser, topicName);
        Post first = new Post(currentUser, bodyText);
        topic.addPost(first);
        branch.addTopic(topic);
        branchDao.update(branch);

        securityService.grantToCurrentUser().role(SecurityConstants.ROLE_ADMIN).admin().on(topic)
                .user(currentUser.getUsername()).role(SecurityConstants.ROLE_ADMIN).admin().on(first);
        notificationService.branchChanged(branch);

        logger.debug("Created new topic id={}, branch id={}, author={}",
                new Object[]{topic.getId(), branchId, currentUser.getUsername()});
        logger.info("Created new topic: \"{}\". Author: {}", topicName, currentUser.getUsername());

        return topic;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Topic> getRecentTopics(DateTime date) {
        if (date == null) {
            date = new DateTime().minusDays(1);
        }
        return this.getDao().getTopicsUpdatedSince(date);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Topic> getUnansweredTopics() {
        return this.getDao().getUnansweredTopics();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#topicId, 'org.jtalks.jcommune.model.entity.Topic', admin)")
    public void updateTopic(long topicId, String topicName, String bodyText)
            throws NotFoundException {
        updateTopic(topicId, topicName, bodyText, 0, false, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#topicId, 'org.jtalks.jcommune.model.entity.Topic', admin)")
    public void updateTopic(long topicId, String topicName, String bodyText, int topicWeight,
                            boolean sticked, boolean announcement) throws NotFoundException {
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

        logger.debug("Topic id={} updated", topic.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#topicId, 'org.jtalks.jcommune.model.entity.Topic', admin) or " +
            "hasPermission(#topicId, 'org.jtalks.jcommune.model.entity.Topic', delete)")
    public Branch deleteTopic(long topicId) throws NotFoundException {
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
    @PreAuthorize("hasAnyRole('" + SecurityConstants.ROLE_ADMIN + "')")
    public void moveTopic(Long topicId, Long branchId) throws NotFoundException {
        Topic topic = get(topicId);
        Branch currentBranch = topic.getBranch();
        Branch targetBranch = branchService.get(branchId);
        targetBranch.addTopic(topic);
        branchDao.update(targetBranch);

        logger.info("Moved topic \"{}\". Topic id: {}", topic.getTitle(), topicId);

        notificationService.topicChanged(topic);
        notificationService.branchChanged(currentBranch);
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
    public void markTopicPageAsRead(Topic topic, int pageNum, boolean pagingEnabled) {
        JCUser current = securityService.getCurrentUser();
        if (current != null) { // topics are always unread for anonymous users
            int postIndex = this.calculatePostIndex(current, topic, pageNum, pagingEnabled);
            saveLastReadPost(current, topic, postIndex);
        }
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
    @PreAuthorize(AUTHENTICATED)
    private int calculatePostIndex(JCUser user, Topic topic, int pageNum, boolean pagingEnabled) {
        if (pagingEnabled) {  // last post on the page given
            int maxPostIndex = user.getPageSize() * pageNum;
            return Math.min(topic.getPostCount() - 1, maxPostIndex);
        } else {              // last post in the topic
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
    @PreAuthorize(AUTHENTICATED)
    private void saveLastReadPost(JCUser user, Topic topic, int postIndex) {
        LastReadPost post = postDao.getLastReadPost(user, topic);
        if (post == null) {
            post = new LastReadPost(user, topic, postIndex);
        } else {
            post.setPostIndex(postIndex);
        }
        postDao.saveLastReadPost(post);
    }
}

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
package org.jtalks.jcommune.service.transactional;

import org.joda.time.DateTime;
import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
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
 */
public class TransactionalTopicService extends AbstractTransactionalEntityService<Topic, TopicDao>
        implements TopicService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final SecurityService securityService;
    private BranchService branchService;
    private BranchDao branchDao;

    /**
     * Create an instance of User entity based service
     *
     * @param dao             data access object, which should be able do all CRUD operations with topic entity
     * @param securityService {@link SecurityService} for retrieving current user
     * @param branchService   {@link org.jtalks.jcommune.service.BranchService} instance to be injected
     * @param branchDao       used for checking branch existance
     */
    public TransactionalTopicService(TopicDao dao, SecurityService securityService,
                                     BranchService branchService, BranchDao branchDao) {
        this.securityService = securityService;
        this.dao = dao;
        this.branchService = branchService;
        this.branchDao = branchDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasAnyRole('" + SecurityConstants.ROLE_USER + "','" + SecurityConstants.ROLE_ADMIN + "')")
    public Post addAnswer(long topicId, String answerBody) throws NotFoundException {
        User currentUser = securityService.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("User should log in to post answers.");
        }

        Topic topic = dao.get(topicId);
        if (topic == null) {
            throw new NotFoundException("Topic with id: " + topicId + " not found");
        }

        Post answer = new Post(topic, currentUser, answerBody);
        topic.addPost(answer);

        dao.saveOrUpdate(topic);
        logger.debug("Added answer to topic {}", topicId);

        securityService.grantToCurrentUser().role(SecurityConstants.ROLE_ADMIN).admin().on(answer);
        return answer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasAnyRole('" + SecurityConstants.ROLE_USER + "','" + SecurityConstants.ROLE_ADMIN + "')")
    public Topic createTopic(String topicName, String bodyText, long branchId) throws NotFoundException {
        User currentUser = securityService.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("User should log in to post answers.");
        }

        Topic topic = new Topic(branchService.get(branchId), currentUser, topicName);
        Post first = new Post(topic, currentUser, bodyText);
        topic.addFirstPost(first);

        dao.saveOrUpdate(topic);
        logger.debug("Created new topic {}", topic.getId());

        securityService.grantToCurrentUser().role(SecurityConstants.ROLE_ADMIN).admin().on(topic)
                .user(currentUser.getUsername()).role(SecurityConstants.ROLE_ADMIN).admin().on(first);

        return topic;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#postId, 'org.jtalks.jcommune.model.entity.Post', admin) or " +
            "hasPermission(#postId, 'org.jtalks.jcommune.model.entity.Post', delete)")
    public void deletePost(long topicId, long postId) throws NotFoundException {
        Topic topic = dao.get(topicId);
        if (topic == null) {
            throw new NotFoundException("Topic with id: " + topicId + " not found");
        }
        deletePostFromTopic(postId, topic);
        dao.saveOrUpdate(topic);
        logger.debug("Deleted post with id: {}", postId);
    }

    private void deletePostFromTopic(long postId, Topic topic) throws NotFoundException {
        List<Post> posts = topic.getPosts();
        for (Post post : posts) {
            if (post.getId() == postId) {
                topic.removePost(post);
                securityService.deleteFromAcl(post);
                return;
            }
        }
        throw new NotFoundException("Post with id: " + postId + " not found");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Topic> getTopicRangeInBranch(long branchId, int start, int max) throws NotFoundException {
        if (!branchDao.isExist(branchId)) {
            throw new NotFoundException("Branch with id: " + branchId + " not found");
        }
        return dao.getTopicRangeInBranch(branchId, start, max);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTopicsInBranchCount(long branchId) throws NotFoundException {
        if (!branchDao.isExist(branchId)) {
            throw new NotFoundException("Branch with id: " + branchId + " not found");
        }
        return dao.getTopicsInBranchCount(branchId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#topicId, 'org.jtalks.jcommune.model.entity.Topic', admin)")
    public void saveTopic(long topicId, String topicName, String bodyText)
            throws NotFoundException {
        if (!dao.isExist(topicId)) {
            throw new NotFoundException("Topic with id: " + topicId + " not found");
        }

        Topic topic = dao.get(topicId);
        topic.setTitle(topicName);
        Post post = topic.getFirstPost();
        post.setPostContent(bodyText);
        post.setCreationDate(new DateTime());
        topic.setLastPost(post);

        dao.saveOrUpdate(topic);
        logger.debug("Update the topic {}", topic.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#topicId, 'org.jtalks.jcommune.model.entity.Topic', admin) or " +
            "hasPermission(#topicId, 'org.jtalks.jcommune.model.entity.Topic', delete)")
    public void deleteTopic(long topicId) throws NotFoundException {
        if (!dao.isExist(topicId)) {
            throw new NotFoundException("Topic with id: " + topicId + " not found");
        }
        dao.delete(topicId);
        securityService.deleteFromAcl(Topic.class, topicId);
    }
}

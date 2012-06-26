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

import org.jtalks.common.security.SecurityService;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.LastReadPostService;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Post service class. This class contains method needed to manipulate with Post persistent entity.
 *
 * @author Osadchuck Eugeny
 */
public class TransactionalPostService extends AbstractTransactionalEntityService<Post, PostDao> implements PostService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private TopicDao topicDao;
    private SecurityService securityService;
    private NotificationService notificationService;
    private LastReadPostService lastReadPostService;

    /**
     * Create an instance of Post entity based service
     *
     * @param dao                 data access object, which should be able do all CRUD operations with post entity.
     * @param topicDao            this dao used for checking branch existance
     * @param securityService     service for authorization
     * @param notificationService to send email updates for subscribed users
     * @param lastReadPostService to modify last read post information when topic structure is changed
     */
    public TransactionalPostService(PostDao dao, TopicDao topicDao, SecurityService securityService,
                                    NotificationService notificationService, LastReadPostService lastReadPostService) {
        super(dao);
        this.topicDao = topicDao;
        this.securityService = securityService;
        this.notificationService = notificationService;
        this.lastReadPostService = lastReadPostService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#postId, 'POST', 'GeneralPermission.WRITE')")
    public void updatePost(long postId, String postContent) throws NotFoundException {
        Post post = get(postId);
        post.setPostContent(postContent);
        post.updateModificationDate();

        this.getDao().update(post);
        notificationService.topicChanged(post.getTopic());

        logger.debug("Post id={} updated.", post.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#branchId, 'BRANCH', 'BranchPermission.DELETE_OWN_POSTS') or " +
            "hasPermission(#branchId, 'BRANCH', 'BranchPermission.DELETE_OTHERS_POSTS')")
    public void deletePost(long postId, long branchId) throws NotFoundException {
        Post post = get(postId);
        JCUser user = post.getUserCreated();
        user.setPostCount(user.getPostCount() - 1);
        Topic topic = post.getTopic();
        topic.removePost(post);

        // todo: event API?
        topicDao.update(topic);
        securityService.deleteFromAcl(post);
        notificationService.topicChanged(topic);
        lastReadPostService.updateLastReadPostsWhenPostIsDeleted(post);

        logger.debug("Deleted post id={}", postId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Post> getPostsOfUser(JCUser userCreated) {
        return this.getDao().getUserPosts(userCreated);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int calculatePageForPost(Post post) {
        Topic topic = post.getTopic();
        JCUser user = (JCUser) securityService.getCurrentUser();
        int index = topic.getPosts().indexOf(post) + 1;
        int pageSize = (user == null) ? JCUser.DEFAULT_PAGE_SIZE : user.getPageSize();
        int pageNum = index / pageSize;
        if (index % pageSize == 0) {
            return pageNum;
        } else {
            return pageNum + 1;
        }
    }

}

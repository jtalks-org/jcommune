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
import org.jtalks.jcommune.model.dto.JcommunePageRequest;
import org.jtalks.jcommune.model.dto.JcommunePageable;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.LastReadPostService;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.NotificationService;
import org.jtalks.jcommune.service.nontransactional.PaginationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;


/**
 * Post service class. This class contains method needed to manipulate with Post persistent entity.
 *
 * @author Osadchuck Eugeny
 * @author Anuar Nurmakanov
 */
public class TransactionalPostService extends AbstractTransactionalEntityService<Post, PostDao> implements PostService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private TopicDao topicDao;
    private SecurityService securityService;
    private NotificationService notificationService;
    private LastReadPostService lastReadPostService;
    private PaginationService paginationService;

    /**
     * Create an instance of Post entity based service
     *
     * @param dao                 data access object, which should be able do all CRUD operations with post entity.
     * @param topicDao            this dao used for checking branch existance
     * @param securityService     service for authorization
     * @param notificationService to send email updates for subscribed users
     * @param lastReadPostService to modify last read post information when topic structure is changed
     * @param paginationService   this service provides functionality, that is needed for pagination
     */
    public TransactionalPostService(
            PostDao dao,
            TopicDao topicDao,
            SecurityService securityService,
            NotificationService notificationService,
            LastReadPostService lastReadPostService,
            PaginationService paginationService) {
        super(dao);
        this.topicDao = topicDao;
        this.securityService = securityService;
        this.notificationService = notificationService;
        this.lastReadPostService = lastReadPostService;
        this.paginationService = paginationService;
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
    public Page<Post> getPostsOfUser(JCUser userCreated, int page, boolean pagingEnabled) {
        JcommunePageable pageRequest = new JcommunePageRequest(
                page, paginationService.getPageSizeForCurrentUser());
        return this.getDao().getUserPosts(userCreated, pageRequest, pagingEnabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int calculatePageForPost(Post post) {
        Topic topic = post.getTopic();
        int index = topic.getPosts().indexOf(post) + 1;
        int pageSize = paginationService.getPageSizeForCurrentUser();
        int pageNum = index / pageSize;
        if (index % pageSize == 0) {
            return pageNum;
        } else {
            return pageNum + 1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Post> getPosts(Topic topic, int page, boolean pagingEnabled) {
        JcommunePageable pageRequest = new JcommunePageRequest(
                page, paginationService.getPageSizeForCurrentUser());
        return getDao().getPosts(topic, pageRequest, pagingEnabled);
    }
}

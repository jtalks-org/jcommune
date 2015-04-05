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
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.common.security.SecurityService;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.dto.PageRequest;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.plugin.api.PluginLoader;
import org.jtalks.jcommune.plugin.api.core.Plugin;
import org.jtalks.jcommune.plugin.api.core.TopicPlugin;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.plugin.api.filters.StateFilter;
import org.jtalks.jcommune.plugin.api.filters.TypeFilter;
import org.jtalks.jcommune.plugin.api.service.PluginPostService;
import org.jtalks.jcommune.service.BranchLastPostService;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.nontransactional.NotificationService;
import org.jtalks.jcommune.service.security.AclClassName;
import org.jtalks.jcommune.service.security.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Post service class. This class contains method needed to manipulate with Post persistent entity.
 *
 * @author Osadchuck Eugeny
 * @author Anuar Nurmakanov
 */
public class TransactionalPostService extends AbstractTransactionalEntityService<Post, PostDao>
        implements PostService, PluginPostService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private TopicDao topicDao;
    private SecurityService securityService;
    private NotificationService notificationService;
    private UserService userService;
    private BranchLastPostService branchLastPostService;
    private PermissionService permissionService;
    private PluginLoader pluginLoader;

    /**
     * Create an instance of Post entity based service
     *
     * @param dao                   data access object, which should be able do all CRUD operations with post entity.
     * @param topicDao              this dao used for checking branch existance
     * @param securityService       service for authorization
     * @param notificationService   to send email updates for subscribed users
     * @param userService           to get current user
     * @param branchLastPostService to refresh the last post of the branch
     */
    public TransactionalPostService(
            PostDao dao,
            TopicDao topicDao,
            SecurityService securityService,
            NotificationService notificationService,
            UserService userService,
            BranchLastPostService branchLastPostService,
            PermissionService permissionService,
            PluginLoader pluginLoader) {
        super(dao);
        this.topicDao = topicDao;
        this.securityService = securityService;
        this.notificationService = notificationService;
        this.userService = userService;
        this.branchLastPostService = branchLastPostService;
        this.permissionService = permissionService;
        this.pluginLoader = pluginLoader;
    }

    /**
     * Performs update with security checking.
     *
     * @param post        an instance of post, that will be updated
     * @param postContent new content of the post
     * @throws AccessDeniedException if user tries to update the first post of code review which should be impossible,
     *                               see <a href="http://jtalks.org/display/jcommune/1.1+Larks">Requirements</a>
     *                               for details
     */
    @PreAuthorize("(hasPermission(#post.id, 'POST', 'GeneralPermission.WRITE') and " +
            "hasPermission(#post.topic.branch.id, 'BRANCH', 'BranchPermission.EDIT_OWN_POSTS')) or " +
            "(not hasPermission(#post.id, 'POST', 'GeneralPermission.WRITE') and " +
            "hasPermission(#post.topic.branch.id, 'BRANCH', 'BranchPermission.EDIT_OTHERS_POSTS'))")
    @Override
    public void updatePost(Post post, String postContent) {
        Topic postTopic = post.getTopic();
        if (postTopic.isCodeReview() && postTopic.getPosts().get(0).getId() == post.getId()) {
            throw new AccessDeniedException("It is impossible to edit code review!");
        }
        post.setPostContent(postContent);
        post.updateModificationDate();

        this.getDao().saveOrUpdate(post);
        userService.notifyAndMarkNewlyMentionedUsers(post);

        logger.debug("Post id={} updated.", post.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("(hasPermission(#post.topic.branch.id, 'BRANCH', 'BranchPermission.DELETE_OWN_POSTS') and " +
            "#post.userCreated.username == principal.username) or " +
            "(hasPermission(#post.topic.branch.id, 'BRANCH', 'BranchPermission.DELETE_OTHERS_POSTS') and " +
            "#post.userCreated.username != principal.username)")
    public void deletePost(Post post) {
        JCUser user = post.getUserCreated();
        user.setPostCount(user.getPostCount() - 1);
        Topic topic = post.getTopic();
        topic.removePost(post);
        Branch branch = topic.getBranch();
        boolean deletedPostIsLastPostInBranch = branch.isLastPost(post);
        if (deletedPostIsLastPostInBranch) {
            branch.clearLastPost();
        }


        // todo: event API?
        topicDao.saveOrUpdate(topic);
        securityService.deleteFromAcl(post);
        notificationService.subscribedEntityChanged(topic);
        if (deletedPostIsLastPostInBranch) {
            branchLastPostService.refreshLastPostInBranch(branch);
        }

        logger.debug("Deleted post id={}", post.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Post> getPostsOfUser(JCUser userCreated, String page) {
        JCUser currentUser = userService.getCurrentUser();
        List<Long> allowedBranchesIds = topicDao.getAllowedBranchesIds(currentUser);

        PageRequest pageRequest = new PageRequest(page, currentUser.getPageSize());

        if (allowedBranchesIds.isEmpty()) {
            return new PageImpl<>(Collections.<Post>emptyList());
        } else {
            return this.getDao().getUserPosts(userCreated, pageRequest, allowedBranchesIds);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int calculatePageForPost(Post post) {
        Topic topic = post.getTopic();
        int index = topic.getPosts().indexOf(post) + 1;
        int pageSize = userService.getCurrentUser().getPageSize();
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
    public Page<Post> getPosts(Topic topic, String page) {
        PageRequest pageRequest = new PageRequest(page, userService.getCurrentUser().getPageSize());
        return getDao().getPosts(topic, pageRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Post getLastPostFor(Branch branch) {
        return getDao().getLastPostFor(branch);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Post> getLastPostsFor(Branch branch, int postCount) {
        return getDao().getLastPostsFor(Arrays.asList(branch.getId()), postCount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PostComment addComment(Long postId, Map<String, String> attributes, String body) throws NotFoundException {
        Post targetPost = get(postId);
        JCUser currentUser = userService.getCurrentUser();
        assertCommentAllowed(targetPost.getTopic());
        PostComment comment = new PostComment();
        comment.putAllAttributes(attributes);
        comment.setBody(body);
        comment.setCreationDate(new DateTime(System.currentTimeMillis()));
        comment.setAuthor(currentUser);
        if (currentUser.isAutosubscribe()) {
            targetPost.getTopic().getSubscribers().add(currentUser);
        }
        targetPost.addComment(comment);
        getDao().saveOrUpdate(targetPost);
        notificationService.subscribedEntityChanged(targetPost);

        return comment;
    }

    /**
     * Checks permissions before deleting comment
     * 
     * {@inheritDoc}
     */
    @PreAuthorize("(hasPermission(#post.topic.branch.id, 'BRANCH', 'BranchPermission.DELETE_OWN_POSTS') and " +
            "#comment.author.username == principal.username) or " +
            "(hasPermission(#post.topic.branch.id, 'BRANCH', 'BranchPermission.DELETE_OTHERS_POSTS') and " +
            "#comment.author.username != principal.username)")
    public void deleteComment(Post post, PostComment comment) {
        post.getComments().remove(comment);
        getDao().saveOrUpdate(post);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#post.topic.branch.id, 'BRANCH', 'BranchPermission.CREATE_POSTS') " +
            "and #post.userCreated.username != principal.username")
    public Post vote(Post post, PostVote vote) {
        JCUser currentUser = userService.getCurrentUser();
        if (!post.canBeVotedBy(currentUser, vote.isVotedUp())) {
            logger.info("User [{}] tries to vote for post with id={} in same direction more than one time",
                    currentUser.getUsername(), post.getId());
            throw new AccessDeniedException("User can't vote in same direction more than one time");
        }
        vote.setUser(currentUser);
        int ratingChanges = post.calculateRatingChanges(vote);
        post.putVote(vote);
        getDao().saveOrUpdate(post);
        getDao().changeRating(post.getId(), ratingChanges);
        return post;
    }

    /**
     * Checks if current user can create comments in specified topic
     *
     * @param topic topic to check permission
     * @throws AccessDeniedException if user can't create comments in specified topic
     */
    private void assertCommentAllowed(Topic topic) {
        if (topic.isCodeReview()) {
            permissionService.checkPermission(topic.getBranch().getId(), AclClassName.BRANCH,
                    BranchPermission.LEAVE_COMMENTS_IN_CODE_REVIEW);
        } else if (topic.isPlugable()) {
            assertCommentsAllowedForPlugableTopic(topic);
        } else {
            throw new AccessDeniedException("Adding comments not allowed for core topic types");
        }
        if (topic.isClosed()) {
            permissionService.checkPermission(topic.getBranch().getId(), AclClassName.BRANCH,
                    BranchPermission.CLOSE_TOPICS);
        }
    }

    /**
     * Checks if current user can create comments in specified plugable topic
     *
     * @param topic plugable topic to check permission
     * @throws AccessDeniedException  if user not granted to create comments in specified topic type or if type of
     * current topic is unknown
     */
    private void assertCommentsAllowedForPlugableTopic(Topic topic) {
        List<Plugin> topicPlugins = pluginLoader.getPlugins(new TypeFilter(TopicPlugin.class),
                new StateFilter(Plugin.State.ENABLED));
        boolean pluginFound = false;
        for (Plugin plugin : topicPlugins) {
            TopicPlugin topicPlugin = (TopicPlugin)plugin;
            if (topicPlugin.getTopicType().equals(topic.getType())) {
                pluginFound = true;
                permissionService.checkPermission(topic.getBranch().getId(), AclClassName.BRANCH,
                        topicPlugin.getCommentPermission());
                break;
            }
        }
        if (!pluginFound) {
            throw new AccessDeniedException("Creation of comments not allowed for unknown topic type");
        }
    }
}

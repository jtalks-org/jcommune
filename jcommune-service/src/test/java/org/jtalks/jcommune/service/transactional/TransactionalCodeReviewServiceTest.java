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

import static org.jgroups.util.Util.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;

import org.jtalks.common.model.dao.ChildRepository;
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.CodeReview;
import org.jtalks.jcommune.model.entity.CodeReviewComment;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.CodeReviewService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.NotificationService;
import org.jtalks.jcommune.service.security.AclClassName;
import org.jtalks.jcommune.service.security.PermissionService;
import org.mockito.Mock;
import org.springframework.security.access.AccessDeniedException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TransactionalCodeReviewServiceTest {
    private static final long CR_ID = 1L;

    @Mock
    private ChildRepository<CodeReview> dao;
    @Mock
    private UserService userService;
    @Mock
    private PermissionService permissionService;
    @Mock
    private NotificationService notificationService;
    
    private CodeReviewService codeReviewService;

    private CodeReview review;

    private JCUser currentUser;

    @BeforeMethod
    public void initEnvironmental() {
        initMocks(this);
        codeReviewService = new TransactionalCodeReviewService(
                dao, userService, permissionService, notificationService);
    }

    @BeforeMethod
    public void prepareTestData() {
        Branch branch = new Branch(null, null);
        Topic topic = new Topic();
        Post firstPost = new Post(null, null);
        firstPost.setId(1l);
        topic.addPost(firstPost);
        topic.setBranch(branch);
        review = new CodeReview();
        review.setTopic(topic);
        currentUser = new JCUser("", null, null);

        when(dao.get(CR_ID)).thenReturn(review);
        when(dao.isExist(CR_ID)).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    public void testAddCommentSuccess() throws AccessDeniedException, NotFoundException {
        CodeReviewComment comment = codeReviewService.addComment(CR_ID, 1, "body");

        assertEquals(review.getComments().size(), 1);
        assertEquals(comment.getLineNumber(), 1);
        assertEquals(comment.getBody(), "body");
        assertEquals(comment.getAuthor(), currentUser);
    }

    @Test
    public void testDeleteCommentSuccess() throws AccessDeniedException, NotFoundException {
        CodeReview codeReview = new CodeReview();
        ArrayList<CodeReviewComment> codeReviewComments = new ArrayList<CodeReviewComment>();
        CodeReviewComment reviewComment = createCodeReviewComment(String.valueOf(CR_ID));
        codeReviewComments.add(reviewComment);
        codeReviewComments.add(createCodeReviewComment("134"));
        codeReviewComments.add(createCodeReviewComment("1341"));
        codeReview.setComments(codeReviewComments);
        int oldSize = codeReview.getComments().size();

        codeReviewService.deleteComment(reviewComment, codeReview);

        verify(dao).update(codeReview);
        assertEquals(codeReview.getComments().size(), oldSize - 1);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testAddCommentReviewNotFound() throws AccessDeniedException, NotFoundException {
        codeReviewService.addComment(123L, 0, null);
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void testAddCommentUserHasNoPermission() throws AccessDeniedException, NotFoundException {
        doThrow(new AccessDeniedException(""))
                .when(permissionService).checkPermission(anyLong(), any(AclClassName.class), any(JtalksPermission.class));
        codeReviewService.addComment(CR_ID, 0, null);
    }

    @Test
    public void testAddUserToSubscriptedByComment() throws AccessDeniedException, NotFoundException {
        JCUser user = new JCUser("username", null, null);
        user.setAutosubscribe(true);
        when(userService.getCurrentUser()).thenReturn(user);
        codeReviewService.addComment(CR_ID, 1, "body");
        assertTrue(review.getSubscribers().contains(user));
    }

    @Test
    public void testNotAddUserToSubscriptedByComment() throws AccessDeniedException, NotFoundException {
        JCUser user = new JCUser("username", null, null);
        user.setAutosubscribe(false);
        when(userService.getCurrentUser()).thenReturn(user);
        codeReviewService.addComment(CR_ID, 1, "body");
        assertFalse(review.getSubscribers().contains(user));
    }

    private CodeReviewComment createCodeReviewComment(String uuid) {
        CodeReviewComment reviewComment = new CodeReviewComment();
        reviewComment.setUuid(uuid);
        return reviewComment;
    }

}

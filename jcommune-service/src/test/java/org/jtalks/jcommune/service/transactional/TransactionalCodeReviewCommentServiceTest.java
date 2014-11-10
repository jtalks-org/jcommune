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

import org.jtalks.common.model.dao.Crud;
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.service.CodeReviewService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.NotificationService;
import org.jtalks.jcommune.service.security.PermissionService;
import org.mockito.Mock;
import org.springframework.security.access.AccessDeniedException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

/**
 * @author Vyacheslav Mishcheryakov
 */
public class TransactionalCodeReviewCommentServiceTest {

    private static final String COMMENT_BODY = "body";
    private static final long BRANCH_ID = 1L;
    private static final long CR_ID = 1L;

    @Mock
    private Crud<CodeReviewComment> dao;
    @Mock
    private PermissionService permissionService;
    @Mock
    private UserService userService;
    @Mock
    NotificationService notificationService;
    @Mock
    private CodeReviewService codeReviewService;

    private TransactionalCodeReviewCommentService codeReviewCommentService;

    private CodeReviewComment comment;
    private JCUser currentUser;

    @BeforeMethod
    public void initEnvironmental() {
        initMocks(this);
        codeReviewCommentService = new TransactionalCodeReviewCommentService(
                dao, permissionService, userService);
    }

    @BeforeMethod
    public void prepareTestData() {

        currentUser = givenCurrentUser("code-review comment author");
        comment = new CodeReviewComment();
        comment.setAuthor(currentUser);
        
        Topic codeReviewTopic = new Topic();
        Post post = new Post(null, null);
        post.setId(48l);
        codeReviewTopic.addPost(post);
        CodeReview codeReview = new CodeReview();
        codeReview.setTopic(codeReviewTopic);
        codeReview.addComment(comment);

        when(dao.get(CR_ID)).thenReturn(comment);
        when(dao.isExist(CR_ID)).thenReturn(true);

        givenUserHasPermissionToEditOwnPosts(true);
        givenUserHasPermissionToEditOthersPosts(true);
    }

    @Test
    public void testUpdateCommentSuccess() throws Exception {
        givenUserHasPermissionToEditOwnPosts(true);
        CodeReviewComment comment = codeReviewCommentService.updateComment(CR_ID, COMMENT_BODY, BRANCH_ID);

        assertEquals(comment.getBody(), COMMENT_BODY);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testUpdateCommentNotFound() throws Exception {
        codeReviewCommentService.updateComment(123L, null, BRANCH_ID);
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void testUpdateCommentNoBothPermission() throws NotFoundException {
        givenUserHasPermissionToEditOwnPosts(false);
        givenUserHasPermissionToEditOthersPosts(false);
        codeReviewCommentService.updateComment(CR_ID, null, BRANCH_ID);
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void testUpdateCommentNoEditOwnPermission() throws NotFoundException {
        givenUserHasPermissionToEditOwnPosts(false);
        givenUserHasPermissionToEditOthersPosts(true);
        codeReviewCommentService.updateComment(CR_ID, null, BRANCH_ID);
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void testUpdateCommentNotOwnerNoEditOthersPermission() throws NotFoundException {
        givenCurrentUser("not-the-author-of-comment");
        givenUserHasPermissionToEditOthersPosts(false);
        givenUserHasPermissionToEditOwnPosts(true);
        codeReviewCommentService.updateComment(CR_ID, null, BRANCH_ID);
    }

    @Test
    public void testUpdateCommentNotOwnerButHasEditOthersPermission() throws NotFoundException {
        givenCurrentUser("not-the-author-of-comment");
        givenUserHasPermissionToEditOwnPosts(false);
        givenUserHasPermissionToEditOthersPosts(true);
        CodeReviewComment comment = codeReviewCommentService.updateComment(CR_ID, COMMENT_BODY, BRANCH_ID);

        assertEquals(comment.getBody(), COMMENT_BODY);
    }

    @Test
    public void testSubscriberNotGetNotificationAboutEditingComment() throws Exception {
        codeReviewCommentService.updateComment(CR_ID, COMMENT_BODY + "updated", BRANCH_ID);
        verifyZeroInteractions(notificationService);
    }

    private void givenUserHasPermissionToEditOwnPosts(boolean isGranted) {
        doReturn(isGranted).when(permissionService).hasBranchPermission(BRANCH_ID, BranchPermission.EDIT_OWN_POSTS);
    }

    private void givenUserHasPermissionToEditOthersPosts(boolean isGranted) {
        doReturn(isGranted).when(permissionService).hasBranchPermission(BRANCH_ID, BranchPermission.EDIT_OTHERS_POSTS);
    }

    private JCUser givenCurrentUser(String username) {
        JCUser currentUser = new JCUser(username, null, null);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        return currentUser;
    }

}

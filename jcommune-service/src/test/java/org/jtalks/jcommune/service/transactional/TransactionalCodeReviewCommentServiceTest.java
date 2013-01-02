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

import org.jtalks.common.model.dao.ChildRepository;
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.jcommune.model.entity.CodeReviewComment;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.security.AclClassName;
import org.jtalks.jcommune.service.security.PermissionService;
import org.mockito.Mock;
import org.springframework.security.access.AccessDeniedException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * 
 * @author Vyacheslav Mishcheryakov
 */
public class TransactionalCodeReviewCommentServiceTest {

    private static final String COMMENT_BODY = "body";
    private static final long BRANCH_ID = 1L;
    private static final long CR_ID = 1L;
    private static final long USER_ID = 1L;
    
    @Mock
    private ChildRepository<CodeReviewComment> dao;
    @Mock
    private PermissionService permissionService;
    @Mock
    private UserService userService;
    
    private TransactionalCodeReviewCommentService codeReviewCommentService;
    
    private CodeReviewComment comment;
    
    @BeforeMethod
    public void initEnvironmental() {
        initMocks(this);
        
        codeReviewCommentService = new TransactionalCodeReviewCommentService(
                dao,
                permissionService,
                userService);
        
    }
    
    @BeforeMethod 
    public void prepareTestData() {
        JCUser currentUser = new JCUser("", null, null);
        currentUser.setId(USER_ID);
        
        comment = new CodeReviewComment();
        comment.setAuthor(currentUser);
        
        when(dao.get(CR_ID)).thenReturn(comment);
        when(dao.isExist(CR_ID)).thenReturn(true);
        
        when(userService.getCurrentUser()).thenReturn(currentUser);
        
        when(permissionService.hasPermission(BRANCH_ID, AclClassName.BRANCH, 
                BranchPermission.EDIT_OTHERS_POSTS)).thenReturn(true);
        when(permissionService.hasPermission(BRANCH_ID, AclClassName.BRANCH, 
                BranchPermission.EDIT_OWN_POSTS)).thenReturn(true);
    }
    
    @Test
    public void testUpdateCommentSuccess() throws AccessDeniedException, NotFoundException {
        CodeReviewComment comment = codeReviewCommentService.updateComment(CR_ID, COMMENT_BODY, BRANCH_ID);
        
        assertEquals(comment.getBody(), COMMENT_BODY);
    }
    
    @Test(expectedExceptions=NotFoundException.class)
    public void testUpdateCommentNotFound() throws AccessDeniedException, NotFoundException {
        codeReviewCommentService.updateComment(123L, null, BRANCH_ID);
    }
    
    @Test(expectedExceptions=AccessDeniedException.class)
    public void testNoPermission() throws NotFoundException {
        when(permissionService.hasPermission(BRANCH_ID, AclClassName.BRANCH, 
            BranchPermission.EDIT_OTHERS_POSTS)).thenReturn(false);
        when(permissionService.hasPermission(BRANCH_ID, AclClassName.BRANCH, 
            BranchPermission.EDIT_OWN_POSTS)).thenReturn(false);
        codeReviewCommentService.updateComment(CR_ID, null, BRANCH_ID);
    }
    
    @Test(expectedExceptions=AccessDeniedException.class)
    public void testNotOwner() throws NotFoundException {
        JCUser otherCurrentUser = new JCUser("", null, null);
        when(userService.getCurrentUser()).thenReturn(otherCurrentUser);
        when(permissionService.hasPermission(BRANCH_ID, AclClassName.BRANCH, 
            BranchPermission.EDIT_OTHERS_POSTS)).thenReturn(false);
        when(permissionService.hasPermission(BRANCH_ID, AclClassName.BRANCH, 
            BranchPermission.EDIT_OWN_POSTS)).thenReturn(true);
        codeReviewCommentService.updateComment(CR_ID, null, BRANCH_ID);
    }
    
}

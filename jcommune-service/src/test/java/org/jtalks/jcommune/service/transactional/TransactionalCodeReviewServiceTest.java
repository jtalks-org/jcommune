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

import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.jcommune.model.dao.CodeReviewDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.CodeReview;
import org.jtalks.jcommune.model.entity.CodeReviewComment;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.CodeReviewService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.security.PermissionService;
import org.mockito.Mock;
import org.springframework.security.access.AccessDeniedException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class TransactionalCodeReviewServiceTest {

    private static final long CR_ID = 1L;
    
    @Mock
    private CodeReviewDao dao;
    @Mock
    private UserService userService;
    @Mock
    private PermissionService permissionService;
    
    private CodeReviewService codeReviewService;
    
    private CodeReview review;
    
    private JCUser currentUser;
    
    @BeforeMethod
    public void initEnvironmental() {
        initMocks(this);
        
        codeReviewService = new TransactionalCodeReviewService(dao, userService, permissionService);
    }
    
    @BeforeMethod 
    public void prepareTestData() {
        Branch branch = new Branch(null, null);
        Topic topic = new Topic();
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
    
    @Test(expectedExceptions=NotFoundException.class)
    public void testAddCommentReviewNotFound() throws AccessDeniedException, NotFoundException {
        codeReviewService.addComment(123L, 0, null);
    }
    
    @Test(expectedExceptions=AccessDeniedException.class)
    public void testAddCommentUserHasNoPermission() throws AccessDeniedException, NotFoundException {
        doThrow(new AccessDeniedException(""))
            .when(permissionService).checkPermission(anyLong(), anyString(), any(JtalksPermission.class));
        codeReviewService.addComment(CR_ID, 0, null);
    }
    
}

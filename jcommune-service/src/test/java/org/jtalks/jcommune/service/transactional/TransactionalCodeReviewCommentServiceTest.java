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
import org.jtalks.jcommune.model.entity.CodeReviewComment;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
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
    
    @Mock
    private ChildRepository<CodeReviewComment> dao;
    
    private TransactionalCodeReviewCommentService codeReviewCommentService;
    
    private CodeReviewComment comment;
    
    @BeforeMethod
    public void initEnvironmental() {
        initMocks(this);
        
        codeReviewCommentService = new TransactionalCodeReviewCommentService(dao);
    }
    
    @BeforeMethod 
    public void prepareTestData() {
        comment = new CodeReviewComment();
        
        when(dao.get(CR_ID)).thenReturn(comment);
        when(dao.isExist(CR_ID)).thenReturn(true);
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
    
}

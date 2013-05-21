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

import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.service.BranchLastPostService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * @author Anuar Nurmakanov
 */
public class TransactionalBranchLastPostServiceTest {
    private static final String BRANCH_NAME = "branch name";
    private static final String BRANCH_DESCRIPTION = "branch description";
    @Mock
    private PostDao postDao;
    @Mock
    private BranchDao branchDao;

    private BranchLastPostService branchLastPostService;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        branchLastPostService = new TransactionalBranchLastPostService(postDao, branchDao);
    }

    @Test
    public void testUpdateLastPostInBranchWhenPostDeletedIsLastPost() {
        Branch branchOfDeletedPost = new Branch(BRANCH_NAME, BRANCH_DESCRIPTION);
        Post expectedNewLastPost = new Post(null, null);
        when(postDao.getLastPostFor(branchOfDeletedPost))
                .thenReturn(expectedNewLastPost);

        branchLastPostService.refreshLastPostInBranch(branchOfDeletedPost);
        Post actualNewLastPost = branchOfDeletedPost.getLastPost();

        assertEquals(actualNewLastPost, expectedNewLastPost, "Incorrect last post was setted.");
        verify(branchDao).saveOrUpdate(branchOfDeletedPost);
        verify(postDao).getLastPostFor(branchOfDeletedPost);
    }
}

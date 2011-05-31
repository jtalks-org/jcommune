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

import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.service.BranchService;
import org.mockito.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * This test class is intended to test all topic-related forum branch facilities
 * 
 * @author Kravchenko Vitaliy
 */
public class TransactionalTopicBranchServiceTest {
    private long BRANCH_ID = 1;

    private BranchDao branchDao;
    private BranchService branchService;

    @BeforeMethod
    public void setUp() throws Exception {
        branchDao = mock(BranchDao.class);
        branchService = new TransactionalBranchService(branchDao);
    }

    @Test
    public void deleteByIdTest(){
        branchService.delete(BRANCH_ID);

        verify(branchDao, times(1)).delete(Matchers.anyLong());
    }

    @Test
    public void getByIdTest(){
        when(branchDao.get(BRANCH_ID)).thenReturn(getTopicBranch());
        Branch post = branchService.get(BRANCH_ID);
        Assert.assertEquals(post, getTopicBranch());
        verify(branchDao, times(1)).get(Matchers.anyLong());
    }

    @Test
    public void getAllTest(){
        List<Branch> expectedBranchList = new ArrayList<Branch>();
        expectedBranchList.add(getTopicBranch());
        when(branchDao.getAll()).thenReturn(expectedBranchList);
        List<Branch> actualBranchList = branchService.getAll();
        Assert.assertEquals(actualBranchList, expectedBranchList);
        verify(branchDao, times(1)).getAll();
    }

    private Branch getTopicBranch(){
        Branch branch = new Branch();
        branch.setId(BRANCH_ID);
        branch.setDescription("some info");
        branch.setName("Java Core");
        return branch;
    }
}

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
import org.jtalks.jcommune.service.exceptions.NotFoundException;
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
 * @author Kirill Afonin
 */
public class TransactionalBranchServiceTest {
    private long BRANCH_ID = 1;

    private BranchDao branchDao;
    private BranchService branchService;

    @BeforeMethod
    public void setUp() throws Exception {
        branchDao = mock(BranchDao.class);
        branchService = new TransactionalBranchService(branchDao);
    }

    @Test
    public void testDelete() throws NotFoundException {
        when(branchDao.isExist(BRANCH_ID)).thenReturn(true);

        branchService.delete(BRANCH_ID);

        verify(branchDao).isExist(BRANCH_ID);
        verify(branchDao).delete(BRANCH_ID);
    }

    @Test
    public void testGet() throws NotFoundException {
        when(branchDao.isExist(BRANCH_ID)).thenReturn(true);
        when(branchDao.get(BRANCH_ID)).thenReturn(getBranch());

        Branch branch = branchService.get(BRANCH_ID);

        Assert.assertEquals(branch, getBranch(), "Posts aren't equals");
        verify(branchDao).isExist(BRANCH_ID);
        verify(branchDao).get(BRANCH_ID);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testGetIncorrectId() throws NotFoundException {
        when(branchDao.isExist(BRANCH_ID)).thenReturn(false);

        branchService.get(BRANCH_ID);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testDeleteIncorrectId() throws NotFoundException {
        when(branchDao.isExist(BRANCH_ID)).thenReturn(false);

        branchService.delete(BRANCH_ID);
    }

    @Test
    public void getAllTest() {
        List<Branch> expectedBranchList = new ArrayList<Branch>();
        expectedBranchList.add(getBranch());
        when(branchDao.getAll()).thenReturn(expectedBranchList);

        List<Branch> actualBranchList = branchService.getAll();

        Assert.assertEquals(actualBranchList, expectedBranchList);
        verify(branchDao, times(1)).getAll();
    }

    private Branch getBranch() {
        Branch branch = new Branch();
        branch.setId(BRANCH_ID);
        branch.setUuid("xxx");
        branch.setDescription("some info");
        branch.setName("Java Core");
        return branch;
    }
}

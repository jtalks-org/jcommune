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
import org.jtalks.jcommune.model.dao.SectionDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * This test class is intended to test all topic-related forum branch facilities
 *
 * @author Kravchenko Vitaliy
 * @author Kirill Afonin
 */
public class TransactionalBranchServiceTest {
    private long BRANCH_ID = 1L;
    private long SECTION_ID = 2L;
    final String BRANCH_NAME = "branch name";

    private BranchDao branchDao;
    private SectionDao sectionDao;
    private BranchService branchService;

    @BeforeMethod
    public void setUp() throws Exception {
        branchDao = mock(BranchDao.class);
        sectionDao = mock(SectionDao.class);
        branchService = new TransactionalBranchService(branchDao, sectionDao);
    }

    @Test
    public void testGet() throws NotFoundException {
        Branch expectedBranch = new Branch(BRANCH_NAME);
        when(branchDao.isExist(BRANCH_ID)).thenReturn(true);
        when(branchDao.get(BRANCH_ID)).thenReturn(expectedBranch);

        Branch branch = branchService.get(BRANCH_ID);

        assertEquals(branch, expectedBranch, "Posts aren't equals");
        verify(branchDao).isExist(BRANCH_ID);
        verify(branchDao).get(BRANCH_ID);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testGetIncorrectId() throws NotFoundException {
        when(branchDao.isExist(BRANCH_ID)).thenReturn(false);

        branchService.get(BRANCH_ID);
    }

    @Test
    public void testGetBranchesInSection() throws NotFoundException {
        List<Branch> expectedList = new ArrayList<Branch>();
        expectedList.add(Branch.createNewBranch());
        when(sectionDao.isExist(SECTION_ID)).thenReturn(true);
        when(branchDao.getBranchesInSection(SECTION_ID)).thenReturn(expectedList);

        List<Branch> branches = branchService.getBranchesInSection(SECTION_ID);

        assertNotNull(branches);
        verify(branchDao).getBranchesInSection(SECTION_ID);
        verify(sectionDao).isExist(SECTION_ID);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testGetBranchesInNonExistentSection() throws NotFoundException {
        when(sectionDao.isExist(SECTION_ID)).thenReturn(false);

        branchService.getBranchesInSection(SECTION_ID);
    }

    @Test
    public void testGetBranchesInSectionCount() throws NotFoundException {
        int expectedCount = 10;
        when(sectionDao.isExist(SECTION_ID)).thenReturn(true);
        when(branchDao.getBranchesInSectionCount(SECTION_ID)).thenReturn(expectedCount);

        int count = branchService.getBranchesInSectionCount(SECTION_ID);

        assertEquals(count, expectedCount);
        verify(sectionDao).isExist(SECTION_ID);
        verify(branchDao).getBranchesInSectionCount(SECTION_ID);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testGetBranchesCountInNonExistentSection() throws NotFoundException {
        when(sectionDao.isExist(SECTION_ID)).thenReturn(false);

        branchService.getBranchesInSectionCount(SECTION_ID);
    }
}

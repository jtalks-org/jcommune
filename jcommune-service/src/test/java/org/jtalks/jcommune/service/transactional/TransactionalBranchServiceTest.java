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
import org.jtalks.jcommune.model.dao.SectionDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.mockito.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * This test class is intended to test all topic-related forum branch facilities
 *
 * @author Kravchenko Vitaliy
 * @author Kirill Afonin
 */
public class TransactionalBranchServiceTest {
    private long BRANCH_ID = 1L;
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

        assertEquals(branch, expectedBranch, "Branches aren't equal");
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
        List<Branch> list = Collections.singletonList(new Branch(BRANCH_NAME));
        when(sectionDao.isExist(Matchers.<Long>any())).thenReturn(true);
        when(branchDao.getBranchesInSection(anyLong())).thenReturn(list);
        
        List<Branch> result = branchService.getBranchesInSection(BRANCH_ID);
        assertEquals(list, result);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testGetBranchesInSectionFail() throws NotFoundException {
        when(sectionDao.isExist(Matchers.<Long>any())).thenReturn(false);
        branchService.getBranchesInSection(BRANCH_ID);
    }

    @Test
    public void testGetAllBranches(){
        List<Branch> list = Collections.singletonList(new Branch(BRANCH_NAME));

        when(branchDao.getAllBranches()).thenReturn(list);

        List<Branch> result = branchService.getAllBranches();
        assertEquals(list, result);
    }
}

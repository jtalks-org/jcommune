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

package org.jtalks.poulpe.web.controller.branch;

import java.util.ArrayList;
import java.util.List;
import org.jtalks.poulpe.service.exceptions.NotUniqueException;

import static org.testng.Assert.*;
import static org.mockito.Mockito.*;
import org.testng.annotations.*;
import org.jtalks.poulpe.model.entity.Branch;
import org.jtalks.poulpe.service.BranchService;
import org.jtalks.poulpe.web.controller.branch.BranchPresenter;
import org.jtalks.poulpe.web.controller.branch.BranchView;
import org.mockito.*;

/**
 * @author Bekrenev Dmitry
 * */

public class BranchPresenterTest {

    final private int COUNT_BRANCES = 3;

    private BranchPresenter presenter = new BranchPresenter();
    @Mock
    BranchService branchService;
    @Mock
    BranchView view;

    @Captor
    ArgumentCaptor<List<Branch>> branchCaptor;

    public Branch compareWithBranch(Branch branch) {
        return argThat(new sameBranchAs(branch));
    }

    public List<Branch> getFakeListBranches() {
        List<Branch> branches = new ArrayList<Branch>();

        for (int times = 0; times < COUNT_BRANCES; times++) {
            String strTimes = String.valueOf(times);
            branches.add(makeFakeBranch("Branch #" + strTimes, "Description #"
                    + strTimes, false));
        }

        return branches;
    }

    private Branch makeFakeBranch(String name, String description,
            boolean deleted) {
        Branch branch = new Branch();
        branch.setName(name);
        branch.setDescription(description);
        branch.setDeleted(deleted);
        return branch;
    };

    @BeforeClass
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        presenter.setBranchService(branchService);
        presenter.initView(view);
    }

    @BeforeMethod
    public void before() {
        reset(branchService);
        reset(view);
    }

    @Test
    public void testInitView() {

        when(branchService.getAll()).thenReturn(getFakeListBranches());

        presenter.initView(view);

        verify(view).showBranches(branchCaptor.capture());
        assertEquals(branchCaptor.getValue().size(), COUNT_BRANCES);
    }

    @Test
    public void testAddNewBranchIfAllCorrect() throws NotUniqueException {

        // Setup moc
        Branch newBranch = makeFakeBranch("New Branch", "New Description",
                false);

        when(branchService.isBranchNameExists("New Branch")).thenReturn(false);
        when(view.getNewBranchName()).thenReturn(newBranch.getName());
        when(view.getNewBranchDescription()).thenReturn(
                newBranch.getDescription());

        // make call
        presenter.addNewBranch();

        // check
        verify(branchService).saveBranch(compareWithBranch(newBranch));
        verify(view).showBranch(compareWithBranch(newBranch));
        verify(branchService).isBranchNameExists("New Branch");
    }

    @Test
    public void testAddNewBranchIfBranchExists() {

        final String name = "exists name";
        when(branchService.isBranchNameExists(name)).thenReturn(true);
        when(view.getNewBranchName()).thenReturn(name);
        presenter.addNewBranch();

        verify(branchService).isBranchNameExists(name);
        verify(view).openErrorPopupInNewBranchDialog();
    }

    @Test
    public void testOpenEditDialog() {

        Branch selectedBranch = makeFakeBranch("Selected Branch",
                "Selected Branch", false);

        when(view.getSelectedBranch()).thenReturn(selectedBranch);

        presenter.openEditDialog();

        verify(view).setEditBranchName(selectedBranch.getName());
        verify(view).setEditBranchDescription(selectedBranch.getDescription());
    }

    @Test
    public void testEditBranchIfAllCorrect() throws NotUniqueException {
        Branch selectedBranch = makeFakeBranch("Selected Branch",
                "Selected Branch", false);

        when(view.getSelectedBranch()).thenReturn(selectedBranch);
        when(view.getEditBranchName()).thenReturn("Edited Branch");
        when(view.getEditBranchDescription()).thenReturn("Edited Description");

        presenter.editBranch();

        verify(branchService).saveBranch(compareWithBranch(selectedBranch));
        verify(view).updateBranch(selectedBranch);
        verify(branchService).isBranchNameExists("Edited Branch");

    }

    @Test
    public void testEditBranchIfBranchExists() {
        final String name = "exists branch";

        when(view.getEditBranchName()).thenReturn(name);
        when(branchService.isBranchNameExists(name)).thenReturn(true);

        presenter.editBranch();

        verify(branchService).isBranchNameExists(name);
        verify(view).openErrorPopupInEditBranchDialog();
    }

    @Test
    public void testDeleteBranchIfBranchSelected() {
        Branch selectedBranch = makeFakeBranch("Selected Branch",
                "Selected Branch", false);

        when(view.getSelectedBranch()).thenReturn(selectedBranch);

        presenter.deleteBranch();

        verify(branchService).deleteBranch(compareWithBranch(selectedBranch));
        verify(view).removeBranch(selectedBranch);
    }

    @Test
    public void testDeleteBranchIfBranchNotSelected() {
        when(view.getSelectedBranch()).thenReturn(null);

        presenter.deleteBranch();

        verify(branchService, never()).deleteBranch((Branch) any());
        verify(view, never()).removeBranch((Branch) any());
    }

}

class sameBranchAs extends ArgumentMatcher<Branch> {
    private Branch branch;

    public sameBranchAs(Branch branch) {
        this.branch = branch;
    }

    @Override
    public boolean matches(Object obj) {
        Branch compareBranch = (Branch) obj;

        return branch.getName().equals(compareBranch.getName())
                && branch.getDescription().equals(
                        compareBranch.getDescription())
                && branch.getDeleted() == branch.getDeleted();
    }
}

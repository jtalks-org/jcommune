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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jtalks.poulpe.model.entity.Branch;
import org.jtalks.poulpe.service.BranchService;
import org.jtalks.poulpe.service.exceptions.NotUniqueException;

/**
 * This class is implementation the presenter in pattern Model-View-Presenter
 * 
 * @author Bekrenev Dmitry
 * */

public class BranchPresenter {

    private BranchView view;
    private BranchService branchService;

    /**
     * Sets the service instance which is used for manipulating with stored
     * branches
     * 
     * @param service
     *            The instance branch service
     * */
    public void setBranchService(BranchService service) {
        branchService = service;
    }

    /**
     * Initialize view instance before first rendering
     * 
     * @param view
     *            The instance view
     * */
    public void initView(BranchView view) {
        this.view = view;
        view.showBranches(branchService.getAll());
        view.closeDialogs();
    }

    /**
     * Use for store new branch from dialog window
     * */
    public void addNewBranch() {
        String name = view.getNewBranchName();
        String desc = view.getNewBranchDescription();

        if (!branchService.isBranchNameExists(name)) {
            Branch branch = new Branch();
            branch.setName(name);
            branch.setDescription(desc);
            try {
                branchService.saveBranch(branch);
            } catch (NotUniqueException ex) {
                // TODO: add processing here
                Logger.getLogger(BranchPresenter.class.getName()).log(
                        Level.SEVERE, null, ex);
            }

            view.showBranch(branch);
            view.closeNewBranchDialog();
        } else {
            view.openErrorPopupInNewBranchDialog();
        }
    }

    /**
     * Open modal dialog window for editing selected branch
     * */
    public void openEditDialog() {
        Branch branch = view.getSelectedBranch();

        view.setEditBranchName(branch.getName());
        view.setEditBranchDescription(branch.getDescription());
        view.openEditBranchDialog();
    }

    /**
     * Use for store edited branch from dialog window
     * */
    public void editBranch() {

        Branch branch = view.getSelectedBranch();
        if (!branchService.isBranchNameExists(view.getEditBranchName())) {
            branch.setName(view.getEditBranchName());
            branch.setDescription(view.getEditBranchDescription());
            try {
                branchService.saveBranch(branch);
            } catch (NotUniqueException ex) {
                // TODO: add processing here
                Logger.getLogger(BranchPresenter.class.getName()).log(
                        Level.SEVERE, null, ex);
            }

            view.updateBranch(branch);
            view.closeEditBranchDialog();
        } else {
            view.openErrorPopupInEditBranchDialog();
        }
    }

    /**
     * Delete branch
     * */
    public void deleteBranch() {

        Branch branch = view.getSelectedBranch();

        if (branch != null) {
            branchService.deleteBranch(branch);
            view.removeBranch(branch);
        }
    }

}

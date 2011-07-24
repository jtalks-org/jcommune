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

import java.util.List;

import org.jtalks.poulpe.model.entity.Branch;

/**
 * Interface for managing representation branches in view
 * 
 * @author Bekrenev Dmitry
 * */
public interface BranchView {

    /**
     * Show branches List
     * 
     * @param branches
     *            List of branches for showing
     * */
    void showBranches(List<Branch> branches);

    /**
     * Show branch
     * 
     * @param branch
     *            branch for showing
     * */
    void showBranch(Branch branch);

    /**
     * Remove branch from view. When branch marking as deleted this method allow
     * update view.
     * 
     * @param branch
     *            branch for remove from view
     * */
    void removeBranch(Branch branch);

    /**
     * Update showed branch When branch will edited is method use for update
     * view
     * 
     * @param branch
     *            branch which was edited and should be updated
     * */
    void updateBranch(Branch branch);

    /**
     * Open modal dialog for edit branch
     */
    void openEditBranchDialog();

    /**
     * Set name of branch which editing Use for initialize edit dialog
     * 
     * @param name
     *            name of editing branch
     * */
    void setEditBranchName(String name);

    /**
     * Set description of branch which editing Use for initialize edit dialog
     * 
     * @param description
     *            description of editing branch
     * */
    void setEditBranchDescription(String description);

    /**
     * Get name of branch which edited After edit this method use for get new
     * name of branch
     * 
     * @return edited name of branch
     * */
    String getEditBranchName();

    /**
     * Get description of branch which edited After edit this method use for get
     * new description of branch
     * 
     * @return edited description of branch
     * */
    String getEditBranchDescription();

    /**
     * Get selected branch When user select branch in list branches with the
     * help of this method we can get selected branch
     * 
     * @return selected branch
     * */
    Branch getSelectedBranch();

    /**
     * Get new branch name which will be stored
     * 
     * @return new branch name
     * */
    String getNewBranchName();

    /**
     * Get new branch description which will be stored
     * 
     * @return new branch description
     * */
    String getNewBranchDescription();

    /**
     * Method close all modal dialogs use for initialize view
     * */
    void closeDialogs();

    /**
     * Close new branch dialog
     * */

    void closeNewBranchDialog();

    /**
     * Close edit branch dialog
     * */
    void closeEditBranchDialog();

    /**
     * Open popup message when name new branch already exists
     * */
    void openErrorPopupInNewBranchDialog();

    /**
     * Open popup message when name edit branch already exists
     * */
    void openErrorPopupInEditBranchDialog();

}

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
import org.jtalks.poulpe.web.controller.DialogManager;
import org.jtalks.poulpe.web.controller.DialogManagerImpl;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * This class is implementation view for managing branches
 * 
 * @author Bekrenev Dmitry
 * */

public class BranchViewImpl extends Window implements BranchView, AfterCompose {

    private static final long serialVersionUID = -7175904766962858866L;
    private Listbox branchesList;

    /**
     * Important! If we are going to serialize/deserialize this class, this
     * field must be initialized explicitly during deserialization
     */
    private transient BranchPresenter presenter;

    private Window newBranchDialog;
    private Textbox newBranchDialog$branchName;
    private Textbox newBranchDialog$branchDescription;

    private Window editBranchDialog;
    private Textbox editBranchDialog$branchName;
    private Textbox editBranchDialog$branchDescription;

    private ListModelList branchesListModel;

    /**
     * Use for render ListItem This class draws two labels for branch name and
     * description for change view attributes branch list item use css classes:
     * .branch-name and .branch-description
     * */
    private static ListitemRenderer branchRenderer = new ListitemRenderer() {
        @Override
        public void render(Listitem item, Object data) {
            Branch branch = (Branch) data;

            Listcell cell = new Listcell();
            Label name = new Label(branch.getName());
            Label desc = new Label(branch.getDescription());
            Vbox vbox = new Vbox();

            name.setSclass("branch-name");
            desc.setSclass("branch-description");
            name.setParent(vbox);
            desc.setParent(vbox);
            vbox.setParent(cell);
            cell.setParent(item);
            item.setId(String.valueOf(branch.getId()));
        }
    };

    /**
     * {@inheritDoc}
     * */
    @Override
    public void afterCompose() {
        Components.addForwards(this, this);
        Components.wireVariables(this, this);

        branchesListModel = new ListModelList();
        branchesList.setModel(branchesListModel);
        branchesList.setItemRenderer(branchRenderer);
        presenter.initView(this);
    }

    /**
     * Set presenter
     * 
     * @see BranchPresenter
     * @param presenter
     *            instance presenter for view
     * */
    public void setPresenter(BranchPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public String getNewBranchDescription() {
        return newBranchDialog$branchDescription.getText();
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public String getNewBranchName() {
        return newBranchDialog$branchName.getText();
    }

    /**
     * {@inheritDoc}
     * */

    @Override
    public String getEditBranchName() {
        return editBranchDialog$branchName.getText();
    }

    /**
     * {@inheritDoc}
     * */

    @Override
    public void setEditBranchName(String branchName) {
        this.editBranchDialog$branchName.setText(branchName);
    }

    /**
     * {@inheritDoc}
     * */

    public String getEditBranchDescription() {
        return editBranchDialog$branchDescription.getText();
    }

    /**
     * {@inheritDoc}
     * */

    @Override
    public void setEditBranchDescription(String editBranchDescription) {
        this.editBranchDialog$branchDescription.setText(editBranchDescription);
    }

    /**
     * {@inheritDoc}
     * */

    @Override
    public void showBranches(List<Branch> branches) {
        branchesListModel.addAll(branches);
    }

    /**
     * {@inheritDoc}
     * */

    @Override
    public void showBranch(Branch branch) {
        branchesListModel.add(branch);

    }

    /**
     * {@inheritDoc}
     * */

    @Override
    public void removeBranch(Branch branch) {
        branchesListModel.remove(branch);
    }

    /**
     * {@inheritDoc}
     * */

    @Override
    public void updateBranch(Branch branch) {
        int index = branchesListModel.indexOf(branch);
        branchesListModel.set(index, branch);
    }

    /**
     * {@inheritDoc}
     * */

    @Override
    public void closeDialogs() {
        closeNewBranchDialog();
        closeEditBranchDialog();
    }

    /**
     * {@inheritDoc}
     * */

    @Override
    public Branch getSelectedBranch() {
        int index = branchesList.getSelectedIndex();

        if (index != -1) {
            return (Branch) branchesListModel.get(index);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * */

    @Override
    public void openEditBranchDialog() {
        editBranchDialog.setVisible(true);
    }

    /**
     * Event which happen when user click on add new branch button after it
     * opens dialog for adding new branch
     * */
    public void onClick$addBranchButton() {
        openNewBranchDialog();
    }

    /**
     * Event which happen when user click on delete branch button after it
     * opening confirmation dialog and if user confirms selected branch will be
     * marked as deleted if no one branch selected then not happen
     * */
    public void onClick$delBranchButton() {
        if (branchesList.getSelectedCount() == 1) {
            Branch branch = (Branch) branchesListModel.get(branchesList
                    .getSelectedIndex());
            DialogManager dmanager = new DialogManagerImpl();
            dmanager.confirmDeletion(branch.getName(),
                    new DialogManager.Performable() {

                        @Override
                        public void execute() {
                            presenter.deleteBranch();
                        }
                    });
        }
    }

    /**
     * Event which happen when user double click on branch after it will open
     * edit dialog
     * */
    public void onDoubleClick$branchesList() {
        presenter.openEditDialog();
    }

    /**
     * Event which happen when user click on edit button in edit branch dialog
     * window this cause save changed branch
     * */
    public void onClick$editButton$editBranchDialog() {
        presenter.editBranch();
    }

    /**
     * Event which happen when name branch textbox field get focus in edit
     * dialog This event cause clear error message
     * */
    public void onFocus$branchName$editBranchDialog() {
        editBranchDialog$branchName.setErrorMessage("");
    }

    /**
     * Event which happen when user click on cancel button in edit branch dialog
     * window this cause close edit dialog
     * */
    public void onClick$closeButton$editBranchDialog() {
        closeEditBranchDialog();
    }

    /**
     * Event which happen when user click on cancel button in new branch dialog
     * window this cause close new branch dialog
     * */
    public void onClick$closeButton$newBranchDialog() {
        closeNewBranchDialog();
    }

    /**
     * Event which happen when user click on add button in new branch dialog
     * window this cause save new branch
     * */
    public void onClick$addButton$newBranchDialog() {
        presenter.addNewBranch();
    }

    /**
     * Event which happen when name branch textbox field get focus This event
     * cause clear error message
     * */
    public void onFocus$branchName$newBranchDialog() {
        newBranchDialog$branchName.setErrorMessage("");
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void openErrorPopupInNewBranchDialog() {
        final String message = Labels
                .getLabel("branches.error.branch_name_already_exists");
        newBranchDialog$branchName.setErrorMessage(message);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void openErrorPopupInEditBranchDialog() {
        final String message = Labels
                .getLabel("branches.error.branch_name_already_exists");
        editBranchDialog$branchName.setErrorMessage(message);
    }

    /** {@inheritDoc} */
    @Override
    public void closeNewBranchDialog() {
        newBranchDialog.setVisible(false);
        newBranchDialog$branchName.setText("");
        newBranchDialog$branchDescription.setText("");
    }

    /** {@inheritDoc} */
    @Override
    public void closeEditBranchDialog() {
        editBranchDialog.setVisible(false);
        editBranchDialog$branchName.setText("");
        editBranchDialog$branchDescription.setText("");
    }

    /** Open new branch dialog */
    private void openNewBranchDialog() {
        newBranchDialog.setVisible(true);
    }

}

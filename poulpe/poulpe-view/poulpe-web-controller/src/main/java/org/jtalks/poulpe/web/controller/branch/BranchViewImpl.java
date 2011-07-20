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
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
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
    private static final long serialVersionUID = 8689635788676853738L;
    private Listbox branchesList;
    private BranchPresenter presenter;

    private Window newBranchDialog;
    private Textbox newBranchDialog$branchName;
    private Textbox newBranchDialog$branchDescription;

    private Window editBranchDialog;
    private Textbox editBranchDialog$branchName;
    private Textbox editBranchDialog$branchDescription;

    private ListModelList branchesListModel;

    /**
     * {@inheritDoc}
     * */
    @Override
    public void afterCompose() {
        Components.addForwards(this, this);
        Components.wireVariables(this, this);

        branchesListModel = new ListModelList();
        branchesList.setModel(branchesListModel);
        branchesList.setItemRenderer(new ListitemRenderer() {

            @Override
            public void render(Listitem item, Object data) throws Exception {
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
        });

        presenter.initView(this);
    }

    /**
     * Set presenter
     * 
     * @see BranchPresenter
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
    public void onClick$addBranchButton(Event event) {
        openNewBranchDialog();
    }

    /**
     * Event which happen when user click on delete branch button after it
     * selected branch will be marked as deleted if no one branch selected then
     * not happen
     * */
    public void onClick$delBranchButton(Event event) {
        if (branchesList.getSelectedCount() == 1) {
            presenter.markBranchAsDelete();
        }
    }

    /**
     * Event which happen when user double click on branch after it will open
     * edit dialog
     * */
    public void onDoubleClick$branchesList(Event event) {
        presenter.openEditDialog();
    }

    /**
     * Event which happen when user click on edit button in edit branch dialog
     * window this cause save changed branch
     * */
    public void onClick$editButton$editBranchDialog() {
        presenter.editBranch();
        closeEditBranchDialog();
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
    public void onClick$closeButton$newBranchDialog(Event event) {
        closeNewBranchDialog();
    }

    /**
     * Event which happen when user click on add button in new branch dialog
     * window this cause save new branch
     * */
    public void onClick$addButton$newBranchDialog(Event event) {
        presenter.addNewBranch();
        closeNewBranchDialog();
    }

    /* Close new branch dialog add flush fields */
    private void closeNewBranchDialog() {
        newBranchDialog.setVisible(false);
        newBranchDialog$branchName.setText("");
        newBranchDialog$branchDescription.setText("");
    }

    /* Close edit dialog */
    private void closeEditBranchDialog() {
        editBranchDialog.setVisible(false);
        editBranchDialog$branchName.setText("");
        editBranchDialog$branchDescription.setText("");
    }

    /* Open new branch dialog */
    private void openNewBranchDialog() {
        newBranchDialog.setVisible(true);
    }

}

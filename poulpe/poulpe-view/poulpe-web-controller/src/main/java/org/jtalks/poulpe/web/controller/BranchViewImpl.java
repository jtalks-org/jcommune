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

package org.jtalks.poulpe.web.controller;

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

    @Override
    public void afterCompose() {
        Components.addForwards(this, this);
        Components.wireVariables(this, this);
        presenter.initView(this);
        branchesList.setItemRenderer(new ListBranchesRenderer());
    }

    public void setPresenter(BranchPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String getNewBranchDescription() {
        return newBranchDialog$branchDescription.getText();
    }

    @Override
    public String getNewBranchName() {
        return newBranchDialog$branchName.getText();
    }

    public String getEditBranchName() {
        return editBranchDialog$branchName.getText();
    }

    public void setEditBranchName(String branchName) {
        this.editBranchDialog$branchName.setText(branchName);
    }

    public String getEditBranchDescription() {
        return editBranchDialog$branchDescription.getText();
    }

    public void setEditBranchDescription(String editBranchDescription) {
        this.editBranchDialog$branchDescription.setText(editBranchDescription);
    }

    public void onClick$addBranchButton(Event event) {
        openNewBranchDialog();
    }

    public void onClick$delBranchButton(Event event) {
        if (branchesList.getSelectedCount() == 1) {
            presenter.markBranchAsDelete();
        }
    }

    public void onDoubleClick$branchesList(Event event) {
        presenter.openEditDialog();
    }

    public void onClick$editButton$editBranchDialog() {
        presenter.editBranch();
        closeEditBranchDialog();
    }

    public void onClick$closeButton$editBranchDialog() {
        closeEditBranchDialog();
    }

    public void onClick$closeButton$newBranchDialog(Event event) {
        closeNewBranchDialog();
    }

    public void onClick$addButton$newBranchDialog(Event event) {
        presenter.addNewBranch();
        closeNewBranchDialog();
    }

    @Override
    public void setBranchListModel(ListModelList branchModel) {
        branchesList.setModel(branchModel);
    }

    @Override
    public void closeDialogs() {
        closeNewBranchDialog();
        closeEditBranchDialog();
    }

    @Override
    public int getSelectedBranchIndex() {
        return branchesList.getSelectedIndex();
    }

    @Override
    public void openEditBranchDialog() {
        editBranchDialog.setVisible(true);
    }

    private void closeNewBranchDialog() {
        newBranchDialog.setVisible(false);
        newBranchDialog$branchName.setText("");
        newBranchDialog$branchDescription.setText("");
    }

    private void closeEditBranchDialog() {
        editBranchDialog.setVisible(false);
    }

    private void openNewBranchDialog() {
        newBranchDialog.setVisible(true);
    }

}

class ListBranchesRenderer implements ListitemRenderer {

    @Override
    public void render(Listitem item, Object data) throws Exception {
        Branch branch = (Branch) data;

        Listcell cell = new Listcell();
        Label name = new Label(branch.getName());
        Label desc = new Label(branch.getDescription());
        Vbox vbox = new Vbox();

        name.setParent(vbox);
        desc.setParent(vbox);
        vbox.setParent(cell);
        cell.setParent(item);
        item.setId(String.valueOf(branch.getId()));
    }
}

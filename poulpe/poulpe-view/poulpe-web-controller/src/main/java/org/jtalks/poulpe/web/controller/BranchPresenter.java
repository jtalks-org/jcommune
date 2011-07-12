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
 * Creation date: July 10, 2011
 * The jtalks.org Project
 */

package org.jtalks.poulpe.web.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jtalks.poulpe.model.entity.Branch;
import org.jtalks.poulpe.service.BranchService;
import org.jtalks.poulpe.service.exceptions.NotFoundException;
import org.zkoss.zul.ListModelList;

public class BranchPresenter {

	private BranchView view;
	private BranchService branchService;
	
	private ListModelList branchModel;
	
	
	public void setBranchService(BranchService service){
		branchService = service;
	}
	
	public void initView(BranchView view){
		this.view = view;
		branchModel = new ListModelList(getBranches());
		view.setBranchListModel(branchModel);
		view.closeDialogs();
	}

	public void addNewBranch() {		
		String name = view.getNewBranchName();
		String desc = view.getNewBranchDescription();
		
		Branch branch = new Branch();
		branch.setName(name);
		branch.setDescription(desc);
		branchService.saveBranch(branch);
		branchModel.add(branch);
	}
	
	public void openEditDialog(){
		int id = view.getSelectedBranchIndex();		
		Branch branch = (Branch)branchModel.get(id);			
			
		view.setEditBranchName( branch.getName());
		view.setEditBranchDescription( branch.getDescription() );
		view.openEditBranchDialog();		
	}
	
	public void editBranch(){
		int index = view.getSelectedBranchIndex();
		Branch branch = (Branch)branchModel.get(index);
						
		branch.setName(view.getEditBranchName());
		branch.setDescription(view.getEditBranchDescription());
		
		branchService.saveBranch(branch);
		branchModel.set(index, branch);		
	}
	
	public void markBranchAsDelete(){
		int index = view.getSelectedBranchIndex();
		Branch branch = (Branch)branchModel.get(index);
		
		branchModel.remove(branch);
		branchService.deleteBranch(branch);		
	}
	
	private List<Branch> getBranches(){
		List<Branch> branches = new ArrayList<Branch>();
		
		for(Branch branch: branchService.getAll()){
			if(! branch.getDeleted())
				branches.add(branch);
		}
		return branches;
	}	
	
}

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

import java.util.List;

import org.jtalks.poulpe.model.entity.Branch;
import org.zkoss.zul.ListModelList;

public interface BranchView {
	
	void setBranchListModel(ListModelList branchModel);
	
	void openEditBranchDialog();
	
	void setEditBranchName(String name);
	void setEditBranchDescription(String description);
	
	String getEditBranchName();
	String getEditBranchDescription();
	
	int getSelectedBranchIndex();
	String getNewBranchName();
	String getNewBranchDescription();
	
	void closeDialogs();	
	
}

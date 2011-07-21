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
package org.jtalks.poulpe.service;

import org.jtalks.poulpe.service.exceptions.NotUniqueException;
import org.jtalks.poulpe.model.entity.Branch;

import java.util.List;

/**
 * @author Vitaliy Kravchenko
 * @author Kirill Afonin
 */
public interface BranchService extends EntityService<Branch> {

    /**
     * Get list of all persistence objects T currently present in database.
     *
     * @return - list of persistence objects T.
     */
    List<Branch> getAll();

    /**
     * Mark the branch as deleted.
     * @param selectedBranch branch to delete
     */
    void deleteBranch(Branch selectedBranch);

    /**
     * Save or update branch.
     * @param selectedBranch instance to save
     * @throws NotUniqueException if branch with the same name already exists
     */
    void saveBranch(Branch selectedBranch) throws NotUniqueException;
    
    /**
     * Check if branch with given name exists.
     * @param branchName
     * @return true if exists
     */
    boolean isBranchNameExists(String branchName);
}
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
package org.jtalks.jcommune.service;

import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.jcommune.model.dto.GroupsPermissions;
import org.jtalks.jcommune.model.dto.PermissionChanges;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.service.exceptions.NotFoundException;

import java.util.List;

/**
 * The interface to manipulate with branches
 *
 * @author Vitaliy Kravchenko
 * @author Kirill Afonin
 * @author Max Malakhov
 * @author Eugeny Batov
 */

public interface BranchService extends EntityService<Branch> {

    /**
     * Get all available for move topic branches.
     *
     * @param currentTopicId topic id that we want to move
     * @return list of {@code Branch} objects
     */
    List<Branch> getAllAvailableBranches(long currentTopicId);

    /**
     * Get available for move topic branches in section.
     *
     * @param sectionId section id from which we obtain branches
     * @param currentTopicId topic id that we want to move
     * @return list of {@code Branch} objects
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when section not found
     */
    List<Branch> getAvailableBranchesInSection(long sectionId, long currentTopicId) throws NotFoundException;
    
    /**
     * Fills the statistical information for each branch from the list:
     * 1)count of topics in the branch
     * 2)count of posts in the branch
     *  
     * @param branches list of branches
     */
    void fillStatisticInfo(List<org.jtalks.common.model.entity.Branch> branches);
    
    /**
     * Deletes all topics in this branch and recalculates user posts.
     *
     * @param branchId branch id
     * @throws NotFoundException when branch not found
     * @return branch for the id given
     */
    Branch deleteAllTopics(long branchId) throws NotFoundException;

    /**
     * Sets new title and description for the branch with specified ID
     * @param componentId ID of the component of the branch
     * @param branchId ID of the branch to change the information
     * @param title new branch title
     * @param description new branch description
     */
    void changeBranchInfo(long componentId, long branchId, String title, String description) throws NotFoundException;

    /**
     * Create new branch in the specified section.
     * @param componentId ID of the component of the branch
     * @param sectionId section id where the branch should be created
     * @param title new branch title
     * @param description new branch description
     */
    void createNewBranch(long componentId, long sectionId, String title, String description);
    
    /**
     * Check if branch exists
     * @param branchId Id of the branch
     * @throws NotFoundException if branch does not exist
     */
    void checkIfBranchExists(long branchId) throws NotFoundException;

    /**
     * Gets the list of permissions for given branch
     * @param componentId ID of the component of the branch (needed only for the authorization purpose)
     * @param branchId ID of the branch to the permissions
     * @return information about the permissions of the branch
     * @throws NotFoundException when branch does not exist
     */
     GroupsPermissions getPermissionsFor(long componentId, long branchId) throws NotFoundException;

    /**
     * For given branch and permission get the list of the groups for which permission has been allowed or restricted
     * @param componentId ID of the component of the branch (needed only for the authorization purpose)
     * @param branchId ID of the branch to get the permissions
     * @param allowed if true then result will contain the list of the Groups for which permission is allowed,
     *                if false then result will contain the list of the Groups for which permission is restricted
     * @param permission permission to get the group list
     * @return the list of the groups for which permission has been allowed or restricted
     * @throws NotFoundException when branch does not exist
     */
    <T extends JtalksPermission> List<Group> getPermissionGroupsFor(long componentId, long branchId, boolean allowed, T permission)
            throws NotFoundException;

    /**
     * Changes permissions for given branch
     * @param componentId ID of the component of the branch (needed only for the authorization purpose)
     * @param branchId ID of the branch which permissions will be changed
     * @param allowed if true then branch grants will be changed
     *                if false then branch restrictions will be changed
     * @param changes object which represent permission changes for given branch
     * @throws NotFoundException when branch does not exist
     * @see org.jtalks.jcommune.model.dto.PermissionChanges
     */
    void changeBranchPermissions(long componentId, long branchId, boolean allowed, PermissionChanges changes)
            throws NotFoundException;
}
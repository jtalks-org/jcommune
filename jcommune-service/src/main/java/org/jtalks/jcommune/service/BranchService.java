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

import java.util.List;

import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.service.exceptions.NotFoundException;

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
     * Get all existing branches.
     *
     * @return list of {@code Branch} objects
     */
    List<Branch> getAllBranches();

    /**
     * Get branches from section.
     *
     * @param sectionId section id from which we obtain branches
     * @return list of {@code Branch} objects
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when section not found
     */
    List<Branch> getBranchesInSection(long sectionId) throws NotFoundException;
    
    /**
     * Fills the statistical information for each branch from the list:
     * 1)count of topics in the branch
     * 2)count of posts in the branch
     *  
     * @param branches list of branches
     */
    void fillStatisticInfo(List<org.jtalks.common.model.entity.Branch> branches);
    
    /**
     * Find the latest post in the latest updated topic and saves in
     * a transient field in the branch for all branches in the list.
     * 
     * @param branches list of branches
     */
    void fillLastPostInLastUpdatedTopic(List<org.jtalks.common.model.entity.Branch> branches);
}
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

import org.jtalks.jcommune.model.entity.Branch;

/**
 * Provides an ability to find and refresh the last post in the branch.
 * 
 * @author Anuar_Nurmakanov
 *
 */
public interface BranchLastPostService {
    /**
     * Determine the last post in the branch and update it value
     * in the branch when a post was deleted in this branch.
     * 
     * @param branch for this branch it determine the last post
     */
    void refreshLastPostInBranch(Branch branch);
}

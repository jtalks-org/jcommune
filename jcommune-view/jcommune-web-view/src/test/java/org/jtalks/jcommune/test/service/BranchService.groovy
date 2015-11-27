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
package org.jtalks.jcommune.test.service

import org.jtalks.jcommune.model.dao.BranchDao
import org.jtalks.jcommune.model.entity.Branch
import org.springframework.beans.factory.annotation.Autowired

/**
 * @author Mikhail Stryzhonok
 */
class BranchService {

    @Autowired
    private BranchDao branchDao

    def create() {
        def branch = new Branch("Branch name", "Description")
        branchDao.saveOrUpdate(branch)
        return branch
    }

    def isExist(String name) {
        def branches = branchDao.getAllBranches()
        for (def branch : branches) {
            if (branch.name == name) {
                return true;
            }
        }
        return false
    }
}

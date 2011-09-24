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
package org.jtalks.jcommune.service.transactional;

import org.jtalks.jcommune.model.dao.SectionDao;
import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;

import java.util.List;

/**
 * The implementation of BranchService
 * 
 * @author Vitaliy Kravchenko
 * @author Max Malakhov
 */

public class TransactionalBranchService extends AbstractTransactionalEntityService<Branch, BranchDao>
        implements BranchService {

    private SectionDao sectionDao;

    /**
     * Create an instance of entity based service
     *
     * @param branchDao - data access object, which should be able do all CRUD operations.
     * @param sectionDao - used for checking branch existance.
     */
    public TransactionalBranchService(BranchDao branchDao, SectionDao sectionDao) {
        this.dao = branchDao;
        this.sectionDao = sectionDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Branch> getBranchesInSection(long sectionId) throws NotFoundException {
        if (!sectionDao.isExist(sectionId)) {
            throw new NotFoundException("Section with id: " + sectionId + " not found");
        }
        return dao.getBranchesInSection(sectionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBranchesInSectionCount(long sectionId) throws NotFoundException {
        if (!sectionDao.isExist(sectionId)) {
            throw new NotFoundException("Section with id: " + sectionId + " not found");
        }
        return dao.getBranchesInSectionCount(sectionId);
    }
}
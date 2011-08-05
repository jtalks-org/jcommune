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
package org.jtalks.poulpe.service.transactional;

import org.jtalks.poulpe.model.dao.BranchDao;
import org.jtalks.poulpe.model.entity.Branch;
import org.jtalks.poulpe.service.BranchService;

import java.util.List;
import org.hibernate.exception.ConstraintViolationException;
import org.jtalks.poulpe.service.exceptions.NotUniqueException;

/**
 * 
 * @author Vitaliy Kravchenko
 * @author Pavel Vervenko
 */
public class TransactionalBranchService extends AbstractTransactionalEntityService<Branch, BranchDao>
        implements BranchService {

    /**
     * Create an instance of entity based service
     *
     * @param branchDao - data access object, which should be able do all CRUD operations.
     */
    public TransactionalBranchService(BranchDao branchDao) {
        this.dao = branchDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Branch> getAll() {
        return dao.getAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteBranch(Branch selectedBranch) {
          // TODO: check returned value? 
          dao.delete(selectedBranch.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveBranch(Branch selectedBranch) throws NotUniqueException {
        try {
            dao.saveOrUpdate(selectedBranch);
        } catch (ConstraintViolationException e) {
            throw new NotUniqueException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBranchNameExists(String branchName) {
        return dao.isBranchNameExists(branchName);
    }
}
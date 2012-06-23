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

import org.jtalks.common.model.permissions.GeneralPermission;
import org.jtalks.jcommune.model.dao.SimplePageDao;
import org.jtalks.jcommune.model.entity.SimplePage;
import org.jtalks.jcommune.service.SimplePageService;
import org.jtalks.jcommune.service.dto.SimplePageInfoContainer;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.persistence.EntityExistsException;

/**
 * SimplePage service class. This class contains method needed to manipulate with simple page persistent entity.
 *
 * @author Scherbakov Roman
 * @author Alexander Gavrikov
 */

public class TransactionalSimplePageService extends AbstractTransactionalEntityService<SimplePage, SimplePageDao> implements SimplePageService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *  Create an instance of Simple Page entity based service
     *
     *  @param simplePageDao - data access object which should be create or get simplePage object from database
     */

    public TransactionalSimplePageService(SimplePageDao simplePageDao) {
        super(simplePageDao);
    }

    /**
     * {@inheritDoc}
     */
    @PreAuthorize("hasPermission(#simplePageInfoContainer.id, 'org.jtalks.jcommune.model.entity.SimplePage', 'GeneralPermission.WRITE')")
    @Override
    public void updatePage(SimplePageInfoContainer simplePageInfoContainer) throws NotFoundException {

        SimplePage simplePage = get(simplePageInfoContainer.getId());
        if (simplePage == null) {
            String message = "Simple page with id = " + simplePage.getId() + " not found.";
            logger.info(message);
            throw new NotFoundException(message);
        }

        simplePage.setName(simplePageInfoContainer.getName());
        simplePage.setContent(simplePageInfoContainer.getContent());

        this.getDao().update(simplePage);

        logger.info("Simple page with id = " + simplePage.getId() + " update.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SimplePage getPageByPathName(String pathName) throws NotFoundException {
        SimplePage simplePage = this.getDao().getPageByPathName(pathName);
        if (simplePage == null) {
            String msg = "SimplePage " + pathName + " not found.";
            logger.info(msg);
            throw new NotFoundException(msg);
        }
        return simplePage;
    }


    /**
     * {@inheritDoc}
     */
    @PreAuthorize("hasPermission(#simplePage.id, 'org.jtalks.jcommune.model.entity.SimplePage', 'GeneralPermission.WRITE')")
    @Override
    public SimplePage createPage(SimplePage simplePage) throws EntityExistsException {

        if(getDao().isExist(simplePage.getPathName())) {
            String msg = "SimplePage with pathName = " + simplePage.getPathName() + " already exists.";
            logger.info(msg);
            throw new EntityExistsException(msg);
        }

        this.getDao().update(simplePage);
        logger.info("SimplePage registered: {}", simplePage.getName());
        return simplePage;
    }

}
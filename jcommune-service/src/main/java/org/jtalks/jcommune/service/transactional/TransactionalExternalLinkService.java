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

import org.jtalks.common.model.entity.Component;
import org.jtalks.jcommune.model.dao.ExternalLinkDao;
import org.jtalks.jcommune.model.entity.ExternalLink;
import org.jtalks.jcommune.service.ExternalLinkService;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * @author Alexandre Teterin
 *         Date: 03.02.13
 */
public class TransactionalExternalLinkService extends AbstractTransactionalEntityService<ExternalLink, ExternalLinkDao>
        implements ExternalLinkService {

    /**
     * Subclass may use this constructor to store entity DAO or parent
     * entity DAO if necessary
     *
     * @param dao subclass-provided dao object
     */
    public TransactionalExternalLinkService(ExternalLinkDao dao) {
        super(dao);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ExternalLink> getLinks() {
        return getDao().getAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#forumComponent.id, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public void saveLink(ExternalLink link, Component forumComponent) {
        getDao().saveOrUpdate(link);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#forumComponent.id, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public boolean deleteLink(long id, Component forumComponent) {
        return getDao().delete(id);
    }
}

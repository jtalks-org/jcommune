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
import org.jtalks.jcommune.model.dao.ComponentDao;
import org.jtalks.jcommune.model.entity.ComponentInformation;
import org.jtalks.jcommune.service.ComponentService;

/**
 * The implementation of {@link ComponentService}.
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class TransactionalComponentService extends AbstractTransactionalEntityService<Component, ComponentDao> 
    implements ComponentService {

    /**
     * Constructs an instance with required fields.
     * 
     * @param dao to get component
     */
    public TransactionalComponentService(ComponentDao dao) {
        super(dao);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getComponentOfForum() {
        return getDao().getComponent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setComponentInformation(ComponentInformation componentInformation) {
        getDao().setComponentInformation(componentInformation);
    }
}

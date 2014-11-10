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

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.SimplePage;
import org.jtalks.jcommune.service.dto.SimplePageInfoContainer;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;

import javax.persistence.EntityExistsException;

/**
 * This interface should have methods which give us more abilities in working Simple page persistent entity.
 *
 * @author Scherbakov Roman
 * @author Alexander Gavrikov
 */

public interface SimplePageService extends EntityService<SimplePage> {

    /**
     * Change name or/and content of current {@link SimplePage}
     * If entity {@link SimplePage} with given ID wasn't found it throws NotFoundExeption
     *
     * @param simplePage Entity of simple page {@link SimplePage}
     *
     * @throws NotFoundException if entity {@link SimplePage} with given ID wasn't found
     */
    public void updatePage(SimplePageInfoContainer simplePageInfoContainer) throws NotFoundException;

    /**
     * Create new {@link SimplePage} and save it
     * Path name is unique field and there should not exist a page with the same path name
     * @param simplePage    simple page which will be created
     *
     * @return created {@link SimplePage}
     *
     * @throws EntityExistsException if {@link SimplePage} with path name of new page already exists
     */
    public SimplePage createPage(SimplePage simplePage, JCUser userCreator) throws EntityExistsException;

    /**
     * Get {@link SimplePage} entity which associated of pathName in browser
     *
     * @param pathName          address in browser which associated with current simple page
     * @return simplePage       with current associated which current browser
     *
     * @throws NotFoundException if entity {@link SimplePage} with given path name wasn't found
     */
    public SimplePage getPageByPathName(String pathName) throws NotFoundException;
}


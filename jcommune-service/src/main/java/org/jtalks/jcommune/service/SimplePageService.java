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

import org.jtalks.jcommune.model.entity.SimplePage;
import org.jtalks.jcommune.service.dto.SimplePageInfoContainer;
import org.jtalks.jcommune.service.exceptions.NotFoundException;

/**
 * This interface should have methods which give us more abilities in working Simple page persistent entity.
 *
 * @author Scherbakov Roman
 * @author Alexander Gavrikov
 */

public interface SimplePageService extends EntityService<SimplePage> {

    /**
     * change name or/and content of current SimplePage
     *
     * @param pageId        identifier of simple page
     * @param pageName      name of simple page
     * @param pageContent   content of simple page
     */
    public void updatePage(long pageId, String pageName, String pageContent) throws NotFoundException;

    /**
     * create new SimplePage, put it in database
     *
     * @param simplePage    simple page which will be created
     */
    public SimplePage createPage(SimplePage simplePage);

    /**
     * get SimplePage object which associated of pathName in browser
     *
     * @param pathName          address in browser which associated with current simple page
     * @return simplePage       with current associated which current browser
     */
    public SimplePage getPageByPathName(String pathName) throws NotFoundException;
}


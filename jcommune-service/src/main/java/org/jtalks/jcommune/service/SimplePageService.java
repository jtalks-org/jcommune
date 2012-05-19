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
import org.jtalks.jcommune.service.exceptions.NotFoundException;

/**
 * This interface should have methods which give us more abilities in working Simple page persistent entity.
 *
 * @author Scherbakov Roman
 * @author Alexander Gavrikov
 */

public interface SimplePageService extends EntityService<SimplePage> {

    /**
     * Update page with given content.
     *
     * @param pageId      page id
     * @param pageContent page content
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when page not found
     */
    public void updatePage(long pageId, String pageName, String pageContent) throws NotFoundException;

    public SimplePage createPage(SimplePage simplePage);

    /**
     * get SimplePage by name
     *
     * @param pathName path name
     * @return simplePage with current path name
     */
    public SimplePage getPageByPathName(String pathName) throws NotFoundException;
}


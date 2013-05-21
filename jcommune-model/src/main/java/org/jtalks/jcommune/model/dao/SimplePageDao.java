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
package org.jtalks.jcommune.model.dao;

import org.jtalks.common.model.dao.Crud;
import org.jtalks.jcommune.model.entity.SimplePage;
import org.springframework.security.acls.model.NotFoundException;

/**
 * Interface allows to make basic CRUD operations with the
 * {@link org.jtalks.jcommune.model.entity.SimplePage} objects.
 * At the current moment it doesn't provides any additional methods over the basic {@link Crud} interface
 * but some specific methods will be added soon.
 *
 * @author Scherbakov Roman
 * @author Alexander Gavrikov
 */
public interface SimplePageDao extends Crud<SimplePage> {

    /**
     * Create a new simple page and then put it into database
     *
     * @param simplePage simple page object which will be created
     */
    public void createPage(SimplePage simplePage);

    /**
     * Get a simple page associated with current path name in browser
     *
     * @param pathName      address in browser which associated with current simple page
     * @return SimplePage   simple page with the specified by path name
     */
    public SimplePage getPageByPathName(String pathName) throws NotFoundException;

    /**
     * Check does {@link SimplePage} with current path name exist
     * @param pagePathName path name of {@link SimplePage}
     * @return true if page exists or false if there's no
     */
    public boolean isExist(String pagePathName);

}

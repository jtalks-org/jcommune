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
import org.jtalks.jcommune.model.entity.Banner;
import org.jtalks.jcommune.model.entity.BannerPosition;

import java.util.Collection;

/**
 * DAO for the {@link Banner}. It provides all CRUD operations
 * to work with {@link Banner} in repository.
 * 
 * @author Anuar_Nurmakanov
 *
 */
public interface BannerDao extends Crud<Banner> {
    /**
     * Get banner by position. By default we don't have banner
     * for every position on page, so it's possible that you don't
     * find banner.
     * 
     * @param positionOnPage position of banner on page
     * @return banner if it has been attached to concrete position, 
     *         otherwise null
     */
    Banner getByPosition(BannerPosition positionOnPage);
    
    /**
     * Get all banners of the forum.
     * 
     * @return all banners of the forum
     */
    Collection<Banner> getAll();
}

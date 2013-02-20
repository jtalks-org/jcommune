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

import org.jtalks.common.model.entity.Component;
import org.jtalks.jcommune.model.entity.Banner;

import java.util.Map;

/**
 * Provides an API to work with banners. Note, that there is no such method as "delete" banner, if user actually wants
 * to remove one, she uploads an empty banner.
 *
 * @author Anuar_Nurmakanov
 */
public interface BannerService {
    /**
     * Adds a banner to the page. If a banner with such position ({@link org.jtalks.jcommune.model.entity
     * .BannerPosition}) already exists, then content is just updated. Empty banner means user removed it.
     *
     * @param banner         a banner uploaded by a user
     * @param forumComponent a component of the forum
     */
    void uploadBanner(Banner banner, Component forumComponent);

    /**
     * Get all banners of the forum. Result contains string keys, because it's requirement of UI that can't work with
     * enumerations.
     *
     * @return all banners of the forum grouped in pairs: name of position and banner
     * @see org.jtalks.jcommune.model.entity.BannerPosition
     */
    Map<String, Banner> getAllBanners();
}

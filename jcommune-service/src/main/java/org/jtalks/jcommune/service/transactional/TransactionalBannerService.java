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

import org.apache.commons.lang.ObjectUtils;
import org.jtalks.common.model.entity.Component;
import org.jtalks.jcommune.model.dao.BannerDao;
import org.jtalks.jcommune.model.entity.Banner;
import org.jtalks.jcommune.model.entity.BannerPosition;
import org.jtalks.jcommune.service.BannerService;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** @author Anuar_Nurmakanov */
public class TransactionalBannerService extends AbstractTransactionalEntityService<Banner, BannerDao>
        implements BannerService {

    /**
     * Constructs an instance with given DAO, so it addresses to repository (in our case database).
     *
     * @param bannerDao to search and change banner in database
     */
    public TransactionalBannerService(BannerDao bannerDao) {
        super(bannerDao);
    }

    /**
     * {@inheritDoc}
     *
     * @param forumComponent this one is passed only to check whether current user has admin permissions for the
     *                       component and thus is allowed to upload banners
     */
    @Override
    @PreAuthorize("hasPermission(#forumComponent.id, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public void uploadBanner(Banner uploadedBanner, Component forumComponent) {
        Banner existBanner = getDao().getByPosition(uploadedBanner.getPositionOnPage());
        if (existBanner == null) {
            existBanner = uploadedBanner;
        } else {
            existBanner.setContent(uploadedBanner.getContent());
        }
        getDao().saveOrUpdate(existBanner);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Banner> getAllBanners() {
        Collection<Banner> allBanners = getDao().getAll();
        Map<String, Banner> positionAndBannerMap = new HashMap<String, Banner>();
        for (Banner banner : allBanners) {
            BannerPosition positionOnPage = banner.getPositionOnPage();
            positionAndBannerMap.put(ObjectUtils.toString(positionOnPage), banner);
        }
        return positionAndBannerMap;
    }
}

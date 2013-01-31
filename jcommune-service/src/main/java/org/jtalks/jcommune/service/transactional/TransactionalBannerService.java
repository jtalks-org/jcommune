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

import org.jtalks.jcommune.model.dao.BannerDao;
import org.jtalks.jcommune.model.entity.Banner;
import org.jtalks.jcommune.service.BannerService;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class TransactionalBannerService implements BannerService {
    private BannerDao bannerDao;
    
    /**
     * Constructs an instance with required fields.
     * 
     * @param bannerDao to working with banner repository(database in our case)
     */
    public TransactionalBannerService(BannerDao bannerDao) {
        this.bannerDao = bannerDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void attachBannerToPosition(int positionOnPage, String bannerContent) {
        Banner banner = getBannerByPosition(positionOnPage);
        if (banner == null) {
            banner = new Banner();
            banner.setPositionOnPage(positionOnPage);
        }
        banner.setContent(bannerContent);
        bannerDao.saveOrUpdate(banner);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Banner getBannerByPosition(int positionOnPage) {
        return bannerDao.getByPosition(positionOnPage);
    }
}

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
package org.jtalks.jcommune.model.dao.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jtalks.jcommune.model.dao.BannerDao;
import org.jtalks.jcommune.model.entity.Banner;
import org.jtalks.jcommune.model.entity.BannerPosition;
import org.jtalks.jcommune.model.entity.ObjectsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.testng.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Anuar_Nurmakanov
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class BannerHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private SessionFactory sessionFactory;
    private Session session;
    //
    @Autowired
    private BannerDao bannerDao;

    @BeforeMethod
    public void init() {
        session = sessionFactory.getCurrentSession();
    }
    /*===== Common methods =====*/

    @Test
    public void correctBannerShouldBeSaved() {
        Banner banner = ObjectsFactory.getDefaultBanner();

        bannerDao.saveOrUpdate(banner);

        assertNotSame(banner.getId(), 0, "Id not created");

        session.evict(banner);
        Banner bannerInDatabase = (Banner) session.get(Banner.class, banner.getId());

        assertReflectionEquals(banner, bannerInDatabase);
    }

    @Test(expectedExceptions = {Exception.class})
    public void bannerWithNullContentShoulNotBeSaved() {
        Banner banner = ObjectsFactory.getDefaultBanner();
        banner.setContent(null);

        bannerDao.saveOrUpdate(banner);
    }

    @Test
    public void correctBannerShouldBeUpdated() {
        String newContent = "<html><h1>New Header</h1><html>";
        Banner banner = ObjectsFactory.getDefaultBanner();
        session.save(banner);
        banner.setContent(newContent);
        //
        bannerDao.saveOrUpdate(banner);
        session.flush();
        session.evict(banner);
        Banner result = (Banner) session.get(Banner.class, banner.getId());

        assertEquals(result.getContent(), newContent);
    }

    @Test(expectedExceptions = org.hibernate.exception.ConstraintViolationException.class)
    public void bannerWithNullContentShouldNotBeUpdated() {
        Banner banner = ObjectsFactory.getDefaultBanner();
        session.save(banner);
        banner.setContent(null);

        bannerDao.saveOrUpdate(banner);
        session.flush();
    }

    @Test
    public void existsBannerShouldBeDeletedIfItIdPassed() {
        Banner banner = ObjectsFactory.getDefaultBanner();
        session.save(banner);
        session.flush();

        boolean isDeleted = bannerDao.delete(banner.getId());

        assertTrue(isDeleted, "Entity must be deleted by id.");
    }

    @Test
    public void notExistsBannerShouldNotBeDeleted() {
        boolean isDeleted = bannerDao.delete(-1500L);

        assertFalse(isDeleted, "Entity can't be deleted, because it doesn't exist in database.");
    }

    /*===== Specific methods =====*/
    @Test
    public void existsBannerShouldBeFoundByPosition() {
        Banner banner = ObjectsFactory.getDefaultBanner();
        banner.setPositionOnPage(BannerPosition.TOP);
        session.save(banner);
        session.flush();
        session.evict(banner);

        Banner bannerInDatabase = bannerDao.getByPosition(BannerPosition.TOP);

        assertNotNull(bannerInDatabase, "Banner should be found by position, because it was saved.");
        assertReflectionEquals(banner, bannerInDatabase);
    }

    @Test
    public void existsBannerShouldNotBeFoundByPosition() {
        Banner banner = ObjectsFactory.getDefaultBanner();
        banner.setPositionOnPage(BannerPosition.TOP);
        session.save(banner);
        session.flush();
        session.evict(banner);

        Banner bannerInDatabase = bannerDao.getByPosition(BannerPosition.BOTTOM);

        assertNull(bannerInDatabase,
                "Banner shouldn't be found by position, because different banner was saved.");
    }

    @Test
    public void allBannersShouldBeReturnedWhenGetAllCalled() {
        Collection<Banner> persistedBanners = givenPersistedBanners();

        Collection<Banner> foundBanners = bannerDao.getAll();

        assertEquals(foundBanners.size(), persistedBanners.size(),
                "Incorrect collection of banners were returned from database.");
        assertEquals(foundBanners, persistedBanners,
                "Incorrect collection of banners were returned from database.");
    }

    private Collection<Banner> givenPersistedBanners() {
        Collection<Banner> banners = ObjectsFactory.getBanners();
        for (Banner banner : banners) {
            session.save(banner);
        }
        session.flush();
        return banners;
    }

    @Test
    public void emptyCollectionShouldBeReturnedWhenBannersDoNotExist() {
        Collection<Banner> foundBanners = bannerDao.getAll();

        assertTrue(foundBanners.isEmpty(),
                "Incorrect banners were returned, because database doesn't contain any banner");
    }
}

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

import org.jtalks.common.model.dao.GroupDao;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.User;
import org.jtalks.common.model.permissions.GeneralPermission;
import org.jtalks.common.security.acl.builders.CompoundAclBuilder;
import org.jtalks.jcommune.model.dao.SimplePageDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.SimplePage;
import org.jtalks.jcommune.service.SimplePageService;
import org.jtalks.jcommune.service.dto.SimplePageInfoContainer;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.service.security.AdministrationGroup;
import org.jtalks.jcommune.service.security.SecurityService;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.persistence.EntityExistsException;

import static org.jtalks.jcommune.service.TestUtils.mockAclBuilder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class TransactionalSimplePageServiceTest {

    private static final long ID = 2L;
    private static final String NAME = "name";
    private static final String CONTENT = "content";
    private static final String PATH_NAME = "path_name";

    @Mock
    private SimplePageDao dao;

    @Mock
    private GroupDao groupDao;

    @Mock
    private SecurityService securityService;


    private SimplePageService simplePageService;

    private CompoundAclBuilder aclBuilder;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        aclBuilder = mockAclBuilder();
        when(securityService.<User>createAclBuilder()).thenReturn(aclBuilder);
        simplePageService = new TransactionalSimplePageService(dao, groupDao, securityService);
    }

    @Test
    void testUpdate() throws NotFoundException {
        SimplePage simplePage = new SimplePage(NAME, CONTENT, PATH_NAME);
        simplePage.setId(ID);


        String updatedName = "new name";
        String updatedContent = "new content";

        SimplePageInfoContainer simplePageInfoContainer = new SimplePageInfoContainer(ID, updatedName, updatedContent);

        when(dao.isExist(simplePage.getId())).thenReturn(true);
        when(dao.get(simplePage.getId())).thenReturn(simplePage);

        simplePageService.updatePage(simplePageInfoContainer);
        assertEquals(simplePage.getName(), updatedName);
        assertEquals(simplePage.getContent(), updatedContent);

        verify(dao).get(ID);
        verify(dao).isExist(ID);
    }

    @Test
    void testGetPageByPathName() throws NotFoundException {
        SimplePage samplePage = new SimplePage(NAME, CONTENT, PATH_NAME);
        when(dao.getPageByPathName(PATH_NAME)).thenReturn(samplePage);

        SimplePage actualSimplePage = simplePageService.getPageByPathName(PATH_NAME);

        assertNotNull(actualSimplePage);

        verify(dao).getPageByPathName(PATH_NAME);
    }

    @Test
    void testCreatePage() throws EntityExistsException {
        SimplePage simplePage = new SimplePage(NAME, CONTENT, PATH_NAME);
        JCUser user = new JCUser("username", "email", "password");

        when(dao.isExist(PATH_NAME)).thenReturn(false);
        when(groupDao.getGroupByName(AdministrationGroup.ADMIN.getName())).thenReturn(new Group());

        SimplePage actualSimplePage = simplePageService.createPage(simplePage, user);

        assertEquals(actualSimplePage.getName(), NAME);
        assertEquals(actualSimplePage.getContent(), CONTENT);
        assertEquals(actualSimplePage.getPathName(), PATH_NAME);

        verify(dao).isExist(PATH_NAME);
        verify(dao).saveOrUpdate(simplePage);
        verify(aclBuilder).grant(GeneralPermission.WRITE);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    void testUpdateFailPageNotFound() throws NotFoundException {

        String updatedName = "new name";
        String updatedContent = "new content";

        SimplePageInfoContainer simplePageInfoContainer = new SimplePageInfoContainer(ID, updatedName, updatedContent);

        when(dao.isExist(ID)).thenReturn(false);
        when(dao.get(ID)).thenReturn(null);

        simplePageService.updatePage(simplePageInfoContainer);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    void testGetPageByPathNameFailPageNotFound() throws NotFoundException {

        when(dao.getPageByPathName(PATH_NAME)).thenReturn(null);

        SimplePage actualSimplePage = simplePageService.getPageByPathName(PATH_NAME);
    }

    @Test(expectedExceptions = {EntityExistsException.class})
    void testCreatePageFailPageAlreadyExists() throws EntityExistsException {

        SimplePage simplePage = new SimplePage(NAME, CONTENT, PATH_NAME);
        JCUser user = new JCUser("username", "email", "password");

        when(dao.isExist(PATH_NAME)).thenReturn(true);
        SimplePage actualSimpePage = simplePageService.createPage(simplePage, user);
    }

}
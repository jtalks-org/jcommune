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


import org.jtalks.jcommune.model.dao.SamplePageDao;
import org.jtalks.jcommune.service.SamplePageService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.jtalks.jcommune.model.entity.SamplePage;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransactionalSimplePageServiceTest {
  /*
    private static final long ID = 2L;
    private static final String NAME = "name";
    private static final String CONTENT = "content";

    @Mock
    private SamplePageDao dao;

    private SamplePageService samplePageService;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        samplePageService = new TransactionalSapmlePageService(dao);
    }

    @Test
    void testUpdate() throws NotFoundException{
        SamplePage samplePage = new SamplePage(NAME, CONTENT);
        String updatedName = "new name";
        String updatedContent = "new content";
        when(dao.isExist(ID)).thenReturn(true);
        when(dao.get(ID)).thenReturn(samplePage);
        
        samplePageService.updatePage(ID, updatedName, updatedContent);
        Assert.assertEquals(samplePage.getName(), updatedName);
        Assert.assertEquals(samplePage.getContent(), updatedContent);

        verify(dao.get(ID));
        verify(dao.isExist(ID));
    }
   */
}

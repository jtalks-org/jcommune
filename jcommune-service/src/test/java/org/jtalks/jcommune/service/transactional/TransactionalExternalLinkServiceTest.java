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

import org.jtalks.common.model.entity.Component;
import org.jtalks.jcommune.model.dao.ExternalLinkDao;
import org.jtalks.jcommune.model.entity.ExternalLink;
import org.jtalks.jcommune.service.ExternalLinkService;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Alexandre Teterin
 */
public class TransactionalExternalLinkServiceTest {
    @Mock
    private ExternalLinkDao dao;
    private ExternalLinkService service;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        service = new TransactionalExternalLinkService(dao);
    }

    @Test
    public void testGetLinks() throws Exception {
        service.getLinks();
        verify(dao).getAll();
    }

    @Test
    public void testAddLink() throws Exception {
        ExternalLink linkToSave = new ExternalLink();
        Component component = new Component();
        service.saveLink(linkToSave, component);
        verify(dao).saveOrUpdate(linkToSave);
    }

    @Test
    public void testRemoveLink() throws Exception {
        Component component = new Component();
        service.deleteLink(1L, component);
        verify(dao).delete(eq(1L));
    }
}

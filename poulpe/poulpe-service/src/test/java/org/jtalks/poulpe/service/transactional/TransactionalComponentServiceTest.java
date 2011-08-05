/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.poulpe.service.transactional;

import org.jtalks.poulpe.model.entity.Component;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.jtalks.poulpe.model.dao.ComponentDao;
import org.jtalks.poulpe.model.entity.ComponentType;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 *
 * @author Pavel Vervenko
 */
public class TransactionalComponentServiceTest {

    private ComponentDao dao;
    private TransactionalComponentService instance;
    @BeforeMethod
    public void setUp() throws Exception {
        dao = mock(ComponentDao.class);
        instance = new TransactionalComponentService(dao);
    }

    @Test
    public void testGetAll() {
        List<Component> expectedList = new ArrayList<Component>();
        when(dao.getAll()).thenReturn(expectedList);

        List<Component> actualList = instance.getAll();

        assertEquals(expectedList, actualList);
        verify(dao).getAll();
    }

    @Test
    public void testSaveComponent() {
        Component component = new Component();

        instance.saveComponent(component);

        verify(dao).saveOrUpdate(component);
    }

    @Test
    public void testDeleteComponent() {
        Component component = new Component();

        instance.deleteComponent(component);

        verify(dao).delete(component.getId());
    }

    @Test
    void testGetAvailableTypes() {
        when(dao.getAvailableTypes()).thenReturn(new LinkedHashSet<ComponentType>());

        instance.getAvailableTypes();

        verify(dao).getAvailableTypes();
    }
}

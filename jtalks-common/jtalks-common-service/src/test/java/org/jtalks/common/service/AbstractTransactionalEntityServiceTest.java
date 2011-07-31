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

package org.jtalks.common.service;

import org.jtalks.common.model.dao.Dao;
import org.jtalks.common.model.entity.Persistent;
import org.jtalks.common.service.exceptions.NotFoundException;
import org.jtalks.common.service.transactional.AbstractTransactionalEntityService;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class AbstractTransactionalEntityServiceTest {
    private class AbstractTransactionalEntityServiceObject extends AbstractTransactionalEntityService {
        private AbstractTransactionalEntityServiceObject(Dao dao) {
            this.dao = dao;
        }
    }

    private long ID = 1L;

    private AbstractTransactionalEntityService abstractTransactionalEntityService;
    private Dao abstractDao;
    private Persistent persistent;

    @BeforeMethod
    public void setUp() throws Exception {
          abstractDao = mock(Dao.class);
          persistent = mock(Persistent.class);
          abstractTransactionalEntityService = new AbstractTransactionalEntityServiceObject(abstractDao);
    }


    @Test(expectedExceptions = {NotFoundException.class})
    public void testGetIncorrectId() throws NotFoundException {
        when(abstractDao.isExist(ID)).thenReturn(false);

        abstractTransactionalEntityService.get(ID);
    }
    
    @Test
    public void testGetCorrectId() throws NotFoundException {
        when(abstractDao.isExist(ID)).thenReturn(true);
        when(abstractDao.get(ID)).thenReturn(persistent);

        abstractTransactionalEntityService.get(ID);
                                         
        verify(abstractDao).isExist(ID);
        verify(abstractDao).get(ID);
    }

}

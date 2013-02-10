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
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.jtalks.jcommune.model.dao.ComponentDao;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class TransactionalComponentServiceTest {
    @Mock
    private ComponentDao componentDao;
    private TransactionalComponentService componentService;
    
    @BeforeMethod
    public void init() {
        initMocks(this);
        componentService = new TransactionalComponentService(componentDao);
    }
    
    @Test
    public void getComponentShouldBeDelegatedToDao() {
        componentService.getComponentOfForum();
        
        verify(componentDao).getComponent();
    }
}
    

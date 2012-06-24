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
package org.jtalks.jcommune.service.nontransactional;

import org.jtalks.jcommune.model.entity.JCUser;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar Nurmakanov
 *
 */
public class PaginationServiceTest {
    @Mock
    private SecurityService securityService;
    private PaginationService paginationService;
    
    @BeforeTest
    public void init() {
        MockitoAnnotations.initMocks(this);
        paginationService = new PaginationService(securityService);
    }
    
    @Test(dataProvider = "getPageSizeForUserParameters")
    public void getPageSizeForCurrentUser(JCUser user, int expectedPageSize) {
        Mockito.when(securityService.getCurrentUser()).thenReturn(user);
        
        int actualPageSize = paginationService.getPageSizeForCurrentUser();
        
        Assert.assertEquals(actualPageSize, expectedPageSize, "Invalid page size for the user.");
    }
    
    @Test(dataProvider = "getPageSizeForUserParameters")
    public void getPageSizeForUser(JCUser user, int expectedPageSize) {
        int actualPageSize = paginationService.getPageSizeFor(user);
        
        Assert.assertEquals(actualPageSize, expectedPageSize, "Invalid page size for the user.");
    }
    
    @DataProvider(name = "getPageSizeForUserParameters")
    public Object[][] getPageSizeForUserParameters() {
        int pageSize = JCUser.DEFAULT_PAGE_SIZE + 1;
        JCUser user = new JCUser("username", "email", "password");
        user.setPageSize(pageSize);
        
        return new Object[][] {
                {null, JCUser.DEFAULT_PAGE_SIZE},
                {user, pageSize}
        };
    }
}

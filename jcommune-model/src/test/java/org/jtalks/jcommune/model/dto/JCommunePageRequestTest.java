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
package org.jtalks.jcommune.model.dto;

import org.jtalks.jcommune.model.entity.JCUser;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class JCommunePageRequestTest {
    
    private static final int PAGE_SIZE = 10;
    private static final String PAGE_NUMBER = "5";
    public static final int PARSED_PAGE_NUMBER = Integer.parseInt(PAGE_NUMBER);
    private static final int INDEX_OF_FIRST_ITEM = (PARSED_PAGE_NUMBER - 1) * PAGE_SIZE;

    private JCommunePageRequest pageRequest;
    
    @Test
    public void testConstructor() {
        pageRequest = new JCommunePageRequest(PAGE_NUMBER, PAGE_SIZE);
        
        assertEquals(pageRequest.getPageNumber(), PARSED_PAGE_NUMBER);
        assertEquals(pageRequest.getPageSize(), PAGE_SIZE);
        assertEquals(pageRequest.getOffset(), INDEX_OF_FIRST_ITEM);
        assertEquals(pageRequest.getSort(), null);
    }
    
    @Test
    public void testConstructorPageNumberLessThanOne() {
        pageRequest = new JCommunePageRequest("0", PAGE_SIZE);
        
        assertEquals(pageRequest.getPageNumber(), 1);
        assertEquals(pageRequest.getPageSize(), PAGE_SIZE);
        assertEquals(pageRequest.getOffset(), 0);
        assertEquals(pageRequest.getSort(), null);
    }
    
    @Test()
    public void testConstructorWrongPageSize() {
        pageRequest = new JCommunePageRequest(PAGE_NUMBER, 0);
        assertEquals(pageRequest.getPageSize(), JCUser.DEFAULT_PAGE_SIZE);
    }
    
    @Test
    public void testCreateWithPagingEnabled() {
        pageRequest = JCommunePageRequest.createPageRequest(PAGE_NUMBER, PAGE_SIZE);
        
        assertEquals(pageRequest.getPageNumber(), PARSED_PAGE_NUMBER);
        assertEquals(pageRequest.getPageSize(), PAGE_SIZE);
        assertEquals(pageRequest.getSort(), null);
    }

    @Test
    public void testGetOffsetPageNumberLessThanOne() {
        pageRequest = new JCommunePageRequest(PAGE_NUMBER, PAGE_SIZE);
        pageRequest.setPageNumber(-1);
        
        assertEquals(pageRequest.getOffset(), 0);
    }
    
    @Test(dataProvider="pageNumbersToAdjust")
    public void testAdjustPageNumber(int pageNumber, int totalItems, int adjustedPageNumber) {
        pageRequest = new JCommunePageRequest(PAGE_NUMBER, PAGE_SIZE);
        pageRequest.setPageNumber(pageNumber);
        
        pageRequest.adjustPageNumber(totalItems);
        assertEquals(pageRequest.getPageNumber(), adjustedPageNumber);
    }
    
    @DataProvider(name="pageNumbersToAdjust")
    public Integer[][] getPageNumbersToAdjust() {
        return new Integer[][] {
                // page number, total items, expected result
                {-1, 10, 1},
                {1000, PAGE_SIZE * 2, 2},
                {PAGE_SIZE * 2 + 1, PAGE_SIZE * 2, 2},
                {2000000000, PAGE_SIZE * 2, 2} 
        };
    }
    
    

}

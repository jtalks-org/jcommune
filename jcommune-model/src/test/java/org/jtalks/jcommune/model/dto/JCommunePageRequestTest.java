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

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class JCommunePageRequestTest {
    
    private static final int PAGE_SIZE = 10;
    private static final int PAGE_NUMBER = 5;
    private static final int INDEX_OF_FIRST_ITEM = (PAGE_NUMBER - 1) * PAGE_SIZE;
    private static final boolean PAGING_ENABLED = true;
    
    private JCommunePageRequest pageRequest;
    
    @Test
    public void testConstructor() {
        pageRequest = new JCommunePageRequest(PAGE_NUMBER, PAGE_SIZE, PAGING_ENABLED);
        
        assertEquals(pageRequest.getPageNumber(), PAGE_NUMBER);
        assertEquals(pageRequest.getPageSize(), PAGE_SIZE);
        assertEquals(pageRequest.isPagingEnabled(), PAGING_ENABLED);
        assertEquals(pageRequest.getOffset(), INDEX_OF_FIRST_ITEM);
        assertEquals(pageRequest.getSort(), null);
    }
    
    @Test
    public void testConstructorPageNumberLessThanOne() {
        pageRequest = new JCommunePageRequest(0, PAGE_SIZE, PAGING_ENABLED);
        
        assertEquals(pageRequest.getPageNumber(), 1);
        assertEquals(pageRequest.getPageSize(), PAGE_SIZE);
        assertEquals(pageRequest.isPagingEnabled(), PAGING_ENABLED);
        assertEquals(pageRequest.getOffset(), 0);
        assertEquals(pageRequest.getSort(), null);
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class)
    public void testConstructorWrongPageSize() {
        pageRequest = new JCommunePageRequest(PAGE_NUMBER, 0, PAGING_ENABLED);
    }
    
    @Test
    public void testCreateWithPagingEnabled() {
        pageRequest = JCommunePageRequest.createWithPagingEnabled(PAGE_NUMBER, PAGE_SIZE);
        
        assertEquals(pageRequest.getPageNumber(), PAGE_NUMBER);
        assertEquals(pageRequest.getPageSize(), PAGE_SIZE);
        assertEquals(pageRequest.isPagingEnabled(), true);
        assertEquals(pageRequest.getSort(), null);
    }
    
    @Test
    public void testCreateWithPagingDisabled() {
        pageRequest = JCommunePageRequest.createWithPagingDisabled(PAGE_NUMBER, PAGE_SIZE);
        
        assertEquals(pageRequest.getPageNumber(), PAGE_NUMBER);
        assertEquals(pageRequest.getPageSize(), PAGE_SIZE);
        assertEquals(pageRequest.isPagingEnabled(), false);
        assertEquals(pageRequest.getSort(), null);
    }
    
    @Test
    public void testGetOffsetPageNumberLessThanOne() {
        pageRequest = new JCommunePageRequest(PAGE_NUMBER, PAGE_SIZE, PAGING_ENABLED);
        pageRequest.setPageNumber(-1);
        
        assertEquals(pageRequest.getOffset(), 0);
    }
    
    @Test(dataProvider="pageNumbersToAdjust")
    public void testAdjustPageNumber(int pageNumber, int totalItems, int adjustedPageNumber) {
        pageRequest = new JCommunePageRequest(PAGE_NUMBER, PAGE_SIZE, PAGING_ENABLED);
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

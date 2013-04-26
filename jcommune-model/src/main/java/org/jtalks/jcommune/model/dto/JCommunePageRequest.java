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

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Data transfer object that needed for pagination in JCommune.
 * It contains additional help methods for calculation of
 * pagination.
 * 
 * @author Anuar Nurmakanov
 */
public class JCommunePageRequest implements Pageable {
    private static final long serialVersionUID = -9054794147449741044L;
    
    private int pageNumber;
    private int pageSize;
    private boolean pagingEnabled;
    
    /**
     * Creates a new {@link JCommunePageRequest}. 
     * 
     * @param pageSize size of page
     * @param pageNumber page number
     * @param pagingEnabled true if pagination is enabled, false if pagination is disabled
     */
    public JCommunePageRequest(int pageNumber, int pageSize, boolean pagingEnabled) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Page size must not be less than or equal to zero!");
        }
        
        if (pageNumber > 0) {
            this.pageNumber = pageNumber;
        } else { 
            this.pageNumber = 1;
        }
        this.pageSize = pageSize;
        this.pagingEnabled = pagingEnabled;
        
    }
    
    /**
     * Create an instance of {@link JCommunePageRequest} with enabled paging.
     *
     * @param pageNumber page number
     * @param pageSize size of page
     * @return an instance of {@link JCommunePageRequest} with enabled paging
     */
    public static JCommunePageRequest createWithPagingEnabled(int pageNumber, int pageSize) {
        return new JCommunePageRequest(pageNumber, pageSize, true);
    }
    
    /**
     * Create an instance of {@link JCommunePageRequest} with disabled paging.
     * 
     * @param pageSize size of page
     * @param pageNumber page number
     * @return an instance of {@link JCommunePageRequest} with disabled paging.
     */
    public static JCommunePageRequest createWithPagingDisabled(int pageNumber, int pageSize) {
        return new JCommunePageRequest(pageNumber, pageSize, false);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getPageNumber() {
        return pageNumber;
    }
    
    /**
     * @param pageNumber the pageNumber to set
     */
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPageSize() {
        return pageSize;
    }
    
    /**
     * @param pageSize the pageSize to set
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    /**
     * Get the flag, which is an indicator of enabling/disabling pagination.
     * 
     * @return true if pagination is enabled, false if pagination is disabled
     */
    public boolean isPagingEnabled() {
        return pagingEnabled;
    }
    
    /**
     * @param pagingEnabled the pagingEnabled to set
     */
    public void setPagingEnabled(boolean pagingEnabled) {
        this.pagingEnabled = pagingEnabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOffset() {
        return getOffset(pageNumber);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sort getSort() {
        return null;
    }
    
    /**
     * Get number of page for element with given index
     * @param index index of element starting with 0
     * @return number of page for element
     */
    private int getPageNumber(int index) {
        if (index > 0) {
            return index / pageSize + 1;
        } else {
            return 1;
        }
    }
    
    /**
     * Get index of first item for given page
     * @param pageNumber number of page 
     * @return index of first item
     */
    private int getOffset(int pageNumber) {
        if (pageNumber > 0) {
            return (pageNumber - 1) * pageSize;
        } else {
            return 0;
        }
    }

    /**
     * Sets page number to valid value based on total count of items (to 1 if 
     * page number <= 1 and to last page if it is too big).
     * @param totalCount total count of items
     */
    public void adjustPageNumber(int totalCount) {
        if (pageNumber <= 1) {
            pageNumber = 1;
        } else if (pageNumber > getPageNumber(totalCount - 1)) {
            pageNumber = getPageNumber(totalCount - 1);
        }
    }

}

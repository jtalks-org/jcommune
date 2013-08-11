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
    public static final int FIRST_PAGE_NUMBER = 1;

    private int pageNumber;
    private int pageSize;

    /**
     * Creates a new {@link JCommunePageRequest}. 
     * 
     * @param pageSize size of page
     * @param pageNumber page number
     */
    JCommunePageRequest(int pageNumber, int pageSize) {
        if (pageNumber <= 0) {
            this.pageNumber = FIRST_PAGE_NUMBER;
        } else {
            this.pageNumber = pageNumber;
        }
        if (pageSize <= 0) {
            this.pageSize = JCUser.DEFAULT_PAGE_SIZE;
        } else {
            this.pageSize = pageSize;
        }
    }

    /**
     * Creates a new {@link JCommunePageRequest}.
     *
     * @param requestedPageNumber page number as a String. If specified string is not valid integer,
     *                            page number will be equal 1.
     * @param pageSize size of page
     */
    public JCommunePageRequest(String requestedPageNumber, int pageSize) {
        if (requestedPageNumber.matches("\\d+")) {
            new JCommunePageRequest(Integer.valueOf(requestedPageNumber), pageSize);
        } else {
            new JCommunePageRequest(FIRST_PAGE_NUMBER, pageSize);
        }
    }
    
    /**
     * Create an instance of {@link JCommunePageRequest} with enabled paging.
     *
     * @param pageNumber page number
     * @param pageSize size of page
     * @return an instance of {@link JCommunePageRequest} with enabled paging
     */
    public static JCommunePageRequest createPageRequest(int pageNumber, int pageSize) {
        return new JCommunePageRequest(pageNumber, pageSize);
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
        if (pageNumber <= FIRST_PAGE_NUMBER) {
            pageNumber = FIRST_PAGE_NUMBER;
        } else if (pageNumber > getPageNumber(totalCount - 1)) {
            pageNumber = getPageNumber(totalCount - 1);
        }
    }

}

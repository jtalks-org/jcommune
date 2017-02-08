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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static com.google.common.base.Preconditions.checkArgument;
import static org.jtalks.jcommune.model.entity.JCUser.DEFAULT_PAGE_SIZE;

/**
 * Data transfer object that needed for pagination in JCommune.
 * It contains additional help methods for calculation of
 * pagination.
 * 
 * @author Anuar Nurmakanov
 */
public class PageRequest implements Pageable {
    public static final int FIRST_PAGE_NUMBER = 1;
    public static final int MAX_PAGE = 999999999;

    private int pageNumber;
    private final int pageSize;

    /**
     * Creates a new {@link PageRequest}.
     *
     * @param requestedPageNumber positive page number (max value is {@link PageRequest#MAX_PAGE}) as a string.
     * @param pageSize size of page
     */
    public PageRequest(String requestedPageNumber, int pageSize) {
        this(preparePageNumber(requestedPageNumber), preparePageSize(pageSize));
    }

    public PageRequest(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    private static int preparePageSize(int pageSize) {
        return (pageSize <= 0) ? DEFAULT_PAGE_SIZE : pageSize;
    }

    /**
     * Convert specified string to valid page number.
     * @param pageNumber specified string.
     * @return {@link PageRequest#MAX_PAGE} if specified string contains non numeric symbol.<br/>
     *         int number representing specified string if it value is <br/>
     *         0 < specified string <= {@link PageRequest#MAX_PAGE}(leading zeroes is omitted)<br/>
     *         {@link Integer#MAX_VALUE} if specified string is greater than {@link PageRequest#MAX_PAGE}<br/>
     *         {@link PageRequest#FIRST_PAGE_NUMBER} if specified string is not a number
     */
    private static int preparePageNumber(String pageNumber) {
        int result;
        pageNumber = pageNumber.replaceFirst("^0+(?!$)", "");//removing trailing zeroes
        if (pageNumber.matches("\\d{1,9}")) {
            result = Integer.valueOf(pageNumber);
        } else if (pageNumber.matches("\\d+")) {
            result = Integer.MAX_VALUE;
        } else {
            result = FIRST_PAGE_NUMBER;
        }
        return (result <= 0) ? FIRST_PAGE_NUMBER : result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPageSize() {
        return pageSize;
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
        checkArgument(pageNumber > 0, "Was less than one");
        return (pageNumber - 1) * pageSize;
    }

    /**
     * Sets page number to valid value based on total count of items (to 1 if 
     * page number <= 1 and to last page if it is too big).
     * @param totalCount total count of items
     */
    public void adjustPageNumber(int totalCount) {
        int maxPageNumber = getPageNumber(totalCount - 1);
        if (pageNumber > maxPageNumber) {
            pageNumber = maxPageNumber;
        }
    }


    public static PageRequest fetchFromPage(Page page) {
        return new PageRequest(page.getNumber(), page.getSize());
    }

}

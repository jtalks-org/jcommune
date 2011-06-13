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
package org.jtalks.jcommune.web;

/**
 * Class for pagination.
 *
 * @author Kirill Afonin
 */
public class Pagination {
    private Integer page;
    private int pageSize;
    private float itemsCount;
    public static final int DEFAULT_PAGE_SIZE = 5;

    /**
     * Create instance.
     *
     * @param page       page (default 1)
     * @param pageSize   number of items on the page (default 5)
     * @param itemsCount total number of items
     */
    public Pagination(Integer page, Integer pageSize, float itemsCount) {
        this.page = page == null ? Integer.valueOf(1) : page;
        this.pageSize = pageSize == null ? Integer.valueOf(DEFAULT_PAGE_SIZE) : pageSize;
        this.itemsCount = itemsCount;
    }

    /**
     * @return page
     */
    public Integer getPage() {
        return page;
    }

    /**
     * @return number of items on the page
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @return index with which a page starts
     */
    public int getStart() {
        return (getPage() - 1) * getPageSize();
    }

    /**
     * @return not rounded page count
     */
    private float getPageCount() {
        return itemsCount / pageSize;
    }

    /**
     * @return number of pages
     */
    public int getMaxPages() {
        float pageCount = getPageCount();
        return (int) ((pageCount > (int) pageCount || pageCount == 0.0) ? pageCount + 1 : pageCount);
    }
}

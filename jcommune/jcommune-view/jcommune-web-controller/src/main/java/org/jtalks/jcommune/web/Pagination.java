package org.jtalks.jcommune.web;

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

/**
 *
 */
public class Pagination {
    private Integer page;
    private int pageSize;
    private float itemsCount;
    public static final int DEFAULT_PAGE_SIZE = 5;

    /**
     *
     * @param page
     * @param pageSize
     * @param itemsCount
     */
    public Pagination(Integer page, Integer pageSize, float itemsCount) {
        this.page = page == null ? new Integer(1) : page;
        this.pageSize = pageSize == null ? new Integer(DEFAULT_PAGE_SIZE) : pageSize;
        this.itemsCount = itemsCount;
    }

    /**
     *
     * @return
     */
    public Integer getPage() {
        return page;
    }

    /**
     *
     * @return
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     *
     * @return
     */
    public int getStart() {
        return (getPage() - 1) * getPageSize();
    }

    /**
     *
     * @return
     */
    private float getPageCount() {
        return itemsCount / pageSize;
    }

    /**
     *
     * @return
     */
    public int getMaxPages() {
        float pc = getPageCount();
        return (int) ((pc > (int) pc || pc == 0.0) ? pc + 1 : pc);
    }
}

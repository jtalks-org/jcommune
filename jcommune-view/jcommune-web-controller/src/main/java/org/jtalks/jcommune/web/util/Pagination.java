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
package org.jtalks.jcommune.web.util;

/**
 * Class for pagination.
 *
 * @author Kirill Afonin
 * @author Andrey Kluev
 */
public class Pagination {
    private Integer page;
    private int pageSize;
    private int itemsCount;
    private int defaultPageSize = 4;

    /**
     * Create instance.
     *
     * @param page       page (default 1)
     * @param pageSize   number of items on the page (default 5)
     * @param itemsCount total number of items
     */
    public Pagination(Integer page, Integer pageSize, int itemsCount) {
        this.page = page == null ? Integer.valueOf(1) : page;
        this.pageSize = pageSize == null ? Integer.valueOf(defaultPageSize) : pageSize;
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
     * @return page count
     */
    private int getPageCount() {
        return itemsCount / pageSize;
    }

    /**
     * @return total number of pages
     */
    public int getMaxPages() {
        return isRounded() ? getPageCount() : getPageCount() + 1;
    }

    /**
     * @return {@code true} if number of pages rounded else {@code false}
     */
    private boolean isRounded() {
        return (itemsCount % pageSize) == 0;
    }
}

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
 * Holds a list of available values of items(like topics and posts) on the page.
 *
 * @author Eugeny Batov
 */
public enum PageSize {

    FIVE(5),
    TEN(10),
    TWENTY(20),
    FIFTY(50),
    HUNDRED(100),
    TWO_HUNDRED_FIFTY(250);

    private final int size;

    /**
     * Constructor that accepts size.
     *
     * @param size - amount of items on the page
     */
    PageSize(int size) {
        this.size = size;
    }

    /**
     * @return pageSize
     */
    public int getSize() {
        return size;
    }

}

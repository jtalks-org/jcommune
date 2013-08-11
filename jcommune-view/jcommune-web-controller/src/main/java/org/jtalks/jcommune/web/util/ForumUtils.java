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

import org.springframework.stereotype.Component;

/**
 * Contains misc forum level helper methods.
 *
 * @author Alexandre Teterin
 */
@Component
public class ForumUtils {



    /**
     * Validate and return requested page.
     *
     * @param page a requested page as a string provided by user
     * @param pageSize the user profile page size.
     * @param elementCount the page element count.
     * @return <ul>
     *     <li>requested topic page, if specified page valid;</li>
     *     <li>max page, if specified page is greater then max page;</li>
     *     <li>1, if specified page is not a number.</li>
     * </ul>
     *
     */
    public int prepareRequestedPage(String page, int pageSize, int elementCount) {
        int result = 1;
        int maxPageNumber = elementCount % pageSize == 0 ?
                elementCount / pageSize
                : (elementCount / pageSize) + 1;
        if (page.matches("\\d+")) {
            int requestedPage = Integer.valueOf(page);
            if (requestedPage <= maxPageNumber) {
                result = requestedPage;
            } else {
                result = maxPageNumber;
            }
        }
        return  result;
    }
}

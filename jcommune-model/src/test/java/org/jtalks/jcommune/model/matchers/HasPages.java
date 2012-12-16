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
package org.jtalks.jcommune.model.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.springframework.data.domain.Page;

/**
 * Hamcrest's matcher for verifying paginated results.
 * @author Vitaliy Kravchenko
 */

public class HasPages extends TypeSafeMatcher<Page>{

    @Override
    public boolean matchesSafely(Page page) {
        return ((page.getSize() > 0 || page.getTotalElements() > 0) && (page.getSize() <= page.getTotalElements()
                && page.getTotalPages() > 0));
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Page size is zero ,page total elements count is zero or page total pages equals to zero");
    }

    @Factory
    public static <T> Matcher<Page> hasPages() {
        return new HasPages();
    }
}

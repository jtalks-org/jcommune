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

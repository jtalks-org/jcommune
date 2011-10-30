package org.jtalks.jcommune.model.entity;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Kirill Afonin
 */
public class PostTest {
    private static final String LONG_TEXT = "Lorem ipsum dolor sit amet, " +
            "consectetur adipisicing elit, sed do eiusmod tempor incididunt ut " +
            "labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud " +
            "exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum " +
            "dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non " +
            "proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
    private static final String SHORT_TEXT = "labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud";

    Post post = new Post();

    @Test
    public void shortContentShouldBeMax200Symbols() {
        post.setPostContent(LONG_TEXT);

        String shortContent = post.getShortContent();

        assertTrue(shortContent.length() <= 200);
    }

    @Test
    public void shortContentShouldEndsWithThreeDots() {
        post.setPostContent(LONG_TEXT);

        String shortContent = post.getShortContent();

        assertTrue(shortContent.endsWith("..."));
    }

    @Test
    public void shortContentAndShortTextShouldBeEqual() {
        post.setPostContent(SHORT_TEXT);

        String shortContent = post.getShortContent();

        assertEquals(shortContent, SHORT_TEXT);
    }

}

package org.jtalks.jcommune.model.entity;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Alexandre Teterin
 *         Date: 04.03.13
 */


public class ExternalLinkTest {

    @Test
    public void testThreeArgsConstructorAndGetters() throws Exception {
        String url = "http://javatalks.ru";
        String title = "title";
        String hint = "hint";
        ExternalLink externalLink = new ExternalLink(url, title, hint);
        assertEquals(url, externalLink.getUrl());
        assertEquals(title, externalLink.getTitle());
        assertEquals(hint, externalLink.getHint());
    }
}

package org.jtalks.jcommune.web.tags;

import org.testng.annotations.Test;

import static org.junit.Assert.assertEquals;

public class UrlUtilTest {

    @Test
    public void testEncodeUrl() {
        String url = "http://jtalks.org/poulpe auth plugin";
        assertEquals(UrlUtil.encodeUrl(url), "http%3A%2F%2Fjtalks.org%2Fpoulpe%20auth%20plugin");
    }

    @Test
    public void testEncodeUrlIfNull() {
        assertEquals(UrlUtil.encodeUrl(null), "");
    }
}

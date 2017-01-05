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
package org.jtalks.jcommune.service.bb2htmlprocessors;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class UrlToLinkConvertPostProcessorTest {
    UrlToLinkConvertPostProcessor postProcessor = new UrlToLinkConvertPostProcessor();

    @Test
    void doesNothingIfNoLinksPresent() {
        String originalText = "Silent sir say desire fat him letter. Whatever settling goodness too and \n" +
                "honoured she building answered her. Strongly thoughts remember mr to do consider debating. \n" +
                "Spirits musical behaved on we he farther letters. Repulsive he he as deficient newspaper dashwoods we. \n" +
                "Discovered her his pianoforte insipidity entreaties. Began he at terms meant as fancy. Breakfast \n" +
                "arranging he if furniture we described on. И чуть-чуть на русском. Astonished thoroughly unpleasant \n" +
                "especially you dispatched \n" +
                "bed favourable.";
        assertEquals(postProcessor.postProcess(originalText), originalText);
    }

    @Test
    void doesNothingIfNoTextPresent() {
        assertEquals(postProcessor.postProcess(""), "");
    }

    @Test(dataProvider = "urlShouldBeHighlighted")
    void turnUrlToHrefTag_IfUrlShouldBeHighlighted(String originalText, String expectedOutput) {
        assertEquals(postProcessor.postProcess(originalText), expectedOutput);
    }

    @Test(dataProvider = "urlShouldNotBeHighlighted")
    void dontTurnUrlToHrefTag_IfUrlShouldNotBeHighlighted(String originalText) {
        assertEquals(postProcessor.postProcess(originalText), originalText);
    }

    @Test(dataProvider = "allJoinedUrlCases")
    void turnUrlToHrefTagIfNeeded(String originalText, String expectedOutput) {
        assertEquals(postProcessor.postProcess(originalText), expectedOutput);
    }

    @DataProvider
    public Object[][] urlShouldBeHighlighted() {
        return new Object[][] {
                {"http://javatalks.ru/common",
                        "<a href=\"http://javatalks.ru/common\">http://javatalks.ru/common</a>"},
                {"https://javatalks.ru/common",
                        "<a href=\"https://javatalks.ru/common\">https://javatalks.ru/common</a>"},
                {"www.javatalks.ru/common",
                        "<a href=\"http://www.javatalks.ru/common\">www.javatalks.ru/common</a>"},
                {"ftp.javatalks.ru/common",
                        "<a href=\"ftp://ftp.javatalks.ru/common\">ftp.javatalks.ru/common</a>"},
                {"ftp://javatalks.ru/common",
                        "<a href=\"ftp://javatalks.ru/common\">ftp://javatalks.ru/common</a>"},
                {"file://javatalks.ru/common",
                        "<a href=\"file://javatalks.ru/common\">file://javatalks.ru/common</a>"},
                {"<div class=divclass>http://javatalks.ru/common</div>",
                        "<div class=divclass><a href=\"http://javatalks.ru/common\">http://javatalks.ru/common</a></div>"},
                {"Text текст ftp.javatalks.ru/common text \n text http://javatalks.ru/common text and text и текст \n the end.",
                        "Text текст <a href=\"ftp://ftp.javatalks.ru/common\">ftp.javatalks.ru/common</a> text \n text " +
                        "<a href=\"http://javatalks.ru/common\">http://javatalks.ru/common</a> text and text и текст \n the end."},
                {"http://привет.рф/информация",
                        "<a href=\"http://привет.рф/информация\">http://привет.рф/информация</a>"},
                {"www.привет.рф/информация",
                        "<a href=\"http://www.привет.рф/информация\">www.привет.рф/информация</a>"},
                {"ftp://привет.рф/информация",
                        "<a href=\"ftp://привет.рф/информация\">ftp://привет.рф/информация</a>"},
                {"ftp.привет.рф/информация",
                        "<a href=\"ftp://ftp.привет.рф/информация\">ftp.привет.рф/информация</a>"},
                {"file://привет.рф/информация",
                        "<a href=\"file://привет.рф/информация\">file://привет.рф/информация</a>"}
        };
    }

    @DataProvider
    public Object[][] urlShouldNotBeHighlighted() {
        return new Object[][] {
                {"<pre class=cls>http://google.com</pre>"},
                {"<pre>http://google.com</pre>"},
                {"<pre>http://привет.рф/информация</pre>"},
                {"<pre class=cls>http://google.com\nwww.ya.ru\nabc abc abc ftp.server.com</pre>"},
                {"Text <pre class=cls>http://google.com\nwww.ya.ru\nabc abc abc ftp.server.com</pre> text"},
                {"<a href=http://www.google.com>www.google.com</a>"},
                {"<img src=http://www.ya.ru/img.jpg>img.jpg</img>"},
                {"text <img src=http://www.ya.ru/img.jpg>img.jpg</img> text"}
        };
    }

    @DataProvider
    public Object[][] allJoinedUrlCases() {
        StringBuilder originTextBuilder = new StringBuilder();
        StringBuilder expectedTextBuilder = new StringBuilder();
        for (Object[] objects : urlShouldBeHighlighted()) {
            originTextBuilder.append((String) objects[0]);
            originTextBuilder.append("\n");
            expectedTextBuilder.append((String) objects[1]);
            expectedTextBuilder.append("\n");
        }
        for (Object[] objects : urlShouldNotBeHighlighted()) {
            originTextBuilder.append((String) objects[0]);
            originTextBuilder.append("\n");
            expectedTextBuilder.append((String) objects[0]);
            expectedTextBuilder.append("\n");
        }
        return new Object[][] {
                {originTextBuilder.toString(), expectedTextBuilder.toString()}
        };
    }
}

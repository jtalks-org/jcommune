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
package org.jtalks.jcommune.service.nontransactional;

import org.jtalks.jcommune.model.entity.JCUser;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Evgeniy Naumenko
 */
public class BBCodeServiceTest {

    private BBCodeService service;

    private static final String SOURCE = "source";
    private static final JCUser USER = new JCUser("name", "mail", "pass");

    @BeforeMethod
    public void setUp() {
        service = new BBCodeService();
    }

    @Test
    public void testQuote() {
        String result = service.quote(SOURCE, USER);
        assertEquals(result, "[quote=\"name\"]source[/quote]");
    }

    public void testRemoveBBCodes(){
        String incoming = "[bb] lol [code] [/omg]";
        String expected = " lol  ";

        assertEquals(service.removeBBCodes(incoming), expected);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, dataProvider = "illegalQuoteArgumentProvider")
    public void testQuoteNullSource(String source, JCUser author) {
        service.quote(source, author);
    }

    @DataProvider
    public Object[][] illegalQuoteArgumentProvider() {
        return new Object[][]{{null, null}, {null, USER}, {SOURCE, null}};
    }

    @Test(dataProvider = "validBBCodes")
    public void testBBCodeConversion(String bbCode, String expectedResult) {
        assertEquals(service.convertBbToHtml(bbCode), expectedResult);
    }

    @DataProvider
    public Object[][] validBBCodes() {
        return new Object[][]{  // {"bb code", "html code"}
                //bold
                {"[b]Bold text[/b]", "<span style=\"font-weight:bold;\">Bold text</span>"},
                //italic
                {"[i]Italic text[/i]", "<span style=\"font-style:italic;\">Italic text</span>"},
                //underlined
                {"[u]Underline text[/u]", "<span style=\"text-decoration:underline;\">Underline text</span>"},
                //strong
                {"[s]Strong text[/s]", "<span style=\"text-decoration:line-through;\">Strong text</span>"},
                //colored
                {"[color=red]Colored text[/color]", "<span style=\"color:red;\">Colored text</span>"},
                {"[color=FF0000]Colored text[/color]", "<span style=\"color:#FF0000;\">Colored text</span>"},
                //font size
                {"[size=18]Large text[/size]", "<span class=\"textSize18\">Large text</span>"},
                //font family
                {"[font=system]Custom font[/font]", "<span style=\"font-style:system;\">Custom font</span>"},
                //highlight
                {"[highlight]Highlited text[/highlight]", "<span class=\"highlight\">Highlited text</span>"},
                //alignment
                {"[left]Left aligned text[/left]", "<p class=\"leftText\">Left aligned text</p>"},
                {"[right]Right aligned text[/right]", "<p class=\"rightText\">Right aligned text</p>"},
                {"[center]Center aligned text[/center]", "<p class=\"centerText\">Center aligned text</p>"},
                //indentation
                {"[indent=25]Indent text[/indent]", "<p class=\"marginLeft25\">Indent text</p>"},
                //hyperlinks
                {"[url=http://www.google.com]Гу\nгл[/url]", "<a href=\"http://www.google.com\">Гу<br/>гл</a>"},
                //list with bullets
                {"[list][*]1й пункт[*]2й пункт[/list]", "<ul class=\"list\"><li>1й пункт</li><li>2й пункт</li></ul>"},
                //images
                {"[img]http://narod.ru/avatar.jpg[/img]",
                        "<a title=\"\" href=\"http://narod.ru/avatar.jpg\" rel=\"prettyPhoto\">" +
                                "<img class=\"thumbnail\" alt=\"\" src=\"http://narod.ru/avatar.jpg\"/></a>"},
                //code
                {"[code]println(\"Hi!\");[/code]", "<p class=\"code\">println(&quot;Hi!&quot;);</p>"},
                //qoutes
                {"[quote]Some text[/quote]",
                        "<div class=\"quote\"><div class=\"quote_title\">Quote:" +
                                "</div><blockquote>Some text</blockquote></div>"},
                {"[quote=\"user\"]Some text[/quote]",
                        "<div class=\"quote\"><div class=\"quote_title\">user:" +
                                "</div><blockquote>Some text</blockquote></div>"},
                //offtopic
                {"[offtop]Some text[/offtop]", "<div class=\"offtop\"><p>Some text</p></div>"}
        };
    }
}

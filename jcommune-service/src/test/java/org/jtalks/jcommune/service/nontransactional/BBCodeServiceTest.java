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

    @Test(dataProvider = "validBBCodes")
    public void testBBCodeConversion(String bbCode, String expectedResult) {
        assertEquals(service.convertBbToHtml(bbCode), expectedResult);
    }

    @Test(dataProvider = "bbCodesToStrip") 
    public void testBBCodesStripping(String bbCode, String expected, String message) {
        assertEquals(service.stripBBCodes(bbCode), expected, message);
    }
    
    @DataProvider
    public Object[][] bbCodesToStrip() {
        return new String[][]{ 
            {"[b]text[/b]", "text", "strip [b]"},
            {"[b][b]text[/b]", "[b]text", "unclosed tags are not stripped"}, 
            {"[not a tag][][/b]text[/b][\\i]", "[not a tag][][/b]text[/b][\\i]", 
                "invalid tags not becomes stripped"},
            {"[code=java]text[/code]", "text", "strip code"},
            {"[img]http://ya.ru/zzz.jpg[/img]", "http://ya.ru/zzz.jpg", "strip img"},
            {"[offtop]offtop[/offtop]", "offtop", "strip offtop"},
            {"[user=http://dev.jtalks.org/jcommune/users/1]admin[/user]text", "admintext", "strip user"},
            {"[url=http://dev.jtalks.org/jcommune/topics/84]display[/url]", "display(http://dev.jtalks.org/jcommune/topics/84)", "strip url"},
            {"[quote=\"admin\"]quote[/quote]", "quote", "strip named quote"},
            {"[b][i][u][s][highlight][left][center][right][color=000033][size=12][quote][indent=15]"
                + "Ваш текст[/indent][/quote][/size][/color][/right][/center][/left][/highlight][/s][/u][/i][/b]",
             "Ваш текст", "strip a pack of bb-codes"}
        };
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
                {"[left]Left aligned text[/left]", "<div class=\"leftText\">Left aligned text</div>"},
                {"[right]Right aligned text[/right]", "<div class=\"rightText\">Right aligned text</div>"},
                {"[center]Center aligned text[/center]", "<div class=\"centerText\">Center aligned text</div>"},
                //indentation
                {"[indent=25]Indent text[/indent]", "<div class=\"marginLeft25\">Indent text</div>"},
                //hyperlinks
                {"[url=http://www.google.com]Гу\nгл[/url]", "<a href=\"http://www.google.com\">Гу<br/>гл</a>"},
                //list with bullets
                {"[list][*]1й пункт[/*][*]2й пункт[/*][/list]", "<ul class=\"list unordered-list\"><li>1й пункт</li><li>2й пункт</li></ul>"},
                {"[list=1][*]1й пункт[/*][*]2й пункт[/*][/list]", "<ol type=\"1\" class=\"list\"><li>1й пункт</li><li>2й пункт</li></ol>"},
                {"[list=a][*]1й пункт[/*][*]2й пункт[/*][/list]", "<ol type=\"a\" class=\"list\"><li>1й пункт</li><li>2й пункт</li></ol>"},
                //images
                {"[img]http://narod.ru/avatar.jpg[/img]",
                        "<a title=\"\" href=\"http://narod.ru/avatar.jpg\" class=\"pretty-photo\">" +
                                "<img class=\"thumbnail\" alt=\"\" src=\"http://narod.ru/avatar.jpg\"/></a>"},
                //code
                {"[code=sql]println(\"Hi!\");[/code]", "<pre class=\"prettyprint linenums sql\">println(&quot;Hi!&quot;);</pre>"},
                //qoutes
                {"[quote]Some text[/quote]",
                        "<div class=\"quote bb_quote_container\"><span class=\"bb_quote_title\">Quote:" +
                                "</span><div class='bb_quote_content'>Some text</div></div>"},
                {"[quote=\"user\"]Some text[/quote]",
                        "<div class=\"quote bb_quote_container\"><span class=\"bb_quote_title\">user:" +
                                "</span><div class='bb_quote_content'>Some text</div></div>"},
                //offtopic
                {"[offtop]Some text[/offtop]", "<div class=\"offtop\"><p>Some text</p></div>"}
        };
    }
}

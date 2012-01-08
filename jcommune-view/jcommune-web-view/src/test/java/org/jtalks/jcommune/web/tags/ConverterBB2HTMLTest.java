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
package org.jtalks.jcommune.web.tags;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class ConverterBB2HTMLTest {

    private ConverterBB2HTML tag;

    @BeforeMethod
    public void setUp() {
        tag = new ConverterBB2HTML();
    }

    @Test
    public void bbCodeBold(){
        tag.setBbCode("[b]Bold text[/b]");
        assertEquals(tag.getBbCode(), "<span style=\"font-weight:bold;\">Bold text</span>");
    }

    @Test
    public void bbCodeItalic(){
        tag.setBbCode("[i]Italic text[/i]");
        assertEquals(tag.getBbCode(), "<span style=\"font-style:italic;\">Italic text</span>");
    }

    @Test
    public void bbCodeUnderline(){
        tag.setBbCode("[u]Underline text[/u]");
        assertEquals(tag.getBbCode(), "<span style=\"text-decoration:underline;\">Underline text</span>");
    }

    @Test
    public void bbCodeStrong(){
        tag.setBbCode("[s]Strong text[/s]");
        assertEquals(tag.getBbCode(), "<span style=\"text-decoration:line-through;\">Strong text</span>");
    }

    @Test
    public void bbCodeColor(){
        tag.setBbCode("[color=red]Colored text[/color]");
        assertEquals(tag.getBbCode(), "<span style=\"color:red;\">Colored text</span>");
    }

    @Test
    public void bbCodeColorCode(){
        tag.setBbCode("[color=FF0000]Colored text[/color]");
        assertEquals(tag.getBbCode(), "<span style=\"color:#FF0000;\">Colored text</span>");
    }

    @Test
    public void bbCodeSize(){
        tag.setBbCode("[size=18]Large text[/size]");
        assertEquals(tag.getBbCode(), "<span class=\"textSize18\">Large text</span>");
    }

    @Test
    public void bbCodeFont(){
        tag.setBbCode("[font=system]Custom font[/font]");
        assertEquals(tag.getBbCode(), "<span style=\"font-style:system;\">Custom font</span>");
    }

    @Test
    public void bbCodeHighlight(){
        tag.setBbCode("[highlight]Highlited text[/highlight]");
        assertEquals(tag.getBbCode(), "<span class=\"highlight\">Highlited text</span>");
    }
    @Test
    public void bbCodeLeft(){
        tag.setBbCode("[left]Left aligned text[/left]");
        assertEquals(tag.getBbCode(), "<p class=\"leftText\">Left aligned text</p>");
    }

    @Test
    public void bbCodeRight(){
        tag.setBbCode("[right]Right aligned text[/right]");
        assertEquals(tag.getBbCode(), "<p class=\"rightText\">Right aligned text</p>");
    }

    @Test
    public void bbCodeCenter(){
        tag.setBbCode("[center]Center aligned text[/center]");
        assertEquals(tag.getBbCode(), "<p class=\"centerText\">Center aligned text</p>");
    }

    @Test
    public void bbCodeIndent(){
        tag.setBbCode("[indent=25]Indent text[/indent]");
        assertEquals(tag.getBbCode(), "<p class=\"marginLeft25\">Indent text</p>");
    }

    @Test
    public void bbCodeURL(){
        tag.setBbCode("[url=http://www.google.com]Гу\nгл[/url]");
        assertEquals(tag.getBbCode(), "<a href=\"http://www.google.com\">Гу<br/>гл</a>");
    }

    @Test
    public void bbCodeList(){
        tag.setBbCode("[list][*]1й пункт[*]2й пункт[/list]");
        assertEquals(tag.getBbCode(), "<ul class=\"list\"><li>1й пункт</li><li>2й пункт</li></ul>");
    }

    @Test
    public void bbCodeImg(){
        tag.setBbCode("[img]http://narod.ru/avatar.jpg[/img]");
        assertEquals(tag.getBbCode(), "<img class=\"thumbnail\" src=\"http://narod.ru/avatar.jpg\"/>");
    }

    @Test
    public void bbCodeCode(){
        tag.setBbCode("[code]System.out.println(\"Hi!\");[/code]");
        assertEquals(tag.getBbCode(), "<p class=\"code\">System.out.println(&quot;Hi!&quot;);</p>");
    }

    @Test
    public void bbCodeQuote(){
        tag.setBbCode("[quote]Some text[/quote]");
        assertEquals(tag.getBbCode(), "<div class=\"quote\"><div class=\"quote_title\">Quote:</div><blockquote>Some text</blockquote></div>");
    }

    @Test
    public void bbCodeNamedQuote(){
        tag.setBbCode("[quote=\"user\"]Some text[/quote]");
        assertEquals(tag.getBbCode(), "<div class=\"quote\"><div class=\"quote_title\">user:</div><blockquote>Some text</blockquote></div>");
    }

    @Test
    public void bbCodeOfftop(){
        tag.setBbCode("[offtop]Some text[/offtop]");
        assertEquals(tag.getBbCode(), "<div class=\"offtop\"><p>Some text</p></div>");
    }
}

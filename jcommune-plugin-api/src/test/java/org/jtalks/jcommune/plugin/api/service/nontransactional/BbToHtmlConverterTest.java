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
package org.jtalks.jcommune.plugin.api.service.nontransactional;

import org.jtalks.jcommune.plugin.api.service.PluginBbCodeService;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Mikhail Stryzhonok
 */
public class BbToHtmlConverterTest {
    @Mock
    private PluginBbCodeService bbCodeService;

    @BeforeMethod
    public void init() {
        initMocks(this);
        BbToHtmlConverter converter = (BbToHtmlConverter)BbToHtmlConverter.getInstance();
        converter.setBbCodeService(bbCodeService);

    }

    @Test
    public void testStripBbCodes() {
        String in = "[b]text[/b]";
        String out = "text";
        when(bbCodeService.stripBBCodes(in)).thenReturn(out);

        String result = BbToHtmlConverter.getInstance().stripBBCodes(in);

        assertEquals(result, out);
    }

    @Test
    public void testConvertBbToHtml() {
        String in = "[b]text[/b]";
        String out = "<span style=\"font-weight:bold;\" data-original-title=\"\">text</span>";
        when(bbCodeService.convertBbToHtml(in)).thenReturn(out);

        String result = BbToHtmlConverter.getInstance().convertBbToHtml(in);

        assertEquals(result, out);
    }
}

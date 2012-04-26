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
package org.jtalks.jcommune.model.search;

import org.apache.commons.lang.StringUtils;
import org.jtalks.jcommune.model.search.BbCodeFilterBridge;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar Nurmakanov
 *
 */
public class BbCodeFilterBridgeTest {
    private BbCodeFilterBridge bridge = new BbCodeFilterBridge();
    
    
    @Test(dataProvider = "parametersRemoveBbCodes")
    public void testRemoveBbCodes(String value, String expected ) {
        String result = bridge.objectToString(value);
        
        Assert.assertEquals(result, expected);
    }
    
    @DataProvider(name = "parametersRemoveBbCodes")
    public Object[][] parametersRemoveBbCodes() {
        return new Object[][] {
                {"[b]spring security[/b]", " spring security "},
                {"[code=java]spring security[/code]", " spring security "},
                {"spring security", "spring security"},
        };
    }
    
    /*Was isolated in a separate test for better understanding.*/
    @Test(dataProvider = "parameterRemoveBbCodesInNotStringValue")
    public void testRemoveBbCodesInNotStringValue(Object value, String expected) {
        String result = bridge.objectToString(value);
        
        Assert.assertEquals(result, expected);
    }
    
    @DataProvider(name = "parameterRemoveBbCodesInNotStringValue")
    public Object[][] parameterRemoveBbCodesInNotStringValue() {
        return new Object[][] {
                {null, StringUtils.EMPTY},
                {Long.valueOf(1), "1"}
        };
    }
}

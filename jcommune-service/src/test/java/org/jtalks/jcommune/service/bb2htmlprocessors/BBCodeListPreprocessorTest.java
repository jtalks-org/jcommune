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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class BBCodeListPreprocessorTest {

    private BBCodeListPreprocessor service;
    
    @BeforeMethod
    public void setUp() {
        service = new BBCodeListPreprocessor();
    }
    
    @Test(dataProvider="validBBCodes")
    public void testProcess(String bbCode, String expectedResult) {
        String result = service.process(bbCode);
        assertEquals(result, expectedResult);
    }
    
    
    @DataProvider
    public Object[][] validBBCodes() {
        return new Object[][]{  // {"bb code before", "bb code after"}
                {"aaa[list][*]bbb[*]ccc[/list]ddd", "aaa[list][*]bbb[/*][*]ccc[/*][/list]ddd"},
                {"aaa[list][*][b]bbb[/b][*][padding=10]ccc[/padding][/list]ddd", "aaa[list][*][b]bbb[/b][/*][*][padding=10]ccc[/padding][/*][/list]ddd"},
                {"[list][*]aaa[*]bbb[/list]", "[list][*]aaa[/*][*]bbb[/*][/list]"}
        };
    }
}

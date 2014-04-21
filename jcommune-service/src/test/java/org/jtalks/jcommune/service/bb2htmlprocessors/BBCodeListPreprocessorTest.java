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

import static org.testng.Assert.assertEquals;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BBCodeListPreprocessorTest {

    private BBCodeListPreprocessor service;
    
    @BeforeMethod
    public void setUp() {
        service = new BBCodeListPreprocessor();
    }
    
    @Test(dataProvider="validLists")
    public void testProcess(String bbCode, String expectedResult) {
        String result = service.process(bbCode);
        assertEquals(result, expectedResult);
    }
    
    @Test(dataProvider="validLists")
    public void testProcessStringBuilder(String bbCode, String expectedResult) {
        StringBuilder result = service.process(new StringBuilder(bbCode));
        assertEquals(result.toString(), expectedResult);
    }
    
    @Test(dataProvider="validLists")
    public void testProcessStringBuffer(String bbCode, String expectedResult) {
        StringBuffer result = service.process(new StringBuffer(bbCode));
        assertEquals(result.toString(), expectedResult);
    }
    
    @Test(dataProvider="validLists")
    public void testProcessCharSequence(String bbCode, String expectedResult) {
        CharSequence result = service.process(new StringBuilder(bbCode).subSequence(0, bbCode.length()));
        assertEquals(result.toString(), expectedResult);
    }
    
    @Test(dataProvider = "nestedLists")
    public void testProcessNestedLists(String bbCode, String expectedResult) {
        CharSequence result = service.process(new StringBuilder(bbCode));
        assertEquals(result.toString(), expectedResult);        
    }    
    
    @Test(dataProvider = "badLists")
    public void testBadLists(String bbCode, String expected) {
        CharSequence result = service.process(new StringBuilder(bbCode));
        assertEquals(result.toString(), expected);         
    }
    
    @DataProvider
    public Object[][] badLists() {
        return new Object[][]{
                {"[list=a:bcb294daef][*][/list]","[list=a:bcb294daef][*][/*][/list]"},
                {"[list][/list]","[list][/list]"},
                {"some long text", "some long text"},
                {"[list][list][list]", "[list][list][list]"},
                {"[*]Duplicate", "[*]Duplicate"},
                {"before list[list][*]listitem[list]after list", "before list[list][*]listitem[list]after list"},
                {"zzz[/list]", "zzz[/list]"},
                {"[list][*]text[/list][*]text[/list]","[list][*]text[/*][/list][*]text[/list]"},
                {"[/list][list][*]text[/list][*]text","[/list][list][*]text[/*][/list][*]text"}
        };
    }
    
    @DataProvider
    public Object[][] nestedLists(){
        return new Object[][] {
            {"[list][*]111[list][*]222[/list][/list]", "[list][*]111[list][*]222[/*][/list][/*][/list]"},
            {"before[list=1][*]111[*][list=2][*]222[list=3][*]333[*]444[/list][*]555[list=4][*]666[*]777[/list][*]888[/list][*]999[/list]after",
                "before[list=1][*]111[/*][*][list=2][*]222[list=3][*]333[/*][*]444[/*][/list][/*][*]555[list=4][*]666[/*][*]777[/*][/list][/*][*]888[/*][/list][/*][*]999[/*][/list]after"},
            {"[list][list][*]text[/list][*]text[/list]","[list][list][*]text[/*][/list][*]text[/*][/list]"}
        };
    }

    @DataProvider
    public Object[][] validLists() {
        return new Object[][]{  // {"bb code before", "bb code after"}
                {"aaa[list][*]bbb[*]ccc[/list]ddd", "aaa[list][*]bbb[/*][*]ccc[/*][/list]ddd"},
                {"aaa[list][*][b]bbb[/b][*][padding=10]ccc[/padding][/list]ddd", "aaa[list][*][b]bbb[/b][/*][*][padding=10]ccc[/padding][/*][/list]ddd"},
                {"[list][*]aaa[*]bbb[/list]", "[list][*]aaa[/*][*]bbb[/*][/list]"},
                {"aaa[list][*]bbb[*]ccc[/list]dddaaa[list][*]bbb[*]ccc[/list]ddd", "aaa[list][*]bbb[/*][*]ccc[/*][/list]dddaaa[list][*]bbb[/*][*]ccc[/*][/list]ddd"},
                {"aaa[list=1][*]bbb[*]ccc[/list]dddaaa[list=1][*]bbb[*]ccc[/list]ddd", "aaa[list=1][*]bbb[/*][*]ccc[/*][/list]dddaaa[list=1][*]bbb[/*][*]ccc[/*][/list]ddd"},
                {"aaa[list=1][*][b]bbb[/b][*][padding=10]ccc[/padding][/list]ddd", "aaa[list=1][*][b]bbb[/b][/*][*][padding=10]ccc[/padding][/*][/list]ddd"},
                {"[list=1][*]aaa[*]bbb[/list]", "[list=1][*]aaa[/*][*]bbb[/*][/list]"},
                {"aaa[list=a][*]bbb[*]ccc[/list]ddd", "aaa[list=a][*]bbb[/*][*]ccc[/*][/list]ddd"},
                {"aaa[list=a][*][b]bbb[/b][*][padding=10]ccc[/padding][/list]ddd", "aaa[list=a][*][b]bbb[/b][/*][*][padding=10]ccc[/padding][/*][/list]ddd"},
                {"[list=a][*]aaa[*]bbb[/list]", "[list=a][*]aaa[/*][*]bbb[/*][/list]"},
                {"aaa[list=1][*]bbb[*]ccc[/list]d[b]dda[/b]aa[list][*]bbb[*]ccc[/list]ddd", "aaa[list=1][*]bbb[/*][*]ccc[/*][/list]d[b]dda[/b]aa[list][*]bbb[/*][*]ccc[/*][/list]ddd"},
                {"aaa[list]\n[*]bbb\n[*]ccc\n[/list]", "aaa[list]\n[*]bbb\n[/*][*]ccc\n[/*][/list]"},
                {"a\nb\nc[list]a\nb\nc[*]a\nb\nc[/list]a\nb\nc", "a\nb\nc[list]a\nb\nc[*]a\nb\nc[/*][/list]a\nb\nc"},
                {"", ""},
                {"[*]text[list][*]text[/list]","[*]text[list][*]text[/*][/list]"},
                {"[*][list][*]text[/list]","[*][list][*]text[/*][/list]"},
                {"[list][*]text[/list][*]","[list][*]text[/*][/list][*]"},
                {"[list][*]text[/list]text[*]text", "[list][*]text[/*][/list]text[*]text"}
        };
    }
}

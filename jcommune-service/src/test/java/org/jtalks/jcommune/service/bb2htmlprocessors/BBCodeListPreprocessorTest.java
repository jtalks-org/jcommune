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
                {"[lIsT=a:bcb294daef][*][/list]","[lIsT=a:bcb294daef][*][/*][/list]"},
                {"[lisT][/lisT]","[lisT][/lisT]"},
                {"some long text", "some long text"},
                {"[lIst][liSt][List]", "[lIst][liSt][List]"},
                {"[*]Duplicate", "[*]Duplicate"},
                {"before list[lisT][*]listitem[List]after list", "before list[lisT][*]listitem[List]after list"},
                {"zzz[/liSt]", "zzz[/liSt]"},
                {"[LIST][*]text[/list][*]text[/list]","[LIST][*]text[/*][/list][*]text[/list]"},
                {"[/liSt][List][*]text[/list][*]text","[/liSt][List][*]text[/*][/list][*]text"},
                {"[lisT][*]text[/lisT][*]text[lisT]","[lisT][*]text[/*][/lisT][*]text[lisT]"}
        };
    }
    
    @DataProvider
    public Object[][] nestedLists(){
        return new Object[][] {
            {"[list][*]111[LIST][*]222[/LIST][/list]", "[list][*]111[LIST][*]222[/*][/LIST][/*][/list]"},
            {"before[LIST=1][*]111[*][list=2][*]222[list=3][*]333[*]444[/list][*]555[LIST=4][*]666[*]777[/list][*]888[/list][*]999[/list]after",
                "before[LIST=1][*]111[/*][*][list=2][*]222[list=3][*]333[/*][*]444[/*][/list][/*][*]555[LIST=4][*]666[/*][*]777[/*][/list][/*][*]888[/*][/list][/*][*]999[/*][/list]after"},
            {"[list][LIST][*]text[/LIST][*]text[/list]","[list][LIST][*]text[/*][/LIST][*]text[/*][/list]"}
        };
    }

    @DataProvider
    public Object[][] validLists() {
        return new Object[][]{  // {"bb code before", "bb code after"}
                {"aaa[LIST][*]bbb[*]ccc[/list]ddd", "aaa[LIST][*]bbb[/*][*]ccc[/*][/list]ddd"},
                {"aaa[List][*][b]bbb[/b][*][padding=10]ccc[/padding][/list]ddd", "aaa[List][*][b]bbb[/b][/*][*][padding=10]ccc[/padding][/*][/list]ddd"},
                {"[list][*]aaa[*]bbb[/LIST]", "[list][*]aaa[/*][*]bbb[/*][/LIST]"},
                {"aaa[LIST][*]bbb[*]ccc[/list]dddaaa[list][*]bbb[*]ccc[/LIST]ddd", "aaa[LIST][*]bbb[/*][*]ccc[/*][/list]dddaaa[list][*]bbb[/*][*]ccc[/*][/LIST]ddd"},
                {"aaa[list=1][*]bbb[*]ccc[/LIST]dddaaa[LIST=1][*]bbb[*]ccc[/list]ddd", "aaa[list=1][*]bbb[/*][*]ccc[/*][/LIST]dddaaa[LIST=1][*]bbb[/*][*]ccc[/*][/list]ddd"},
                {"aaa[list=1][*][b]bbb[/b][*][padding=10]ccc[/padding][/LIST]ddd", "aaa[list=1][*][b]bbb[/b][/*][*][padding=10]ccc[/padding][/*][/LIST]ddd"},
                {"[LiST=1][*]aaa[*]bbb[/list]", "[LiST=1][*]aaa[/*][*]bbb[/*][/list]"},
                {"aaa[lIst=a][*]bbb[*]ccc[/list]ddd", "aaa[lIst=a][*]bbb[/*][*]ccc[/*][/list]ddd"},
                {"aaa[LIST=a][*][b]bbb[/b][*][padding=10]ccc[/padding][/LIST]ddd", "aaa[LIST=a][*][b]bbb[/b][/*][*][padding=10]ccc[/padding][/*][/LIST]ddd"},
                {"[list=a][*]aaa[*]bbb[/lisT]", "[list=a][*]aaa[/*][*]bbb[/*][/lisT]"},
                {"aaa[LIST=1][*]bbb[*]ccc[/list]d[b]dda[/b]aa[list][*]bbb[*]ccc[/list]ddd", "aaa[LIST=1][*]bbb[/*][*]ccc[/*][/list]d[b]dda[/b]aa[list][*]bbb[/*][*]ccc[/*][/list]ddd"},
                {"aaa[lISt]\n[*]bbb\n[*]ccc\n[/list]", "aaa[lISt]\n[*]bbb\n[/*][*]ccc\n[/*][/list]"},
                {"a\nb\nc[LIST]a\nb\nc[*]a\nb\nc[/list]a\nb\nc", "a\nb\nc[LIST]a\nb\nc[*]a\nb\nc[/*][/list]a\nb\nc"},
                {"", ""},
                {"[*]text[lIst][*]text[/list]","[*]text[lIst][*]text[/*][/list]"},
                {"[*][liSt][*]text[/list]","[*][liSt][*]text[/*][/list]"},
                {"[lisT][*]text[/list][*]","[lisT][*]text[/*][/list][*]"},
                {"[lIst][*]text[/lIst]text[*]text", "[lIst][*]text[/*][/lIst]text[*]text"}
        };
    }
}

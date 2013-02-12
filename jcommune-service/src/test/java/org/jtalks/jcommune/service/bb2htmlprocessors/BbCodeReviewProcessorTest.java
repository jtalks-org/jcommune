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

import com.google.common.collect.Lists;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class BbCodeReviewProcessorTest {
    private BbCodeReviewProcessor service;
    @Mock
    private HttpServletRequest request;
    
    @BeforeMethod
    public void setUp() {
        service = spy(new BbCodeReviewProcessor());
        MockitoAnnotations.initMocks(this);
        doReturn(request).when(service).getServletRequest();  
    }
    
    @Test(dataProvider="preProcessingPosts")
    public void regularNoneCodeReviewPostIsReturnedAsSameText(String bbCode, String expectedResult) {
        when(request.getAttribute("isCodeReviewPost")).thenReturn(null);
        assertEquals(service.process(bbCode), bbCode);
    }
        
    @Test(dataProvider="preProcessingPosts")
    public void preprocessorShouldSubstituteClosingCodeTags(String bbCode, String expectedResult) {
        when(request.getAttribute("isCodeReviewPost")).thenReturn("true");
        assertEquals(service.process(bbCode), expectedResult);
    }
    
    @Test(dataProvider="preProcessingPosts")
    public void preprocessorCharSequenceInterfaceShouldWorkAsString(String bbCode, String expectedResult) {
        when(request.getAttribute("isCodeReviewPost")).thenReturn("true");
        CharSequence charSequence = new StringBuilder(bbCode).subSequence(0, bbCode.length());
        assertEquals(service.process(charSequence).toString(), expectedResult);
    }
    
    @Test
    public void postprocessorShouldСorrectlyReturnUserCloseTag() {
        when(request.getAttribute("isCodeReviewPost")).thenReturn("true");
        List<Boolean> replaceHistoryList = Lists.newArrayList(true);
        when(request.getAttribute(BbCodeReviewProcessor.REPLACE_HISTORY_LIST_ATTRIBUTE)).thenReturn(replaceHistoryList);
        assertEquals(service.postProcess("<pre>int good=2;[-code]</pre>"), 
                                         "<pre>int good=2;[/code]</pre>");       
    }

    @Test
    public void postprocessorShouldСorrectlyReturnOurSubstitution() {
        when(request.getAttribute("isCodeReviewPost")).thenReturn("true");
        List<Boolean> replaceHistoryList = Lists.newArrayList(false, false);
        when(request.getAttribute(BbCodeReviewProcessor.REPLACE_HISTORY_LIST_ATTRIBUTE)).thenReturn(replaceHistoryList);
        assertEquals(service.postProcess("<pre>int good=2;[-code][-code]</pre>"),
                                         "<pre>int good=2;[-code][-code]</pre>");     
    }

    @Test
    public void postprocessorShouldСorrectlyReturnUserCloseTagAndOurSubstitution() {
        when(request.getAttribute("isCodeReviewPost")).thenReturn("true");
        List<Boolean> replaceHistoryList = Lists.newArrayList(true, false, true);
        when(request.getAttribute(BbCodeReviewProcessor.REPLACE_HISTORY_LIST_ATTRIBUTE)).thenReturn(replaceHistoryList);
        assertEquals(service.postProcess("<pre>int good=2;[-code][-code][-code]</pre>"),
                                         "<pre>int good=2;[/code][-code][/code]</pre>");       
    }

    @DataProvider
    public Object[][] preProcessingPosts() {
        return new Object[][]{  // {"bb code before", "bb code after"}
                // valid code review posts
                {"[code=java]int good=1;[/code]", "[code=java]int good=1;[/code]"},
                {"[code=java]int good=2;[/code][/code]", "[code=java]int good=2;[-code][/code]"},
                {"[code=java][code=js]int good=2;[/code][/code]", "[code=java][code=js]int good=2;[-code][/code]"},
                {"[code=java][code=js]int good=2;[-code][/code]", "[code=java][code=js]int good=2;[-code][/code]"},
                {"[code=java]fLn();\nsLn();[/code][/code]", "[code=java]fLn();\nsLn();[-code][/code]"},
                // invalid code review posts
                {"[b]int bad = 1;[b]", "[b]int bad = 1;[b]"},
                {"[code=js]int bad = 2;[/code]", "[code=js]int bad = 2;[/code]"},
                {"[code]int bad = 3;[/code]", "[code]int bad = 3;[/code]"},
                {"int bad = 1;", "int bad = 1;"}
        };
    }
}

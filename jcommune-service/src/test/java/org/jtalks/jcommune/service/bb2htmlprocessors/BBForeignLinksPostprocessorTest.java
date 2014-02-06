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

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class BBForeignLinksPostprocessorTest {
    private BBForeignLinksPostprocessor service;
    @Mock
    private HttpServletRequest request;
    private String prefix = "/out?url=";
    private String relAttr = "rel=\"nofollow\"";
    private String serverName = "server_name";

    @BeforeMethod
    public void setUp() {
        service = spy(new BBForeignLinksPostprocessor());
        when(service.getHrefPrefix()).thenReturn(prefix);
        MockitoAnnotations.initMocks(this);
        doReturn(request).when(service).getServletRequest();
        when(request.getServerName()).thenReturn(serverName);
    }

    @Test(dataProvider = "preProcessingCommonLinks")
    public void postprocessorShouldCorrectlyAddPrefix(String incomingText, String outcomingText) {
        assertEquals(service.postProcess(incomingText), outcomingText);
    }

    @Test(dataProvider = "preProcessingImages")
    public void postprocessorShouldCorrectlyReplaceSpaceInImg(String incomingText, String outcomingText) {
        assertEquals(service.postProcess(incomingText), outcomingText);
    }

    @Test(dataProvider = "preProcessingSubDomainLinks")
    public void postprocessorShouldCorrectlyRecognizeSubDomains(String incomingText, String outcomingText) {
        assertEquals(service.postProcess(incomingText), outcomingText);
    }

    @Test(dataProvider = "preProcessingLocalLinks")
    public void postprocessorShouldCorrectlyRecognizeLocalLinks(String incomingText, String outcomingText) {
        assertEquals(service.postProcess(incomingText), outcomingText);
    }

    @DataProvider
    public Object[][] preProcessingImages() {
        return new Object[][]{
                {"<img src=\"http://javatalks.ru/common img\">",
                        "<img alt=\" \" class=\"thumbnail\" src=\"http://javatalks.ru/common%20img\">"}

        };
    }

    @DataProvider
    public Object[][] preProcessingCommonLinks() {
        return new Object[][]{  // {"incoming link (before)", "outcoming link (after)"}
                {"<a href=\"http://javatalks.ru/common\"></a>",
                        "<a " + relAttr + " href=\"" + prefix + "http://javatalks.ru/common\"></a>"},
                {"<a href=\"https://forum.javatalks.ru\"></a>",
                        "<a " + relAttr + " href=\"" + prefix + "https://forum.javatalks.ru\"></a>"},
                {"<a href=\"http://javatalks.ru/common\"></a>",
                        "<a " + relAttr + " href=\"" + prefix + "http://javatalks.ru/common\"></a>"},
                {"<a href=\"http://javatalks.ru/common space\"></a>",
                        "<a " + relAttr + " href=\"" + prefix + "http://javatalks.ru/common%20space\"></a>"}

        };
    }

    @DataProvider
    public Object[][] preProcessingSubDomainLinks() {
        return new Object[][]{  // {"incoming link (before)", "outcoming link (after)"}
                {"<a href=\"http://blog.javatalks.ru\"></a>",
                        "<a " + relAttr + " href=\"" + prefix + "http://blog.javatalks.ru\"></a>"},
                {"<a href=\"http://www.blog.javatalks.ru\"></a>",
                        "<a " + relAttr + " href=\"" + prefix + "http://www.blog.javatalks.ru\"></a>"},
                {"<a href=\"http://com.blog.javatalks.ru\"></a>",
                        "<a " + relAttr + " href=\"" + prefix + "http://com.blog.javatalks.ru\"></a>"},
                {"<a href=\"http://com.blog.javatalks.ru/space space\"></a>",
                        "<a " + relAttr + " href=\"" + prefix + "http://com.blog.javatalks.ru/space%20space\"></a>"},
        };
    }

    @DataProvider
    public Object[][] preProcessingLocalLinks() {
        return new Object[][]{  // {"incoming link (before)", "outcoming link (after)"}
                {"<a href=\"" + serverName + ".ru\"></a>", "<a href=\"" + serverName + ".ru\"></a>"},
                {"<a href=\"http://blog." + serverName + ".ru\"></a>",
                        "<a href=\"http://blog." + serverName + ".ru\"></a>"},
                {"<a href=\"http://blog." + serverName + ".ru/space space\"></a>",
                        "<a href=\"http://blog." + serverName + ".ru/space%20space\"></a>"}
        };
    }
}

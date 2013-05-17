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
    
    @BeforeMethod
    public void setUp() {
        service = spy(new BBForeignLinksPostprocessor());
        MockitoAnnotations.initMocks(this);
        doReturn(request).when(service).getServletRequest();  
    }
    
    @Test(dataProvider = "preProcessingCommonLinks")
    public void postprocessorShouldCorrectlyAddNofollowAttribute(String incomingText, String outcomingText) {
        when(request.getServerName()).thenReturn("javatalks.ru");
        assertEquals(service.postProcess(incomingText), outcomingText);
    }

    @Test(dataProvider = "preProcessingSubDomainLinks")
    public void postprocessorShouldCorrectlyRecognizeSubDomains(String incomingText, String outcomingText) {
        when(request.getServerName()).thenReturn("blog.javatalks.ru");
        assertEquals(service.postProcess(incomingText), outcomingText);
    }

    @Test(dataProvider = "preProcessingFromLocalhost")
    public void postprocessorShouldCorrectlyRecognizeLocalhost(String incomingText, String outcomingText) {
        when(request.getServerName()).thenReturn("localhost");
        assertEquals(service.postProcess(incomingText), outcomingText);
    }

    @DataProvider
    public Object[][] preProcessingCommonLinks() {
        return new Object[][]{  // {"incoming link (before)", "outcoming link (after)"}
            {"<a href=\"http://javatalks.ru/common\"></a>", "<a href=\"http://javatalks.ru/common\"></a>"},
            {"<a href=\"javatalks.ru\"></a>", "<a href=\"javatalks.ru\"></a>"},
            {"<a href=\"www.javatalks.ru\"></a>", "<a href=\"www.javatalks.ru\"></a>"},
            {"<a href=\"blog.javatalks.ru\"></a>", "<a href=\"blog.javatalks.ru\"></a>"},
            {"<a href=\"forum.javatalks.ru\"></a>", "<a href=\"forum.javatalks.ru\"></a>"},
            {"<a width=\"500\" href=\"http://javatalks.ru\" style=\"color:red;\"></a>",
                    "<a width=\"500\" href=\"http://javatalks.ru\" style=\"color:red;\"></a>"},
            {"<a href=\"http://javatalks.ru/common\"></a>", "<a href=\"http://javatalks.ru/common\"></a>"},

            {"<a href=\"роспил.рф\"></a>", "<a href=\"роспил.рф\" rel=\"nofollow\"></a>"},
            {"<a href=\"hello\"></a> <a href=\"hello.ru\"></a>",
                    "<a href=\"hello\" rel=\"nofollow\"></a> <a href=\"hello.ru\" rel=\"nofollow\"></a>"},
            {"<a href=\"example.ru\"></a>", "<a href=\"example.ru\" rel=\"nofollow\"></a>"},
            {"<a width=\"500\" href=\"example1.ru\" style=\"color:red;\"></a>",
                    "<a width=\"500\" href=\"example1.ru\" rel=\"nofollow\" style=\"color:red;\"></a>"},
            {"<a href=\"example1.ru:8181\"></a>", "<a href=\"example1.ru:8181\" rel=\"nofollow\"></a>"},
            {"<a href=\"http://example1.ru\"></a>", "<a href=\"http://example1.ru\" rel=\"nofollow\"></a>"},
            {"<a href=\"http://example1.ru:80\"></a>", "<a href=\"http://example1.ru:80\" rel=\"nofollow\"></a>"},
            {"<a href=\"http://example1.ru/com\"></a>", "<a href=\"http://example1.ru/com\" rel=\"nofollow\"></a>"},
            {"<a href=\"http://example1.ru:80/c\"></a>", "<a href=\"http://example1.ru:80/c\" rel=\"nofollow\"></a>"},
            {"<a href=\"www.example.ru/com\"></a>", "<a href=\"www.example.ru/com\" rel=\"nofollow\"></a>"},
            {"<a href=\"http://www.example.ru/c5\"></a>", "<a href=\"http://www.example.ru/c5\" rel=\"nofollow\"></a>"},
            {"<a href=\"http://www.example.ru/c\"></a>", "<a href=\"http://www.example.ru/c\" rel=\"nofollow\"></a>"},
            {"<a href=\"http://spk-base.splunk.com/ans/77101/ext-link-val\"></a>",
                "<a href=\"http://spk-base.splunk.com/ans/77101/ext-link-val\" rel=\"nofollow\"></a>"},
            {"<a href=\"http://tr.goo.ru/?hl=ru&tab=wT#ru/en/%D0%BF%D1%80%D0%B8%D0%BC%D0%B5%D1%80%20%D" +
                "0%B2%D0%BD%D0%B5%D1%88%D0%BD%D0%B5%D0%B9%20%D1%81%D1%81%D1%8B%D0%BB%D0%BA%D0%B8\"></a>",
                "<a href=\"http://tr.goo.ru/?hl=ru&tab=wT#ru/en/%D0%BF%D1%80%D0%B8%D0%BC%D0%B5%D1%80%20%D0%B2%D0%BD%D" +
                        "0%B5%D1%88%D0%BD%D0%B5%D0%B9%20%D1%81%D1%81%D1%8B%D0%BB%D0%BA%D0%B8\" rel=\"nofollow\"></a>"},

            {"<a href=\"http://localhost:8080/topics/1\"></a>",
                        "<a href=\"http://localhost:8080/topics/1\" rel=\"nofollow\"></a>"},
        };
    }

    @DataProvider
    public Object[][] preProcessingSubDomainLinks() {
        return new Object[][]{  // {"incoming link (before)", "outcoming link (after)"}
                {"<a href=\"blog.javatalks.ru\"></a>", "<a href=\"blog.javatalks.ru\"></a>"},
                {"<a href=\"http://blog.javatalks.ru\"></a>", "<a href=\"http://blog.javatalks.ru\"></a>"},
                {"<a href=\"www.blog.javatalks.ru\"></a>", "<a href=\"www.blog.javatalks.ru\"></a>"},
                {"<a href=\"http://www.blog.javatalks.ru\"></a>", "<a href=\"http://www.blog.javatalks.ru\"></a>"},
                {"<a href=\"http://com.blog.javatalks.ru\"></a>", "<a href=\"http://com.blog.javatalks.ru\"></a>"},

                {"<a href=\"http://localhost:5000/topics/1\"></a>",
                        "<a href=\"http://localhost:5000/topics/1\" rel=\"nofollow\"></a>"},
                {"<a href=\"http://blog.javatalks\"></a>", "<a href=\"http://blog.javatalks\" rel=\"nofollow\"></a>"},

                {"<a href=\"http://javatalks.ru/common\"></a>",
                        "<a href=\"http://javatalks.ru/common\" rel=\"nofollow\"></a>"},
                {"<a href=\"javatalks.ru\"></a>", "<a href=\"javatalks.ru\" rel=\"nofollow\"></a>"},
                {"<a href=\"www.javatalks.ru\"></a>", "<a href=\"www.javatalks.ru\" rel=\"nofollow\"></a>"},
                {"<a href=\"forum.javatalks.ru\"></a>", "<a href=\"forum.javatalks.ru\" rel=\"nofollow\"></a>"},
                {"<a href=\"http://javatalks.ru\"></a>", "<a href=\"http://javatalks.ru\" rel=\"nofollow\"></a>"},
                {"<a href=\"http://javatalks.ru/common\"></a>",
                        "<a href=\"http://javatalks.ru/common\" rel=\"nofollow\"></a>"}
        };
    }

    @DataProvider
    public Object[][] preProcessingFromLocalhost() {
        return new Object[][]{  // {"incoming link (before)", "outcoming link (after)"}
                {"<a href=\"http://localhost:8080/topics/1\"></a>", "<a href=\"http://localhost:8080/topics/1\"></a>"},

                {"<a href=\"foreign-domain.ru\"></a>", "<a href=\"foreign-domain.ru\" rel=\"nofollow\"></a>"},
                {"<a href=\"foreign.domain.com\"></a>", "<a href=\"foreign.domain.com\" rel=\"nofollow\"></a>"},
                {"<a href=\"example1.ru:8181\"></a>", "<a href=\"example1.ru:8181\" rel=\"nofollow\"></a>"},
                {"<a href=\"http://example1.ru\"></a>", "<a href=\"http://example1.ru\" rel=\"nofollow\"></a>"},
        };
    }

}

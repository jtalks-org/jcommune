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
package org.jtalks.jcommune.web.controller;

import org.jtalks.jcommune.model.entity.ExternalLink;
import org.jtalks.jcommune.service.ExternalLinkService;
import org.jtalks.jcommune.web.dto.json.JsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

/**
 * @author Alexandre Teterin
 *         Date: 16.02.13
 */


public class ExternalLinkControllerTest {

//    private static final long ID = 1L;
//    private static final String TITLE = "title";
//    private static final String URL = "url";
//    private static final String HINT = "hint";
//
//    @Mock
//    private ExternalLinkService service;
//    private ExternalLinkController controller;

//    @BeforeMethod
//    public void setUp() {
//        initMocks(this);
//        controller = new ExternalLinkController(service);
//    }

//    @Test
//    public void testSaveLink() throws Exception {
//        JsonResponse expected = controller.saveLink(createLink());
//        assertEquals(expected.getStatus(), JsonResponseStatus.SUCCESS);
//        ExternalLink expectedLink = (ExternalLink) expected.getResult();
//        assertEquals(ID, expectedLink.getId());
//        assertEquals(TITLE, expectedLink.getTitle());
//        assertEquals(HINT, expectedLink.getHint());
//        assertEquals(URL, expectedLink.getUrl());
//        verify(service).saveLink(any(ExternalLink.class));
//    }
//
//    @Test
//    public void testDeleteLink() throws Exception {
//        boolean expectedResult = true;
//        when(service.deleteLink(eq(ID))).thenReturn(expectedResult);
//        JsonResponse expected = controller.deleteLink(ID);
//        assertEquals(expected.getStatus(), JsonResponseStatus.SUCCESS);
//        boolean actualResult = (Boolean) expected.getResult();
//        assertEquals(actualResult, expectedResult);
//        verify(service).deleteLink(eq(ID));
//    }

//    private ExternalLink createLink() {
//        ExternalLink link = new ExternalLink(URL, TITLE, HINT);
//        link.setId(ID);
//        return link;
//    }
}

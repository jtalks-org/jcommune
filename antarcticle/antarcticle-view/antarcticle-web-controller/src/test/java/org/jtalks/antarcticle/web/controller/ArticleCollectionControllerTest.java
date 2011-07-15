/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */

package org.jtalks.antarcticle.web.controller;

import org.jtalks.antarcticle.model.entity.ArticleCollection;
import org.jtalks.antarcticle.service.ArticleCollectionService;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import org.jtalks.antarcticle.service.ArticleService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeAvailable;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;

/**
 * @author Pavel Karpukhin
 */
public class ArticleCollectionControllerTest {

    private ArticleCollectionService articleCollectionService;
    private ArticleService articleService;
    private ArticleCollectionController controller;

    @BeforeMethod
    public void init() {
        articleCollectionService = mock(ArticleCollectionService.class);
        articleService = mock(ArticleService.class);
        controller = new ArticleCollectionController(articleCollectionService, articleService);
    }

    @Test
    public void testDisplayAllArticleCollections() {
        when(articleCollectionService.getAll()).thenReturn(new ArrayList<ArticleCollection>());

        ModelAndView mav = controller.articleCollectionList();

        assertViewName(mav, "articleCollectionList");
        assertModelAttributeAvailable(mav, "articleCollectionList");
        verify(articleCollectionService).getAll();
    }
}

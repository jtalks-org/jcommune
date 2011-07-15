package org.jtalks.antarcticle.web.controller;

import org.jtalks.antarcticle.model.entity.ArticleCollection;
import org.jtalks.antarcticle.service.ArticleCollectionService;
import org.jtalks.antarcticle.service.ArticleService;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

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

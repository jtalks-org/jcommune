package org.jtalks.antarcticle.web.controller;

import org.jtalks.antarcticle.service.ArticleCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Pavel Karpukhin
 */
@Controller
public class ArticleCollectionController {

    private ArticleCollectionService articleCollectionService;

    @Autowired
    public ArticleCollectionController(ArticleCollectionService articleCollectionService) {
        this.articleCollectionService = articleCollectionService;
    }

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public ModelAndView articleCollectionList() {
        return new ModelAndView("articleCollectionList", "articleCollectionList", articleCollectionService.getAll());
    }
}

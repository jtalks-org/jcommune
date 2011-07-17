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

import org.jtalks.antarcticle.model.entity.Article;
import org.jtalks.antarcticle.model.entity.ArticleCollection;
import org.jtalks.antarcticle.service.ArticleCollectionService;
import org.jtalks.antarcticle.service.ArticleService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Pavel Karpukhin
 * @author Vitaliy Kravchenko
 */
@Controller
public class ArticleCollectionController {

    private ArticleCollectionService articleCollectionService;
    private ArticleService articleService;

    /**
     * Constructs MVC controller with objects injected via autowiring
     *
     * @param articleCollectionService  the object which provides actions on {@link ArticleCollection} entity
     * @param articleService  the object which provides actions on {@link Article} entity
     */
    @Autowired
    public ArticleCollectionController(ArticleCollectionService articleCollectionService, ArticleService articleService) {
        this.articleCollectionService = articleCollectionService;
        this.articleService = articleService;
    }

    /**
     * Handles GET request and produces JSP page with all article collections
     *
     * @return {@link ModelAndView} with view name as articleCollectionList
     */
    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public ModelAndView articleCollectionList() {
        return new ModelAndView("articleCollectionList", "articleCollectionList", articleCollectionService.getAll());
    }

    @RequestMapping(value = "/articleCollection/{collectionId}", method = RequestMethod.GET)
    public ModelAndView getCollectionById(@PathVariable("collectionId") long collectionId) throws NotFoundException {
      Article firstArticle = articleService.getFirstArticleFromCollection(collectionId);
      ArticleCollection articleCollection = articleCollectionService.get(collectionId);
        return new ModelAndView("displayArticleCollection", "firstArticle",firstArticle)
                .addObject("articleCollection",articleCollection);
    }
}

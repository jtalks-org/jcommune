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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jtalks.antarcticle.model.entity.Article;
import org.jtalks.antarcticle.model.entity.Comment;
import org.jtalks.antarcticle.service.ArticleService;
import org.jtalks.antarcticle.service.CommentService;
import org.jtalks.common.service.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Dmitry Sokolov
 */
@Controller
@RequestMapping(value="article")
public class ArticleController {
    
    private ArticleService articleService;
    private CommentService commentService;
    
    /**
     * Creates an article controller with injection of {@link ArticleService} and {@link CommentService}
     * @param articleService article service
     * @param commentService comment service
     */
    @Autowired
    public ArticleController(ArticleService articleService, CommentService commentService) {
        this.articleService = articleService;
        this.commentService = commentService;
    }
    
    /**
     * GET handler for getting view of article with comments
     * @param articleId id of article
     * @return articleWithComments {@link ModelAndView} and article and comments as model
     * @throws NotFoundException throws if there is no article with provided articleId
     */
    @RequestMapping(value="/{articleId}", method= RequestMethod.GET)
    public ModelAndView displayArticleWithComments(@PathVariable("articleId") long articleId)
        throws NotFoundException {
        Article article = articleService.get(articleId);
        List<Comment> comments = commentService.getCommentsByArticle(article);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("article", article);
        map.put("comments", comments);
        return new ModelAndView("articleWithComments", map);
    }
}

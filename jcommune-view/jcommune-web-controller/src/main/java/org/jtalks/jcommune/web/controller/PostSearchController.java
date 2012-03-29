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

import java.util.List;

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.service.PostSearchService;
import org.jtalks.jcommune.service.nontransactional.SecurityService;
import org.jtalks.jcommune.web.util.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * The controller for the full-text search posts.
 * 
 * @author Anuar Nurmakanov
 *
 */
@Controller
public class PostSearchController {
    /**
     * The name attribute for the uri.
     */
    public static final String URI_ATTRIBUTE_NAME = "uri";
    /**
     * The name attribute for posts.
     */
    public static final String POSTS_ATTRIBUTE_NAME = "posts";
    /**
     * The name attribute for the pagination.
     */
    public static final String PAGINATION_ATTRIBUTE_NAME = "pagination";
    /**
     * The name attribute for the search text.
     */
    public static final String SEARCH_TEXT_ATTRIBUTE_NAME = "searchText";
    private static final String SEARCH_RESULT_VIEW_NAME = "postSearchResult";
    private PostSearchService postSearchService;
    private SecurityService securityService;
    
    /**
     * Constructor for controller instantiating, dependencies injected via autowiring.
     * 
     * @param postSearchService {@link PostSearchService} instance to be injected
     * @param securityService {@link SecurityService} instance to be injected
     */
    @Autowired
    public PostSearchController(PostSearchService postSearchService, SecurityService securityService) {
        this.postSearchService = postSearchService;
        this.securityService = securityService;
    }
    
    /**
     * This method performs a indexing the data from the database.
     */
    @RequestMapping(value = "/search/index/rebuild")
    public void rebuildIndexes() {
        postSearchService.rebuildIndex();
    }
    
    /**
     * Full-text search for posts. It needed to start the search.
     * 
     * @param searchText search text
     * @return redirect to answer page
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ModelAndView initSearch(@RequestParam String searchText) {
        int firstPage = 1; 
        return search(searchText, firstPage);
    }
    
    /**
     * Full-text search for posts. It needed to continue the search.
     * 
     * @param searchText search text
     * @param page page number
     * @return redirect to answer page
     */
    @RequestMapping(value = "/search/{searchText}", method = RequestMethod.GET)
    public ModelAndView continueSearch(@PathVariable String searchText,
            @RequestParam(value = "page", defaultValue = "1", required = true) Integer page) {
        return search(searchText, page);
    }
    
    /**
     * Contains a common logic for searching the text.
     * 
     * @param searchText search text
     * @param page page number
     * @return result of the search
     */
    private ModelAndView search(String searchText, int page) {
        JCUser currentUser = securityService.getCurrentUser();
        List<Post> posts = postSearchService.searchPostsByPhrase(searchText);
        String uri = searchText;
        Pagination pagination = new Pagination(page, currentUser, posts.size(), true);
        return new ModelAndView(SEARCH_RESULT_VIEW_NAME).
                addObject(POSTS_ATTRIBUTE_NAME, posts).
                addObject(PAGINATION_ATTRIBUTE_NAME, pagination).
                addObject(URI_ATTRIBUTE_NAME, uri).
                addObject(SEARCH_TEXT_ATTRIBUTE_NAME, searchText);
    }
}

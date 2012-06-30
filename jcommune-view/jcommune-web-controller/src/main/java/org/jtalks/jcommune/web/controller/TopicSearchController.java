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

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.TopicFullSearchService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.web.util.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * The controller for the full-text search topics.
 * 
 * @author Anuar Nurmakanov
 *
 */
@Controller
public class TopicSearchController {
    /**
     * The name attribute for the uri.
     */
    public static final String URI_ATTRIBUTE_NAME = "uri";
    /**
     * The name attribute for topics.
     */
    public static final String TOPICS_ATTRIBUTE_NAME = "topics";
    /**
     * The name attribute for the pagination.
     */
    public static final String PAGINATION_ATTRIBUTE_NAME = "pagination";
    /**
     * The name attribute for the search text.
     */
    public static final String SEARCH_TEXT_ATTRIBUTE_NAME = "searchText";
    private static final String SEARCH_RESULT_VIEW_NAME = "searchResult";

    private TopicFullSearchService topicSearchService;
    private UserService userService;
    
    /**
     * Constructor for controller instantiating, dependencies injected via autowiring.
     * 
     * @param topicSearchService {@link TopicFullSearchService} to perform actual search
     * @param userService {@link UserService} to fetch user currently logged in
     */
    @Autowired
    public TopicSearchController(TopicFullSearchService topicSearchService, UserService userService) {
        this.topicSearchService = topicSearchService;
        this.userService = userService;
    }
    
    /**
     * This method performs a indexing the data from the database.
     */
    @RequestMapping(value = "/search/index/rebuild")
    public void rebuildIndexes() {
        topicSearchService.rebuildIndex();
    }
    
    /**
     * Full-text search for topics. It needed to start the search.
     * 
     * @param searchText search text
     * @return redirect to the answer page
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ModelAndView initSearch(@RequestParam String searchText) {
        int firstPage = 1; 
        return search(searchText, firstPage);
    }
    
    /**
     * Full-text search for topics. It needed to continue the search.
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
        JCUser currentUser = userService.getCurrentUser();
        List<Topic> topics = topicSearchService.searchByTitleAndContent(searchText);
        Pagination pagination = new Pagination(page, currentUser, topics.size(), true);
        return new ModelAndView(SEARCH_RESULT_VIEW_NAME).
                addObject(TOPICS_ATTRIBUTE_NAME, topics).
                addObject(PAGINATION_ATTRIBUTE_NAME, pagination).
                addObject(URI_ATTRIBUTE_NAME, searchText).
                addObject(SEARCH_TEXT_ATTRIBUTE_NAME, searchText);
    }
}

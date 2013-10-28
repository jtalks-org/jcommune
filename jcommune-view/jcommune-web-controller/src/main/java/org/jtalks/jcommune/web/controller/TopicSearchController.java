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

import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.LastReadPostService;
import org.jtalks.jcommune.service.TopicFetchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * The controller for the full-text search topics.
 *
 * @author Anuar Nurmakanov
 */
@Controller
public class TopicSearchController {
    /**
     * The name attribute for the uri.
     */
    public static final String URI_ATTRIBUTE_NAME = "uri";
    /**
     * The name attribute for {@link Page}, that contains info for one page
     * of search result.
     */
    public static final String SEARCH_RESULT_ATTRIBUTE_NAME = "searchResultPage";
    /**
     * The name attribute for the search text.
     */
    public static final String SEARCH_TEXT_ATTRIBUTE_NAME = "searchText";

    private static final String SEARCH_RESULT_VIEW_NAME = "topic/searchResult";

    private TopicFetchService topicSearchService;
    
    private LastReadPostService lastReadPostService;

    /**
     * Constructor for controller instantiating, dependencies injected via autowiring.
     *
     * @param topicSearchService {@link TopicFetchService} to perform actual search
     * @param lastReadPostService {@link LastReadPostService} to fill info about topic updates  
     */
    @Autowired
    public TopicSearchController(TopicFetchService topicSearchService,
                                 LastReadPostService lastReadPostService) {
        this.topicSearchService = topicSearchService;
        this.lastReadPostService = lastReadPostService;
    }

    /**
     * This method performs a indexing the data from the database.
     */
    @RequestMapping(value = "/search/index/rebuild")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void rebuildIndexes() {
        topicSearchService.rebuildSearchIndex();
    }

    /**
     * Full-text search for topics. It needed to start the search.
     *
     * @param searchText search text
     * @return redirect to the answer page
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ModelAndView initSearch(@RequestParam String searchText) {
        String firstPage = "1";
        return search(searchText, firstPage);
    }

    /**
     * Full-text search for topics. It needed to continue the search.
     * This method calls only if request contains parameters page and search.
     *
     * @param searchText search text
     * @param page       page number
     * @return redirect to answer page
     */
    @RequestMapping(value = "/search/{searchText}", method = RequestMethod.GET, params= {"page","search"})
    public ModelAndView continueSearch(@PathVariable String searchText,
            @RequestParam(value = "page", defaultValue = "1", required = true) String page) {
        return search(searchText, page);
    }

    /**
     * Contains a common logic for searching the text.
     *
     * @param searchText search text
     * @param page       page number
     * @return result of the search
     */
    private ModelAndView search(String searchText, String page) {
        Page<Topic> searchResultPage = topicSearchService.searchByTitleAndContent(searchText, page);
        lastReadPostService.fillLastReadPostForTopics(searchResultPage.getContent());
        return new ModelAndView(SEARCH_RESULT_VIEW_NAME).
                addObject(SEARCH_RESULT_ATTRIBUTE_NAME, searchResultPage).
                addObject(URI_ATTRIBUTE_NAME, searchText).
                addObject(SEARCH_TEXT_ATTRIBUTE_NAME, searchText);
    }
}

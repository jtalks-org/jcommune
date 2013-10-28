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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.servlet.ModelAndView;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
/**
 * 
 * @author Anuar Nurmakanov
 *
 */
public class TopicSearchControllerTest {
	private static final String DEFAULT_SEARCH_TEXT = "topic content";
	private static final String START_PAGE = "1";
	@Mock
	private TopicFetchService topicFetchService;
	@Mock
	private LastReadPostService lastReadPostService;

	private TopicSearchController topicSearchController;

	@BeforeMethod
	public void init() {
		MockitoAnnotations.initMocks(this);
		topicSearchController = new TopicSearchController(topicFetchService, lastReadPostService);
	}

	@Test
	public void testRebuildIndexes() {
		topicSearchController.rebuildIndexes();
		
		verify(topicFetchService).rebuildSearchIndex();
	}
	
	@Test
	public void testInitSearch() {
		Page<Topic> searchResultPage = new PageImpl<Topic>(Collections.<Topic> emptyList());
		
		when(topicFetchService.searchByTitleAndContent(DEFAULT_SEARCH_TEXT, START_PAGE))
				.thenReturn(searchResultPage);

		ModelAndView modelAndView = topicSearchController.initSearch(DEFAULT_SEARCH_TEXT, "1");
		Map<String, Object> model = modelAndView.getModel();
		
		Assert.assertEquals(searchResultPage, model.get(TopicSearchController.SEARCH_RESULT_ATTRIBUTE_NAME), 
				"The controller must return the result of TopicFullSearchService.");
		Assert.assertEquals(DEFAULT_SEARCH_TEXT, model.get(TopicSearchController.URI_ATTRIBUTE_NAME),
				"Uri and the search text must be identical.");
		verify(topicFetchService).searchByTitleAndContent(DEFAULT_SEARCH_TEXT, START_PAGE);
		verify(lastReadPostService).fillLastReadPostForTopics(searchResultPage.getContent());
	}

    @Test
	public void testContinueSearch() {
        Page<Topic> searchResultPage = new PageImpl<Topic>(Collections.<Topic> emptyList());
		String page = "2";

        when(topicFetchService.searchByTitleAndContent(DEFAULT_SEARCH_TEXT, page))
				.thenReturn(searchResultPage);

		ModelAndView modelAndView = topicSearchController.initSearch(DEFAULT_SEARCH_TEXT, page);
		Map<String, Object> model = modelAndView.getModel();
		
		Assert.assertEquals(searchResultPage, model.get(TopicSearchController.SEARCH_RESULT_ATTRIBUTE_NAME), 
				"The controller must return the result of TopicFullSearchService.");
		Assert.assertEquals(DEFAULT_SEARCH_TEXT, model.get(TopicSearchController.URI_ATTRIBUTE_NAME),
				"Uri and the search text must be identical.");
		verify(topicFetchService).searchByTitleAndContent(DEFAULT_SEARCH_TEXT, page);
		verify(lastReadPostService).fillLastReadPostForTopics(searchResultPage.getContent());
	}
}

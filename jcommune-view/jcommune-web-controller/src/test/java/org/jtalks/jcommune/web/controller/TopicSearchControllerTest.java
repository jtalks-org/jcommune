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

import org.jtalks.common.security.SecurityService;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.TopicFullSearchService;
import org.jtalks.jcommune.web.util.Pagination;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
/**
 * 
 * @author Anuar Nurmakanov
 *
 */
public class TopicSearchControllerTest {
	private static final String DEFAULT_SEARCH_TEXT = "topic content";
	@Mock
	private TopicFullSearchService topicFullSearchService;
	@Mock
	private SecurityService securityService;
	private TopicSearchController topicSearchController;

	@BeforeMethod
	public void init() {
		MockitoAnnotations.initMocks(this);
		topicSearchController = new TopicSearchController(topicFullSearchService, securityService);
	}

	@Test
	public void testRebuildIndexes() {
		topicSearchController.rebuildIndexes();
		
		Mockito.verify(topicFullSearchService).rebuildIndex();
	}
	
	@Test
	public void testInitSearch() {
		List<Topic> resultTopics = Collections.emptyList();
		JCUser user = new JCUser("username", "email", "password");
		
		Mockito.when(topicFullSearchService.searchByTitleAndContent(DEFAULT_SEARCH_TEXT))
				.thenReturn(resultTopics);
		Mockito.when(securityService.getCurrentUser()).thenReturn(user);
		
		ModelAndView modelAndView = topicSearchController.initSearch(DEFAULT_SEARCH_TEXT);
		Map<String, Object> model = modelAndView.getModel();
		Pagination pagination = (Pagination) model.get(TopicSearchController.PAGINATION_ATTRIBUTE_NAME);
		
		Assert.assertEquals(resultTopics, model.get(TopicSearchController.TOPICS_ATTRIBUTE_NAME), 
				"The controller must return the result of TopicFullSearchService.");
		Assert.assertEquals(DEFAULT_SEARCH_TEXT, model.get(TopicSearchController.URI_ATTRIBUTE_NAME),
				"Uri and the search text must be identical.");
		Assert.assertEquals(Integer.valueOf(1), pagination.getPage(), "The page number should be the first.");
		Mockito.verify(topicFullSearchService).searchByTitleAndContent(DEFAULT_SEARCH_TEXT);
	}

    @Test
	public void testContinueSearch() {
		List<Topic> resultTopics = Collections.emptyList();
		JCUser user = new JCUser("username", "email", "password");
		int page = 2;
		
		Mockito.when(topicFullSearchService.searchByTitleAndContent(DEFAULT_SEARCH_TEXT))
				.thenReturn(resultTopics);
		Mockito.when(securityService.getCurrentUser()).thenReturn(user);
		
		ModelAndView modelAndView = topicSearchController.continueSearch(DEFAULT_SEARCH_TEXT, page);
		Map<String, Object> model = modelAndView.getModel();
		Pagination pagination = (Pagination) model.get(TopicSearchController.PAGINATION_ATTRIBUTE_NAME);
		
		Assert.assertEquals(resultTopics, model.get(TopicSearchController.TOPICS_ATTRIBUTE_NAME), 
				"The controller must return the result of TopicFullSearchService.");
		Assert.assertEquals(DEFAULT_SEARCH_TEXT, model.get(TopicSearchController.URI_ATTRIBUTE_NAME),
				"Uri and the search text must be identical.");
		Assert.assertEquals(Integer.valueOf(page), pagination.getPage(), "The page number should be the first.");
		Mockito.verify(topicFullSearchService).searchByTitleAndContent(DEFAULT_SEARCH_TEXT);
	}
}

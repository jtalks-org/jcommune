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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.service.PostSearchService;
import org.jtalks.jcommune.service.nontransactional.SecurityService;
import org.jtalks.jcommune.web.util.Pagination;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
/**
 * 
 * @author Anuar Nurmakanov
 *
 */
public class PostSearchControllerTest {
	private static final String DEFAULT_SEARCH_TEXT = "post content";
	@Mock
	private PostSearchService postSearchService;
	@Mock
	private SecurityService securityService;
	private PostSearchController postSearchController;

	@BeforeMethod
	public void init() {
		MockitoAnnotations.initMocks(this);
		postSearchController = new PostSearchController(postSearchService, securityService);
	}

	@Test
	public void testRebuildIndexes() {
		postSearchController.rebuildIndexes();
		
		Mockito.verify(postSearchService).rebuildIndex();
	}
	
	@Test
	public void testInitSearchPosts() {
		List<Post> resultPosts = new ArrayList<Post>();
		JCUser user = new JCUser("username", "email", "password");
		
		Mockito.when(postSearchService.searchPostsByPhrase(DEFAULT_SEARCH_TEXT))
				.thenReturn(resultPosts);
		Mockito.when(securityService.getCurrentUser()).thenReturn(user);
		
		ModelAndView modelAndView = postSearchController.initSearch(DEFAULT_SEARCH_TEXT);
		Map<String, Object> model = modelAndView.getModel();
		Pagination pagination = (Pagination) model.get(PostSearchController.PAGINATION_ATTRIBUTE_NAME);
		
		Assert.assertEquals(resultPosts, model.get(PostSearchController.POSTS_ATTRIBUTE_NAME), 
				"The controller must return the result of PostSearchService.");
		Assert.assertEquals(DEFAULT_SEARCH_TEXT, model.get(PostSearchController.URI_ATTRIBUTE_NAME),
				"Uri and the search text must be identical.");
		Assert.assertEquals(Integer.valueOf(1), pagination.getPage(), "The page number should be the first.");
		Mockito.verify(postSearchService).searchPostsByPhrase(DEFAULT_SEARCH_TEXT);
	}
	
	public void testContinueSearchPosts() {
		List<Post> resultPosts = new ArrayList<Post>();
		JCUser user = new JCUser("username", "email", "password");
		int page = 2;
		
		Mockito.when(postSearchService.searchPostsByPhrase(DEFAULT_SEARCH_TEXT))
				.thenReturn(resultPosts);
		Mockito.when(securityService.getCurrentUser()).thenReturn(user);
		
		ModelAndView modelAndView = postSearchController.continueSearch(DEFAULT_SEARCH_TEXT, page);
		Map<String, Object> model = modelAndView.getModel();
		Pagination pagination = (Pagination) model.get(PostSearchController.PAGINATION_ATTRIBUTE_NAME);
		
		Assert.assertEquals(resultPosts, model.get(PostSearchController.POSTS_ATTRIBUTE_NAME), 
				"The controller must return the result of PostSearchService.");
		Assert.assertEquals(DEFAULT_SEARCH_TEXT, model.get(PostSearchController.URI_ATTRIBUTE_NAME),
				"Uri and the search text must be identical.");
		Assert.assertEquals(Integer.valueOf(page), pagination.getPage(), "The page number should be the first.");
		Mockito.verify(postSearchService).searchPostsByPhrase(DEFAULT_SEARCH_TEXT);
	}
}

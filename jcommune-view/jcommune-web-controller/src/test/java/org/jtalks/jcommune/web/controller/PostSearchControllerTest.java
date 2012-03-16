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

import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.service.PostSearchService;
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
	private PostSearchController postSearchController;

	@BeforeMethod
	public void init() {
		MockitoAnnotations.initMocks(this);
		postSearchController = new PostSearchController(postSearchService);
	}

	@Test
	public void testSearchPosts() {
		List<Post> resultPosts = new ArrayList<Post>();
		Mockito.when(postSearchService.searchPostsByPhrase(DEFAULT_SEARCH_TEXT))
				.thenReturn(resultPosts);
		
		ModelAndView modelAndView = postSearchController.search(DEFAULT_SEARCH_TEXT);
		
		Assert.assertEquals(resultPosts, modelAndView.getModel().get("posts"), 
				"The controller must return the result of PostSearchService.");
		Mockito.verify(postSearchService).searchPostsByPhrase(DEFAULT_SEARCH_TEXT);
	}
}

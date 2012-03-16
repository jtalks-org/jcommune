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

import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.service.PostSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
	private PostSearchService postSearchService;
	
	/**
	 * Constructor for controller instantiating, dependencies injected via autowiring.
	 * 
	 * @param postSearchService {@link PostSearchService} instance to be injected
	 */
	@Autowired
	public PostSearchController(PostSearchService postSearchService) {
		this.postSearchService = postSearchService;
	}
	
	/**
	 * Full-text search for posts.
	 * 
	 * @param searchText search text
	 * @return redirect to answer page
	 */
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public ModelAndView search(@RequestParam String searchText) {
		List<Post> posts = postSearchService.searchPostsByPhrase(searchText);
		return new ModelAndView("postSearchResult").
				addObject("posts", posts);
	}
}

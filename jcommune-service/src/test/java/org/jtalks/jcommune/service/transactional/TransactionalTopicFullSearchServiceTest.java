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
package org.jtalks.jcommune.service.transactional;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jtalks.jcommune.model.dao.search.TopicSearchDao;
import org.jtalks.jcommune.model.entity.Topic;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar Nurmakanov
 * 
 */
public class TransactionalTopicFullSearchServiceTest {
	@Mock
	private TopicSearchDao topicSearchDao;
	private TransactionalTopicFullSearchService topicSearchService;
	
	@BeforeMethod
	public void init() {
		MockitoAnnotations.initMocks(this);
		topicSearchService = new TransactionalTopicFullSearchService(topicSearchDao);
	}
	
	@Test
	public void testSearchPosts() {
		String phrase = "phrase";
		
		topicSearchService.searchByTitleAndContent(phrase);
		
		Mockito.verify(topicSearchDao).searchByTitleAndContent(phrase);
	}
	
	@Test(dataProvider = "parameterSearchPostsWithEmptySearchPhrase")
	public void testSearchPostsWithEmptySearchPhrase(String phrase) {
		List<Topic> searchResult = topicSearchService.searchByTitleAndContent(phrase);
		
		Assert.assertTrue(searchResult.isEmpty(), "The search result must be empty.");
	}
	
	@DataProvider(name = "parameterSearchPostsWithEmptySearchPhrase")
	public Object[][] parameterSearchPostsWithEmptySearchPhrase() {
		return new Object[][] {
			{StringUtils.EMPTY},
			{null}	
		};
	}
	
	@Test
	public void testRebuildIndex() {
		topicSearchService.rebuildIndex();
		
		Mockito.verify(topicSearchDao).rebuildIndex();
	}
}

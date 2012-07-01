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

import org.apache.commons.lang.StringUtils;
import org.jtalks.jcommune.model.dao.search.TopicSearchDao;
import org.jtalks.jcommune.model.dto.JCommunePageRequest;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.nontransactional.PaginationService;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
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
    private static final int PAGE = 1;
	@Mock
	private TopicSearchDao topicSearchDao;
	@Mock
	private PaginationService paginationService;
	private TransactionalTopicFullSearchService topicSearchService;
	
	@BeforeMethod
	public void init() {
		MockitoAnnotations.initMocks(this);
		topicSearchService = new TransactionalTopicFullSearchService(topicSearchDao, paginationService);
		Mockito.when(paginationService.getPageSizeForCurrentUser()).thenReturn(50);
	}
	
	@Test
	public void testSearchPosts() {
		String phrase = "phrase";
		
		topicSearchService.searchByTitleAndContent(phrase, PAGE);
		
		Mockito.verify(topicSearchDao).searchByTitleAndContent(
		        Matchers.anyString(), Matchers.<JCommunePageRequest> any());
	}
	
	@Test(dataProvider = "parameterSearchPostsWithEmptySearchPhrase")
	public void testSearchPostsWithEmptySearchPhrase(String phrase) {
		Page<Topic> searchResultPage = topicSearchService.searchByTitleAndContent(phrase, PAGE);
		
		Assert.assertTrue(!searchResultPage.hasContent(), "The search result must be empty.");
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

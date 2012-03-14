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
package org.jtalks.jcommune.model.dao.hibernate;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.jtalks.jcommune.model.ObjectsFactory;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar Nurmakanov
 * 
 */
@ContextConfiguration(locations = { "classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class PostSearchHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private PostDao postDao;
	private FullTextSession fullTextSession;
	
	@BeforeMethod
	public void init() {
		Session session = sessionFactory.getCurrentSession();
        ObjectsFactory.setSession(session);
	}
	
	@BeforeMethod
	public void initHibernateSearch() throws InterruptedException {
		Session session = sessionFactory.getCurrentSession();
		fullTextSession = Search.getFullTextSession(session);
		fullTextSession.createIndexer().startAndWait();
	}
	
	@AfterMethod
	public void clearIndexes() {
		fullTextSession.purgeAll(Post.class);
		fullTextSession.purgeAll(Topic.class);
		fullTextSession.flushToIndexes();
	}
	
	@Test(dataProvider = "parameterFullPhraseSearchPosts")
	public void testFullPhraseSearchPosts(String postContent) {
		Post expectedPost = ObjectsFactory.getDefaultPost();
		
		fullTextSession.save(expectedPost);
		fullTextSession.flushToIndexes();
		
		List<Post> searchResultPosts = postDao.searchPosts(expectedPost.getPostContent());
		
		Assert.assertTrue(searchResultPosts != null, "Search result must not be null.");
		Assert.assertTrue(searchResultPosts.size() != 0, "Search result must not be empty.");
		for (Post post : searchResultPosts) {
			Assert.assertEquals(expectedPost.getPostContent(), post.getPostContent());
		}
	}
	
	@DataProvider(name = "parameterFullPhraseSearchPosts")
	public Object[][] parameterFullPhraseSearchPosts() {
		return new Object[][] {
				{"Содержимое поста."},
				{"Post content."}
		};
	}
	
	@Test(dataProvider = "parameterPiecePhraseSearchPosts")
	public void testPiecePhraseSearchPosts(String firstPiece, char delimeter, String secondPiece){
		String postContent = new StringBuilder().
				append(firstPiece).
				append(delimeter).
				append(secondPiece).
				toString();
		Post expectedPost = ObjectsFactory.getDefaultPost();
		expectedPost.setPostContent(postContent);
		
		fullTextSession.save(expectedPost);
		fullTextSession.flushToIndexes();
		
		for (String piece: Arrays.asList(firstPiece, secondPiece)) {
			List<Post> searchResultPosts = postDao.searchPosts(piece);
			Assert.assertTrue(searchResultPosts != null, "Search result must not be null.");
			Assert.assertTrue(searchResultPosts.size() != 0, "Search result must not be empty.");
		}
	}
	
	@DataProvider
	public Object[] [] parameterPiecePhraseSearchPosts() {
		return new Object[][] {
				{"Содержимое", ' ',  "поста"},
				{"Post", ' ', "content"}
		};
	}
}

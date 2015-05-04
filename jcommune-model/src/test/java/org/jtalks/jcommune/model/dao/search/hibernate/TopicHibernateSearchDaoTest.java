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
package org.jtalks.jcommune.model.dao.search.hibernate;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.model.dto.PageRequest;
import org.jtalks.jcommune.model.search.SearchRequestFilter;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Anuar Nurmakanov
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class TopicHibernateSearchDaoTest extends AbstractTransactionalTestNGSpringContextTests {
    private static final int PAGE_SIZE = 50;
    private static final String TOPIC_CONTENT = "topicContent";
    private static final PageRequest DEFAULT_PAGE_REQUEST = new PageRequest("1", 50);
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private TopicHibernateSearchDao topicSearchDao;
    @Mock
    private SearchRequestFilter invalidCharactersFilter;
    @Mock
    private SearchRequestFilter stopWordsFilter;

    private FullTextSession fullTextSession;

    @BeforeMethod
    public void init() {
        Session session = sessionFactory.getCurrentSession();
        PersistedObjectsFactory.setSession(session);

        MockitoAnnotations.initMocks(this);
        List<SearchRequestFilter> filters = Arrays.asList(invalidCharactersFilter, stopWordsFilter);
        topicSearchDao.setFilters(filters);
    }

    private void configureMocks(String searchText, String result) {
        Mockito.when(invalidCharactersFilter.filter(searchText)).thenReturn(result);
        Mockito.when(stopWordsFilter.filter(searchText)).thenReturn(result);
    }

    @BeforeMethod
    public void initHibernateSearch() throws InterruptedException {
        Session session = sessionFactory.getCurrentSession();
        fullTextSession = Search.getFullTextSession(session);
        fullTextSession.createIndexer().startAndWait();
    }

    @AfterMethod
    public void clearIndexes() {
        fullTextSession.purgeAll(Topic.class);
        fullTextSession.flushToIndexes();
    }

	/*===== Paging testing =====*/

    @Test
    public void testSearchPaging() {
        int totalSize = 50;
        int pageCount = 2;
        int pageSize = totalSize / pageCount;
        String searchText = "JCommune";
        PageRequest pageRequest = new PageRequest("1", pageSize);
        List<Topic> topicList = PersistedObjectsFactory.createAndSaveTopicList(totalSize);
        for (Topic topic : topicList) {
            topic.setTitle(searchText);
        }

        saveAndFlushIndexes(topicList);
        configureMocks(searchText, searchText);

        Page<Topic> searchResultPage = topicSearchDao.searchByTitleAndContent(
                searchText, pageRequest, Arrays.asList(topicList.get(0).getBranch().getId()));

        assertEquals(searchResultPage.getContent().size(), pageSize, "Incorrect count of topics in one page.");
        assertEquals(searchResultPage.getTotalElements(), totalSize, "Incorrect total count.");
        assertEquals(searchResultPage.getTotalPages(), pageCount, "Incorrect count of pages.");

    }

	/*===== Testing of different variations of the search. =====*/

    @Test
    public void testSearchWithFullyDirtySearchText() {
        configureMocks(StringUtils.EMPTY, StringUtils.EMPTY);

        Page<Topic> searchResultPage = topicSearchDao.searchByTitleAndContent(
                StringUtils.EMPTY, DEFAULT_PAGE_REQUEST, Arrays.asList(1L));

        Assert.assertTrue(!searchResultPage.hasContent(), "Search result must be empty.");
    }

    @Test(dataProvider = "parameterFullPhraseSearch")
    public void testFullPhraseSearch(String content) {
        Topic expectedTopic = PersistedObjectsFactory.getDefaultTopic();
        expectedTopic.setTitle(content);

        saveAndFlushIndexes(Arrays.asList(expectedTopic));
        configureMocks(content, content);

        Page<Topic> searchResultPage = topicSearchDao.searchByTitleAndContent(
                content, DEFAULT_PAGE_REQUEST, Arrays.asList(expectedTopic.getBranch().getId()));

        Assert.assertTrue(searchResultPage.hasContent(), "Search result must not be empty.");
        for (Topic topic : searchResultPage.getContent()) {
            Assert.assertEquals(expectedTopic.getTitle(), topic.getTitle(),
                    "Content from the index should be the same as in the database.");
        }
    }

    @Test
    public void testFullPhraseSearchPageNumberTooLow() {
        Topic expectedTopic = PersistedObjectsFactory.getDefaultTopic();
        expectedTopic.setTitle(TOPIC_CONTENT);

        saveAndFlushIndexes(Arrays.asList(expectedTopic));
        configureMocks(TOPIC_CONTENT, TOPIC_CONTENT);

        PageRequest pageRequest = new PageRequest("-1", PAGE_SIZE);
        Page<Topic> searchResultPage = topicSearchDao.searchByTitleAndContent(
                TOPIC_CONTENT, pageRequest, Arrays.asList(expectedTopic.getBranch().getId()));

        Assert.assertEquals(searchResultPage.getNumber(), 1);
        Assert.assertTrue(searchResultPage.hasContent(), "Search result must not be empty.");
        for (Topic topic : searchResultPage.getContent()) {
            Assert.assertEquals(expectedTopic.getTitle(), topic.getTitle(),
                    "Content from the index should be the same as in the database.");
        }
    }

    @Test
    public void testFullPhraseSearchPageNumberTooBig() {
        Topic expectedTopic = PersistedObjectsFactory.getDefaultTopic();
        expectedTopic.setTitle(TOPIC_CONTENT);

        saveAndFlushIndexes(Arrays.asList(expectedTopic));
        configureMocks(TOPIC_CONTENT, TOPIC_CONTENT);

        PageRequest pageRequest = new PageRequest("1000", 50);
        Page<Topic> searchResultPage =
                topicSearchDao.searchByTitleAndContent(TOPIC_CONTENT, pageRequest,
                        Arrays.asList(expectedTopic.getBranch().getId()));

        Assert.assertEquals(searchResultPage.getNumber(), 1);
        Assert.assertTrue(searchResultPage.hasContent(), "Search result must not be empty.");
        for (Topic topic : searchResultPage.getContent()) {
            Assert.assertEquals(expectedTopic.getTitle(), topic.getTitle(),
                    "Content from the index should be the same as in the database.");
        }
    }

    @Test(dataProvider = "parameterFullPhraseSearch")
    public void testPostContentSearch(String content) {
        Topic expectedTopic = PersistedObjectsFactory.getDefaultTopic();
        expectedTopic.getLastPost().setPostContent(content);

        saveAndFlushIndexes(Arrays.asList(expectedTopic));
        configureMocks(content, content);

        Page<Topic> searchResultPage = topicSearchDao.searchByTitleAndContent(
                content, DEFAULT_PAGE_REQUEST, Arrays.asList(expectedTopic.getBranch().getId()));

        Assert.assertTrue(searchResultPage.hasContent(), "Search result must not be empty.");
        for (Topic topic : searchResultPage.getContent()) {
            Assert.assertEquals(expectedTopic.getTitle(), topic.getTitle(),
                    "Content from the index should be the same as in the database.");
        }
    }

    @DataProvider(name = "parameterFullPhraseSearch")
    public Object[][] parameterFullPhraseSearch() {
        return new Object[][]{
                {"Содержимое темы."},
                {"Topic content."}
        };
    }

    @Test(dataProvider = "parameterPiecePhraseSearch")
    public void testPiecePhraseSearch(String firstPiece, char delimeter, String secondPiece) {
        String content = firstPiece + delimeter + secondPiece;

        Topic expectedTopic = PersistedObjectsFactory.getDefaultTopic();
        expectedTopic.setTitle(content);

        saveAndFlushIndexes(Arrays.asList(expectedTopic));

        for (String piece : Arrays.asList(firstPiece, secondPiece)) {
            configureMocks(piece, piece);

            Page<Topic> searchResultPage = topicSearchDao.searchByTitleAndContent(
                    piece, DEFAULT_PAGE_REQUEST, Arrays.asList(expectedTopic.getBranch().getId()));

            Assert.assertTrue(searchResultPage.hasContent(), "Search result must not be empty.");
        }
    }

    @DataProvider(name = "parameterPiecePhraseSearch")
    public Object[][] parameterPiecePhraseSearch() {
        return new Object[][]{
                {"Содержимое", ' ', "топика"},
                {"Topic", ' ', "content"}
        };
    }

    @Test(dataProvider = "parameterIncorrectPhraseSearch")
    public void testIncorrectPhraseSearch(String correct, String incorrect) {
        Topic expectedTopic = PersistedObjectsFactory.getDefaultTopic();
        expectedTopic.setTitle(correct);

        saveAndFlushIndexes(Arrays.asList(expectedTopic));
        configureMocks(incorrect, incorrect);

        Page<Topic> searchResultPage = topicSearchDao.searchByTitleAndContent(
                incorrect, DEFAULT_PAGE_REQUEST, Arrays.asList(expectedTopic.getBranch().getId()));

        Assert.assertTrue(!searchResultPage.hasContent(), "Search result must be empty.");
    }

    private <E> void saveAndFlushIndexes(List<E> entityList) {
        for (E entity : entityList) {
            fullTextSession.save(entity);
        }
        fullTextSession.flushToIndexes();
    }

    @DataProvider(name = "parameterIncorrectPhraseSearch")
    public Object[][] parameterIncorrectPhraseSearch() {
        return new Object[][]{
                {"Содержимое поста", "Железный человек"},
                {"Post content", "Iron Man"}
        };
    }

    @Test(dataProvider = "parameterSearchByRoot")
    public void testSearchByRoot(String word, String wordWithSameRoot) {
        Topic expectedTopic = PersistedObjectsFactory.getDefaultTopic();
        expectedTopic.setTitle(word);

        saveAndFlushIndexes(Arrays.asList(expectedTopic));
        configureMocks(wordWithSameRoot, wordWithSameRoot);

        Page<Topic> searchResultPage = topicSearchDao.searchByTitleAndContent(
                wordWithSameRoot, DEFAULT_PAGE_REQUEST, Arrays.asList(expectedTopic.getBranch().getId()));
        Assert.assertTrue(searchResultPage.hasContent(), "Search result must not be empty.");
    }

    @DataProvider(name = "parameterSearchByRoot")
    public Object[][] parameterSearchByRoot() {
        return new Object[][]{
                {"Keys", "Key"},
                {"Key", "Keys"},
                {"testing", "Tests"},
                {"tests", "TeStIng"},
                {"Полеты", "полет"},
                {"барабан", "барабаны"}
        };
    }

    @Test(dataProvider = "parameterSearchByBbCodes")
    public void testSearchByBbCodes(String content, String bbCode) {
        Topic expectedTopic = PersistedObjectsFactory.getDefaultTopic();
        expectedTopic.getLastPost().setPostContent(content);

        saveAndFlushIndexes(Arrays.asList(expectedTopic));
        configureMocks(bbCode, bbCode);

        Page<Topic> searchResultPage = topicSearchDao.searchByTitleAndContent(
                bbCode, DEFAULT_PAGE_REQUEST, Arrays.asList(expectedTopic.getBranch().getId()));
        Assert.assertTrue(!searchResultPage.hasContent(), "Search result must be empty.");
    }

    @DataProvider(name = "parameterSearchByBbCodes")
    public Object[][] parameterSearchByBbCodes() {
        return new Object[][]{
                {"[code=java]spring[/code]", "code"},
                {"[b]gwt[/b]", "b"}
        };
    }

    @Test(dataProvider = "parameterSearchByBbCodesContent")
    public void testSearchByBbCodesContent(String content, String bbCodeContent) {
        Topic expectedTopic = PersistedObjectsFactory.getDefaultTopic();
        expectedTopic.getLastPost().setPostContent(content);

        saveAndFlushIndexes(Arrays.asList(expectedTopic));
        configureMocks(bbCodeContent, bbCodeContent);

        Page<Topic> searchResultPage = topicSearchDao.searchByTitleAndContent(
                bbCodeContent, DEFAULT_PAGE_REQUEST, Arrays.asList(expectedTopic.getBranch().getId()));
        Assert.assertTrue(searchResultPage.hasContent(), "Search result must not be empty.");
    }

    @Test
    public void searchByTitleAndContentShouldIgnoreTopicIfOnlyDraftSatisfiesCondition() {
        String phrase = "java";
        Topic expectedTopic = PersistedObjectsFactory.getDefaultTopic();
        expectedTopic.addPost(new Post(PersistedObjectsFactory.getDefaultUser(), phrase, PostState.DRAFT));

        saveAndFlushIndexes(Arrays.asList(expectedTopic));
        configureMocks(phrase, phrase);

        Page<Topic> searchResultPage = topicSearchDao.searchByTitleAndContent(
                phrase, DEFAULT_PAGE_REQUEST, Arrays.asList(expectedTopic.getBranch().getId()));

        assertFalse(searchResultPage.hasContent());
    }

    @Test
    public void searchByTitleAndContentShouldFindTopicIfDraftAndDisplayedPostsSatisfiesCondition() {
        String phrase = "java";
        Topic expectedTopic = PersistedObjectsFactory.getDefaultTopic();
        expectedTopic.addPost(new Post(PersistedObjectsFactory.getDefaultUser(), phrase, PostState.DRAFT));
        expectedTopic.addPost(new Post(PersistedObjectsFactory.getUser("name", "mail@mail.ru"), phrase));

        saveAndFlushIndexes(Arrays.asList(expectedTopic));
        configureMocks(phrase, phrase);

        Page<Topic> searchResultPage = topicSearchDao.searchByTitleAndContent(
                phrase, DEFAULT_PAGE_REQUEST, Arrays.asList(expectedTopic.getBranch().getId()));

        assertTrue(searchResultPage.hasContent());
    }

    @DataProvider(name = "parameterSearchByBbCodesContent")
    public Object[][] parameterSearchByBbCodesContent() {
        return new Object[][]{
                {"[code=java]code[/code]", "code"},
                {"[b]b[/b]", "b"}
        };
    }
}

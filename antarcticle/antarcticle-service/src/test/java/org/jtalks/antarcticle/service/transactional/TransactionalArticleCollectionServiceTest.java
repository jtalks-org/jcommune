/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */

package org.jtalks.antarcticle.service.transactional;

import org.jtalks.antarcticle.model.dao.ArticleCollectionDao;
import org.jtalks.antarcticle.model.entity.ArticleCollection;
import org.jtalks.antarcticle.service.ArticleCollectionService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * @author Pavel Karpukhin
 */
public class TransactionalArticleCollectionServiceTest {

    private long ARTICLE_COLLECTION_ID = 1L;

    private ArticleCollectionDao articleCollectionDao;
    private ArticleCollectionService articleCollectionService;

    @BeforeMethod
    public void setUp() throws Exception {
        articleCollectionDao = mock(ArticleCollectionDao.class);
        articleCollectionService = new TransactionalArticleCollectionService(articleCollectionDao);
    }

    @Test
    public void testGet() throws NotFoundException {
        ArticleCollection expectedArticleCollection = new ArticleCollection();
        when(articleCollectionDao.isExist(ARTICLE_COLLECTION_ID)).thenReturn(true);
        when(articleCollectionDao.get(ARTICLE_COLLECTION_ID)).thenReturn(expectedArticleCollection);

        ArticleCollection articleCollection = articleCollectionService.get(ARTICLE_COLLECTION_ID);

        assertEquals(articleCollection, expectedArticleCollection, "ArticleCollections don't equal");
        verify(articleCollectionDao).isExist(ARTICLE_COLLECTION_ID);
        verify(articleCollectionDao).get(ARTICLE_COLLECTION_ID);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testGetIncorrectId() throws NotFoundException {
        when(articleCollectionDao.isExist(ARTICLE_COLLECTION_ID)).thenReturn(false);

        articleCollectionService.get(ARTICLE_COLLECTION_ID);
    }

    @Test
    public void testGetAll() {
        List<ArticleCollection> expectedArticleCollectionList = new ArrayList<ArticleCollection>();
        expectedArticleCollectionList.add(new ArticleCollection());
        when(articleCollectionDao.getAll()).thenReturn(expectedArticleCollectionList);

        List<ArticleCollection> articleCollectionList = articleCollectionService.getAll();

        assertEquals(articleCollectionList, expectedArticleCollectionList);
        verify(articleCollectionDao).getAll();
    }
}

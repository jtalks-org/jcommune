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

import org.jtalks.jcommune.model.dao.TopicTypeDao;
import org.jtalks.jcommune.model.entity.TopicType;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.service.TopicTypeService;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Mikhail Stryzhonok
 */
public class TransactionalTopicTypeServiceTest {

    @Mock
    private TopicTypeDao dao;

    private TopicTypeService topicTypeService;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        topicTypeService = new TransactionalTopicTypeService(dao);
    }

    @Test
    public void createTopicTypeIfNotExistShouldCreateNewTopicTypeIfNotExist() {
        String name = "type";
        when(dao.getByName(name)).thenReturn(null);

        topicTypeService.createTopicTypeIfNotExist(name);

        verify(dao).saveOrUpdate(any(TopicType.class));
    }

    @Test
    public void createTopicTypeIfNotExistShouldNotCreateNewTopicTypeIfExist() {
        String name = "type";
        TopicType type = new TopicType(name);
        when(dao.getByName(name)).thenReturn(type);

        topicTypeService.createTopicTypeIfNotExist(name);

        verify(dao, never()).saveOrUpdate(any(TopicType.class));
    }

    @Test
    public void testGetTopicTypeByName() throws Exception {
        String name = "name";
        TopicType expected = new TopicType(name);
        when(dao.getByName(name)).thenReturn(expected);

        TopicType actual = topicTypeService.getTopicTypeByName(name);

        assertEquals(expected, actual);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testGetTopicTypeByNameShouldThrowExceptionIfTopicTypeNotFound() throws Exception {
        when(dao.getByName(anyString())).thenReturn(null);

        topicTypeService.getTopicTypeByName("");
    }
}

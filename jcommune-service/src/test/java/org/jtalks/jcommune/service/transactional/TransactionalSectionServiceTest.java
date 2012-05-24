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

import org.jtalks.common.model.entity.Section;
import org.jtalks.jcommune.model.dao.SectionDao;
import org.jtalks.jcommune.service.SectionService;
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
 * This test class is intended to test all topic-related forum branch facilities
 *
 * @author Max Malakhov
 */
public class TransactionalSectionServiceTest {
    final long SECTION_ID = 1L;
    final String SECTION_NAME = "section name";

    private SectionDao sectionDao;
    private SectionService sectionService;

    @BeforeMethod
    public void setUp() throws Exception {
        sectionDao = mock(SectionDao.class);
        sectionService = new TransactionalSectionService(sectionDao);
    }

    @Test
    public void testGet() throws NotFoundException {
        Section expectedSection = new Section(SECTION_NAME);
        when(sectionDao.isExist(SECTION_ID)).thenReturn(true);
        when(sectionDao.get(SECTION_ID)).thenReturn(expectedSection);

        Section section = sectionService.get(SECTION_ID);

        assertEquals(section, expectedSection, "Sections aren't equals");
        verify(sectionDao).isExist(SECTION_ID);
        verify(sectionDao).get(SECTION_ID);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testGetIncorrectId() throws NotFoundException {
        when(sectionDao.isExist(SECTION_ID)).thenReturn(false);

        sectionService.get(SECTION_ID);
    }

    @Test
    public void testGetAll() {
        List<Section> expectedSectionList = new ArrayList<Section>();
        expectedSectionList.add(new Section(SECTION_NAME));
        when(sectionDao.getAll()).thenReturn(expectedSectionList);

        List<Section> actualSectionList = sectionService.getAll();

        assertEquals(actualSectionList, expectedSectionList);
        verify(sectionDao).getAll();
    }
}

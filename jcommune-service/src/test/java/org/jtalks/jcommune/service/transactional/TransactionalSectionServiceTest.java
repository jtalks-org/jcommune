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

import org.jtalks.common.model.entity.Branch;
import org.jtalks.common.model.entity.Section;
import org.jtalks.jcommune.model.dao.SectionDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.ObjectsFactory;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.SectionService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.mockito.Mockito;
import org.springframework.security.access.AccessDeniedException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * This test class is intended to test all topic-related forum branch facilities
 *
 * @author Max Malakhov
 */
public class TransactionalSectionServiceTest {
    final long SECTION_ID = 1L;
    final long TOPIC_ID = 1L;
    final String SECTION_NAME = "section name";
    final String USER_NAME = "user name";
    final String USER_PASSWORD = "password";
    final String EMAIL = "test@email.test";

    private SectionDao sectionDao;
    private BranchService branchService;
    private UserService userService;
    private SectionService sectionService;
    private TopicDao topicDao;

    @BeforeMethod
    public void setUp() throws Exception {
        sectionDao = mock(SectionDao.class);
        branchService = mock(BranchService.class);
        userService = mock(UserService.class);
        topicDao = mock(TopicDao.class);
        sectionService = new TransactionalSectionService(sectionDao, branchService, userService, topicDao);
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

    @Test
    public void getAllAvailableSections() {
        JCUser user = ObjectsFactory.getDefaultUser();
        Topic topic = ObjectsFactory.getTopic(user, 1);
        org.jtalks.jcommune.model.entity.Branch topicBranch = ObjectsFactory.getDefaultBranchWithTopic(100L, topic);
        Section sectionWithAvaliableBranches = ObjectsFactory.getDefaultSectionWithBranches();

        List<Section> allSections = new ArrayList<>();
        allSections.add(ObjectsFactory.getDefaultSection());
        allSections.add(ObjectsFactory.getDefaultSectionWithBranch(topicBranch));
        allSections.add(sectionWithAvaliableBranches);

        List<Section> expectedSections = new ArrayList<>();
        expectedSections.add(sectionWithAvaliableBranches);

        when(sectionDao.getAll()).thenReturn(allSections);
        when(topicDao.get(TOPIC_ID)).thenReturn(topic);
        when(userService.getCurrentUser()).thenReturn(user);
        when(sectionDao.getCountAvailableBranches(user, new ArrayList<Branch>())).thenReturn(0L);
        when(sectionDao.getCountAvailableBranches(user, sectionWithAvaliableBranches.getBranches())).thenReturn(3L);

        List<Section> actualSectionList = sectionService.getAllAvailableSections(TOPIC_ID);
        assertEquals(actualSectionList, expectedSections, "Should return all available sections.");
    }
    
    @Test
    public void testPrepareSectionsForView() {
        List<Section> sections = Arrays.asList(new Section(SECTION_NAME), new Section(SECTION_NAME));
        int sectionSize = sections.size();
        
        sectionService.prepareSectionsForView(sections);

        verify(branchService, Mockito.times(sectionSize))
            .fillStatisticInfo(Mockito.anyListOf(Branch.class));
    }
    
    @Test
    public void testDeleteAllBranches() throws NotFoundException {
        Section expectedSection = new Section(SECTION_NAME);
        expectedSection.addOrUpdateBranch(new Branch(null, null));
        expectedSection.addOrUpdateBranch(new Branch(null, null));
        
        when(sectionDao.isExist(SECTION_ID)).thenReturn(true);
        when(sectionDao.get(SECTION_ID)).thenReturn(expectedSection);
        
        Section actualSection = sectionService.deleteAllTopicsInSection(SECTION_ID);
        
        assertEquals(actualSection, expectedSection, "Sections aren't equals");
        verify(sectionDao).isExist(SECTION_ID);
        verify(sectionDao).get(SECTION_ID);
    }
    
    @Test
    public void testDeleteAllBranchesInEmptySection() throws NotFoundException {
        Section expectedSection = new Section(SECTION_NAME);
        
        when(sectionDao.isExist(SECTION_ID)).thenReturn(true);
        when(sectionDao.get(SECTION_ID)).thenReturn(expectedSection);
        
        Section actualSection = sectionService.deleteAllTopicsInSection(SECTION_ID);
        
        assertEquals(actualSection, expectedSection, "Sections aren't equals");
        verify(sectionDao).isExist(SECTION_ID);
        verify(sectionDao).get(SECTION_ID);
    }
    
    @Test(expectedExceptions=NotFoundException.class)
    public void testDeleteAllBranchesWithIncorrectId() throws NotFoundException {
        when(sectionDao.isExist(SECTION_ID)).thenReturn(false);

        sectionService.deleteAllTopicsInSection(SECTION_ID);
        assertTrue(false);
    }

    @Test
    public void testDeleteAllTopics() throws NotFoundException {
        Section expectedSection = new Section(SECTION_NAME);
        expectedSection.setId(SECTION_ID);
        when(sectionDao.getAll()).thenReturn(Collections.singletonList(expectedSection));
        when(sectionDao.isExist(SECTION_ID)).thenReturn(true);
        when(sectionDao.get(SECTION_ID)).thenReturn(expectedSection);

        sectionService.deleteAllTopicsInForum();

        verify(sectionDao).isExist(SECTION_ID);
        verify(sectionDao).get(SECTION_ID);
    }

    @Test(expectedExceptions=NotFoundException.class)
    public void testDeleteAllTopicsWithIncorrectId() throws NotFoundException {
        Section section = new Section(SECTION_NAME);
        section.setId(SECTION_ID);
        when(sectionDao.isExist(SECTION_ID)).thenReturn(false);
        when(sectionDao.getAll()).thenReturn(Collections.singletonList(section));

        sectionService.deleteAllTopicsInForum();
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void testCheckAccessForVisibleException()throws AccessDeniedException{
        JCUser user = new JCUser(USER_NAME, EMAIL, USER_PASSWORD);
        List<Branch> branches = new ArrayList<Branch>();
        Section section = new Section(SECTION_NAME);
        when(userService.getCurrentUser()).thenReturn(user);
        when(sectionDao.getCountAvailableBranches(user,branches)).thenReturn(0L);

        sectionService.ifSectionIsVisible(section);
    }

    @Test
    public void testCheckAccessForVisibleNoException()throws AccessDeniedException{
        JCUser user = new JCUser(USER_NAME, EMAIL, USER_PASSWORD);
        List<Branch> branches = new ArrayList<Branch>();
        Section section = new Section(SECTION_NAME);
        when(userService.getCurrentUser()).thenReturn(user);
        when(sectionDao.getCountAvailableBranches(user,branches)).thenReturn(1L);

        sectionService.ifSectionIsVisible(section);
    }
}

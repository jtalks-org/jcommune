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

import org.jtalks.jcommune.model.entity.Section;
import org.jtalks.jcommune.service.SectionService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.Breadcrumb;
import org.jtalks.jcommune.web.dto.BreadcrumbBuilder;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.ModelAndViewAssert.assertAndReturnModelAttributeOfType;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeAvailable;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.testng.Assert.assertEquals;

/**
 * @author Max Malakhov
 * @author Alexandre Teterin
 */
public class SectionControllerTest {
    private SectionService sectionService;
    private SessionRegistryImpl sessionRegistry;
    private SectionController controller;
    private BreadcrumbBuilder breadcrumbBuilder;

    @BeforeMethod
    public void init() {
        sectionService = mock(SectionService.class);
        breadcrumbBuilder = mock(BreadcrumbBuilder.class);
        sessionRegistry = mock(SessionRegistryImpl.class);
        controller = new SectionController(sectionService, breadcrumbBuilder, sessionRegistry);
    }

    @Test
    public void testDisplayAllSections() {
        //set expectations
        expectationsForAllSections();

        //invoke the object under test
        ModelAndView mav = controller.sectionList();

        //check expectations
        verifyAndAssertAllSections(mav);
    }

    private void verifyAndAssertAllSections(ModelAndView mav) {
        verify(sectionService).getAll();
        verify(breadcrumbBuilder).getForumBreadcrumb();

        //check result
        assertViewName(mav, "sectionList");
        assertModelAttributeAvailable(mav, "sectionList");
        assertModelAttributeAvailable(mav, "breadcrumbList");
        assertModelAttributeAvailable(mav, "messagesCount");
        assertModelAttributeAvailable(mav, "registeredUsersCount");
        assertModelAttributeAvailable(mav, "visitors");
        assertModelAttributeAvailable(mav, "usersRegistered");
        assertModelAttributeAvailable(mav, "visitorsRegistered");
        assertModelAttributeAvailable(mav, "visitorsGuests");
    }

    private void expectationsForAllSections() {
        when(sectionService.getAll()).thenReturn(new ArrayList<Section>());
        when(breadcrumbBuilder.getForumBreadcrumb()).thenReturn(new ArrayList<Breadcrumb>());
    }

    @Test
    public void testBranchesInSection() throws NotFoundException {
        long sectionId = 1L;
        Section section = new Section("section name");
        section.setId(sectionId);

        //set expectations
        when(sectionService.get(sectionId)).thenReturn(section);
        when(breadcrumbBuilder.getForumBreadcrumb()).thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView mav = controller.branchList(sectionId);

        //check expectations
        verify(sectionService).get(sectionId);
        verify(breadcrumbBuilder).getForumBreadcrumb();

        //check result
        assertViewName(mav, "branchList");
        assertModelAttributeAvailable(mav, "section");
        Section actualSection = assertAndReturnModelAttributeOfType(mav, "section", Section.class);
        assertEquals((long) actualSection.getId(), sectionId);
        assertModelAttributeAvailable(mav, "breadcrumbList");
    }

    @Test
    public void testRoot() {
        //set expectations
        expectationsForAllSections();

        //invoke the object under test
        ModelAndView mav = controller.root();

        //check expectations
        verifyAndAssertAllSections(mav);
    }
}

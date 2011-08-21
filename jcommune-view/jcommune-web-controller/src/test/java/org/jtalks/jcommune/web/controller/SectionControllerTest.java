package org.jtalks.jcommune.web.controller;

import org.jtalks.jcommune.model.entity.Section;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.SectionService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.Breadcrumb;
import org.jtalks.jcommune.web.dto.BreadcrumbBuilder;
import org.mockito.Mock;
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
    private SectionController controller;
    private BreadcrumbBuilder breadcrumbBuilder;

    @BeforeMethod
    public void init() {
        sectionService = mock(SectionService.class);
        breadcrumbBuilder = mock(BreadcrumbBuilder.class);
        controller = new SectionController(sectionService);
        controller.setBreadcrumbBuilder(breadcrumbBuilder);
    }

    @Test
    public void testDisplayAllSections() {
        //set expectations
        when(sectionService.getAll()).thenReturn(new ArrayList<Section>());
        when(breadcrumbBuilder.getForumBreadcrumb()).thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView mav = controller.sectionList();

        //check expectations
        verify(sectionService).getAll();
        verify(breadcrumbBuilder).getForumBreadcrumb();

        //check result
        assertViewName(mav, "sectionList");
        assertModelAttributeAvailable(mav, "sectionList");
        assertModelAttributeAvailable(mav, "breadcrumbList");
    }

    @Test
    public void testBranchesInSection() throws NotFoundException {
        long sectionId = 1L;
        Section section = new Section("section name");
        section.setId(sectionId);

        //set expectations
        when(sectionService.get(sectionId)).thenReturn(section);
        when(breadcrumbBuilder.getForumBreadcrumb(section)).thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView mav = controller.branchList(sectionId);

        //check expectations
        verify(sectionService).get(sectionId);
        verify(breadcrumbBuilder).getForumBreadcrumb(section);

        //check result
        assertViewName(mav, "branchList");
        assertModelAttributeAvailable(mav, "section");
        Section actualSection = assertAndReturnModelAttributeOfType(mav, "section", Section.class);
        assertEquals((long) actualSection.getId(), sectionId);
        assertModelAttributeAvailable(mav, "breadcrumbList");
    }
}

package org.jtalks.jcommune.web.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.ModelAndViewAssert.assertAndReturnModelAttributeOfType;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeAvailable;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;

import org.jtalks.jcommune.model.entity.Section;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.SectionService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Max Malakhov
 */
public class SectionControllerTest {
    private SectionService sectionService;
    private BranchService branchService;
    private SectionController controller;

    @BeforeMethod
    public void init() {
        sectionService = mock(SectionService.class);
        branchService = mock(BranchService.class);
        controller = new SectionController(sectionService, branchService);
    }

    @Test
    public void testDisplayAllSections() {
        when(sectionService.getAll()).thenReturn(new ArrayList<Section>());

        ModelAndView mav = controller.sectionList();

        assertViewName(mav, "sectionList");
        assertModelAttributeAvailable(mav, "sectionList");
        assertAndReturnModelAttributeOfType(mav, "sectionList", Section.class);
        
        verify(sectionService).getAll();
   }

    @Test
    public void testBranchesInSection() throws NotFoundException {
        long sectionId = 1L;
        Section section = new Section("section name");
        section.setId(sectionId);
        when(sectionService.get(sectionId)).thenReturn(section);
        
        ModelAndView mav = controller.branchList(sectionId);

        assertViewName(mav, "branchList");
        assertModelAttributeAvailable(mav, "section");
        Section actaulSection = assertAndReturnModelAttributeOfType(mav, "section", Section.class);
        assertEquals((long) actaulSection.getId(), sectionId);
        
        verify(sectionService).get(sectionId);
    }
}
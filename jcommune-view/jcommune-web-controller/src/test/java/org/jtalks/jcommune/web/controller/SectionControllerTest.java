package org.jtalks.jcommune.web.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.ModelAndViewAssert.assertAndReturnModelAttributeOfType;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeAvailable;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.jtalks.jcommune.model.entity.Branch;
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

        ModelAndView mav = controller.sectionsList();

        assertViewName(mav, "sectionsList");
        assertModelAttributeAvailable(mav, "branchesSectionList");
        verify(sectionService).getAll();
   }

    @Test
    public void testBranchesInSection() throws NotFoundException {
        long sectionId = 1L;

        when(branchService.getBranchRangeInSection(sectionId)).thenReturn(new ArrayList<Branch>());

        ModelAndView mav = controller.show(sectionId);

        assertViewName(mav, "branchesList");
        assertAndReturnModelAttributeOfType(mav, "branchs", List.class);
        Long actualSection = assertAndReturnModelAttributeOfType(mav, "sectionId", Long.class);
        assertEquals((long) actualSection, sectionId);
        verify(branchService).getBranchRangeInSection(sectionId);
    }
}
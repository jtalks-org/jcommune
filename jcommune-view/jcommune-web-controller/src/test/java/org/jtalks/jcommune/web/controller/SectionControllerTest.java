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
 * @author Alexandre Teterin
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
        //set expectations
        when(sectionService.getAll()).thenReturn(new ArrayList<Section>());

        //invoke the object under test
        ModelAndView mav = controller.sectionsList();

        //check expectations
        verify(sectionService).getAll();

        //check result
        assertViewName(mav, "sectionList");
        assertModelAttributeAvailable(mav, "sectionList");
        assertModelAttributeAvailable(mav, "breadcrumbList");
    }

    @Test(enabled = false)
    public void testBranchesInSection() throws NotFoundException {
        long sectionId = 1L;

        //set expectations
        when(branchService.getBranchRangeInSection(sectionId)).thenReturn(new ArrayList<Branch>());

        //invoke the object under test
        ModelAndView mav = controller.show(sectionId);

        //check expectations
        verify(branchService).getBranchRangeInSection(sectionId);

        //check result
        assertViewName(mav, "branchList");
        assertAndReturnModelAttributeOfType(mav, "branchList", List.class);
        Long actualSection = assertAndReturnModelAttributeOfType(mav, "sectionId", Long.class);
        assertEquals((long) actualSection, sectionId);
        assertModelAttributeAvailable(mav, "breadcrumbList");
    }
}
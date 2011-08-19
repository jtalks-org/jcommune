package org.jtalks.jcommune.service.transactional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.jtalks.jcommune.model.dao.SectionDao;
import org.jtalks.jcommune.model.entity.Section;
import org.jtalks.jcommune.service.SectionService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * This test class is intended to test all topic-related forum branch facilities
 *
 * @author Max Malakhov
 */
public class TransactionalSectionServiceTest {
    private long SECTION_ID = 13L;

    private SectionDao sectionDao;
    private SectionService sectionService;

    @BeforeMethod
    public void setUp() throws Exception {
        sectionDao = mock(SectionDao.class);
        sectionService = new TransactionalSectionService(sectionDao);
    }

    @Test
    public void testGet() throws NotFoundException {
        Section expectedSection = new Section();
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
        expectedSectionList.add(new Section());
        when(sectionDao.getAll()).thenReturn(expectedSectionList);

        List<Section> actualSectionList = sectionService.getAll();

        assertEquals(actualSectionList, expectedSectionList);
        verify(sectionDao).getAll();
    }
}

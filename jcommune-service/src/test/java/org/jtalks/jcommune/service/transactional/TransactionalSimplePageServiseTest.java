package org.jtalks.jcommune.service.transactional;


import org.jtalks.jcommune.model.dao.SamplePageDao;
import org.jtalks.jcommune.service.SamplePageService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.jtalks.jcommune.model.entity.SamplePage;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransactionalSimplePageServiseTest {

    private static final long ID = 2L;
    private static final String NAME = "name";
    private static final String CONTENT = "content";

    @Mock
    private SamplePageDao dao;

    private SamplePageService samplePageService;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        samplePageService = new TransactionalSapmlePageService(dao);
    }

    @Test
    void testUpdate() throws NotFoundException{
        SamplePage samplePage = new SamplePage(NAME, CONTENT);
        String updatedName = "new name";
        String updatedContent = "new content";
        when(dao.isExist(ID)).thenReturn(true);
        when(dao.get(ID)).thenReturn(samplePage);
        
        samplePageService.updatePage(ID, updatedName, updatedContent);
        Assert.assertEquals(samplePage.getName(), updatedName);
        Assert.assertEquals(samplePage.getContent(), updatedContent);

        verify(dao.get(ID));
        verify(dao.isExist(ID));
    }

}

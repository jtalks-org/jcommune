package org.jtalks.poulpe.service.transactional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.jtalks.poulpe.model.dao.TopicTypeDao;
import org.jtalks.poulpe.model.entity.TopicType;
import org.jtalks.poulpe.service.exceptions.NotUniqueException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TransactionalTopicTypeServiceTest extends TestCase {

	private TransactionalTopicTypeService service;

	@Mock
	private TopicTypeDao dao;

	@Override
    @BeforeMethod
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		service = new TransactionalTopicTypeService();
		service.setDao(dao);
	}

	@Test
	public void testGetAll() {
		List<TopicType> expectedList = new ArrayList<TopicType>();
		when(dao.getAll()).thenReturn(expectedList);

		List<TopicType> actualList = service.getAll();

		assertEquals(expectedList, actualList);
		verify(dao).getAll();
	}

	@Test
	public void testSaveTopicType() throws NotUniqueException {
	    TopicType topicType = new TopicType();
	    try {
		    topicType.setTitle(null);
		    service.saveTopicType(topicType);
		    fail("null name not allowed");
		} catch (IllegalArgumentException e) {
		    // ok
		}
		
		try {
            topicType.setTitle("");
            service.saveTopicType(topicType);
            fail("null name not allowed");
        } catch (IllegalArgumentException e) {
            // ok
        }
		
        when(dao.isTopicTypeNameExists(anyString())).thenReturn(true);
        try {
            topicType.setTitle("some type");
            service.saveTopicType(topicType);
            fail();
        } catch (NotUniqueException e) {
            verify(dao).isTopicTypeNameExists("some type");
        }
		
        // test successful
        reset(dao);
        when(dao.isTopicTypeNameExists(anyString())).thenReturn(false);
        service.saveTopicType(topicType);
        verify(dao).isTopicTypeNameExists("some type");
        verify(dao).saveOrUpdate(topicType);
	}

	@Test
	public void testDeleteTopicType() {
		TopicType topicType = new TopicType();
		Long testId = 12L;
		topicType.setId(testId);
		service.deleteTopicType(topicType);
		verify(dao).delete(testId);
	}
	
	@Test
    public void testDeleteTopicTypes() {
	    TopicType topicType1 = new TopicType();
	    topicType1.setId(12L);
	    TopicType topicType2 = new TopicType();
        topicType2.setId(13L);
        List<TopicType> list = Arrays.asList(topicType1, topicType2);
        service.deleteTopicTypes(list);
        for (TopicType topicType : list) {
            verify(dao).delete(topicType.getId());
        }
    }

}

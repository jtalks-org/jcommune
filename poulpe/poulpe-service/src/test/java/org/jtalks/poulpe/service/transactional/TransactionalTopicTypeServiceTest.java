package org.jtalks.poulpe.service.transactional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.jtalks.poulpe.model.dao.TopicTypeDao;
import org.jtalks.poulpe.model.entity.TopicType;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TransactionalTopicTypeServiceTest extends TestCase {

	private TransactionalTopicTypeService service;

	@Mock
	private TopicTypeDao dao;

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
	public void testSaveComponent() {
		TopicType topicType = new TopicType();
		service.saveTopicType(topicType);
		verify(dao).saveOrUpdate(topicType);
	}

	@Test
	public void testDeleteComponent() {
		TopicType topicType = new TopicType();
		Long testId = 12L;
		topicType.setId(testId);
		service.deleteTopicType(topicType);
		verify(dao).delete(testId);
	}

}

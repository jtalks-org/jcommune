package org.jtalks.jcommune.model.dao.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.jtalks.common.model.entity.Component;
import org.jtalks.common.model.entity.ComponentType;
import org.jtalks.common.model.entity.Property;
import org.jtalks.jcommune.model.dao.ComponentDao;
import org.jtalks.jcommune.model.dao.PropertyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @autor masyan
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class ComponentHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {
    private static final String PROPERTY_NAME = "property_name";
    private static final String PROPERTY_DESCRIPTION = "property_description";
    private Component cmp = new Component(PROPERTY_NAME, PROPERTY_DESCRIPTION, ComponentType.FORUM);
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private ComponentDao componentDao;
    private Session session;

    @BeforeMethod
    public void setUp() {
        session = sessionFactory.getCurrentSession();
    }

    @Test
    public void testGetComponent() {
        session.save(cmp);

        Component result = componentDao.getComponent();

        assertNotNull(result, "Property is not found by name.");
        assertEquals(result.getId(), cmp.getId(), "Property not found");
    }
}

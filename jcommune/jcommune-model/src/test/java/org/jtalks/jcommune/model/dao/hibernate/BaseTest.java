/*
 * JTalks for uniting people
 * Copyright (C) 2011  JavaTalks Team
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * Also add information on how to contact you by electronic and paper mail.
 *
 * This file creation date: Apr 12, 2011 / 8:05:19 PM
 * The JTalks Project
 * http://www.jtalks.org
 */
package org.jtalks.jcommune.model.dao.hibernate;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.jtalks.jcommune.model.entity.Persistent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;

/**
 * BaseTest for extension by unit test classes
 *
 *
 * @author Artem Mamchych
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
public abstract class BaseTest extends AbstractTransactionalTestNGSpringContextTests {

    /** Assert fail messages definitions */
    public static final String PERSISTENCE_ERROR = "Not all fields of entity successfully saved/restored from DB!";
    public static final String DB_TABLE_NOT_EMPTY = "DB table is not empty";
    public static final String ENTITIES_IS_NOT_INCREASED_BY_1 = "count of entities in DB is NOT increased by 1";
    public static final String ENTITIES_IS_NOT_INCREASED_BY_2 = "count of entities in DB is NOT increased by 2";
    public static final String DB_MUST_BE_NOT_EMPTY = "DB table must contain entities added by testSave()";
    public static final String SESSION_FACTORY_IS_NULL = "session factory is null!";

    /**
     * Cleans database table from all records.
     * @param entity instance of {@link Persistent} interface
     * @param sessionFactory Hibernate Session Factory instance
     */
    protected void clearDbTable(Persistent entity, SessionFactory sessionFactory) {
        Query query = sessionFactory.getCurrentSession().createQuery("delete "
                + entity.getClass().getSimpleName());
        query.executeUpdate();
    }
}

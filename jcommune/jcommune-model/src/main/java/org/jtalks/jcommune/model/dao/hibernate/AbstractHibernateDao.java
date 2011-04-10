package org.jtalks.jcommune.model.dao.hibernate;


import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.jtalks.jcommune.model.dao.Dao;
import org.jtalks.jcommune.model.entity.Persistent;

/**
 *
 * @author Temdegon
 */
public abstract class AbstractHibernateDao<T extends Persistent> implements Dao<T> {
    private SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}

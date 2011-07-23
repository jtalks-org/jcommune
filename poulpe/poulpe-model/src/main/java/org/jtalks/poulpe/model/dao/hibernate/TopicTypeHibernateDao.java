package org.jtalks.poulpe.model.dao.hibernate;

import java.util.List;

import org.jtalks.poulpe.model.entity.TopicType;

/**
 * @author Vladimir Bukhtoyarov
 */
public class TopicTypeHibernateDao extends AbstractHibernateDao<TopicType>
        implements org.jtalks.poulpe.model.dao.TopicTypeDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<TopicType> getAll() {
        return getSession().createQuery("from TopicType").list();
    }

}

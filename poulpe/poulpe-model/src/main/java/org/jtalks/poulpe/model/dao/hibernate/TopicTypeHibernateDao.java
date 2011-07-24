package org.jtalks.poulpe.model.dao.hibernate;

import java.util.List;

import org.hibernate.Query;
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

    @Override
    public boolean isTopicTypeNameExists(String topicTypeName) {
        String hql = "select count(*) from TopicType t where t.title = ?";
        Query query = getSession().createQuery(hql);
        query.setString(0, topicTypeName);
        Number count = (Number) query.uniqueResult();
        return count.intValue() != 0;
    }

}

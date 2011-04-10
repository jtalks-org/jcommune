package org.jtalks.jcommune.model.dao.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.jtalks.jcommune.model.entity.Persistent;
import org.jtalks.jcommune.model.entity.User;

/**
 *
 * @author Temdegon
 */
public class UserHibernateDao extends AbstractHibernateDao<Persistent> {

    @Override
    public void saveOrUpdate(Persistent user) {
        getSession().save(user);
    }

    @Override
    public void delete(Long userId) {
        Query query = getSession().createQuery("delete Author where id= :authorId");
        query.setLong("authorId", userId);
        query.executeUpdate();
    }

    @Override
    public void delete(Persistent user) {
        getSession().delete(user);
    }

    @Override
    public User get(Long id) {
        return (User)getSession().load(User.class, id);
    }

    @Override
    public List<Persistent> getAll() {
        return getSession().createQuery("from User").list();
    }
    
}

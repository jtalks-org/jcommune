package org.jtalks.jcommune.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jtalks.jcommune.model.dao.Dao;
import org.jtalks.jcommune.model.entity.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Temdegon
 */
public class Starter {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("/org/jtalks/jcommune/model/entity/applicationContext-dao.xml");
        Dao<User> uhd = (Dao) context.getBean("userDao");
        SessionFactory factory = (SessionFactory) context.getBean("sessionFactory");
        Session session = factory.openSession();
        User user = new User();
        user.setNickName("TEmdegon");

        Transaction trans = session.beginTransaction();
        trans.begin();
        //uhd.saveOrUpdate(user);
        trans.commit();
    }
}
